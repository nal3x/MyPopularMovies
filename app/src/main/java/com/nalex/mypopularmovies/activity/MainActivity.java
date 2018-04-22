package com.nalex.mypopularmovies.activity;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.nalex.mypopularmovies.R;
import com.nalex.mypopularmovies.adapter.MovieAdapter;
import com.nalex.mypopularmovies.data.FavoriteMoviesContract;
import com.nalex.mypopularmovies.model.Movie;
import com.nalex.mypopularmovies.model.MovieResultsPage;
import com.nalex.mypopularmovies.network.MovieDbService;
import com.nalex.mypopularmovies.network.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    //TODO: Implement a Loading indicator (Polish)
    //TODO: ScrollListener to load more pages and cache results

    private final static String TAG = MainActivity.class.getSimpleName();
    private final static String SORT_BY_POPULARITY_KEY = "SORT_BY_POPULARITY";
    private final static String SORT_BY_RATING_KEY = "SORT_BY_RATING";
    private List<Movie> mMoviesList;
    private MovieAdapter adapter;

    @BindView(R.id.main_toolbar) Toolbar myToolbar;
    @BindView(R.id.rv_movies) RecyclerView recyclerView;
    @BindString(R.string.THEMOVIEDB_API_KEY) String apiKey;
    @BindInt(R.integer.num_of_cols) int numberOfColumns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(myToolbar);

        mMoviesList = new ArrayList<>();

        adapter = new MovieAdapter(MainActivity.this,
                MainActivity.this, (ArrayList)mMoviesList);

        recyclerView.setAdapter(adapter);

        getMovieDataFromInternet(SORT_BY_POPULARITY_KEY);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.sort_by_popularity: {
                getMovieDataFromInternet(SORT_BY_POPULARITY_KEY);
                return true;
            }
            case R.id.sort_by_rating: {
                getMovieDataFromInternet(SORT_BY_RATING_KEY);
                return true;
            }
            case R.id.watchlist: {
                loadWatchList();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void getMovieDataFromInternet(String sortCriteria) {

        MovieDbService movieDbService = NetworkUtils.getMovieDbService();
        Call<MovieResultsPage> call;

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        switch (sortCriteria) {
            case SORT_BY_POPULARITY_KEY: {
                call = movieDbService.getPopularMovies(apiKey);
                myToolbar.setTitle(R.string.sort_by_popularity);
            }
            break;
            case SORT_BY_RATING_KEY: {
                call = movieDbService.getTopRatedMovies(apiKey);
                myToolbar.setTitle(R.string.sort_by_rating);
            }
            break;
            default: call = movieDbService.getPopularMovies(apiKey);
        }

        call.enqueue(new Callback<MovieResultsPage>() {
            @Override
            public void onResponse(Call<MovieResultsPage> call, Response<MovieResultsPage> response) {
                if (null != response.body().getResults()) {
                    mMoviesList.clear();
                    mMoviesList.addAll(response.body().getResults());
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<MovieResultsPage> call, Throwable t) {
                //TODO: Notify user that fetching data has failed
                Log.d(TAG, "Failed to Fetch data, check List contents for null");
            }
        });

    }

    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(DetailActivity.DETAIL_ACTIVITY_INTENT_KEY, movie);
        startActivity(intent);
    }

    private void loadWatchList () {

        mMoviesList.clear();
        myToolbar.setTitle(R.string.watchlist);

        //Recently added movies go first, can give users a preference with this String
        String sortOrder =
                FavoriteMoviesContract.MovieEntry.COLUMN_TIME_ADDED + " DESC";

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
}