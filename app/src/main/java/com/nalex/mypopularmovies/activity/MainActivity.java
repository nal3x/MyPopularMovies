package com.nalex.mypopularmovies.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.nalex.mypopularmovies.R;
import com.nalex.mypopularmovies.model.MovieResultsPage;
import com.nalex.mypopularmovies.network.MovieDbService;
import com.nalex.mypopularmovies.network.NetworkUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Testing if API KEY can be read
        String myApiKey = getString(R.string.THEMOVIEDB_API_KEY);
        Toast.makeText(getApplicationContext(), myApiKey, Toast.LENGTH_LONG).show();

        MovieDbService movieDbService = NetworkUtils.getMovieDbService();

        Call<MovieResultsPage> call = movieDbService.getPopularMovies(myApiKey);

        //Log to display if the correct URL was called
        Log.d(MainActivity.class.getSimpleName(), "URL called: " + call.request().url() + "");

        call.enqueue(new Callback<MovieResultsPage>() { //asynchronous call
            @Override
            public void onResponse(Call<MovieResultsPage> call, Response<MovieResultsPage> response) {
                Log.d(MainActivity.class.getSimpleName(),
                        "Data fetched: " + response.body().getResults().get(0).getTitle());
            }

            @Override
            public void onFailure(Call<MovieResultsPage> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Please try again", Toast.LENGTH_LONG).show();
            }
        });
    }
}