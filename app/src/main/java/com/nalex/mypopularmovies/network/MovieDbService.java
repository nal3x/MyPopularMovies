package com.nalex.mypopularmovies.network;

import com.nalex.mypopularmovies.model.Configuration;
import com.nalex.mypopularmovies.model.MovieResultsPage;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieDbService {
    /*
     * Provides methods that correspond to HTTP methods
     * Annotations provide the method name and relative URL
     */

    //fetches the first page of movies based on popularity
    @GET("movie/popular")
    Call<MovieResultsPage> getPopularMovies(@Query("api_key")String apiKey);

    //fetches the first page of movies based on rating
    @GET("movie/top_rated")
    Call<MovieResultsPage> getTopRatedMovies(@Query("api_key")String apiKey);

    //method to fetch configuration
    @GET("configuration")
    Call<Configuration> getConfiguration(@Query("api_key")String apiKey);

}
