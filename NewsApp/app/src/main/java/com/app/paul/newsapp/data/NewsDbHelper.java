package com.app.paul.newsapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Class helper for database statements needed on DB creation
 */
public class NewsDbHelper extends SQLiteOpenHelper {

    private static final int DATA_BASE_VERSION = 1;
    private static final String DATABASE_NAME = "News.db";

    NewsDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATA_BASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_NEW);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    //Method for creating database
    private static final String SQL_CREATE_NEW =  "CREATE TABLE " + NewsContract.NewsEntry.TABLE_NAME + " (" +
            NewsContract.NewsEntry._ID + " TEXT PRIMARY KEY," +
            NewsContract.NewsEntry.COLUMN_CATEGORY + " INTEGER," +
            NewsContract.NewsEntry.COLUMN_NEWS_NAME + " TEXT," +
            NewsContract.NewsEntry.COLUMN_NEWS_BODY + " TEXT," +
            NewsContract.NewsEntry.COLUMN_NEWS_SECTION + " TEXT," +
            NewsContract.NewsEntry.COLUMN_NEWS_IMG + " TEXT," +
            NewsContract.NewsEntry.COLUMN_IS_READ_LATER + " INTEGER)";
}
