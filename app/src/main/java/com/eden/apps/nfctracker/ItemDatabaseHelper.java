package com.eden.apps.nfctracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ItemDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "items.db";
    private static final int DB_VERSION = 1;

    public ItemDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS items (" +
                "id INTEGER PRIMARY KEY," +
                "name TEXT," +
                "tag TEXT," +
                "category TEXT" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS items");
        onCreate(db);
    }
}