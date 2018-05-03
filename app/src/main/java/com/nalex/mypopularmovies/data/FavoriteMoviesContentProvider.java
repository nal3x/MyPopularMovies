package com.nalex.mypopularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class FavoriteMoviesContentProvider extends ContentProvider {

    private String TAG = FavoriteMoviesContentProvider.class.getSimpleName();

    public static final int MOVIES = 100;
    public static final int MOVIE_WITH_ID = 101;

    private FavoriteMoviesDbHelper mFavoriteMoviesDbHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {

        // Initialize a UriMatcher with no matches by passing in NO_MATCH to the constructor
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(FavoriteMoviesContract.AUTHORITY, FavoriteMoviesContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(FavoriteMoviesContract.AUTHORITY, FavoriteMoviesContract.PATH_MOVIES + "/#", MOVIE_WITH_ID);

        return uriMatcher;
    }


    @Override
    public boolean onCreate() {
        Context context = getContext();
        mFavoriteMoviesDbHelper = new FavoriteMoviesDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase db = mFavoriteMoviesDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor returnedCursor;

        switch (match) {
            case MOVIES:
                returnedCursor = db.query(FavoriteMoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case MOVIE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = FavoriteMoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = ? "; //movieId column
                String[] mSelectionArgs = new String[]{id}; //a single ID as selection argument

                returnedCursor = db.query(FavoriteMoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        null);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set a notification URI on the Cursor and return that Cursor
        returnedCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return returnedCursor;
    }


    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mFavoriteMoviesDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch(match) {
            case MOVIES:
                long id = db.insert(FavoriteMoviesContract.MovieEntry.TABLE_NAME,
                        null,
                        values);
                Log.d(TAG, "ID RETURNED: " + id);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(FavoriteMoviesContract.MovieEntry.CONTENT_URI, id);
                } else {
                    return null; //failed to insert movie in favorites
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = mFavoriteMoviesDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int numberOfRowsDeleted;

        switch (match) {
            case MOVIES:
                //To remove all rows and get a count pass "1" as the whereClause.
                numberOfRowsDeleted = db.delete(FavoriteMoviesContract.MovieEntry.TABLE_NAME,
                        "1",
                        null);
                break;
            case MOVIE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = FavoriteMoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ";
                String[] mSelectionArgs = new String[]{id};

                numberOfRowsDeleted = db.delete(FavoriteMoviesContract.MovieEntry.TABLE_NAME,
                        mSelection,
                        mSelectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (numberOfRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numberOfRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
