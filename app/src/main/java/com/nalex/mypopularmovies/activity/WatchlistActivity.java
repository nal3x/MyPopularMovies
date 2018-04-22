package com.nalex.mypopularmovies.activity;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.nalex.mypopularmovies.R;
import com.nalex.mypopularmovies.adapter.MovieAdapter;
import com.nalex.mypopularmovies.data.FavoriteMoviesContract;
import com.nalex.mypopularmovies.model.Movie;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;

public class WatchlistActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler,
        MovieAdapter.MovieAdapterOnLongClickHandler {

    //TODO: save state! when device is rotated selected movies are lost :(

    private final static String TAG = WatchlistActivity.class.getSimpleName();
    private final static String SORT_RECENTLY_ADDED = FavoriteMoviesContract.MovieEntry.COLUMN_TIME_ADDED + " DESC";
    private final static String SORT_BY_RATING = FavoriteMoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE + " DESC";
    private List<Movie> mMoviesList;
    private MovieAdapter adapter;
    private boolean stateSelected;
    @BindView(R.id.main_toolbar) Toolbar myToolbar; //TODO: CHANGE TOOLBAR
    @BindView(R.id.rv_movies) RecyclerView recyclerView;
    @BindInt(R.integer.num_of_cols) int numberOfColumns;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(myToolbar);
        myToolbar.setTitle(R.string.watchlist);

        mMoviesList = new ArrayList<>();

        adapter = new MovieAdapter(WatchlistActivity.this,
                WatchlistActivity.this, this, (ArrayList)mMoviesList);

        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        loadData(SORT_RECENTLY_ADDED);

    }

    private void loadData(String sortOrder) {

        mMoviesList.clear();

        //try with resources to close cursor
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

                Movie movieToAdd = new Movie(movieId, movieTitle, moviePoster, movieReleaseDate, movieVoteAverage, movieVoteCount,
                        movieOriginalTitle, movieOverview);
                mMoviesList.add(movieToAdd);
            }
        }
        adapter.notifyDataSetChanged();


    }

    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(WatchlistActivity.this, DetailActivity.class);
        intent.putExtra(DetailActivity.DETAIL_ACTIVITY_INTENT_KEY, movie);
        startActivity(intent);
    }

    @Override
    public void onLongClick(Movie movie) {
        if(movie.isSelected()) {
            movie.setSelected(false);
        }
        else {
            movie.setSelected(true);
        }

        for (Movie m : mMoviesList) {
            if (m.isSelected()) {
                stateSelected = true;
                break;
            }
            stateSelected = false;
        }
        String state = "State: " + stateSelected;
        Toast.makeText(this, state, Toast.LENGTH_SHORT).show();
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
                loadData(SORT_RECENTLY_ADDED);
                return true;
            }
            case R.id.sortWatchlistRating: {
                loadData(SORT_BY_RATING);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
