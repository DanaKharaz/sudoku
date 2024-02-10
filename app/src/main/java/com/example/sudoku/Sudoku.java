package com.example.sudoku;

import android.util.Pair;

import java.util.Objects;
import java.util.Random;

public class Sudoku {
    // DocMe : general

    // vars
    private int[][] grid;

    // constructors
    public Sudoku(int known) {
        // DocMe

        grid = new int[9][9];
        boolean[][] taken = new boolean[9][9]; // all false by default
        int i = 0;
        while (i < known) {
            int row;
            int col;
            do {
                row = new Random().nextInt(9);
                col = new Random().nextInt(9);
            } while (taken[row][col]); // until available cell found

            int val;
            do {
                val = new Random().nextInt(9) + 1;
            } while (isIllegal(row, col, val)); // until legal value is found

            grid[row][col] = val;
            i++;

            if (i == known && Objects.isNull(solve())) {
                // restart
                i = 0;
                grid = new int[9][9];
                taken = new boolean[9][9]; // all false by default
            }
        }
    }
    public Sudoku(int[][] grid) {
        // DocMe

        this.grid = grid;
    }

    // methods
    public int[][] getGrid() {return grid;}

    public void putValue(int row, int col, int val) {grid[row][col] = val;}
    public void removeValue(int row, int col) {grid[row][col] = 0;}
    public boolean isIllegal(int row, int col, int val) {
        // DocMe

        boolean res = true; // legal by default

        // check row and column
        for (int i = 0; i < 9; i++) {
            if (grid[i][col] == val || grid[row][i] == val) {
                res = false; // same val in same row or col - illegal move
                break;
            }
        }

        // check 3x3 box
        int startRow = row/3*3; // determine block's lowest row index
        int startCol = col/3*3; // determine block's lowest col index
        for (int i = startRow; i < startRow + 3; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                if (grid[i][j] == val) {
                    res = false; // same val in same block - illegal move
                    break;
                }
            }
        }

        return !res;
    }
    private Pair<Integer, Integer> findAvailable() {
        // DocMe

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                // empty cell found
                if (grid[i][j] == 0) {return new Pair<>(i, j);}
            }
        }
        return null;
    }
    public int[][] solve() {
        // DocMe

        Pair<Integer, Integer> available = findAvailable();

        // solution found - all cells filled
        if (Objects.isNull(available)) {return null;}

        assert available != null; // not necessary
        int row = available.first;
        int col = available.second;
        for (int val = 1; val < 10; val++) {
            if (isIllegal(row, col, val)) {continue;}
            putValue(row, col, val);
            int[][] provRes = solve();
            if (!Objects.isNull(provRes)) {removeValue(row, col);} // not solved
            else {return null;} // solved
        }
        return grid;
    }

    public Sudoku copy() {return new Sudoku(grid);}
}
