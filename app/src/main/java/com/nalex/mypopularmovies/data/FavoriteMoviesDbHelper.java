package com.nalex.mypopularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.nalex.mypopularmovies.data.FavoriteMoviesContract.MovieEntry;

public class FavoriteMoviesDbHelper extends SQLiteOpenHelper {

    //DB name
    private static final String DATABASE_NAME = "favoriteMoviesDb.db";

    // Version number used in case the DB schema is updated
    private static final int VERSION = 1;

    //constructor
    public FavoriteMoviesDbHelper (Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    /* Types follow the model (see Movie class)
     * Movie id from the TMDB is used as a primary key
     * so as to prevent duplicates in favorites.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry.COLUMN_MOVIE_ID       + " INTEGER PRIMARY KEY, " +
                MovieEntry.COLUMN_TITLE          + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSTER         + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE   + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_VOTE_AVERAGE   + " REAL NOT NULL, " +
                MovieEntry.COLUMN_VOTE_COUNT     + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW    + " TEXT NOT NULL);";

        db.execSQL(CREATE_TABLE);
    }

    //When version number of the DB changes, this method drops the old table and calls
    //onCreate to create a new one.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);
    }

}
