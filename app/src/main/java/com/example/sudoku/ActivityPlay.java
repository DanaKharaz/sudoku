package com.example.sudoku;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Objects;
import java.util.Stack;

import kotlin.Triple;

public class ActivityPlay extends AppCompatActivity {
    // DocMe : general activity
    /** This activity... */

    // TODO: add timer


    // FIXME : organize variables and clearly annotate
    Sudoku sudoku;
    boolean[][] playable = new boolean[9][9];
    int[][] numberPositions = new int[9][9]; // positions of each number formatted as (10*row + col + 1)

    int mistakes = 0;
    TextView[] hearts = new TextView[3];

    boolean marking = false; // true when pencil is clicked
    boolean[][][] marks = new boolean[9][9][9]; // x - row, y - col, z - number
    HashMap<Integer, Integer> boldMarkCodes = new HashMap<>();

    int cellSelected = 0; // formatted as (10*row + col + 1), 0 when not selected
    boolean erasing = false;
    boolean hinting = false;
    int selectedNumber = 0; // 0 when no number is selected

    TextView btnUndo;
    View layoutUndo;
    TextView btnErase;
    View layoutErase;
    TextView btnPencil;
    View layoutPencil;
    TextView btnHint;
    View layoutHint;
    int hintsLeft = 1; // FixMe : test value of 1

    TextView[] btnNumbers = new TextView[9];
    int[] numberCounts = {9, 9, 9, 9, 9, 9, 9, 9, 9}; // FixMe change number background when 0->1 or 1->0
    TextView[][] btnCells = new TextView[9][9];

    int iconBtnTopMargin; // for 'un-clicking' erase, pencil, and hint buttons
    int numberBtnTopMargin; // for 'un-clicking' number buttons

    Stack<Triple<Integer, Integer, Integer>> moveHistory = new Stack<>(); // first - position (10*row+col+1), second - number (0 unless erased), third - when mark added (otherwise 0)

    @SuppressLint({"UseCompatLoadingForDrawables", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        // TODO:
        // create new Sudoku object if making a puzzle
        // retrieve needed Sudoku object if solving a puzzle
        // set number of hints

        // set codePoints for bold marks
        boldMarkCodes.put(1, 120813);
        boldMarkCodes.put(2, 120814);
        boldMarkCodes.put(3, 120815);
        boldMarkCodes.put(4, 120816);
        boldMarkCodes.put(5, 120817);
        boldMarkCodes.put(6, 120818);
        boldMarkCodes.put(7, 120819);
        boldMarkCodes.put(8, 120820);
        boldMarkCodes.put(9, 120821);

        // set hearts
        hearts[0] = findViewById(R.id.heart1);
        hearts[1] = findViewById(R.id.heart2);
        hearts[2] = findViewById(R.id.heart3);

        // set the grid
        setCells();
        //int[][] grid = sudoku.getGrid(); // UI TESTS !!!
        int [][] grid = new int[9][9];
        grid[2][0] = 1;
        grid[4][1] = 1;
        grid[7][2] = 1;
        grid[5][3] = 1;
        grid[1][4] = 1;
        grid[6][5] = 1;
        grid[0][6] = 1;
        grid[8][7] = 1;
        grid[3][8] = 1;
        grid[4][4] = 5;
        grid[8][8] = 7;
        sudoku = new Sudoku(grid);

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                btnCells[i][j].setClickable(true);

                // text params
                btnCells[i][j].setGravity(Gravity.CENTER);
                btnCells[i][j].setTypeface(Typeface.MONOSPACE);
                btnCells[i][j].setTextSize(30);
                btnCells[i][j].setTextColor(getColor(R.color.purple_dark));

                if (grid[i][j] != 0) {
                    numberCounts[grid[i][j] - 1]--; // one less instance of this number left
                    numberPositions[grid[i][j] - 1][8 - numberCounts[grid[i][j] - 1]] = 10*i + j + 1; // add position of this number

                    btnCells[i][j].setText(String.valueOf(grid[i][j]));

                    // set onClick
                    int finalI = i;
                    int finalJ = j;
                    btnCells[i][j].setOnClickListener(v -> {
                        if (marking) {
                            Toast.makeText(this, "Cannot use pencil on pre-filled cell", Toast.LENGTH_SHORT).show();
                        } else if (hinting) {
                            Toast.makeText(this, "Cannot use hint on pre-filled cell", Toast.LENGTH_SHORT).show();
                        } else if (erasing) {
                            Toast.makeText(this, "Cannot erase pre-filled cell", Toast.LENGTH_SHORT).show();
                        } else { // highlights needed cells
                            selectCell(finalI, finalJ);
                            // TestMe
                            // put down all number buttons
                            for (int n = 0; n < 9; n++) {
                                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) btnNumbers[n].getLayoutParams();
                                params.topMargin = numberBtnTopMargin;
                                btnNumbers[n].setLayoutParams(params);
                            }
                        }
                    });
                } else {
                    playable[i][j] = true;

                    // change to rounded corners
                    if (i < 3 && (j < 3 || j > 5) ||
                            i > 5 && (j < 3 || j > 5) ||
                            i > 2 && i < 6 && j > 2 && j < 6) {
                        btnCells[i][j].setBackground(getDrawable(R.drawable.cell_play_purple));
                    } else {
                        btnCells[i][j].setBackground(getDrawable(R.drawable.cell_play_white));
                    }

                    // set onClick
                    int finalI = i;
                    int finalJ = j;
                    btnCells[i][j].setOnClickListener(v -> {
                        if (hinting) {
                            if (hintsLeft == 0) {
                                Toast.makeText(this, "No hints left", Toast.LENGTH_SHORT).show();
                            } else {
                                if (playable[finalI][finalJ]) {
                                    Sudoku temp = sudoku.copy();
                                    int[][] sol = temp.solve();
                                    if (!Objects.isNull(sol)) { // no solution currently
                                        Toast.makeText(this, "In the current state, the puzzle appears to be unsolvable, try to change values and use the hint again", Toast.LENGTH_SHORT).show();
                                    } else {
                                        int n = temp.getGrid()[finalI][finalJ];
                                        sudoku.putValue(finalI, finalJ, n);
                                        hintsLeft--;
                                        // fix UI
                                        btnCells[finalI][finalJ].setText(String.valueOf(n));
                                        btnCells[finalI][finalJ].setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);
                                    }
                                } else {
                                    Toast.makeText(this, "This is a pre-filled cell, please choose another one", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else if (erasing) {
                            int n = sudoku.getGrid()[finalI][finalJ];
                            if (n == 0) {
                                Toast.makeText(this, "Nothing to erase", Toast.LENGTH_SHORT).show();
                            } else {
                                sudoku.removeValue(finalI, finalJ); // fix grid
                                for (int k = 0; k < 9; k++) { // fix numberPositions
                                    if (numberPositions[n - 1][k] == 0) {
                                        break;
                                    } else if (numberPositions[n - 1][k] == 10*finalI + finalJ + 1) {
                                        do {
                                            try {
                                                numberPositions[n - 1][k] = numberPositions[n - 1][k + 1];
                                            } catch (Exception e) {
                                                numberPositions[n - 1][k] = 0;
                                            }
                                            k++;
                                        } while (k < 9 && numberPositions[n - 1][k] != 0);
                                        break;
                                    }
                                }
                                // fix UI
                                btnCells[finalI][finalJ].setText("");
                                btnCells[finalI][finalJ].setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);

                                // adjust number counts and positions
                                numberCounts[n - 1]++;
                                btnNumbers[n - 1].setText(styleBtnText(n, numberCounts[n - 1]));

                                // add to move history
                                moveHistory.push(new Triple<>(10*finalI + finalJ + 1, n, 0));
                            }
                        } else if (marking) {
                            selectCell(finalI, finalJ); // highlight needed cells
                            // raise marked numbers if there are any
                            for (int n = 0; n < 9; n++) {
                                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) btnNumbers[n].getLayoutParams();
                                if (marks[finalI][finalJ][n]) {
                                    params.topMargin = 20;
                                } else {
                                    params.topMargin = numberBtnTopMargin;
                                }
                                btnNumbers[n].setLayoutParams(params);
                            }
                        } else { // TestMe
                            if (selectedNumber != 0) { // a number button is clicked, so write that number here
                                if (sudoku.isIllegal(finalI, finalJ, selectedNumber)) { // FixMe
                                    Toast.makeText(this, "not legal", Toast.LENGTH_SHORT).show();
                                } else {
                                    btnCells[finalI][finalJ].setText(String.valueOf(selectedNumber));
                                    btnCells[finalI][finalJ].setTextSize(30);
                                    sudoku.putValue(finalI, finalJ, selectedNumber);

                                    numberCounts[selectedNumber - 1]--;
                                    btnNumbers[selectedNumber - 1].setText(styleBtnText(selectedNumber, numberCounts[selectedNumber - 1]));

                                    int index = 0;
                                    do {
                                        index++;
                                    } while (index < 9 && numberPositions[selectedNumber - 1][index] != 0);
                                    numberPositions[selectedNumber - 1][index] = 10 * finalI + finalJ + 1;

                                    for (int m = 0; m < 9; m++) {marks[finalI][finalJ][m] = false;} // remove marks
                                }
                            }
                            selectCell(finalI, finalJ); // highlight needed cells
                        }
                    });
                }
            }
        }

        // set number buttons
        btnNumbers[0] = findViewById(R.id.number1);
        btnNumbers[1] = findViewById(R.id.number2);
        btnNumbers[2] = findViewById(R.id.number3);
        btnNumbers[3] = findViewById(R.id.number4);
        btnNumbers[4] = findViewById(R.id.number5);
        btnNumbers[5] = findViewById(R.id.number6);
        btnNumbers[6] = findViewById(R.id.number7);
        btnNumbers[7] = findViewById(R.id.number8);
        btnNumbers[8] = findViewById(R.id.number9);
        // top margin value for all number buttons
        numberBtnTopMargin = ((ViewGroup.MarginLayoutParams) btnNumbers[0].getLayoutParams()).topMargin;
        // stylize buttons and set onClick
        for (int k = 0; k < 9; k++) {
            btnNumbers[k].setText(styleBtnText(k + 1, numberCounts[k])); // text: number, showing how many instances of it are left
            // change background for already 'completed' numbers (no more instances left)
            if (numberCounts[k] == 0) {btnNumbers[k].setBackground(getDrawable(R.drawable.num_purple));}

            int finalK = k;
            btnNumbers[k].setOnClickListener(v -> {
                if (numberCounts[finalK] == 0) { // cannot write or mark this number as it is 'completed'
                    Toast.makeText(this, "All positions with this number have been filled already", Toast.LENGTH_SHORT).show();
                } else if (marking && cellSelected != 0) { // adding/removing a mark from the selected cell
                    int i = (cellSelected - 1)/10;
                    int j = (cellSelected - 1)%10;
                    if (marks[i][j][finalK]) { // removing the mark and lowering the button
                        String markCur = (String) btnCells[i][j].getText();
                        String markNew = markCur.replace(String.valueOf(finalK + 1), " ");
                        if (markNew.equals("     \n     \n     ")) { // no more marks in this cell
                            btnCells[i][j].setTextSize(30);
                            btnCells[i][j].setText("");
                        } else {
                            btnCells[i][j].setText(markNew);
                        }
                        // lower the button
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) btnNumbers[finalK].getLayoutParams();
                        params.topMargin = numberBtnTopMargin;
                        btnNumbers[finalK].setLayoutParams(params);
                        // change marks
                        marks[i][j][finalK] = false;
                    } else { // adding a mark and raising the button
                        String markCur;
                        if (!(marks[i][j][0] || marks[i][j][1] || marks[i][j][2] ||
                                marks[i][j][3] || marks[i][j][4] || marks[i][j][5] ||
                                marks[i][j][6] || marks[i][j][7] || marks[i][j][8])) { // no marks here yet, so change text size
                            markCur = "     \n     \n     ";
                            btnCells[i][j].setTextSize(10);
                        } else {
                            markCur = (String) btnCells[i][j].getText();
                        }
                        String markNew = markCur.substring(0, 2*finalK).concat(String.valueOf(finalK + 1)).concat(markCur.substring(2*finalK + 1));
                        btnCells[i][j].setText(markNew);
                        // raise the button
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) btnNumbers[finalK].getLayoutParams();
                        params.topMargin = 20;
                        btnNumbers[finalK].setLayoutParams(params);
                        // change marks
                        marks[i][j][finalK] = true;
                    }
                } else if (marking) {
                    // TODO : this number will be added to all cells selected after as marks
                    //
                } else if (cellSelected != 0) { // trying to write this number in the selected cell
                    int i = (cellSelected - 1)/10;
                    int j = (cellSelected - 1)%10;
                    if (sudoku.isIllegal(i, j, finalK + 1)) {
                        // TODO : wrong, so fix UI and don't change numberCounts
                        Toast.makeText(this, "illegal", Toast.LENGTH_SHORT).show();
                    } else {
                        int n = sudoku.getGrid()[i][j];

                        sudoku.putValue(i, j, finalK + 1);
                        numberCounts[finalK]--;
                        btnNumbers[finalK].setText(styleBtnText(finalK + 1, numberCounts[finalK]));

                        // number positions
                        int index = 0;
                        do {index++;} while (numberPositions[finalK][index] != 0);
                        numberPositions[finalK][index] = 10*i + j + 1;

                        if (n != 0) { // rewriting the cell, so fix count and position of the old number as well
                            numberCounts[n - 1]++;
                            btnNumbers[n - 1].setText(styleBtnText(n, numberCounts[n - 1]));

                            index = 0;
                            do {index++;} while (numberPositions[n - 1][index] != 10*i + j + 1);
                            numberPositions[n - 1][index] = 0;
                        }

                        btnCells[i][j].setText(String.valueOf(finalK + 1));
                        btnCells[i][j].setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);

                        // move history
                        moveHistory.push(new Triple<>(10*i + j + 1, finalK + 1, 0));

                        // remove marks
                        for (int m = 0; m < 9; m ++) {marks[i][j][m] = false;}
                    }
                } else { // choosing or un-choosing a number to write in cells PENDING
                    // fix alignment of other numbers
                    if (selectedNumber == finalK + 1) { // un-choosing this number TestMe
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) btnNumbers[finalK].getLayoutParams();
                        params.topMargin = numberBtnTopMargin;
                        btnNumbers[finalK].setLayoutParams(params);
                        selectedNumber = 0;

                        resetCellBackgroundsAndFonts();
                    } else {
                        for (int i = 0; i < 9; i++) {
                            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) btnNumbers[i].getLayoutParams();
                            if (i == finalK) {
                                params.topMargin = 20;
                            } else {
                                params.topMargin = numberBtnTopMargin;
                            }
                            btnNumbers[i].setLayoutParams(params);
                        }
                        selectedNumber = finalK + 1;

                        // TestMe : make selected number bold
                        // highlight same number
                        int index = 0;
                        while (index < 9 && numberPositions[finalK][index] != 0) {
                            int i = (numberPositions[finalK][index] - 1) / 10;
                            int j = (numberPositions[finalK][index] - 1) % 10;
                            btnCells[i][j].setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
                            index++;
                        }

                        // highlight marks with the same number
                        for (int x = 0; x < 9; x++) {
                            for (int y = 0; y < 9; y++) {
                                if (marks[x][y][finalK]) { // make n bold, the rest normal
                                    String marksCur = (String) btnCells[x][y].getText();
                                    String marksNew = marksCur.substring(0, 2 * finalK)
                                            .concat(String.valueOf(Character.toChars(boldMarkCodes.get(finalK + 1))))
                                            .concat(marksCur.substring(2 * finalK + 1));
                                    btnCells[x][y].setText(marksNew);
                                    btnCells[x][y].setTextSize(10);
                                }
                            }
                        }
                    }
                }
            });
        }

        // set undo, erase, pencil, and hint buttons
        btnUndo = findViewById(R.id.undo);
        layoutUndo = findViewById(R.id.layoutUndo);
        // top margin value for other icon buttons
        iconBtnTopMargin = ((ViewGroup.MarginLayoutParams) layoutUndo.getLayoutParams()).topMargin;
        btnUndo.setOnClickListener(v -> { // FixMe : erase or mark
            // 'un-click' other buttons
            if (erasing) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layoutErase.getLayoutParams();
                params.topMargin = iconBtnTopMargin;
                layoutErase.setLayoutParams(params);
                erasing = false;
            } else if (marking) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layoutPencil.getLayoutParams();
                params.topMargin = iconBtnTopMargin;
                layoutPencil.setLayoutParams(params);
                marking = false;
            } else if (hinting) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layoutHint.getLayoutParams();
                params.topMargin = iconBtnTopMargin;
                layoutHint.setLayoutParams(params);
                hinting = false;
            }

            if (moveHistory.size() == 0) {
                Toast.makeText(this, "Nothing to undo", Toast.LENGTH_SHORT).show();
            } else {
                Triple<Integer, Integer, Integer> move = moveHistory.pop();
                int row = (move.component1() - 1)/10;
                int col = (move.component1() - 1)%10;
                int erased = move.component2();
                int mark = move.component3();

                if (erased == 0 && mark == 0) { // removing number from cell
                    int n = sudoku.getGrid()[row][col];
                    sudoku.removeValue(row, col); // fix grid

                    int index = 8;
                    do {index--;} while (index >= 0 && numberPositions[n - 1][index] == 0);
                    numberPositions[n - 1][index] = 0; // fix numberPositions

                    btnCells[row][col].setText(""); // fix UI
                    selectCell(row, col);

                    numberCounts[n - 1]++;
                    btnNumbers[n - 1].setText(styleBtnText(n, numberCounts[n - 1]));
                } else if (erased == 0) { // removing mark from cell
                    marks[row][col][mark - 1] = true;
                    String markCur;
                    int count = 0;
                    for (int c = 0; c < 9; c++) {if (marks[row][col][c]) {count++;}}
                    // no marks currently if count == 1
                    if (count == 1) {markCur = "     \n     \n     ";} else {markCur = (String) btnCells[row][col].getText();}
                    // TestMe : index may be out of range when mark == 9 ???
                    String markNew = markCur.substring(0, 2*mark - 2).concat(String.valueOf(mark)).concat(markCur.substring(2*mark - 1));
                    btnCells[row][col].setText(markNew);
                    btnCells[row][col].setTextSize(10);
                } else if (mark == 0) { // adding a number back to cell TestMe
                    if (sudoku.isIllegal(row, col, erased)) {
                        // TODO : write the number again, highlight wrong, but don't remove hearts
                    } else {
                        sudoku.putValue(row, col, erased);
                        btnCells[row][col].setText(String.valueOf(erased));
                        btnCells[row][col].setTextSize(30);

                        numberCounts[erased - 1]++;
                        btnNumbers[erased - 1].setText(styleBtnText(erased, numberCounts[erased - 1]));

                        int index = 0;
                        do {index++;} while (index < 9 && numberPositions[erased - 1][index] != 0);
                        numberPositions[erased - 1][index] = 10*row + col + 1;

                        for (int m = 0; m < 9; m++) {marks[row][col][m] = false;} // remove marks in the cell
                    }
                } else { // should never happen
                    Toast.makeText(this, "Oops... something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnErase = findViewById(R.id.erase);
        layoutErase = findViewById(R.id.layoutErase);
        btnErase.setOnClickListener(v -> {
            if (cellSelected != 0) {
                // PENDING : combine partially with else
            } else {
                resetCellBackgroundsAndFonts();

                // adjust button position
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layoutErase.getLayoutParams();
                if (erasing) {
                    params.topMargin = iconBtnTopMargin; // now same as others
                } else {
                    params.topMargin = 40; // now above others

                    // 'un-click' the rest
                    if (marking) {
                        ViewGroup.MarginLayoutParams paramsPencil = (ViewGroup.MarginLayoutParams) layoutPencil.getLayoutParams();
                        paramsPencil.topMargin = iconBtnTopMargin;
                        layoutPencil.setLayoutParams(paramsPencil);
                        marking = false;
                    } else if (hinting) {
                        ViewGroup.MarginLayoutParams paramsHint = (ViewGroup.MarginLayoutParams) layoutHint.getLayoutParams();
                        paramsHint.topMargin = iconBtnTopMargin;
                        layoutHint.setLayoutParams(paramsHint);
                        hinting = false;
                    }
                }
                erasing = !erasing;
                layoutErase.setLayoutParams(params);
            }
        });

        btnPencil = findViewById(R.id.pencil);
        layoutPencil = findViewById(R.id.layoutPencil);
        btnPencil.setOnClickListener(v -> {
            /*
            cellSelected = 0;
            resetCellBackgroundsAndFonts(); // SomethingStrange : why unselect the potentially selected cell??
             */

            // adjust button position
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layoutPencil.getLayoutParams();
            if (marking) {
                params.topMargin = iconBtnTopMargin; // now same as others

                // fix alignment of number buttons
                for (int n = 0; n < 9; n++) {
                    ViewGroup.MarginLayoutParams paramsNum = (ViewGroup.MarginLayoutParams) btnNumbers[n].getLayoutParams();
                    paramsNum.topMargin = numberBtnTopMargin;
                    btnNumbers[n].setLayoutParams(paramsNum);
                }
            } else {
                params.topMargin = 40; // now above others

                // 'un-click' the rest
                if (erasing) {
                    ViewGroup.MarginLayoutParams paramsErase = (ViewGroup.MarginLayoutParams) layoutErase.getLayoutParams();
                    paramsErase.topMargin = iconBtnTopMargin;
                    layoutErase.setLayoutParams(paramsErase);
                    erasing = false;
                } else if (hinting) {
                    ViewGroup.MarginLayoutParams paramsHint = (ViewGroup.MarginLayoutParams) layoutHint.getLayoutParams();
                    paramsHint.topMargin = iconBtnTopMargin;
                    layoutHint.setLayoutParams(paramsHint);
                    hinting = false;
                }
            }
            marking = !marking;
            layoutPencil.setLayoutParams(params);
        });


        btnHint = findViewById(R.id.hint);
        layoutHint = findViewById(R.id.layoutHint);
        btnHint.setOnClickListener(v -> {
            if (cellSelected != 0) {
                // PENDING : combine partially with else
            } else {
                resetCellBackgroundsAndFonts();

                // adjust button position
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layoutHint.getLayoutParams();
                if (hinting) {
                    params.topMargin = iconBtnTopMargin; // now same as others
                } else {
                    params.topMargin = 40; // now above others

                    // 'un-click' the rest
                    if (marking) {
                        ViewGroup.MarginLayoutParams paramsPencil = (ViewGroup.MarginLayoutParams) layoutPencil.getLayoutParams();
                        paramsPencil.topMargin = iconBtnTopMargin;
                        layoutPencil.setLayoutParams(paramsPencil);
                        marking = false;
                    } else if (erasing) {
                        ViewGroup.MarginLayoutParams paramsErase = (ViewGroup.MarginLayoutParams) layoutErase.getLayoutParams();
                        paramsErase.topMargin = iconBtnTopMargin;
                        layoutErase.setLayoutParams(paramsErase);
                        erasing = false;
                    }
                }
                hinting = !hinting;
                layoutHint.setLayoutParams(params);
            }
        });
    }

    /** Creates a string of different-size text
     * Used on number buttons so the number on the button also has a smaller number indicating how many instances of the button number are left in the sudoku */
    public Spanned styleBtnText(int num, int count) {
        String html = "<h1><big><font color='#4F1964'>" +
                String.valueOf(num) +
                "</font></big><br/><small><font color='#4F1964'>" +
                String.valueOf(count) +
                "</font></small></h1>";
        return((Html.fromHtml(html, 0)));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void selectCell(int row, int col) {
        resetCellBackgroundsAndFonts();

        if (cellSelected == 10*row + col + 1) { // un-choosing the cell
            cellSelected = 0;
            return;
        }

        int n = sudoku.getGrid()[row][col] - 1;

        if (marking) {
            // PENDING
            // SomethingStrange
        }

        // highlighting cells
        if (n != -1) {
            // highlight same number
            int index = 0;
            while (index < 9 && numberPositions[n][index] != 0) {
                int i = (numberPositions[n][index] - 1) / 10;
                int j = (numberPositions[n][index] - 1) % 10;
                btnCells[i][j].setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
                index++;
            }

            // highlight marks with the same number
            for (int x = 0; x < 9; x++) {
                for (int y = 0; y < 9; y++) {
                    if (marks[x][y][n]) { // make n bold, the rest normal
                        String marksCur = (String) btnCells[x][y].getText();

                        String marksNew = marksCur.substring(0, 2*n)
                                .concat(String.valueOf(Character.toChars(boldMarkCodes.get(n + 1))))
                                .concat(marksCur.substring(2*n + 1));
                        btnCells[x][y].setText(marksNew);
                        btnCells[x][y].setTextSize(10);
                    }
                }
            }

            // raise the corresponding number button TestMe
            for (int i = 0; i < 9; i++) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) btnNumbers[i].getLayoutParams();
                if (i == n) {
                    params.topMargin = 20;
                } else {
                    params.topMargin = numberBtnTopMargin;
                }
                btnNumbers[i].setLayoutParams(params);
            }
        }

        // find and highlight 'neighbours'
        // row and column
        for (int i = 0; i < 9; i++) {
            // same row
            if (playable[row][i]) {
                btnCells[row][i].setBackground(getDrawable(R.drawable.cell_play_highlight));}
            else {
                btnCells[row][i].setBackground(getDrawable(R.drawable.cell_set_highlight));}
            // same column
            if (playable[i][col]) {
                btnCells[i][col].setBackground(getDrawable(R.drawable.cell_play_highlight));}
            else {
                btnCells[i][col].setBackground(getDrawable(R.drawable.cell_set_highlight));}
        }
        // 3x3 box
        int startRow = row/3*3; // determine block's lowest row index
        int startCol = col/3*3; // determine block's lowest col index
        for (int i = startRow; i < startRow + 3; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                if (playable[i][j]) {
                    btnCells[i][j].setBackground(getDrawable(R.drawable.cell_play_highlight));}
                else {
                    btnCells[i][j].setBackground(getDrawable(R.drawable.cell_set_highlight));}
            }
        }

        // highlight the cell itself
        if (playable[row][col]) {
            btnCells[row][col].setBackground(getDrawable(R.drawable.cell_play_chosen));
        } else {
            btnCells[row][col].setBackground(getDrawable(R.drawable.cell_set_chosen));
        }

        cellSelected = 10*row + col + 1;
    }


    /** All cell backgrounds are set to default (white or purple), all numbers or marks are made not bold */
    @SuppressLint("UseCompatLoadingForDrawables")
    public void resetCellBackgroundsAndFonts() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                boolean purpleCondition = i < 3 && (j < 3 || j > 5) ||
                                          i > 5 && (j < 3 || j > 5) ||
                                          i > 2 && i < 6 && j > 2 && j < 6;
                if (playable[i][j]) {
                    if (purpleCondition) {
                        btnCells[i][j].setBackground(getDrawable(R.drawable.cell_play_purple));}
                    else {
                        btnCells[i][j].setBackground(getDrawable(R.drawable.cell_play_white));}
                }
                else {
                    if (purpleCondition) {
                        btnCells[i][j].setBackground(getDrawable(R.drawable.cell_set_purple));}
                    else {
                        btnCells[i][j].setBackground(getDrawable(R.drawable.cell_set_white));}
                }
                btnCells[i][j].setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);
                String mark = "     \n     \n     ";
                boolean found = false;
                for (int k = 0; k < 9; k++) {
                    if (marks[i][j][k]) {
                        found = true;
                        mark = mark.substring(0, 2*k).concat(String.valueOf(k + 1).concat(mark.substring(2*k + 1)));
                    }
                }
                if (found) {btnCells[i][j].setText(mark);}
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void check() {
        // PENDING
        // TODO: check for mistakes and highlight wring answers

        // adjust hearts
        for (int i = 2; i >= 3 - mistakes; i--) {
            hearts[i].setBackground(getDrawable(R.drawable.ic_heart_dead));
        }
        if (mistakes == 3) {
            gameOver();
        }

        // TODO: check if solved
    }

    public void gameOver() {
        // PENDING
        // TODO: popup about the game

        Toast.makeText(this, "Game Over", Toast.LENGTH_SHORT).show();
    }

    /** Initializing main UI elements with setCells(). Each sudoku cell is a clickable TextView. */
    public void setCells() {
        // block 1
        btnCells[0][0] = findViewById(R.id.cell11);
        btnCells[0][1] = findViewById(R.id.cell12);
        btnCells[0][2] = findViewById(R.id.cell13);
        btnCells[1][0] = findViewById(R.id.cell21);
        btnCells[1][1] = findViewById(R.id.cell22);
        btnCells[1][2] = findViewById(R.id.cell23);
        btnCells[2][0] = findViewById(R.id.cell31);
        btnCells[2][1] = findViewById(R.id.cell32);
        btnCells[2][2] = findViewById(R.id.cell33);
        // block 2
        btnCells[0][3] = findViewById(R.id.cell14);
        btnCells[0][4] = findViewById(R.id.cell15);
        btnCells[0][5] = findViewById(R.id.cell16);
        btnCells[1][3] = findViewById(R.id.cell24);
        btnCells[1][4] = findViewById(R.id.cell25);
        btnCells[1][5] = findViewById(R.id.cell26);
        btnCells[2][3] = findViewById(R.id.cell34);
        btnCells[2][4] = findViewById(R.id.cell35);
        btnCells[2][5] = findViewById(R.id.cell36);
        // block 3
        btnCells[0][6] = findViewById(R.id.cell17);
        btnCells[0][7] = findViewById(R.id.cell18);
        btnCells[0][8] = findViewById(R.id.cell19);
        btnCells[1][6] = findViewById(R.id.cell27);
        btnCells[1][7] = findViewById(R.id.cell28);
        btnCells[1][8] = findViewById(R.id.cell29);
        btnCells[2][6] = findViewById(R.id.cell37);
        btnCells[2][7] = findViewById(R.id.cell38);
        btnCells[2][8] = findViewById(R.id.cell39);
        // block 4
        btnCells[3][0] = findViewById(R.id.cell41);
        btnCells[3][1] = findViewById(R.id.cell42);
        btnCells[3][2] = findViewById(R.id.cell43);
        btnCells[4][0] = findViewById(R.id.cell51);
        btnCells[4][1] = findViewById(R.id.cell52);
        btnCells[4][2] = findViewById(R.id.cell53);
        btnCells[5][0] = findViewById(R.id.cell61);
        btnCells[5][1] = findViewById(R.id.cell62);
        btnCells[5][2] = findViewById(R.id.cell63);
        // block 5
        btnCells[3][3] = findViewById(R.id.cell44);
        btnCells[3][4] = findViewById(R.id.cell45);
        btnCells[3][5] = findViewById(R.id.cell46);
        btnCells[4][3] = findViewById(R.id.cell54);
        btnCells[4][4] = findViewById(R.id.cell55);
        btnCells[4][5] = findViewById(R.id.cell56);
        btnCells[5][3] = findViewById(R.id.cell64);
        btnCells[5][4] = findViewById(R.id.cell65);
        btnCells[5][5] = findViewById(R.id.cell66);
        // block 6
        btnCells[3][6] = findViewById(R.id.cell47);
        btnCells[3][7] = findViewById(R.id.cell48);
        btnCells[3][8] = findViewById(R.id.cell49);
        btnCells[4][6] = findViewById(R.id.cell57);
        btnCells[4][7] = findViewById(R.id.cell58);
        btnCells[4][8] = findViewById(R.id.cell59);
        btnCells[5][6] = findViewById(R.id.cell67);
        btnCells[5][7] = findViewById(R.id.cell68);
        btnCells[5][8] = findViewById(R.id.cell69);
        // block 7
        btnCells[6][0] = findViewById(R.id.cell71);
        btnCells[6][1] = findViewById(R.id.cell72);
        btnCells[6][2] = findViewById(R.id.cell73);
        btnCells[7][0] = findViewById(R.id.cell81);
        btnCells[7][1] = findViewById(R.id.cell82);
        btnCells[7][2] = findViewById(R.id.cell83);
        btnCells[8][0] = findViewById(R.id.cell91);
        btnCells[8][1] = findViewById(R.id.cell92);
        btnCells[8][2] = findViewById(R.id.cell93);
        // block 8
        btnCells[6][3] = findViewById(R.id.cell74);
        btnCells[6][4] = findViewById(R.id.cell75);
        btnCells[6][5] = findViewById(R.id.cell76);
        btnCells[7][3] = findViewById(R.id.cell84);
        btnCells[7][4] = findViewById(R.id.cell85);
        btnCells[7][5] = findViewById(R.id.cell86);
        btnCells[8][3] = findViewById(R.id.cell94);
        btnCells[8][4] = findViewById(R.id.cell95);
        btnCells[8][5] = findViewById(R.id.cell96);
        // block 9
        btnCells[6][6] = findViewById(R.id.cell77);
        btnCells[6][7] = findViewById(R.id.cell78);
        btnCells[6][8] = findViewById(R.id.cell79);
        btnCells[7][6] = findViewById(R.id.cell87);
        btnCells[7][7] = findViewById(R.id.cell88);
        btnCells[7][8] = findViewById(R.id.cell89);
        btnCells[8][6] = findViewById(R.id.cell97);
        btnCells[8][7] = findViewById(R.id.cell98);
        btnCells[8][8] = findViewById(R.id.cell99);
    }
}