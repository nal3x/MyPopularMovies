package com.nalex.mypopularmovies.data;


import android.net.Uri;
import android.provider.BaseColumns;

public class FavoriteMoviesContract {

    // Authority usually matches the package name
    public static final String AUTHORITY = "com.nalex.mypopularmovies";

    // Base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Path for the "movies" directory
    public static final String PATH_MOVIES = "movies";


    public static final class MovieEntry implements BaseColumns {

        //MovieEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        //Db table name
        public static final String TABLE_NAME = "favorite_movies";

       //DB columns
        public static final String COLUMN_MOVIE_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_VOTE_AVERAGE = "voteAverage";
        public static final String COLUMN_VOTE_COUNT = "voteCount";
        public static final String COLUMN_ORIGINAL_TITLE = "originalTitle";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_TIME_ADDED = "timeAdded";

    }
}
