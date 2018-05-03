package com.nalex.mypopularmovies.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nalex.mypopularmovies.R;
import com.nalex.mypopularmovies.adapter.ReviewsAdapter;
import com.nalex.mypopularmovies.adapter.ThumbnailAdapter;
import com.nalex.mypopularmovies.data.FavoriteMoviesContract;
import com.nalex.mypopularmovies.model.Movie;
import com.nalex.mypopularmovies.model.MovieReviewResult;
import com.nalex.mypopularmovies.model.MovieReviews;
import com.nalex.mypopularmovies.model.MovieVideoResult;
import com.nalex.mypopularmovies.model.MovieVideos;
import com.nalex.mypopularmovies.network.MovieDbService;
import com.nalex.mypopularmovies.network.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity implements ThumbnailAdapter.ThumbnailAdapterOnClickHandler {

    public final static String DETAIL_ACTIVITY_INTENT_KEY = "MOVIE_DETAILS";
    private final static String FAB_ICON_FOR_SAVED_MOVIE = "SAVED";
    private final static String FAB_ICON_FOR_UNSAVED_MOVIE = "UNSAVED";
    private final static String TAG = DetailActivity.class.getSimpleName();
    private Movie mMovie;
    private List<MovieVideoResult> mMovieVideos;
    private List<MovieReviewResult> mMovieReviews;
    private ReviewsAdapter reviewsAdapter;
    private ThumbnailAdapter thumbnailAdapter;

    @BindView(R.id.movie_title_tv) TextView movieTitleTextView;
    @BindView(R.id.movie_poster_iv) ImageView moviePoster;
    @BindView(R.id.vote_average_tv) TextView voteAverageView;
    @BindView(R.id.total_votes_tv) TextView totalVotes;
    @BindView(R.id.original_title_tv) TextView originalTitle;
    @BindView(R.id.movie_description_tv) TextView movieDescription;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.detail_toolbar) Toolbar myToolbar;
    @BindView(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.reviews_recyclerview) RecyclerView reviewsRecyclerView;
    @BindView(R.id.thumbnails_recyclerview) RecyclerView thumbnailsRecyclerView;
    @BindString(R.string.THEMOVIEDB_API_KEY) String apiKey;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        setSupportActionBar(myToolbar);
        final ActionBar supportActionBar = getSupportActionBar();
        if (null != supportActionBar) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setTitle(R.string.detail_activity_title);
        }


        //Get Movie parcel from MainActivity
        Intent intent = getIntent();
        mMovie = intent.getParcelableExtra(DETAIL_ACTIVITY_INTENT_KEY);

        //fill Views with data that came from the parcel
        movieTitleTextView.setText(mMovie.getTitle());
        String relativePath = mMovie.getPosterPath();
        URL imageURL = NetworkUtils.buildImageUrl(relativePath);
        if (null != imageURL.toString())
            Picasso.get().load(imageURL.toString()).into(moviePoster);
        TextView releasedDate = findViewById(R.id.release_date_tv);
        releasedDate.setText(formatYearReleased(mMovie.getReleaseDate()));
        String voteAverage = Float.toString(mMovie.getVoteAverage());
        voteAverageView.setText(formatVoteAverage(voteAverage));
        String votesString = getString(R.string.votes_string);
        String voteCount = Integer.toString(mMovie.getVoteCount());
        totalVotes.setText(voteCount + " " + votesString);
        originalTitle.setText(getString(R.string.original_title) + " " + mMovie.getOriginalTitle());
        movieDescription.setText(mMovie.getOverview());

        initializeFabIcon();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isMovieInWatclist()) {
                    boolean successfulyDeleted = deleteMovieFromWatchlist();
                    if (successfulyDeleted)
                        setFabIconState(FAB_ICON_FOR_UNSAVED_MOVIE);
                }
                else { //if not, add movie to watchlist and update fab
                    boolean successfullyAdded = addMovieToWatchlist();
                    if (successfullyAdded)
                        setFabIconState(FAB_ICON_FOR_SAVED_MOVIE);
                }
            }
        });


        //setting up video thumbnail recyclerview
        mMovieVideos = new ArrayList<>();
        thumbnailAdapter = new ThumbnailAdapter(this, this, (ArrayList)mMovieVideos);
        thumbnailsRecyclerView.setAdapter(thumbnailAdapter);
        LinearLayoutManager thumbnailsLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL,
                false);
        thumbnailsRecyclerView.setLayoutManager(thumbnailsLayoutManager);
        thumbnailsRecyclerView.setHasFixedSize(false);


        //setting up reviews recyclerview
        mMovieReviews = new ArrayList<>();
        reviewsAdapter = new ReviewsAdapter(this, (ArrayList)mMovieReviews);
        reviewsRecyclerView.setAdapter(reviewsAdapter);
        LinearLayoutManager reviewsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        reviewsRecyclerView.setLayoutManager(reviewsLayoutManager);
        reviewsRecyclerView.setHasFixedSize(false);

        //Get reviews and videos for this movie using MovieDbService calls
        getMovieVideosFromInternet(mMovie.getId(), apiKey);
        getMovieReviewsFromInternet(mMovie.getId(), apiKey, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate menu resource file
        getMenuInflater().inflate(R.menu.detail, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Get the ID of the clicked item */
        int id = item.getItemId();

        /* Share menu item clicked */
        if (id == R.id.action_share) {
            Intent shareIntent = createShareIntent();
            if (shareIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(shareIntent);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Create and return the Share Intent
    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String sharedUrl = "";
        for (MovieVideoResult video : mMovieVideos) {
            if (video.getType().equalsIgnoreCase("trailer") &&
                    video.getSite().equalsIgnoreCase("youtube")) {
                sharedUrl = NetworkUtils.buildYoutubeVideoURL(video.getKey()).toString();
                break;
            }
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, sharedUrl);
        return shareIntent;
    }

    /*  Helper method to set the fab icon when user enters detail activity.
     *  Requires a lookup at the provider. Uses isMovieInWatchlist.
     */
    private void initializeFabIcon() {
        if (isMovieInWatclist()) {
            setFabIconState(FAB_ICON_FOR_SAVED_MOVIE);
        }
        else
            setFabIconState(FAB_ICON_FOR_UNSAVED_MOVIE);
    }

    // Helper method to set the proper fab icon
    private void setFabIconState (String fabState) {
        if (fabState.equals(FAB_ICON_FOR_SAVED_MOVIE)) {
            fab.setImageResource(R.drawable.ic_star_black_24px);
        }
        else
            fab.setImageResource(R.drawable.ic_star_border_black_24dp);
    }

    // method which queries the content resolver for a single movie based on it's ID.
    private boolean isMovieInWatclist() {

        //constructing the proper uri based on movie id
        Uri uri = FavoriteMoviesContract.MovieEntry.buildMovieUriWithId(mMovie.getId());

        Cursor retCursor = getContentResolver().query(uri,
                null,
                null,
                null,
                null);

        //we can't use the notification uri to identify if movie is present in the Watchlist DB
        boolean movieFound = false;
        if (retCursor != null && retCursor.moveToFirst()) //we have valid data
            movieFound = true;

        retCursor.close();

        return movieFound;

    }

    //Method to remove a movie from the Watchlist.
    private boolean deleteMovieFromWatchlist() {

        Uri uri = FavoriteMoviesContract.MovieEntry.buildMovieUriWithId(mMovie.getId());
        //resolver returns # of movies deleted
        int deletedMovies = getContentResolver().delete(uri, null, null);

        if (deletedMovies == 1) {
            Snackbar.make(coordinatorLayout, R.string.snackbar_success_delete_text, Snackbar.LENGTH_LONG).show();
            return true;
        }
        else {
            Snackbar.make(coordinatorLayout, R.string.snackbar_failed_delete_text, Snackbar.LENGTH_LONG).show();
            return false;
        }

    }

    private boolean addMovieToWatchlist() {

        ContentValues cv = new ContentValues();

        cv.put(FavoriteMoviesContract.MovieEntry.COLUMN_MOVIE_ID, mMovie.getId());
        cv.put(FavoriteMoviesContract.MovieEntry.COLUMN_TITLE, mMovie.getTitle());
        cv.put(FavoriteMoviesContract.MovieEntry.COLUMN_POSTER, mMovie.getPosterPath());
        cv.put(FavoriteMoviesContract.MovieEntry.COLUMN_RELEASE_DATE, mMovie.getReleaseDate());
        cv.put(FavoriteMoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, mMovie.getVoteAverage());
        cv.put(FavoriteMoviesContract.MovieEntry.COLUMN_VOTE_COUNT, mMovie.getVoteCount());
        cv.put(FavoriteMoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE, mMovie.getOriginalTitle());
        cv.put(FavoriteMoviesContract.MovieEntry.COLUMN_OVERVIEW, mMovie.getOverview());

        Uri uri = getContentResolver().insert(FavoriteMoviesContract.MovieEntry.CONTENT_URI, cv);

        if (uri != null) {
            Snackbar.make(coordinatorLayout, R.string.snackbar_success_insert_text, Snackbar.LENGTH_LONG).show();
            return true;
        }
        else {
            Snackbar.make(coordinatorLayout, R.string.snackbar_failed_insert_text, Snackbar.LENGTH_LONG)
                    .show();
            return false;
        }
    }

    //Returns a list of videos (MovieVideoResult) for a specific movie ID.
    private void getMovieVideosFromInternet(int movieId, final String apiKey) {

        MovieDbService movieDbService = NetworkUtils.getMovieDbService();
        Call<MovieVideos> call = movieDbService.getVideosForMovie(movieId, apiKey);
        call.enqueue(new Callback<MovieVideos>() {
            @Override
            public void onResponse(Call<MovieVideos> call, Response<MovieVideos> response) {
                if (null != response.body()) {
                    mMovieVideos.addAll(response.body().getResults());
                    thumbnailAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<MovieVideos> call, Throwable t) {
                //TODO: Notify user that fetching data has failed
                Log.d(TAG, "Failed to Fetch data, check List contents for null");
            }
        });

    }

    private void getMovieReviewsFromInternet(int movieId, String apiKey, final Context context) {

        MovieDbService movieDbService = NetworkUtils.getMovieDbService();
        Call<MovieReviews> call = movieDbService.getReviewsForMovie(movieId, apiKey);
        call.enqueue(new Callback<MovieReviews>() {
            @Override
            public void onResponse(Call<MovieReviews> call, Response<MovieReviews> response) {
                if (null != response.body()) {
                    mMovieReviews.addAll(response.body().getResults());
                    reviewsAdapter.notifyDataSetChanged();
                }

            }
            @Override
            public void onFailure(Call<MovieReviews> call, Throwable t) {
                //TODO: Notify user that fetching data has failed
                Log.d(TAG, "Failed to Fetch data, check List contents for null");
            }
        });

    }

    //ThumbnailAdapter.ThumbnailAdapterOnClickHandler starts video when thumbnail is clicked
    @Override
    public void onClick(MovieVideoResult movieVideoResult) {
        String videoKey = movieVideoResult.getKey();
        URL url = NetworkUtils.buildYoutubeVideoURL(videoKey);
        Intent startVideoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()));
        //checking that there is an activity that can handle our intent
        if (startVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(startVideoIntent);
        }
    }

    //helper methods to obtain year from date released and format vote average
    private String formatYearReleased(String dateReleased) {
        return dateReleased.substring(0, 4);
    }

    private String formatVoteAverage(String voteAverage) {
        return voteAverage + "/10.0";
    }
}

