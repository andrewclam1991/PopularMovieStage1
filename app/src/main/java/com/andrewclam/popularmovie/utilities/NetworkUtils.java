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

import android.net.Uri;
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
 * NetworkUtil class contains method for communicating with server on a background thread.
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
    // URL Paths
    // Movie Entry
    private static final String TMDB_PATH_BASE_URL = "https://api.themoviedb.org/3";
    //    private static final String TMDB_PATH_DISCOVER = "discover";
    private static final String TMDB_PATH_MOVIE = "movie";
    // Image
    private static final String TMDB_PATH_BASE_IMAGE_URL = " https://image.tmdb.org/t/p";
    private static final String TMDB_PATH_IMAGE_SIZE_W500 = "w500";

    // Uri Query Parameters
    private static final String TMDB_API_KEY = "api_key";
//    private static final String TMDB_SORT_BY = "sort_by";

    // URL Query Parameter Values - Uncomment to use query parameters
//    public static final String TMDB_NO_SORT_VAL = "no_val";
//    public static final String TMDB_POPULARITY_DESC = "popularity.desc";
//    public static final String TMDB_TOP_RATED_DESC = "vote_average.desc";
//    private static final String TMDB_VOTE_COUNT_GTE = "vote_count.gte"; // *see usage below

    /* Methods */

    /**
     * buildUrl method builds the URL used to communicate with the TMDB server using the user
     * supplied sortByValue.
     *
     * @param sortByValue the user selected sort value
     * @return The URL to use to query the TMDB server with the sort parameter set to the
     * sortByValue.
     */
    public static URL buildUrl(String sortByValue, String apiKey) {
        // Use the Uri.parse() to build the Uri according to the template
        // the resulting uri is used to generate the URL that we will use to query the TMDB server
        Uri.Builder builder = Uri.parse(TMDB_PATH_BASE_URL).buildUpon()
                .appendPath(TMDB_PATH_MOVIE)
                .appendPath(sortByValue)
                .appendQueryParameter(TMDB_API_KEY, apiKey); // << Define API KEY in resource

//  TODO - Use path instead of sort_by_value
//        if (sortByValue != null && !sortByValue.equals(TMDB_NO_SORT_VAL)) {
//            /*
//            * Why use the VOTE_COUNT_GTE?
//            * If sort by value is of type get top rated, also set the vote_count.gte to
//            * some minimum number, this is to avoid a condition where a very obscure rated 10 movie
//            * with a loneWolf vote would quite unfairly show up as top rated.
//            *
//            * Setting vote_count.gte too high would bias the result to popular feature films, too
//            * low would have too small of a sample size. Perhaps let user pick a number?
//            * */
//            if (sortByValue.equals(TMDB_TOP_RATED_DESC)) {
//                builder.appendQueryParameter(TMDB_SORT_BY, TMDB_TOP_RATED_DESC);
//                builder.appendQueryParameter(TMDB_VOTE_COUNT_GTE, "150"); // 150 seems to be a good number
//
//            } else {
//                // For other types of sortByValue
//                // build the query with a sort by value parameter, use it directly
//                builder.appendQueryParameter(TMDB_SORT_BY, sortByValue);
//            }
//        }

        Uri builtUri = builder.build();

        @Nullable URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "buildUrl() from Uri failed due to malformed URL");
            e.printStackTrace();
        }

        // Log the url for debug purposes
        Log.i(TAG, "buildUrl() url returns: " + url);

        return url;
    }

    /**
     * Builds the image url from base image url, given the poster's path.
     *
     * @param posterPath the movie entry's poster path
     * @return a image url to the movie's poster image
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
}
