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

import android.util.Log;

import com.andrewclam.popularmovie.models.MovieListing;
import com.andrewclam.popularmovie.models.RelatedVideo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Andrew Chi Heng Lam on 8/19/2017.
 * <p>
 * TMDBJsonUtils contain methods to parse the JSON response into individual readable fields, and stores each object
 * in a model class (eg. movie)
 */

public final class TMDBJsonUtils {

    // Log Tag
    private static final String TAG = TMDBJsonUtils.class.getSimpleName();

    /**
     * This method parses JSON from a web response and returns an ArrayList of Movie listing objects
     *
     * @param jsonResponse JSON response from server
     * @return an ArrayList of Movie listing objects, each containing the movie's data
     * @throws JSONException If JSON data cannot be properly parsed
     */

    public static ArrayList<MovieListing> getMovieDataFromJson(String jsonResponse) throws JSONException {
        // Test if the response is null, return null if it is
        if (jsonResponse == null) {
            Log.w(TAG, "Nothing to parse because jsonResponse is undefined");
            return null;
        }

        /* Movie Information. Each movie's info is an element of the "result" array */
        final String TMDB_RESULT = "results";

        /* Start parsing the JSON into objects by defining the fields that we want to fetch from*/

        // Id - Unique id that identifies the movie on the database, might be of use for local SQLite
        final String TMDB_ID = "id";

        // Title
        final String TMDB_TITLE = "title";

        // Release date
        final String TMDB_RELEASE_DATE = "release_date";

        // Poster Path - The path where to look for the thumbnail image
        final String TMDB_POSTER_PATH = "poster_path";

        // Vote Average - The Average Ratings given by users
        final String TMDB_VOTE_AVERAGE = "vote_average";

        // Vote Count - The number of voters
        final String TMDB_VOTE_COUNT = "vote_count";

        // Popularity - the popularity index given the the TMDB
        final String TMDB_POPULARITY = "popularity";

        // Overview - A short description of the movie
        final String TMDB_OVERVIEW = "overview";

        // Initialize an arrayList to store movie objects. This data will back the recycler view adapter.
        ArrayList<MovieListing> movieEntries = new ArrayList<>();

        // Create a new JSON object out of the jsonResponse
        JSONObject resultJSON = new JSONObject(jsonResponse);

        // get the JSON array from the root json object
        JSONArray resultArray = resultJSON.getJSONArray(TMDB_RESULT);

        // Loop through each element result in the resultArray
        for (int i = 0; i < resultArray.length(); i++) {
            /* Get each element of the resultArray as a result element*/
            JSONObject result = resultArray.getJSONObject(i);

            if (result != null) {
                /* Create an instance of the model class to store the retrieved elements */
                MovieListing entry = new MovieListing();

                /* Retrieve each element from the result JSONObject */
                long id = result.getLong(TMDB_ID);
                String title = result.getString(TMDB_TITLE);
                String releaseDate = result.getString(TMDB_RELEASE_DATE);
                String posterPath = result.getString(TMDB_POSTER_PATH).replace("/", "");
                double voteAverage = result.getDouble(TMDB_VOTE_AVERAGE);
                long voteCount = result.getInt(TMDB_VOTE_COUNT);
                double popularity = result.getDouble(TMDB_POPULARITY);
                String overView = result.getString(TMDB_OVERVIEW);

                /* Store each element into the data model class */
                entry.setId(id);
                entry.setTitle(title);
                entry.setReleaseDate(releaseDate);
                entry.setPosterPath(posterPath);
                entry.setVoteAverage(voteAverage);
                entry.setVoteCount(voteCount);
                entry.setPopularity(popularity);
                entry.setOverview(overView);

                /* Add the entry object to the list */
                movieEntries.add(entry);
            } else {
                Log.w(TAG, "Error retrieving the json object at index " + i +
                        ", skipping creating movie entry");
            }
        }

        return movieEntries;
    }

    /**
     * This method parses JSON from a web response and returns an ArrayList of Movie VideoInfo objects
     *
     * @param jsonResponse JSON response from server
     * @return an ArrayList of Movie's video resources objects, each containing a movie's recorded
     * associated video assets;
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static ArrayList<RelatedVideo> getVideoInfoFromJson(String jsonResponse) throws JSONException {
        // Test if the response is null, return null if it is
        if (jsonResponse == null) {
            Log.w(TAG, "Nothing to parse because jsonResponse is undefined");
            return null;
        }

        /* Movie Information. Each movie's info is an element of the "result" array */
        final String TMDB_RESULT = "results";

        /* Start parsing the JSON into objects by defining the fields that we want to fetch from*/

        // Id - Unique id that identifies the video resource on TMDB
        final String TMDB_VIDEO_ID = "id";

        // Video Key, identifies the video at the video provider
        final String TMDB_VIDEO_KEY = "key";

        // Video name
        final String TMDB_VIDEO_NAME = "name";

        // Provider Site - The site where to look for the video with the key
        final String TMDB_VIDEO_PROVIDER_SITE = "site";

        // Size - the size of the video asset
        final String TMDB_VIDEO_SIZE = "size";

        // Type - the video resource type as classified by TMDB
        final String TMDB_VIDEO_TYPE = "type";

        // Initialize an arrayList to store associated video info objects.
        // This data will back the recycler view adapter.
        ArrayList<RelatedVideo> videoInfoEntries = new ArrayList<>();

        // Create a new JSON object out of the jsonResponse
        JSONObject resultJSON = new JSONObject(jsonResponse);

        // get the JSON array from the root json object
        JSONArray resultArray = resultJSON.getJSONArray(TMDB_RESULT);

        // Loop through each element result in the resultArray
        for (int i = 0; i < resultArray.length(); i++) {
            /* Get each element of the resultArray as a result element*/
            JSONObject result = resultArray.getJSONObject(i);

            if (result != null) {
                /* Create an instance of the model class to store the retrieved elements */
                RelatedVideo entry = new RelatedVideo();

                /* Retrieve each element from the result JSONObject */
                String movieId = result.getString(TMDB_VIDEO_ID);
                String key = result.getString(TMDB_VIDEO_KEY);
                String name = result.getString(TMDB_VIDEO_NAME);
                String providerSite = result.getString(TMDB_VIDEO_PROVIDER_SITE);
                int size = result.getInt(TMDB_VIDEO_SIZE);
                String type = result.getString(TMDB_VIDEO_TYPE);

                /* Store each element into the data model class */
                entry.setVideoId(movieId);
                entry.setKey(key);
                entry.setName(name);
                entry.setProviderSite(providerSite);
                entry.setSize(size);
                entry.setVideoType(type);

                /************************
                 * VIDEO URL GENERATION *
                 ************************/
                // Use networkUtility to build the provider video url and set the url in the
                // entry object
                URL videoURL = NetworkUtils.buildProviderVideoUrl(key);
                entry.setVideoUrl(videoURL);

                /**********************************
                 * VIDEO THUMBNAIL URL GENERATION *
                 **********************************/
                // Use networkUtility to build the provider video's thumbnail url and set the url in the
                // entry object
                URL thumbnailUrl = NetworkUtils.buildProviderVideoThumbnailUrl(key);
                entry.setThumbnailUrl(thumbnailUrl);

                /* Add the entry object to the list */
                videoInfoEntries.add(entry);


            } else {
                Log.w(TAG, "Error retrieving the json object at index " + i +
                        ", skipping creating movie video info entry");
            }
        }

        return videoInfoEntries;
    }
}
