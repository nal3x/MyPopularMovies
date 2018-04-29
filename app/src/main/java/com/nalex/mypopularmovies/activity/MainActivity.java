package com.nalex.mypopularmovies.activity;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
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

    //TODO: Implement a Loading indicator or indicate that there is no data connection

    private final static String TAG = MainActivity.class.getSimpleName();
    private final static String SORT_BY_POPULARITY_KEY = "SORT_BY_POPULARITY";
    private final static String SORT_BY_RATING_KEY = "SORT_BY_RATING";
    private final static String SHOW_UPCOMING_IN_THEATERS_KEY = "UPCOMING_MOVIES";
    private final static String SHOW_NOW_PLAYING_IN_THEATERS_KEY = "IN_THEATERS";
    private final static String SORT_RECENTLY_ADDED = FavoriteMoviesContract.MovieEntry.COLUMN_TIME_ADDED + " DESC";
    private final static String SORT_BY_RATING = FavoriteMoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE + " DESC";
    private final static String DEFAULT_REGION = "GR"; //should normally be found in preferences...
    //TODO: provide list of country codes for now playing/upcoming movies
    private List<Movie> mMoviesList;
    private int mLastPageLoaded; //assigned by the results fetched, used to inform the rv listener
    private MovieAdapter adapter;
    private String mMovieSortingCriteria;
    //members used for RV scroll listener
    private int previousTotal;
    private boolean loading;
    private int visibleThreshold;
    //toggle to show/hide menu options
    private boolean WatchListMode;

    @BindView(R.id.main_toolbar) Toolbar myToolbar;
    @BindView(R.id.rv_movies) RecyclerView recyclerView;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindString(R.string.THEMOVIEDB_API_KEY) String apiKey;
    @BindInt(R.integer.num_of_cols) int numberOfColumns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(myToolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        //setting the menu triple bar icon as home
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24px);

        mMoviesList = new ArrayList<>();

        adapter = new MovieAdapter(MainActivity.this,
                MainActivity.this, (ArrayList)mMoviesList);

        recyclerView.setAdapter(adapter);
        final GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        //initially we fetch most popular movies page 1 and initialize scroll variables
        //while also hiding the menu

        WatchListMode = false;
        initializeScroll();
        mMovieSortingCriteria = SORT_BY_POPULARITY_KEY;
        getMovieDataFromInternet(mMovieSortingCriteria, 1, null);
        myToolbar.setTitle(R.string.sort_by_popularity);


        // Lazy loading with scroll listener attached to RecyclerView
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = recyclerView.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    //load next page of the same endpoint ;)
                    getMovieDataFromInternet(mMovieSortingCriteria, mLastPageLoaded + 1, DEFAULT_REGION);
                    loading = true;
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(false);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        int id = menuItem.getItemId();
                        switch (id) {
                            case R.id.show_now_playing: {
                                WatchListMode = false;
                                invalidateOptionsMenu();
                                mMoviesList.clear();
                                myToolbar.setTitle(R.string.now_playing);
                                mMovieSortingCriteria = SHOW_NOW_PLAYING_IN_THEATERS_KEY;
                                initializeScroll();
                                getMovieDataFromInternet(mMovieSortingCriteria,1, DEFAULT_REGION);
                                return true;
                            }
                            case R.id.sort_by_popularity: {
                                //whenever we switch the endpoint the list must get cleared
                                //and we return to the first page of results
                                WatchListMode = false;
                                invalidateOptionsMenu();
                                mMoviesList.clear();
                                myToolbar.setTitle(R.string.sort_by_popularity);
                                mMovieSortingCriteria = SORT_BY_POPULARITY_KEY;
                                initializeScroll();
                                getMovieDataFromInternet(mMovieSortingCriteria, 1, null);
                                return true;
                            }
                            case R.id.sort_by_rating: {
                                WatchListMode = false;
                                invalidateOptionsMenu();
                                mMoviesList.clear();
                                myToolbar.setTitle(R.string.sort_by_rating);
                                mMovieSortingCriteria = SORT_BY_RATING_KEY;
                                initializeScroll();
                                getMovieDataFromInternet(mMovieSortingCriteria, 1, null);
                                return true;
                            }
                            case R.id.show_upcoming: {
                                WatchListMode = false;
                                invalidateOptionsMenu();
                                mMoviesList.clear();
                                myToolbar.setTitle(R.string.upcoming);
                                mMovieSortingCriteria = SHOW_UPCOMING_IN_THEATERS_KEY;
                                initializeScroll();
                                getMovieDataFromInternet(mMovieSortingCriteria, 1, DEFAULT_REGION);
                                return true;
                            }
                            case R.id.watchlist: {
                                WatchListMode = true;
                                invalidateOptionsMenu();
                                loadData(SORT_RECENTLY_ADDED);
                                myToolbar.setTitle(R.string.watchlist);
                                return true;
                            }
                        }
                        return true;
                    }
                });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home: {
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
            case R.id.sortWatchlistRecent: {
                loadData(SORT_RECENTLY_ADDED);
                return true;
            }
            case R.id.sortWatchlistRating: {
                loadData(SORT_BY_RATING);
                return true;
            }
            //TODO: mass delete main R.id.clearWatchlist
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        if (!WatchListMode) {
            menu.setGroupVisible(R.id.watchlist_group,false);
        }
        else
            menu.setGroupVisible(R.id.watchlist_group,true);

        return true;
    }


    private void getMovieDataFromInternet(String sortCriteria, int requestPage, String region) {

        MovieDbService movieDbService = NetworkUtils.getMovieDbService();
        Call<MovieResultsPage> call;

        switch (sortCriteria) {
            case SHOW_NOW_PLAYING_IN_THEATERS_KEY: {
                call = movieDbService.getNowPlayingMovies(apiKey, requestPage, region);
            }
            break;
            case SORT_BY_POPULARITY_KEY: {
                call = movieDbService.getPopularMovies(apiKey, requestPage);
            }
            break;
            case SORT_BY_RATING_KEY: {
                call = movieDbService.getTopRatedMovies(apiKey, requestPage);
            }
            break;
            case SHOW_UPCOMING_IN_THEATERS_KEY: {
                call = movieDbService.getNowPlayingMovies(apiKey, requestPage, region);
            }
            break;
            default: call = movieDbService.getPopularMovies(apiKey);
        }

        call.enqueue(new Callback<MovieResultsPage>() {
            @Override
            public void onResponse(Call<MovieResultsPage> call, Response<MovieResultsPage> response) {
                if (null != response.body()) {
                    mLastPageLoaded = response.body().getPage();
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

    private void initializeScroll () {
        previousTotal = 0;
        loading = true;
        visibleThreshold = 100;
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

}

