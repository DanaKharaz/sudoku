package com.example.sudoku;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Objects;

public class DatabaseSQL extends SQLiteOpenHelper {
    private final Context context;
    private static final String DATABASE_NAME = "SudokuDatabase.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "puzzle_options";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NUM_1 = "one_position";
    private static final String COLUMN_MARK_1 = "one_marked";
    private static final String COLUMN_NUM_2 = "two_position";
    private static final String COLUMN_MARK_2 = "two_marked";
    private static final String COLUMN_NUM_3 = "three_position";
    private static final String COLUMN_MARK_3 = "three_marked";
    private static final String COLUMN_NUM_4 = "four_position";
    private static final String COLUMN_MARK_4 = "four_marked";
    private static final String COLUMN_NUM_5 = "five_position";
    private static final String COLUMN_MARK_5 = "five_marked";
    private static final String COLUMN_NUM_6 = "six_position";
    private static final String COLUMN_MARK_6 = "six_marked";
    private static final String COLUMN_NUM_7 = "seven_position";
    private static final String COLUMN_MARK_7 = "seven_marked";
    private static final String COLUMN_NUM_8 = "eight_position";
    private static final String COLUMN_MARK_8 = "eight_marked";
    private static final String COLUMN_NUM_9 = "nine_position";
    private static final String COLUMN_MARK_9 = "nine_marked";
    private static final String COLUMN_PLAYABLE = "playable_position";

    public DatabaseSQL(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NUM_1 + " TEXT, " +
                COLUMN_NUM_2 + " TEXT, " +
                COLUMN_NUM_3 + " TEXT, " +
                COLUMN_NUM_4 + " TEXT, " +
                COLUMN_NUM_5 + " TEXT, " +
                COLUMN_NUM_6 + " TEXT, " +
                COLUMN_NUM_7 + " TEXT, " +
                COLUMN_NUM_8 + " TEXT, " +
                COLUMN_NUM_9 + " TEXT, " +
                COLUMN_MARK_1 + " TEXT, " +
                COLUMN_MARK_2 + " TEXT, " +
                COLUMN_MARK_3 + " TEXT, " +
                COLUMN_MARK_4 + " TEXT, " +
                COLUMN_MARK_5 + " TEXT, " +
                COLUMN_MARK_6 + " TEXT, " +
                COLUMN_MARK_7 + " TEXT, " +
                COLUMN_MARK_8 + " TEXT, " +
                COLUMN_MARK_9 + " TEXT, " +
                COLUMN_PLAYABLE + " TEXT);";
        db.execSQL(query);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int a, int b) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public Cursor readAllData(){
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (!(Objects.isNull(db))) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public void addElement(int[][] grid){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        ArrayList<String> list1 = new ArrayList<>();
        ArrayList<String> list2 = new ArrayList<>();
        ArrayList<String> list3 = new ArrayList<>();
        ArrayList<String> list4 = new ArrayList<>();
        ArrayList<String> list5 = new ArrayList<>();
        ArrayList<String> list6 = new ArrayList<>();
        ArrayList<String> list7 = new ArrayList<>();
        ArrayList<String> list8 = new ArrayList<>();
        ArrayList<String> list9 = new ArrayList<>();
        ArrayList<String> plays = new ArrayList<>();

        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                if (grid[x][y] == 1) {
                    list1.add(String.valueOf(10*x + y));
                } else if (grid[x][y] == 2) {
                    list2.add(String.valueOf(10*x + y));
                } else if (grid[x][y] == 3) {
                    list3.add(String.valueOf(10*x + y));
                } else if (grid[x][y] == 4) {
                    list4.add(String.valueOf(10*x + y));
                } else if (grid[x][y] == 5) {
                    list5.add(String.valueOf(10*x + y));
                } else if (grid[x][y] == 6) {
                    list6.add(String.valueOf(10*x + y));
                } else if (grid[x][y] == 7) {
                    list7.add(String.valueOf(10*x + y));
                } else if (grid[x][y] == 8) {
                    list8.add(String.valueOf(10*x + y));
                } else if (grid[x][y] == 9) {
                    list9.add(String.valueOf(10*x + y));
                } else {
                    plays.add(String.valueOf(10*x + y));
                }
            }
        }

        // values are positions (10*row+col) separated by ','
        cv.put(COLUMN_NUM_1, String.join(",", list1));
        cv.put(COLUMN_NUM_2, String.join(",", list2));
        cv.put(COLUMN_NUM_3, String.join(",", list3));
        cv.put(COLUMN_NUM_4, String.join(",", list4));
        cv.put(COLUMN_NUM_5, String.join(",", list5));
        cv.put(COLUMN_NUM_6, String.join(",", list6));
        cv.put(COLUMN_NUM_7, String.join(",", list7));
        cv.put(COLUMN_NUM_8, String.join(",", list8));
        cv.put(COLUMN_NUM_9, String.join(",", list9));

        cv.put(COLUMN_PLAYABLE, String.join(",", plays));

        // no marks yet
        cv.put(COLUMN_MARK_1, "");
        cv.put(COLUMN_MARK_2, "");
        cv.put(COLUMN_MARK_3, "");
        cv.put(COLUMN_MARK_4, "");
        cv.put(COLUMN_MARK_5, "");
        cv.put(COLUMN_MARK_6, "");
        cv.put(COLUMN_MARK_7, "");
        cv.put(COLUMN_MARK_8, "");
        cv.put(COLUMN_MARK_9, "");

        long result = db.insert(TABLE_NAME,null, cv);
        if(result == -1){
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Added Successfully!", Toast.LENGTH_SHORT).show();
        }
    }
}
