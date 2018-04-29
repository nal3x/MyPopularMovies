package com.nalex.mypopularmovies.network;

import com.nalex.mypopularmovies.model.Configuration;
import com.nalex.mypopularmovies.model.Movie;
import com.nalex.mypopularmovies.model.MovieResultsPage;
import com.nalex.mypopularmovies.model.MovieReviews;
import com.nalex.mypopularmovies.model.MovieVideos;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
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

    //method to get most popular movies pages 1, 2, 3...
    @GET("movie/popular")
    Call<MovieResultsPage> getPopularMovies(@Query("api_key")String apiKey, @Query("page") int page);

    //method to get top rated movies pages
    @GET("movie/top_rated")
    Call<MovieResultsPage> getTopRatedMovies(@Query("api_key")String apiKey, @Query("page") int page);

    //method to get movies in theatres @ region region
    @GET("movie/now_playing")
    Call<MovieResultsPage> getNowPlayingMovies(@Query("api_key")String apiKey,
                                               @Query("page") int page,
                                               @Query("region") String region);

    //method to get upcoming movies in theaters @region region
    @GET("movie/upcoming")
    Call<MovieResultsPage> getUpcomingMovies(@Query("api_key")String apiKey,
                                               @Query("page") int page,
                                               @Query("region") String region);

    //method to get videos for a specific movie based on movie ID
    @GET("movie/{movie_id}/videos")
    Call<MovieVideos> getVideosForMovie(@Path("movie_id") int movieId, @Query("api_key")String apiKey);

    //method to get reviews for a specific movie based on movie ID
    @GET("movie/{movie_id}/reviews")
    Call<MovieReviews> getReviewsForMovie(@Path("movie_id") int movieId, @Query("api_key")String apiKey);












}
