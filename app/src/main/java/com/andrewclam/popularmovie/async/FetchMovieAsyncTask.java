/*
 * Copyright <2017> <ANDREW LAM>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.andrewclam.popularmovie.async;

import android.os.AsyncTask;
import android.util.Log;

import com.andrewclam.popularmovie.data.model.Movie;
import com.andrewclam.popularmovie.util.NetworkUtil;
import com.andrewclam.popularmovie.util.TMDBJsonUtil;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Andrew Chi Heng Lam on 9/2/2017.
 * <p>
 * FetchMovieAsyncTask
 * An implementation of the AsyncTask class to do network IO on a separate thread,
 */

public class FetchMovieAsyncTask extends AsyncTask<Void, Void, ArrayList<Movie>> {
    /*Debug Tag*/
    private static final String TAG = FetchMovieAsyncTask.class.getSimpleName();

    /* Listener for callback, optional */
    private FetchMovieAsyncTask.onMovieEntryTaskInteractionListener mListener;

    /* Instance Vars */
    private String mApiKey;
    private String mSortByValue;

    /**
     * no-args default constructor
     */
    public FetchMovieAsyncTask() {
    }

    /**
     * full-args default constructor
     *
     * @param mApiKey      the TMDB APi key
     * @param mSortByValue the user-supplied sortByValue (ListType) for querying TMDB API
     */
    public FetchMovieAsyncTask(String mApiKey, String mSortByValue) {
        this.mApiKey = mApiKey;
        this.mSortByValue = mSortByValue;
    }

    /* Public Setters */
    public FetchMovieAsyncTask setListener(FetchMovieAsyncTask.onMovieEntryTaskInteractionListener mListener) {
        this.mListener = mListener;
        return this;
    }

    public FetchMovieAsyncTask setApiKey(String mApiKey) {
        this.mApiKey = mApiKey;
        return this;
    }

    public FetchMovieAsyncTask setSortByValue(String mSortByValue) {
        this.mSortByValue = mSortByValue;
        return this;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mListener.onPreExecute();

        // Check for required parameter before doInBackground
        String msg = "";
        boolean hasError = false;

        if (this.mApiKey == null) {
            hasError = true;
            msg = msg.concat("Must set the mApiKey for this task." + "\n");
        }

        if (this.mSortByValue == null) {
            hasError = true;
            msg = msg.concat("Must set the mSortByValue for this task." + "\n");
        }

        if (hasError) {
            Log.e(TAG, msg);
            throw new IllegalArgumentException(msg);
        }
    }

    @Override
    protected ArrayList<Movie> doInBackground(Void... voids) {
        // Init a arrayList to store the parsed movie entries
        ArrayList<Movie> entries;

        try {
            // Get the url required by the network util
            URL url = NetworkUtil.buildMovieListingUrl(mSortByValue, mApiKey);

            // Check for null url
            if (url == null) return null;

            // Get httpResponse using the url
            String jsonResponse = NetworkUtil.getResponseFromHttpUrl(url);

            // Check for null response
            if (jsonResponse == null) return null;

            // Got a JsonResponse from the web, parse the jsonResponse using the JsonUtils
            entries = TMDBJsonUtil.getMovieListingFromJson(jsonResponse);

        } catch (IOException e) {
            Log.e(TAG, "FetchMovieAsyncTask - doInBackground - IO Error occurred while getting the jsonResponse from the url");
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            Log.e(TAG, "FetchMovieAsyncTask - doInBackground - JSONException occurred while parsing the jsonResponse into model class");
            e.printStackTrace();
            return null;
        }

        // return the entries
        return entries;
    }

    @Override
    protected void onPostExecute(ArrayList<Movie> entries) {
        super.onPostExecute(entries);
        if (mListener != null) mListener.onPostExecute(entries);
    }

    /**
     * Interface for callback to the listener at stages where UI change is required
     * in preExecute and postExecute.
     */
    public interface onMovieEntryTaskInteractionListener {
        void onPreExecute();

        void onPostExecute(ArrayList<Movie> entries);
    }
}
