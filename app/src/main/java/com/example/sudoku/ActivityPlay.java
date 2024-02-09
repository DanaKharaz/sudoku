package com.example.sudoku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityPlay extends AppCompatActivity {

    Sudoku sudoku;
    boolean[][] playable = new boolean[9][9];

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
    TextView txtErase;
    TextView btnPencil;
    View layoutPencil;
    TextView txtPencil;
    TextView btnHint;
    View layoutHint;
    TextView txtHint;

    TextView[] numbers = new Button[9];
    int[] numberCounts = new int[9];
    TextView[][] txtCells = new TextView[9][9];

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        // create new Sudoku object if making a puzzle
        // retrieve needed Sudoku object if solving a puzzle

        // set hearts
        hearts[0] = findViewById(R.id.heart1);
        hearts[2] = findViewById(R.id.heart2);
        hearts[3] = findViewById(R.id.heart3);

        // set the grid
        setCells();
        int[][] grid = sudoku.getGrid();
        for (int i = 0; i < 9; i++) {
            numberCounts[i] = 9; // default value
            for (int j = 0; j < 9; j++) {
                txtCells[i][j].setClickable(true);

                if (grid[i][j] != 0) {
                    numberCounts[grid[i][j] - 1]--; // one less instance of this number left

                    txtCells[i][j].setText(String.valueOf(grid[i][j]));
                } else {
                    playable[i][j] = true;

                    // change to rounded corners
                    if (i <= 3 && (j <=3 || j >= 7) ||
                            i >= 7 && (j <=3 || j >= 7) ||
                            i >= 4 && i <= 6 && j >= 4 && j <= 6) {
                        txtCells[i][j].setBackground(getDrawable(R.drawable.cell_play_purple));
                    } else {
                        txtCells[i][j].setBackground(getDrawable(R.drawable.cell_play_white));
                    }
                }
            }
        }

        // set undo, erase, pencil, and hint buttons
        btnUndo = findViewById(R.id.undo);
        layoutUndo = findViewById(R.id.layoutUndo);
        btnUndo.setOnClickListener(v -> {
            /////////////
        });

        btnErase = findViewById(R.id.erase);
        layoutErase = findViewById(R.id.layoutErase);
        txtErase = findViewById(R.id.txtErase);
        btnErase.setOnClickListener(v -> {
            if (cellSelected) {
                ///////
            } else {
                // adjust button position
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 40, 0, 0);
                if (erasing) {
                    params.setMargins(0, 40, 0, 0); // now same as others
                    txtErase.setTypeface(txtErase.getTypeface(), Typeface.NORMAL);
                } else {
                    params.setMargins(0, 10, 0, 0); // now same as others
                    txtErase.setTypeface(txtErase.getTypeface(), Typeface.BOLD);
                }
                erasing = !erasing;
            }
        });

        btnPencil = findViewById(R.id.pencil);
        layoutPencil = findViewById(R.id.layoutPencil);
        txtPencil = findViewById(R.id.txtPencil);
        btnPencil.setOnClickListener(v -> {
            // adjust button position
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 40, 0, 0);
            if (marking) {
                params.setMargins(0, 40, 0, 0); // now same as others
                txtPencil.setTypeface(txtPencil.getTypeface(), Typeface.NORMAL);
            } else {
                params.setMargins(0, 10, 0, 0); // now above as others
                txtPencil.setTypeface(txtPencil.getTypeface(), Typeface.BOLD);
            }
            marking = !marking;

            /////////////
        });

        btnHint = findViewById(R.id.hint);
        layoutHint = findViewById(R.id.layoutHint);
        txtHint = findViewById(R.id.txtHint);
        btnHint.setOnClickListener(v -> {
            if (cellSelected) {
                ///////
            } else {
                // adjust button position
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 40, 0, 0);
                if (hinting) {
                    params.setMargins(0, 40, 0, 0); // now same as others
                    txtHint.setTypeface(txtHint.getTypeface(), Typeface.NORMAL);
                } else {
                    params.setMargins(0, 10, 0, 0); // now same as others
                    txtHint.setTypeface(txtHint.getTypeface(), Typeface.BOLD);
                }
                hinting = !hinting;
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void check() {
        //////

        // adjust hearts
        for (int i = 2; i >= 3 - mistakes; i--) {
            hearts[i].setBackground(getDrawable(R.drawable.ic_heart_dead));
        }
        if (mistakes == 3) {
            gameOver();
        }
    }

    public void gameOver() {
        ////////
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
        txtCells[2][2] = findViewById(R.id.cell32);
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
        txtCells[8][2] = findViewById(R.id.cell92);
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