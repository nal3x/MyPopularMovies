package com.nalex.mypopularmovies.data;


import android.provider.BaseColumns;

public class FavoriteMoviesContract {

    public static final class MovieEntry implements BaseColumns {

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
