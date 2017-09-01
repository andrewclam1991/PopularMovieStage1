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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
     * This method parses JSON from a web response and returns an ArrayList of Movie Entry objects
     *
     * @param jsonResponse JSON response from server
     * @return an ArrayList of Movie objects, each containing the movie's data
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
                String overView = result.getString(TMDB_OVERVIEW);

                /* Store each element into the data model class */
                entry.setId(id);
                entry.setTitle(title);
                entry.setReleaseDate(releaseDate);
                entry.setPosterPath(posterPath);
                entry.setVoteAverage(voteAverage);
                entry.setVoteCount(voteCount);
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
}
