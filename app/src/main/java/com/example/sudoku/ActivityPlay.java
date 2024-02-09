package com.example.sudoku;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityPlay extends AppCompatActivity {

    Sudoku sudoku;

    int mistakes = 0;
    TextView[] hearts = new TextView[3];

    boolean marking = false; // true when pencil is clicked
    boolean[][][] marks = new boolean[9][9][9]; // x - row, y - col, z - number

    Button btnUndo;
    Button btnErase;
    Button btnPencil;
    Button btnHint;
    Button[] numbers = new Button[9];
    TextView[][] cells = new TextView[9][9];

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
            for (int j = 0; j < 9; j++) {
                if (grid[i][j] != 0) {
                    cells[i][j].setText(String.valueOf(grid[i][j]));
                }
            }
        }


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
        cells[0][0] = findViewById(R.id.cell11);
        cells[0][1] = findViewById(R.id.cell12);
        cells[0][2] = findViewById(R.id.cell13);
        cells[1][0] = findViewById(R.id.cell21);
        cells[1][1] = findViewById(R.id.cell22);
        cells[1][2] = findViewById(R.id.cell23);
        cells[2][0] = findViewById(R.id.cell31);
        cells[2][1] = findViewById(R.id.cell32);
        cells[2][2] = findViewById(R.id.cell32);
        // block 2
        cells[0][3] = findViewById(R.id.cell14);
        cells[0][4] = findViewById(R.id.cell15);
        cells[0][5] = findViewById(R.id.cell16);
        cells[1][3] = findViewById(R.id.cell24);
        cells[1][4] = findViewById(R.id.cell25);
        cells[1][5] = findViewById(R.id.cell26);
        cells[2][3] = findViewById(R.id.cell34);
        cells[2][4] = findViewById(R.id.cell35);
        cells[2][5] = findViewById(R.id.cell36);
        // block 3
        cells[0][6] = findViewById(R.id.cell17);
        cells[0][7] = findViewById(R.id.cell18);
        cells[0][8] = findViewById(R.id.cell19);
        cells[1][6] = findViewById(R.id.cell27);
        cells[1][7] = findViewById(R.id.cell28);
        cells[1][8] = findViewById(R.id.cell29);
        cells[2][6] = findViewById(R.id.cell37);
        cells[2][7] = findViewById(R.id.cell38);
        cells[2][8] = findViewById(R.id.cell39);
        // block 4
        cells[3][0] = findViewById(R.id.cell41);
        cells[3][1] = findViewById(R.id.cell42);
        cells[3][2] = findViewById(R.id.cell43);
        cells[4][0] = findViewById(R.id.cell51);
        cells[4][1] = findViewById(R.id.cell52);
        cells[4][2] = findViewById(R.id.cell53);
        cells[5][0] = findViewById(R.id.cell61);
        cells[5][1] = findViewById(R.id.cell62);
        cells[5][2] = findViewById(R.id.cell62);
        // block 5
        cells[3][3] = findViewById(R.id.cell44);
        cells[3][4] = findViewById(R.id.cell45);
        cells[3][5] = findViewById(R.id.cell46);
        cells[4][3] = findViewById(R.id.cell54);
        cells[4][4] = findViewById(R.id.cell55);
        cells[4][5] = findViewById(R.id.cell56);
        cells[5][3] = findViewById(R.id.cell64);
        cells[5][4] = findViewById(R.id.cell65);
        cells[5][5] = findViewById(R.id.cell66);
        // block 6
        cells[3][6] = findViewById(R.id.cell47);
        cells[3][7] = findViewById(R.id.cell48);
        cells[3][8] = findViewById(R.id.cell49);
        cells[4][6] = findViewById(R.id.cell57);
        cells[4][7] = findViewById(R.id.cell58);
        cells[4][8] = findViewById(R.id.cell59);
        cells[5][6] = findViewById(R.id.cell67);
        cells[5][7] = findViewById(R.id.cell68);
        cells[5][8] = findViewById(R.id.cell69);
        // block 7
        cells[6][0] = findViewById(R.id.cell71);
        cells[6][1] = findViewById(R.id.cell72);
        cells[6][2] = findViewById(R.id.cell73);
        cells[7][0] = findViewById(R.id.cell81);
        cells[7][1] = findViewById(R.id.cell82);
        cells[7][2] = findViewById(R.id.cell83);
        cells[8][0] = findViewById(R.id.cell91);
        cells[8][1] = findViewById(R.id.cell92);
        cells[8][2] = findViewById(R.id.cell92);
        // block 8
        cells[6][3] = findViewById(R.id.cell74);
        cells[6][4] = findViewById(R.id.cell75);
        cells[6][5] = findViewById(R.id.cell76);
        cells[7][3] = findViewById(R.id.cell84);
        cells[7][4] = findViewById(R.id.cell85);
        cells[7][5] = findViewById(R.id.cell86);
        cells[8][3] = findViewById(R.id.cell94);
        cells[8][4] = findViewById(R.id.cell95);
        cells[8][5] = findViewById(R.id.cell96);
        // block 9
        cells[6][6] = findViewById(R.id.cell77);
        cells[6][7] = findViewById(R.id.cell78);
        cells[6][8] = findViewById(R.id.cell79);
        cells[7][6] = findViewById(R.id.cell87);
        cells[7][7] = findViewById(R.id.cell88);
        cells[7][8] = findViewById(R.id.cell89);
        cells[8][6] = findViewById(R.id.cell97);
        cells[8][7] = findViewById(R.id.cell98);
        cells[8][8] = findViewById(R.id.cell99);
    }
}