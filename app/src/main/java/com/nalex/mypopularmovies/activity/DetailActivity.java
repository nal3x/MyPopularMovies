package com.nalex.mypopularmovies.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nalex.mypopularmovies.R;
import com.nalex.mypopularmovies.data.FavoriteMoviesContract;
import com.nalex.mypopularmovies.model.Movie;
import com.nalex.mypopularmovies.network.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    public final static String DETAIL_ACTIVITY_INTENT_KEY = "MOVIE_DETAILS";
    private Movie mMovie;
    private SQLiteDatabase mDb;
    private String TAG = DetailActivity.class.getSimpleName();

    @BindView(R.id.movie_title_tv)
    TextView movieTitleTextView;
    @BindView(R.id.movie_poster_iv)
    ImageView moviePoster;
    @BindView(R.id.vote_average_tv)
    TextView voteAverageView;
    @BindView(R.id.total_votes_tv)
    TextView totalVotes;
    @BindView(R.id.original_title_tv)
    TextView originalTitle;
    @BindView(R.id.movie_description_tv)
    TextView movieDescription;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.detail_toolbar)
    Toolbar myToolbar;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

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

        Intent intent = getIntent();
        mMovie = intent.getParcelableExtra(DETAIL_ACTIVITY_INTENT_KEY);

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
        originalTitle.setText(getString(R.string.original_title) + "\n" + mMovie.getOriginalTitle());
        movieDescription.setText(mMovie.getOverview());
        
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMovieToWatchlist();
            }
        });

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
            startActivity(shareIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    TODO: share video instead of original title (stage 2)

    // Create and return the Share Intent
    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mMovie.getOriginalTitle());
        return shareIntent;
    }

    // Sets new share Intent.
    private void changeShareIntent(Intent shareIntent) {

    }

    private String formatYearReleased(String dateReleased) {
        return dateReleased.substring(0, 4);
    }

    private String formatVoteAverage(String voteAverage) {
        return voteAverage + "/10.0";
    }

    private void addMovieToWatchlist() {

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
            Log.d(TAG, uri.toString());
            Snackbar.make(coordinatorLayout, R.string.snackbar_success_insert_text, Snackbar.LENGTH_LONG)
                    .setAction(R.string.snackbar_action_undo, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getBaseContext(),
                                    "Implement delete based on uri returned ;)",
                                    Toast.LENGTH_LONG).show();
                        }
                    })
                    .show();
        }
        else {
            Snackbar.make(coordinatorLayout, R.string.snackbar_failed_insert_text, Snackbar.LENGTH_LONG)
                    .show();
        }
    }


}
