/*
 * Copyright <2017> <ANDREW LAM>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.andrewclam.popularmovie.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Andrew Chi Heng Lam on 8/19/2017.
 * <p>
 * NetworkUtil class contains method for communicating with server.
 */

public class NetworkUtils {

    public static final String TMDB_PATH_TOP_RATED = "top_rated";

    /* TMDB Service API Guide
     * query template : https://api.themoviedb.org/3/discover/movie?api_key=<<YOUR_API_KEY>>&sort_by=popularity.desc
     * In English : This discovers movies and sort them base on popularity in descending order.
     *
     * see https://www.themoviedb.org/documentation/api
    */
    public static final String TMDB_PATH_POPULAR = "popular";
    /* Instance Vars and Constants */
    // Log tag
    private static final String TAG = NetworkUtils.class.getSimpleName();

    /*************
     * URL Paths *
     *************/
    // TMDB
    private static final String TMDB_PATH_BASE_URL = "https://api.themoviedb.org/3";
    private static final String TMDB_PATH_MOVIE = "movie";
    private static final String TMDB_PATH_VIDEO = "videos";
    private static final String TMDB_PATH_REVIEWS = "reviews";

    // TMDB Uri Query Parameters
    private static final String TMDB_QUERY_API_KEY = "api_key";
    //    private static final String TMDB_SORT_BY = "sort_by";

    // Movie Poster Image Provider (TMDB)
    private static final String TMDB_PATH_BASE_IMAGE_URL = " https://image.tmdb.org/t/p";
    private static final String TMDB_PATH_IMAGE_SIZE_W500 = "w500";

    // Video Provider (Youtube)
    private static final String TMDB_VID_PROVIDER_YOUTUBE_BASE_VIDEO_URL = " https://www.youtube.com/";
    private static final String TMDB_VID_PROVIDER_YOUTUBE_PATH_WATCH = "watch";
    private static final String TMDB_VID_PROVIDER_YOUTUBE_QUERY_VIDEO_KEY = "v";

    // Video Thumbnail Provider (Youtube)
    private static final String TMDB_THUMB_PROVIDER_YOUTUBE_BASE_IMG_URL = "https://img.youtube.com/vi/";
    private static final String TMDB_THUMB_PROVIDER_YOUTUBE_IMAGE_FILE_NAME = "hqdefault.jpg";

    // URL Query Parameter Values - Uncomment to use query parameters
//    public static final String TMDB_NO_SORT_VAL = "no_val";
//    public static final String TMDB_POPULARITY_DESC = "popularity.desc";
//    public static final String TMDB_TOP_RATED_DESC = "vote_average.desc";
//    private static final String TMDB_VOTE_COUNT_GTE = "vote_count.gte"; // *see usage below

    /* Methods */

    /**
     * buildMovieListingUrl method builds the URL used to communicate with the TMDB server to fetch
     * movie listing using the user supplied sortByValue.
     *
     * @param pathSortByValue the user selected sort value
     * @return The URL to use to query the TMDB server with the sort parameter set to the
     * sortByValue.
     */
    public static URL buildMovieListingUrl(@NonNull final String pathSortByValue, @NonNull final String apiKey) {
        // Check sortByValue and apiKey
        if (pathSortByValue.isEmpty()) {
            throw new IllegalArgumentException("pathSortByValue can't be empty for buildMovieListingUrl()");
        }

        if (apiKey.isEmpty()) {
            throw new IllegalArgumentException("apiKey can't be empty for buildMovieListingUrl()");
        }

        // Use the Uri.parse() to build the Uri according to the template
        // the resulting uri is used to generate the URL that we will use to query the TMDB server
        Uri.Builder builder = Uri.parse(TMDB_PATH_BASE_URL).buildUpon()
                .appendPath(TMDB_PATH_MOVIE)
                .appendPath(pathSortByValue)
                .appendQueryParameter(TMDB_QUERY_API_KEY, apiKey); // << Define API KEY in resource

        Uri builtUri = builder.build();

        @Nullable URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "buildMovieListingUrl() from Uri failed due to malformed URL");
            e.printStackTrace();
        }

        // Log the url for debug purposes
        Log.i(TAG, "buildMovieListingUrl() url returns: " + url);

        return url;
    }

    /**
     * Builds the image url from base image url, given the poster's path.
     *
     * @param posterPath the movie entry's poster path
     * @return a image url to the movie's poster image on the TMDB provider
     */
    public static URL buildImageUrl(String posterPath) {
        // Use the Uri.parse() to build the Uri according to the template
        // the resulting uri is used to generate the URL that we will use to query the TMDB image server
        Uri.Builder builder = Uri.parse(TMDB_PATH_BASE_IMAGE_URL).buildUpon()
                .appendPath(TMDB_PATH_IMAGE_SIZE_W500)
                .appendPath(posterPath);

        Uri builtUri = builder.build();
        @Nullable URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "buildImageUrl() from Uri failed due to malformed URL");
            e.printStackTrace();
        }

        // Log the url for debug purposes
        Log.i(TAG, "buildImageUrl url returns: " + url);

        return url;
    }

    /**
     * Builds the url to query the particular movie's associated video keys from the TMDB
     *
     * @param movieId the unique movie id that identities a particular movie on TMDB
     * @param apiKey  the API key to access TMDB API service
     * @return the URL object that is used to queries the TMDB for the movie's video resources.
     */
    public static URL buildVideoKeyUrl(Long movieId, String apiKey) {
        // Convert the movieId to String and check
        final String movieIdStr = String.valueOf(movieId);
        if (movieIdStr == null || movieIdStr.isEmpty()) {
            throw new IllegalArgumentException("PATH_MOVIE_ID can't be empty or null for buildVideoKeyUrl()");
        }

        if (apiKey.isEmpty()) {
            throw new IllegalArgumentException("apiKey can't be empty for buildMovieListingUrl()");
        }

        // Use the Uri.parse() to build the Uri according to the template
        // the resulting uri is used to generate the URL that we will use to query the TMDB server
        // example uri: https://api.themoviedb.org/3/movie/211672/videos?api_key=[key]
        // uri structure :  [BASE_URL] / [PATH_MOVIE] / [PATH_MOVIE_ID] / [PATH_VIDEO] [?Query = Parameter]

        Uri.Builder builder = Uri.parse(TMDB_PATH_BASE_URL).buildUpon()
                .appendPath(TMDB_PATH_MOVIE)
                .appendPath(movieIdStr)
                .appendPath(TMDB_PATH_VIDEO)
                .appendQueryParameter(TMDB_QUERY_API_KEY, apiKey); // << Define API KEY in resource

        Uri builtUri = builder.build();

        @Nullable URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "buildVideoKeyUrl() from Uri failed due to malformed URL");
            e.printStackTrace();
        }

        // Log the url for debug purposes
        Log.i(TAG, "buildVideoKeyUrl() url returns: " + url);

        return url;
    }

    /**
     * Builds the endpoint video url from the base video provider url (in our case Youtube), given
     * the tmdb returned video key, the url would directly go to the provider's resource.
     *
     * @param videoKey the video key that identifies an unique video on our video provider
     *                 this key is retrieved from the TMDB_VIDEO api request.
     * @return an end point URL that points to the video provider's video with the key.
     */
    public static URL buildProviderVideoUrl(String videoKey) {
        // Use the Uri.parse() to build the Uri according to the template
        // the resulting uri is used to generate the URL that we will use to query the video provider's
        // server
        Uri.Builder builder = Uri.parse(TMDB_VID_PROVIDER_YOUTUBE_BASE_VIDEO_URL).buildUpon()
                .appendPath(TMDB_VID_PROVIDER_YOUTUBE_PATH_WATCH)
                .appendQueryParameter(TMDB_VID_PROVIDER_YOUTUBE_QUERY_VIDEO_KEY, videoKey);

        Uri builtUri = builder.build();
        @Nullable URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "buildProviderVideoUrl() from Uri failed due to malformed URL");
            e.printStackTrace();
        }

        // Log the url for debug purposes
        Log.i(TAG, "buildProviderVideoUrl url returns: " + url);

        return url;
    }

    /**
     * Builds the endpoint video's thumbnail url from the base video provider url (in our case Youtube), given
     * the tmdb returned video key, the url would directly go to the provider's resource.
     *
     * @param videoKey the video key that identifies an unique video on our video provider
     *                 this key is retrieved from the TMDB_VIDEO api request.
     * @return an end point URL that points to the video provider's video with the key.
     */
    public static URL buildProviderVideoThumbnailUrl(String videoKey) {
        // Use the Uri.parse() to build the Uri according to the template
        // the resulting uri is used to generate the URL that we will use to query the video provider's
        // server
        // example: https://img.youtube.com/vi/8w3f-RugY60/hqdefault.jpg
        // structure:  base url / video key / image file name qualifier
        Uri.Builder builder = Uri.parse(TMDB_THUMB_PROVIDER_YOUTUBE_BASE_IMG_URL).buildUpon()
                .appendPath(videoKey)
                .appendPath(TMDB_THUMB_PROVIDER_YOUTUBE_IMAGE_FILE_NAME);

        Uri builtUri = builder.build();
        @Nullable URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "buildProviderVideoThumbnailUrl() from Uri failed due to malformed URL");
            e.printStackTrace();
        }

        // Log the url for debug purposes
        Log.i(TAG, "buildProviderVideoThumbnailUrl url returns: " + url);

        return url;
    }


    public static URL buildUserReviewUrl(@NonNull Long movieId, @NonNull String apiKey) {
        // Convert the movieId to String and check
        final String movieIdStr = String.valueOf(movieId);
        if (movieIdStr == null || movieIdStr.isEmpty()) {
            throw new IllegalArgumentException("buildUserReviewUrl() parameter movieId can't be empty or null");
        }

        if (apiKey.isEmpty()) {
            throw new IllegalArgumentException("buildUserReviewUrl() parameter apiKey can't be empty");
        }

        // Use the Uri.parse() to build the Uri according to the template
        // the resulting uri is used to generate the URL that we will use to query the TMDB server
        // example uri: https://api.themoviedb.org/3/movie/211672/reviews?api_key=[key]
        // uri structure :  [BASE_URL] / [PATH_MOVIE] / [PATH_MOVIE_ID] / [PATH_REVIEWS] [?Query = Parameter]

        Uri.Builder builder = Uri.parse(TMDB_PATH_BASE_URL).buildUpon()
                .appendPath(TMDB_PATH_MOVIE)
                .appendPath(movieIdStr)
                .appendPath(TMDB_PATH_REVIEWS)
                .appendQueryParameter(TMDB_QUERY_API_KEY, apiKey); // << Define API KEY in resource

        Uri builtUri = builder.build();

        @Nullable URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "buildUserReviewUrl() from Uri failed due to malformed URL");
            e.printStackTrace();
        }

        // Log the url for debug purposes
        Log.i(TAG, "buildUserReviewUrl() url returns: " + url);

        return url;
    }
    /**
     * This method returns the entire result from the HTTP response.
     * Source: Udacity Sunshine App
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * detectInternetConnection() uses the system service to see if the device is connected
     * to any network with internet access
     *
     * @return boolean flag to indiciate whether the device has internet connection
     */
    public static boolean getNetworkState(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        } else {
            // runtime exception
            throw new RuntimeException("Can't get a reference to the ConnectivityManager");
        }
    }
}
