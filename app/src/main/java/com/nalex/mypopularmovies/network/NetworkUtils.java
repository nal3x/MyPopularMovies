package com.nalex.mypopularmovies.network;

import android.net.Uri;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String BASE_URL_FOR_POSTERS = "http://image.tmdb.org/t/p/";
    private static final String BASE_URL_FOR_MOVIES = "https://api.themoviedb.org/3/";
    private static final String PROPOSED_IMAGE_WIDTH = "w185";
    private static final String BASE_URL_FOR_YOUTUBE_VIDEOS = "https://www.youtube.com/watch";
    private static final String YOUTUBE_VIDEO_PARAMETER_KEY = "v";
    private static final String BASE_URL_FOR_YOUTUBE_THUMBNAILS = "https://img.youtube.com/vi";
    private static final String PATH_FOR_YOUTUBE_FIRST_THUMBNAIL = "0.jpg";

    /* Services creates a Retrofit instance and generate
     * an implementation of the corresponding retrofit
     * interface
     */

    public static MovieDbService getMovieDbService() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_FOR_MOVIES)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieDbService service = retrofit.create(MovieDbService.class);
        return service;

    }

    /* Method which returns a URL to fetch the poster images of the
     * movies that will be presented to the user
     */

    public static URL buildImageUrl (String relativeImageUrl) {

        //removing the first slash character from poster_path, could use trimming
        relativeImageUrl = relativeImageUrl.substring(1);

        Uri imageQueryUri = Uri.parse(BASE_URL_FOR_POSTERS).buildUpon()
                .appendPath(PROPOSED_IMAGE_WIDTH)
                .appendPath(relativeImageUrl)
                .build();
        try {
            URL imageQueryURL = new URL(imageQueryUri.toString());
            return imageQueryURL;
        }catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static URL buildYoutubeVideoURL (String key) {
        Uri youtubeVideoUri = Uri.parse(BASE_URL_FOR_YOUTUBE_VIDEOS).buildUpon()
                .appendQueryParameter(YOUTUBE_VIDEO_PARAMETER_KEY, key)
                .build();
        try {
            URL youtubeVideoUrl = new URL(youtubeVideoUri.toString());
            return youtubeVideoUrl;
        }catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static URL buildYoutubeThumbnailUrl (String key) {
        Uri youtubeThumbnailUri = Uri.parse(BASE_URL_FOR_YOUTUBE_THUMBNAILS).buildUpon()
                .appendPath(key)
                .appendPath(PATH_FOR_YOUTUBE_FIRST_THUMBNAIL)
                .build();
        try {
            URL youtubeThumbnailUrl = new URL(youtubeThumbnailUri.toString());
            return youtubeThumbnailUrl;
        }catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }


}
