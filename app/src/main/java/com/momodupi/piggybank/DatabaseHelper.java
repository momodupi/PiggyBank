package com.momodupi.piggybank;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static String BOOKNAME = "book";

    public static String[] COLUMNSNAME = {"book_type", "book_time", "book_amount", "book_reply"};

    private static Integer Version = 1;

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + BOOKNAME + " ("
                + COLUMNSNAME[0] + " varchar(32), "
                + COLUMNSNAME[1] + " varchar(32), "
                + COLUMNSNAME[2] + " real, "
                + COLUMNSNAME[3] + " varchar(32))";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.delete("books", null, null);
    }

}
