package com.nalex.mypopularmovies.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.nalex.mypopularmovies.R;
import com.nalex.mypopularmovies.adapter.MovieAdapter;
import com.nalex.mypopularmovies.data.FavoriteMoviesContract;
import com.nalex.mypopularmovies.model.Movie;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;

public class WatchListActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<ArrayList<Movie>> {

    private List<Movie> mMoviesList;
    private MovieAdapter adapter;
    private static final int MOVIE_LOADER_ID = 0;
    private final static String LOADER_BUNDLE_SORT_ORDER_KEY = "SORT_ORDER";
    //constants used to sort WatchList
    private final static String SORT_RECENTLY_ADDED = FavoriteMoviesContract.MovieEntry.COLUMN_TIME_ADDED + " DESC";
    private final static String SORT_BY_RATING = FavoriteMoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE + " DESC";

    @BindView(R.id.main_toolbar) Toolbar myToolbar;
    @BindView(R.id.rv_movies) RecyclerView recyclerView;
    @BindInt(R.integer.num_of_cols) int numberOfColumns;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);
        ButterKnife.bind(this);

        setSupportActionBar(myToolbar);
        final ActionBar supportActionBar = getSupportActionBar();
        if (null != supportActionBar) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setTitle(R.string.watchlist_title);
        }

        mMoviesList = new ArrayList<>();

        adapter = new MovieAdapter(WatchListActivity.this, WatchListActivity.this, (ArrayList)mMoviesList);

        recyclerView.setAdapter(adapter);
        final GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        Bundle sortOrderBundle = new Bundle();
        sortOrderBundle.putString(LOADER_BUNDLE_SORT_ORDER_KEY, SORT_RECENTLY_ADDED);
        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, sortOrderBundle, this);

    }

    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.DETAIL_ACTIVITY_INTENT_KEY, movie);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.watchlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.sortWatchlistRecent: {
                Bundle sortOrderBundle = new Bundle();
                sortOrderBundle.putString(LOADER_BUNDLE_SORT_ORDER_KEY, SORT_RECENTLY_ADDED);
                getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, sortOrderBundle, this);
                return true;
            }
            case R.id.sortWatchlistRating: {
                Bundle sortOrderBundle = new Bundle();
                sortOrderBundle.putString(LOADER_BUNDLE_SORT_ORDER_KEY, SORT_BY_RATING);
                getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, sortOrderBundle, this);
                return true;
            }
            //TODO: mass delete by R.id.clearWatchlist
            default:
                return super.onOptionsItemSelected(item);
        }
    }

        /* Using a Loader to load Watchlist in a background thread. When user enters WatchList mode for one time
     * and then enters DetailActivity, then the Back button always showed the WatchList (even if user has entered
     * Detail while viewing endpoint). This means that once a Loader is started, then a lifecycle callback (onRestart?)
     * called onLoadFinished again. Thus, in the onLoadFinished callback we have to destroy our loader.
     * PROBLEM: back button does not show UPDATED WatchList!!!!
     */

    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, @Nullable final Bundle args) {

        return new AsyncTaskLoader<ArrayList<Movie>>(this) {
            //Member which will store our results
            private ArrayList<Movie> mWatchList = new ArrayList<>();

            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Override
            public ArrayList<Movie> loadInBackground() {

                String sortOrder = args.getString(LOADER_BUNDLE_SORT_ORDER_KEY);

                ArrayList<Movie> movies = new ArrayList<>();

                //try-with-resources to autoclose Cursor
                try (Cursor cursor = getContentResolver().query(FavoriteMoviesContract.MovieEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        sortOrder)) {

                    while (cursor.moveToNext()) {
                        int movieId = cursor.getInt(cursor.getColumnIndex(FavoriteMoviesContract.MovieEntry.COLUMN_MOVIE_ID));
                        String movieTitle = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.MovieEntry.COLUMN_TITLE));
                        String moviePoster = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.MovieEntry.COLUMN_POSTER));
                        String movieReleaseDate = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.MovieEntry.COLUMN_RELEASE_DATE));
                        float movieVoteAverage = cursor.getFloat(cursor.getColumnIndex(FavoriteMoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE));
                        int movieVoteCount = cursor.getInt(cursor.getColumnIndex(FavoriteMoviesContract.MovieEntry.COLUMN_VOTE_COUNT));
                        String movieOriginalTitle = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE));
                        String movieOverview = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.MovieEntry.COLUMN_OVERVIEW));
                        //constructing a new movie object based on data in our db
                        Movie movieToAdd = new Movie(movieId, movieTitle, moviePoster, movieReleaseDate, movieVoteAverage, movieVoteCount,
                                movieOriginalTitle, movieOverview);
                        movies.add(movieToAdd);
                    }
                }
                return movies;
            }

            @Override
            public void deliverResult(@Nullable ArrayList<Movie> data) {
                mWatchList.clear();
                mWatchList.addAll(data);
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
        if (null != data) {
            mMoviesList.clear();
            mMoviesList.addAll(data);
            adapter.notifyDataSetChanged();
        }
        else
            return;
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Movie>> loader) { //Not implemented
    }
}

