package com.example.sudoku;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import kotlin.Triple;

public class ActivityChoosePuzzle extends AppCompatActivity {

    // sort
    TextView btnSort;
    LinearLayout menuSort;
    int sortMethod = 0; // 0 - name asc, 1 - name desc, 2 - done asc, 3 - done desc, 4 - unknown asc, 5 - unknown desc
    TextView[] sorts = new TextView[6];

    TextView btnFilter;
    LinearLayout menuFilter;
    CheckBox completed;
    CheckBox started;
    CheckBox unsolved;
    CheckBox unknown1to10;
    CheckBox unknown11to30;
    CheckBox unknown31to50;
    CheckBox unknown51to81;
    CheckBox done1to25;
    CheckBox done26to50;
    CheckBox done51to75;
    CheckBox done76to99;
    CheckBox selfMade;
    CheckBox generated;

    ArrayList<int[][]> grids = new ArrayList<>();
    ArrayList<boolean[][][]> marks = new ArrayList<>();
    ArrayList<boolean[][]> playables = new ArrayList<>();
    ArrayList<Triple<Integer, Integer, Integer>> doneUnknownsHints = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_puzzle);

        btnSort = findViewById(R.id.btnSort);
        menuSort = findViewById(R.id.menuSort);
        sorts[0] = findViewById(R.id.sortNameAsc);
        sorts[1] = findViewById(R.id.sortNameDesc);
        sorts[2] = findViewById(R.id.sortDoneAsc);
        sorts[3] = findViewById(R.id.sortDoneDesc);
        sorts[4] = findViewById(R.id.sortUnknownAsc);
        sorts[5] = findViewById(R.id.sortUnknownDesc);
        btnSort.setOnClickListener(v -> {
            if (menuSort.getVisibility() == View.GONE) { // show
                menuSort.setVisibility(View.VISIBLE);
            } else {menuSort.setVisibility(View.GONE);} // hide
        });
        for (int i = 0; i < 6; i++) {
            int finalI = i;
            sorts[i].setOnClickListener(v -> sortMethod = finalI);
        }

        btnFilter = findViewById(R.id.btnFilter);
        menuFilter = findViewById(R.id.menuFilter);
        completed = findViewById(R.id.chkCompleted);
        started = findViewById(R.id.chkStarted);
        unsolved = findViewById(R.id.chkUnsolved);
        unknown1to10 = findViewById(R.id.chkUnknown1to10);
        unknown11to30 = findViewById(R.id.chkUnknown11to30);
        unknown31to50 = findViewById(R.id.chkUnknown31to50);
        unknown51to81 = findViewById(R.id.chkUnknown51to81);
        done1to25 = findViewById(R.id.chkDone1to25);
        done26to50 = findViewById(R.id.chkDone26to50);
        done51to75 = findViewById(R.id.chkDone51to75);
        done76to99 = findViewById(R.id.chkDone76to99);
        selfMade = findViewById(R.id.chkSelfMade);
        generated = findViewById(R.id.chkGenerated);
        btnFilter.setOnClickListener(v -> {
            if (menuFilter.getVisibility() == View.GONE) { // show
                menuFilter.setVisibility(View.VISIBLE);
            } else {menuFilter.setVisibility(View.GONE);} // hide
        });


        // TODO add names, hints
        DatabaseSQL db = new DatabaseSQL(this);
        Cursor cursor = db.readAllData();
        if (!Objects.isNull(cursor)) {
            while (cursor.moveToNext()) {
                int[][] grid = new int[9][9];
                for (String pos : cursor.getString(1).split(",")) {grid[Integer.parseInt(pos)/10][Integer.parseInt(pos)%10] = 1;}
                for (String pos : cursor.getString(2).split(",")) {grid[Integer.parseInt(pos)/10][Integer.parseInt(pos)%10] = 2;}
                for (String pos : cursor.getString(3).split(",")) {grid[Integer.parseInt(pos)/10][Integer.parseInt(pos)%10] = 3;}
                for (String pos : cursor.getString(4).split(",")) {grid[Integer.parseInt(pos)/10][Integer.parseInt(pos)%10] = 4;}
                for (String pos : cursor.getString(5).split(",")) {grid[Integer.parseInt(pos)/10][Integer.parseInt(pos)%10] = 5;}
                for (String pos : cursor.getString(6).split(",")) {grid[Integer.parseInt(pos)/10][Integer.parseInt(pos)%10] = 6;}
                for (String pos : cursor.getString(7).split(",")) {grid[Integer.parseInt(pos)/10][Integer.parseInt(pos)%10] = 7;}
                for (String pos : cursor.getString(8).split(",")) {grid[Integer.parseInt(pos)/10][Integer.parseInt(pos)%10] = 8;}
                for (String pos : cursor.getString(9).split(",")) {grid[Integer.parseInt(pos)/10][Integer.parseInt(pos)%10] = 9;}

                boolean[][][] mark = new boolean[9][9][9];
                for (String pos : cursor.getString(10).split(",")) {mark[Integer.parseInt(pos)/10][Integer.parseInt(pos)%10][0] = true;}
                for (String pos : cursor.getString(11).split(",")) {mark[Integer.parseInt(pos)/10][Integer.parseInt(pos)%10][1] = true;}
                for (String pos : cursor.getString(12).split(",")) {mark[Integer.parseInt(pos)/10][Integer.parseInt(pos)%10][2] = true;}
                for (String pos : cursor.getString(13).split(",")) {mark[Integer.parseInt(pos)/10][Integer.parseInt(pos)%10][3] = true;}
                for (String pos : cursor.getString(14).split(",")) {mark[Integer.parseInt(pos)/10][Integer.parseInt(pos)%10][4] = true;}
                for (String pos : cursor.getString(15).split(",")) {mark[Integer.parseInt(pos)/10][Integer.parseInt(pos)%10][5] = true;}
                for (String pos : cursor.getString(16).split(",")) {mark[Integer.parseInt(pos)/10][Integer.parseInt(pos)%10][6] = true;}
                for (String pos : cursor.getString(17).split(",")) {mark[Integer.parseInt(pos)/10][Integer.parseInt(pos)%10][7] = true;}
                for (String pos : cursor.getString(18).split(",")) {mark[Integer.parseInt(pos)/10][Integer.parseInt(pos)%10][8] = true;}

                boolean[][] playable = new boolean[9][9];
                for (String pos : cursor.getString(19).split(",")) {playable[Integer.parseInt(pos)/10][Integer.parseInt(pos)%10] = true;}

                grids.add(grid);
                marks.add(mark);
                playables.add(playable);

                // calculate done, unknowns
                int countFilled = 0;
                int countPlayable = 0;
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (playable[i][j]) {
                            countPlayable++;
                            if (grid[i][j] != 0) {countFilled++;}
                        }
                    }
                }
                doneUnknownsHints.add(new Triple<>(100*countFilled/countPlayable, countPlayable, 3)); // FixMe default 3 hints
            }
        }
        else {
            Toast.makeText(this, "nothing :(", Toast.LENGTH_SHORT).show();
        }

        // TODO : display the sudokus in a recyclerview (unknowns, mark count, num count)
    }
}