/*
 * Copyright <2017> <ANDREW LAM>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.andrewclam.popularmovie.sync;

import android.os.AsyncTask;
import android.util.Log;

import com.andrewclam.popularmovie.models.MovieListing;
import com.andrewclam.popularmovie.utilities.NetworkUtils;
import com.andrewclam.popularmovie.utilities.TMDBJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Andrew Chi Heng Lam on 8/31/2017.
 * <p>
 * MovieEntryAsyncTask
 * An implementation of the AsyncTask class to do network IO on a separate thread,
 * (!) Lint suggests to make this class static to avoid memory leak
 */

public class FetchPopularMovieAsyncTask extends AsyncTask<String, Void, ArrayList<MovieListing>> {
    /*Debug Tag*/
    private static final String TAG = FetchPopularMovieAsyncTask.class.getSimpleName();

    /*Listener for callback*/
    private FetchPopularMovieAsyncTask.onMovieEntryTaskInteractionListener mListener;

    private String mApiKey;

    public FetchPopularMovieAsyncTask setListener(FetchPopularMovieAsyncTask.onMovieEntryTaskInteractionListener mListener) {
        this.mListener = mListener;
        return this;
    }

    public FetchPopularMovieAsyncTask setApiKey(String mApiKey) {
        this.mApiKey = mApiKey;
        return this;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mListener.onPreExecute();
    }

    @Override
    protected ArrayList<MovieListing> doInBackground(String... strings) {
        // Get the sortByValue from the strings input
        String sortByValue = strings[0];

        // Init a arrayList to store the parsed movie entries
        ArrayList<MovieListing> entries;

        try {
            // Check for null error, sortByValue should not be null
            if (sortByValue == null) return null;

            // Get the url required by the network util
            URL url = NetworkUtils.buildUrl(sortByValue, mApiKey);

            // Check for null url
            if (url == null) return null;

            // Get httpResponse using the url
            String jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);

            // Check for null response
            if (jsonResponse == null) return null;

            // Got a JsonResponse from the web, parse the jsonResponse using the JsonUtils
            entries = TMDBJsonUtils.getMovieDataFromJson(jsonResponse);

        } catch (IOException e) {
            Log.e(TAG, "MovieEntryAsyncTask - doInBackground - IO Error occurred while getting the jsonResponse from the url");
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            Log.e(TAG, "MovieEntryAsyncTask - doInBackground - JSONException occurred while parsing the jsonResponse into model class");
            e.printStackTrace();
            return null;
        }

        // return the entries
        return entries;
    }

    @Override
    protected void onPostExecute(ArrayList<MovieListing> entries) {
        super.onPostExecute(entries);
        mListener.onPostExecute(entries);
    }

    /**
     * Interface for callback to the listener at stages where UI change is required
     * in preExecute and postExecute.
     */
    public interface onMovieEntryTaskInteractionListener {
        void onPreExecute();

        void onPostExecute(ArrayList<MovieListing> entries);
    }
}
