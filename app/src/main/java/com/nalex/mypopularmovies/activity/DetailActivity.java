package com.nalex.mypopularmovies.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.nalex.mypopularmovies.R;
import com.nalex.mypopularmovies.model.Movie;
import com.nalex.mypopularmovies.network.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class DetailActivity extends AppCompatActivity {

    private Movie mMovie;

//    TODO: Data Binding

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        Toolbar myToolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(myToolbar);

        final ActionBar supportActionBar = getSupportActionBar();

        if (null != supportActionBar) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setTitle(R.string.detail_activity_title);
        }


        Intent intent = getIntent();
        mMovie = intent.getParcelableExtra("Movie");

        TextView movieTitleTextView = findViewById(R.id.movie_title_tv);
        movieTitleTextView.setText(mMovie.getTitle());

        ImageView moviePoster = findViewById(R.id.movie_poster_iv);
        String relativePath = mMovie.getPosterPath();
        URL imageURL = NetworkUtils.buildImageUrl(relativePath);
        if (null != imageURL.toString())
            Picasso.get().load(imageURL.toString()).into(moviePoster);

        TextView releasedDate = findViewById(R.id.release_date_tv);
        releasedDate.setText(getYearReleased(mMovie.getReleaseDate()));

        TextView voteAverageView = findViewById(R.id.vote_average_tv);
        String voteAverage = Float.toString(mMovie.getVoteAverage());
        voteAverageView.setText(formatVoteAverage(voteAverage));

        TextView totalVotes = findViewById(R.id.total_votes_tv);
        String votesString = getString(R.string.votes_string);
        String voteCount = Integer.toString(mMovie.getVoteCount());
        totalVotes.setText(voteCount + votesString);

        TextView originalTitle = findViewById(R.id.original_title_tv);
        originalTitle.setText(getString(R.string.original_title)  + "\n" + mMovie.getOriginalTitle());

        TextView movieDescription = findViewById(R.id.movie_description_tv);
        movieDescription.setText(mMovie.getOverview());


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

    private String getYearReleased (String dateReleased) {
        return dateReleased.substring(0, 4);
    }

    private String formatVoteAverage (String voteAverage) {
        return voteAverage + "/10.0";
    }
}
