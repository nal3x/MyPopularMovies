package com.nalex.mypopularmovies.network;

import com.nalex.mypopularmovies.model.MovieResultsPage;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieDbService {
    /*
     * Provides methods that correspond to HTTP methods
     * Annotations provide the method name and relative URL
     */

    //fetches list of movies based on popularity
    @GET("movie/popular")
    Call<MovieResultsPage> getPopularMovies(@Query("api_key")String apiKey);

}
