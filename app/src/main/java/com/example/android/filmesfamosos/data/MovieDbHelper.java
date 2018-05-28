package com.example.android.filmesfamosos.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.filmesfamosos.data.MovieContract.FavEntry;

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "moviesDb.db";

    private static final int VERSION = 2;

    MovieDbHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE " + FavEntry.TABLE_NAME + " (" +
                FavEntry._ID                    + " INTEGER PRIMARY KEY, " +
                FavEntry.COLUMN_MOVIE_ID        + " INTEGER NOT NULL, " +
                FavEntry.COLUMN_TITLE           + " TEXT NOT NULL, " +
                FavEntry.COLUMN_OVERVIEW        + " TEXT NOT NULL, " +
                FavEntry.COLUMN_RELEASE_DATE    + " TEXT NOT NULL, " +
                FavEntry.COLUMN_VOTE_AVERAGE    + " DOUBLE NOT NULL, " +
                FavEntry.COLUMN_POSTER_PATH     + " TEXT NOT NULL);";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavEntry.TABLE_NAME);
        onCreate(db);

    }
}
