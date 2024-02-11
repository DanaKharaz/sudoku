package com.example.sudoku;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

public class ActivityChoosePuzzle extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_puzzle);

        DatabaseSQL db = new DatabaseSQL(this);
        ArrayList<int[][]> grids = new ArrayList<>();
        ArrayList<boolean[][][]> marks = new ArrayList<>();
        ArrayList<boolean[][]> playables = new ArrayList<>();
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
            }
        }
        else {
            Toast.makeText(this, "nothing :(", Toast.LENGTH_SHORT).show();
        }

        // TODO : display the sudokus in a recyclerview (unknowns, mark count, num count)
    }
}