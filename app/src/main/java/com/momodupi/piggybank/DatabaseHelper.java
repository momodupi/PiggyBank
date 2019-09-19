package com.momodupi.piggybank;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    static String BOOKNAME = "book";

    //private static String[] COLUMNSNAME = {"book_type", "book_time", "book_amount", "book_reply"};

    private Integer Version = 1;

    DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + BOOKNAME + " ("
                + "book_type" + " varchar(32), "
                + "book_time" + " varchar(32), "
                + "book_amount" + " real, "
                + "book_reply" + " varchar(32))";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.delete("books", null, null);
        if (!this.Version.equals(oldVersion)) {
            this.Version = newVersion;
        }
    }

}
