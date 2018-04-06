package com.nalex.mypopularmovies.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.nalex.mypopularmovies.R;
import com.nalex.mypopularmovies.adapter.MovieAdapter;
import com.nalex.mypopularmovies.model.Movie;
import com.nalex.mypopularmovies.model.MovieResultsPage;
import com.nalex.mypopularmovies.network.MovieDbService;
import com.nalex.mypopularmovies.network.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    private final static String SORT_BY_POPULARITY_KEY = "POP";
    private final static String SORT_BY_RATING_KEY = "RATE";

    //creating the retrofit instance and getting instance of the interface
    MovieDbService movieDbService = NetworkUtils.getMovieDbService();

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.rv_movies);

        getMovieData(SORT_BY_POPULARITY_KEY);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainactivity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.sort_by_popularity: {
                getMovieData(SORT_BY_POPULARITY_KEY);
                return true;
            }
            case R.id.sort_by_rating: {
                getMovieData(SORT_BY_RATING_KEY);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void getMovieData(String sortCriteria) {

        MovieDbService movieDbService = NetworkUtils.getMovieDbService();
        Call<MovieResultsPage> call;
        String apiKey = getString(R.string.THEMOVIEDB_API_KEY);

        int numberOfColumns = getResources().getInteger(R.integer.num_of_cols);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,numberOfColumns);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        switch (sortCriteria) {
            case SORT_BY_POPULARITY_KEY: {
                call = movieDbService.getPopularMovies(apiKey);
                Log.d(TAG, "URL called: " + call.request().url() + "");
            }
            break;
            case SORT_BY_RATING_KEY: {
                call = movieDbService.getTopRatedMovies(apiKey);
                Log.d(TAG, "URL called: " + call.request().url() + "");
            }
            break;
            default: call = movieDbService.getPopularMovies(apiKey);
        }

        call.enqueue(new Callback<MovieResultsPage>() {
            @Override
            public void onResponse(Call<MovieResultsPage> call, Response<MovieResultsPage> response) {
                if (response.body() != null) {
                    List<Movie> moviesList = new ArrayList<>(response.body().getResults());
                    Log.d(TAG, "Checking 1st movie title" + moviesList.get(0).getTitle());
                    MovieAdapter adapter = new MovieAdapter((ArrayList)moviesList);
                    recyclerView.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<MovieResultsPage> call, Throwable t) {
                Log.d(TAG, "Failed to Fetch data, check List contents for null");
            }
        });

    }
}