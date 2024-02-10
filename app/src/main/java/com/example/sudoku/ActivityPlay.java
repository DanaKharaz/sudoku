package com.example.sudoku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Stack;

public class ActivityPlay extends AppCompatActivity {

    // TODO: add timer

    Sudoku sudoku;
    boolean[][] playable = new boolean[9][9];
    int[][] numberPositions = new int[9][9]; // positions of each number formatted as (10*row + col + 1)

    int mistakes = 0;
    TextView[] hearts = new TextView[3];

    boolean marking = false; // true when pencil is clicked
    boolean[][][] marks = new boolean[9][9][9]; // x - row, y - col, z - number

    boolean cellSelected = false;
    boolean erasing = false;
    boolean hinting = false;

    TextView btnUndo;
    View layoutUndo;
    TextView btnErase;
    View layoutErase;
    TextView btnPencil;
    View layoutPencil;
    TextView btnHint;
    View layoutHint;

    TextView[] numbers = new TextView[9];
    int[] numberCounts = {9, 9, 9, 9, 9, 9, 9, 9, 9};
    TextView[][] txtCells = new TextView[9][9];

    Stack<Pair<Integer, Integer>> moveHistory = new Stack<>();

    @SuppressLint({"UseCompatLoadingForDrawables", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        // TODO:
        // create new Sudoku object if making a puzzle
        // retrieve needed Sudoku object if solving a puzzle

        // set hearts
        hearts[0] = findViewById(R.id.heart1);
        hearts[1] = findViewById(R.id.heart2);
        hearts[2] = findViewById(R.id.heart3);

        // set the grid
        setCells();
        //int[][] grid = sudoku.getGrid(); // UI TESTS !!!
        int [][] grid = new int[9][9];
        grid[5][7] = 9;
        grid[4][8] = 9;
        grid[0][0] = 1;
        sudoku = new Sudoku(grid);

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                txtCells[i][j].setClickable(true);

                // text params
                txtCells[i][j].setGravity(Gravity.CENTER);
                txtCells[i][j].setTypeface(Typeface.MONOSPACE);
                txtCells[i][j].setTextSize(30);
                txtCells[i][j].setTextColor(getColor(R.color.purple_dark));

                if (grid[i][j] != 0) {
                    numberCounts[grid[i][j] - 1]--; // one less instance of this number left
                    // add position of this number
                    numberPositions[grid[i][j] - 1][8 - numberCounts[grid[i][j] - 1]] = 10*i + j + 1;

                    txtCells[i][j].setText(String.valueOf(grid[i][j]));

                    // set onClick
                    int finalI = i;
                    int finalJ = j;
                    txtCells[i][j].setOnClickListener(v -> {
                        if (marking) {
                            Toast.makeText(this, "Cannot use pencil on pre-filled cell", Toast.LENGTH_SHORT).show();
                        } else if (hinting) {
                            Toast.makeText(this, "Cannot use hint on pre-filled cell", Toast.LENGTH_SHORT).show();
                        } else if (erasing) {
                            Toast.makeText(this, "Cannot erase pre-filled cell", Toast.LENGTH_SHORT).show();
                        } else {
                            selectCell(finalI, finalJ); // highlight needed cells
                        }
                    });
                } else {
                    playable[i][j] = true;

                    // change to rounded corners
                    if (i < 3 && (j < 3 || j > 5) ||
                            i > 5 && (j < 3 || j > 5) ||
                            i > 2 && i < 6 && j > 2 && j < 6) {
                        txtCells[i][j].setBackground(getDrawable(R.drawable.cell_play_purple));
                    } else {
                        txtCells[i][j].setBackground(getDrawable(R.drawable.cell_play_white));
                    }

                    // set onClick
                    int finalI = i;
                    int finalJ = j;
                    txtCells[i][j].setOnClickListener(v -> {
                        if (marking) {
                            // TODO
                        } else if (hinting) {
                            // TODO
                        } else if (erasing) {
                            int n = sudoku.getGrid()[finalI][finalJ];
                            if (n == 0) {
                                Toast.makeText(this, "Nothing to erase", Toast.LENGTH_SHORT).show();
                            } else { // TODO: test
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
                                txtCells[finalI][finalJ].setText(""); // fix UI
                            }
                        } else {
                            selectCell(finalI, finalJ); // highlight needed cells
                        }
                    });
                }
            }
        }

        // set number buttons
        numbers[0] = findViewById(R.id.number1);
        numbers[1] = findViewById(R.id.number2);
        numbers[2] = findViewById(R.id.number3);
        numbers[3] = findViewById(R.id.number4);
        numbers[4] = findViewById(R.id.number5);
        numbers[5] = findViewById(R.id.number6);
        numbers[6] = findViewById(R.id.number7);
        numbers[7] = findViewById(R.id.number8);
        numbers[8] = findViewById(R.id.number9);
        for (int i = 0; i < 9; i++) {
            numbers[i].setText(styleBtnText(i + 1, numberCounts[i]));
            numbers[i].setOnClickListener(v -> {
                // TODO
            });
        }

        // set undo, erase, pencil, and hint buttons
        btnUndo = findViewById(R.id.undo);
        layoutUndo = findViewById(R.id.layoutUndo);
        btnUndo.setOnClickListener(v -> {
            if (moveHistory.size() == 0) {
                Toast.makeText(this, "Nothing to undo", Toast.LENGTH_SHORT).show();
            } else { // TODO: test
                Pair<Integer, Integer> move = moveHistory.pop();
                int n = sudoku.getGrid()[move.first][move.second];

                sudoku.removeValue(move.first, move.second); // fix grid

                int index = 8;
                do {
                    index--;
                } while(numberPositions[n - 1][index] != 0);
                numberPositions[n - 1][index] = 0; // fix numberPositions

                txtCells[move.first][move.second].setText(""); // fix UI
                selectCell(move.first, move.second);
            }
        });

        btnErase = findViewById(R.id.erase);
        layoutErase = findViewById(R.id.layoutErase);
        btnErase.setOnClickListener(v -> {
            if (cellSelected) {
                // TODO
            } else {
                resetCellBackground();

                // adjust button position
                ViewGroup.MarginLayoutParams paramsUndo = (ViewGroup.MarginLayoutParams) layoutUndo.getLayoutParams();
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layoutErase.getLayoutParams();
                if (erasing) {
                    params.topMargin = paramsUndo.topMargin; // now same as others
                } else {
                    params.topMargin = 40; // now above others

                    // 'un-click' the rest
                    if (marking) {
                        ViewGroup.MarginLayoutParams paramsPencil = (ViewGroup.MarginLayoutParams) layoutPencil.getLayoutParams();
                        paramsPencil.topMargin = paramsUndo.topMargin;
                        layoutPencil.setLayoutParams(paramsPencil);
                        marking = false;
                    } else if (hinting) {
                        ViewGroup.MarginLayoutParams paramsHint = (ViewGroup.MarginLayoutParams) layoutHint.getLayoutParams();
                        paramsHint.topMargin = paramsUndo.topMargin;
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
            resetCellBackground();

            // adjust button position
            ViewGroup.MarginLayoutParams paramsUndo = (ViewGroup.MarginLayoutParams) layoutUndo.getLayoutParams();
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layoutPencil.getLayoutParams();
            if (marking) {
                params.topMargin = paramsUndo.topMargin; // now same as others
            } else {
                params.topMargin = 40; // now above others

                // 'un-click' the rest
                if (erasing) {
                    ViewGroup.MarginLayoutParams paramsErase = (ViewGroup.MarginLayoutParams) layoutErase.getLayoutParams();
                    paramsErase.topMargin = paramsUndo.topMargin;
                    layoutErase.setLayoutParams(paramsErase);
                    erasing = false;
                } else if (hinting) {
                    ViewGroup.MarginLayoutParams paramsHint = (ViewGroup.MarginLayoutParams) layoutHint.getLayoutParams();
                    paramsHint.topMargin = paramsUndo.topMargin;
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
            if (cellSelected) {
                // TODO
            } else {
                resetCellBackground();

                // adjust button position
                ViewGroup.MarginLayoutParams paramsUndo = (ViewGroup.MarginLayoutParams) layoutUndo.getLayoutParams();
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layoutHint.getLayoutParams();
                if (hinting) {
                    params.topMargin = paramsUndo.topMargin; // now same as others
                } else {
                    params.topMargin = 40; // now above others

                    // 'un-click' the rest
                    if (marking) {
                        ViewGroup.MarginLayoutParams paramsPencil = (ViewGroup.MarginLayoutParams) layoutPencil.getLayoutParams();
                        paramsPencil.topMargin = paramsUndo.topMargin;
                        layoutPencil.setLayoutParams(paramsPencil);
                        marking = false;
                    } else if (erasing) {
                        ViewGroup.MarginLayoutParams paramsErase = (ViewGroup.MarginLayoutParams) layoutErase.getLayoutParams();
                        paramsErase.topMargin = paramsUndo.topMargin;
                        layoutErase.setLayoutParams(paramsErase);
                        erasing = false;
                    }
                }
                hinting = !hinting;
                layoutHint.setLayoutParams(params);
            }
        });
    }

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
        resetCellBackground();

        if (playable[row][col]) {
            // TODO
        } else {
            // highlight same number
            int n = sudoku.getGrid()[row][col] - 1;
            int index = 0;
            while (numberPositions[n][index] != 0) {
                int i = (numberPositions[n][index] - 1)/10;
                int j = (numberPositions[n][index] - 1)%10;
                txtCells[i][j].setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
                index++;
            }

            // find and highlight 'neighbours'
            // row and column
            for (int i = 0; i < 9; i++) {
                // same row
                if (playable[row][i]) {txtCells[row][i].setBackground(getDrawable(R.drawable.cell_play_highlight));}
                else {txtCells[row][i].setBackground(getDrawable(R.drawable.cell_set_highlight));}
                // same column
                if (playable[i][col]) {txtCells[i][col].setBackground(getDrawable(R.drawable.cell_play_highlight));}
                else {txtCells[i][col].setBackground(getDrawable(R.drawable.cell_set_highlight));}
            }
            // 3x3 box
            int startRow = row/3*3; // determine block's lowest row index
            int startCol = col/3*3; // determine block's lowest col index
            for (int i = startRow; i < startRow + 3; i++) {
                for (int j = startCol; j < startCol + 3; j++) {
                    if (playable[i][j]) {txtCells[i][j].setBackground(getDrawable(R.drawable.cell_play_highlight));}
                    else {txtCells[i][j].setBackground(getDrawable(R.drawable.cell_set_highlight));}
                }
            }

            // highlight the cell itself
            txtCells[row][col].setBackground(getDrawable(R.drawable.cell_set_chosen));
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void resetCellBackground() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                boolean purpleCondition = i < 3 && (j < 3 || j > 5) ||
                                          i > 5 && (j < 3 || j > 5) ||
                                          i > 2 && i < 6 && j > 2 && j < 6;
                if (playable[i][j]) {
                    if (purpleCondition) {txtCells[i][j].setBackground(getDrawable(R.drawable.cell_play_purple));}
                    else {txtCells[i][j].setBackground(getDrawable(R.drawable.cell_play_white));}
                }
                else {
                    if (purpleCondition) {txtCells[i][j].setBackground(getDrawable(R.drawable.cell_set_purple));}
                    else {txtCells[i][j].setBackground(getDrawable(R.drawable.cell_set_white));}
                }
                txtCells[i][j].setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void check() {
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
        // TODO: popup about the game

        Toast.makeText(this, "Game Over", Toast.LENGTH_SHORT).show();
    }

    public void setCells() {
        // block 1
        txtCells[0][0] = findViewById(R.id.cell11);
        txtCells[0][1] = findViewById(R.id.cell12);
        txtCells[0][2] = findViewById(R.id.cell13);
        txtCells[1][0] = findViewById(R.id.cell21);
        txtCells[1][1] = findViewById(R.id.cell22);
        txtCells[1][2] = findViewById(R.id.cell23);
        txtCells[2][0] = findViewById(R.id.cell31);
        txtCells[2][1] = findViewById(R.id.cell32);
        txtCells[2][2] = findViewById(R.id.cell33);
        // block 2
        txtCells[0][3] = findViewById(R.id.cell14);
        txtCells[0][4] = findViewById(R.id.cell15);
        txtCells[0][5] = findViewById(R.id.cell16);
        txtCells[1][3] = findViewById(R.id.cell24);
        txtCells[1][4] = findViewById(R.id.cell25);
        txtCells[1][5] = findViewById(R.id.cell26);
        txtCells[2][3] = findViewById(R.id.cell34);
        txtCells[2][4] = findViewById(R.id.cell35);
        txtCells[2][5] = findViewById(R.id.cell36);
        // block 3
        txtCells[0][6] = findViewById(R.id.cell17);
        txtCells[0][7] = findViewById(R.id.cell18);
        txtCells[0][8] = findViewById(R.id.cell19);
        txtCells[1][6] = findViewById(R.id.cell27);
        txtCells[1][7] = findViewById(R.id.cell28);
        txtCells[1][8] = findViewById(R.id.cell29);
        txtCells[2][6] = findViewById(R.id.cell37);
        txtCells[2][7] = findViewById(R.id.cell38);
        txtCells[2][8] = findViewById(R.id.cell39);
        // block 4
        txtCells[3][0] = findViewById(R.id.cell41);
        txtCells[3][1] = findViewById(R.id.cell42);
        txtCells[3][2] = findViewById(R.id.cell43);
        txtCells[4][0] = findViewById(R.id.cell51);
        txtCells[4][1] = findViewById(R.id.cell52);
        txtCells[4][2] = findViewById(R.id.cell53);
        txtCells[5][0] = findViewById(R.id.cell61);
        txtCells[5][1] = findViewById(R.id.cell62);
        txtCells[5][2] = findViewById(R.id.cell63);
        // block 5
        txtCells[3][3] = findViewById(R.id.cell44);
        txtCells[3][4] = findViewById(R.id.cell45);
        txtCells[3][5] = findViewById(R.id.cell46);
        txtCells[4][3] = findViewById(R.id.cell54);
        txtCells[4][4] = findViewById(R.id.cell55);
        txtCells[4][5] = findViewById(R.id.cell56);
        txtCells[5][3] = findViewById(R.id.cell64);
        txtCells[5][4] = findViewById(R.id.cell65);
        txtCells[5][5] = findViewById(R.id.cell66);
        // block 6
        txtCells[3][6] = findViewById(R.id.cell47);
        txtCells[3][7] = findViewById(R.id.cell48);
        txtCells[3][8] = findViewById(R.id.cell49);
        txtCells[4][6] = findViewById(R.id.cell57);
        txtCells[4][7] = findViewById(R.id.cell58);
        txtCells[4][8] = findViewById(R.id.cell59);
        txtCells[5][6] = findViewById(R.id.cell67);
        txtCells[5][7] = findViewById(R.id.cell68);
        txtCells[5][8] = findViewById(R.id.cell69);
        // block 7
        txtCells[6][0] = findViewById(R.id.cell71);
        txtCells[6][1] = findViewById(R.id.cell72);
        txtCells[6][2] = findViewById(R.id.cell73);
        txtCells[7][0] = findViewById(R.id.cell81);
        txtCells[7][1] = findViewById(R.id.cell82);
        txtCells[7][2] = findViewById(R.id.cell83);
        txtCells[8][0] = findViewById(R.id.cell91);
        txtCells[8][1] = findViewById(R.id.cell92);
        txtCells[8][2] = findViewById(R.id.cell93);
        // block 8
        txtCells[6][3] = findViewById(R.id.cell74);
        txtCells[6][4] = findViewById(R.id.cell75);
        txtCells[6][5] = findViewById(R.id.cell76);
        txtCells[7][3] = findViewById(R.id.cell84);
        txtCells[7][4] = findViewById(R.id.cell85);
        txtCells[7][5] = findViewById(R.id.cell86);
        txtCells[8][3] = findViewById(R.id.cell94);
        txtCells[8][4] = findViewById(R.id.cell95);
        txtCells[8][5] = findViewById(R.id.cell96);
        // block 9
        txtCells[6][6] = findViewById(R.id.cell77);
        txtCells[6][7] = findViewById(R.id.cell78);
        txtCells[6][8] = findViewById(R.id.cell79);
        txtCells[7][6] = findViewById(R.id.cell87);
        txtCells[7][7] = findViewById(R.id.cell88);
        txtCells[7][8] = findViewById(R.id.cell89);
        txtCells[8][6] = findViewById(R.id.cell97);
        txtCells[8][7] = findViewById(R.id.cell98);
        txtCells[8][8] = findViewById(R.id.cell99);
    }
}