package com.example.android.newsstand.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.newsstand.data.NewsContract.NewsEntry;

public class NewsDbOpener extends SQLiteOpenHelper{

    private static final String LOG_TAG = NewsDbOpener.class.getSimpleName();

    private static final String DATABASE_NAME = "newsTable.db";
    private static final int DATABASE_VERSION = 29;


    public NewsDbOpener(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_QUERY =  "CREATE TABLE " + NewsEntry.TABLE_NAME + " ("
                + NewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NewsEntry.COLUMN_NEWS_AUTHOR + " TEXT, "
                + NewsEntry.COLUMN_NEWS_TITLE + " TEXT, "
                + NewsEntry.COLUMN_NEWS_DESCRIPTION + " TEXT, "
                + NewsEntry.COLUMN_NEWS_URL + " TEXT, "
                + NewsEntry.COLUMN_NEWS_URL_IMAGE + " TEXT, "
                + NewsEntry.COLUMN_NEWS_IMAGE + " BLOB, "
                + NewsEntry.COLUMN_NEWS_PUBLISHED + " TEXT);";

        db.execSQL(SQL_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + NewsEntry.TABLE_NAME);
        onCreate(db);
    }
}
