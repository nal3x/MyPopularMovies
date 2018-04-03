package com.nalex.mypopularmovies.network;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class NetworkUtils {


    private static Retrofit retrofit;
    private static final String BASE_URL = "https://api.themoviedb.org/3/";

    /* MovieDbService creates a Retrofit instance and then
     * generates an implementation of the MovieDbService interface
     */

    public static MovieDbService getMovieDbService() {

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieDbService service = retrofit.create(MovieDbService.class);
        return service;

    }


}
