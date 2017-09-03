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

import com.andrewclam.popularmovie.models.UserReview;
import com.andrewclam.popularmovie.utilities.NetworkUtils;
import com.andrewclam.popularmovie.utilities.TMDBJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Andrew Chi Heng Lam on 8/31/2017.
 * <p>
 * FetchRelatedVideoAsyncTask
 * An implementation of the AsyncTask class to do network IO on a separate thread.
 */

public class FetchUserReviewAsyncTask extends AsyncTask<Void, Void, ArrayList<UserReview>> {
    /*Debug Tag*/
    private static final String TAG = FetchUserReviewAsyncTask.class.getSimpleName();

    /*Listener for callback, optional*/
    private OnFetchVideoInfoCompleteListener mListener;

    /*Instance vars*/
    private String mApiKey;
    private Long mMovieId;

    /**
     * no-args constructor
     */
    public FetchUserReviewAsyncTask() {
    }

    /**
     * full-args constructor
     *
     * @param mApiKey  the TMDB APi key
     * @param mMovieId the particular movie's unique id on TMDB
     */
    public FetchUserReviewAsyncTask(String mApiKey, Long mMovieId) {
        this.mApiKey = mApiKey;
        this.mMovieId = mMovieId;
    }

    public FetchUserReviewAsyncTask setListener(OnFetchVideoInfoCompleteListener mListener) {
        this.mListener = mListener;
        return this;
    }

    public FetchUserReviewAsyncTask setApiKey(String mApiKey) {
        this.mApiKey = mApiKey;
        return this;
    }

    public FetchUserReviewAsyncTask setMovieId(Long mMovieId) {
        this.mMovieId = mMovieId;
        return this;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Check for required parameter before doInBackground
        String msg = "";
        boolean hasError = false;

        if (this.mApiKey == null) {
            hasError = true;
            msg = msg.concat("Must set the mApiKey for this task." + "\n");
        }

        if (this.mMovieId == null) {
            hasError = true;
            msg = msg.concat("Must set the mMovieId for this task." + "\n");
        }

        if (hasError) {
            Log.e(TAG, msg);
            throw new IllegalArgumentException(msg);
        }
    }

    @Override
    protected ArrayList<UserReview> doInBackground(Void... voids) {
        // Init a arrayList to store the parsed movie video info entries
        ArrayList<UserReview> entries;

        try {
            // Get the url required by the network util
            URL url = NetworkUtils.buildVideoKeyUrl(mMovieId, mApiKey);

            // Check for null url
            if (url == null) return null;

            // Get httpResponse using the url
            String jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);

            // Check for null response
            if (jsonResponse == null) return null;

            // Got a JsonResponse from the web, parse the jsonResponse using the JsonUtils
            entries = TMDBJsonUtils.getUserReviewsFromJson(jsonResponse);

        } catch (IOException e) {
            Log.e(TAG, "FetchRelatedVideoAsyncTask - doInBackground - IO Error occurred while getting the jsonResponse from the url");
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            Log.e(TAG, "FetchRelatedVideoAsyncTask - doInBackground - JSONException occurred while parsing the jsonResponse into model class");
            e.printStackTrace();
            return null;
        }

        // return the entries
        return entries;
    }

    @Override
    protected void onPostExecute(ArrayList<UserReview> entries) {
        super.onPostExecute(entries);
        if (mListener != null) mListener.onComplete(entries);
    }

    /**
     * Interface for callback to the listener at stages where UI change is required
     * in preExecute and postExecute.
     */
    public interface OnFetchVideoInfoCompleteListener {
        void onComplete(ArrayList<UserReview> entries);
    }
}
