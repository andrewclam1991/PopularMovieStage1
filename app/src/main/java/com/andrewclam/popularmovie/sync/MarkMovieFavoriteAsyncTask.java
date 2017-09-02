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

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by Andrew Chi Heng Lam on 8/31/2017.
 * <p>
 * MovieEntryAsyncTask
 * An implementation of the AsyncTask class to do network IO on a separate thread,
 * (!) Lint suggests to make this class static to avoid memory leak
 */

public class MarkMovieFavoriteAsyncTask extends AsyncTask<Void, Void, Boolean> {
    /*Debug Tag*/
    private static final String TAG = MarkMovieFavoriteAsyncTask.class.getSimpleName();

    /*Listener for callback*/
    private MarkMovieFavoriteAsyncTask.OnMarkMovieFavoriteInteractionListener mListener;

    /*Update Uri for the content resolver update method*/
    private Uri mUpdateUri;

    /*Content values*/
    private ContentValues mContentValues;

    /*ContentResolver*/
    private ContentResolver mContentResolver;

    public MarkMovieFavoriteAsyncTask setListener(MarkMovieFavoriteAsyncTask.OnMarkMovieFavoriteInteractionListener mListener) {
        this.mListener = mListener;
        return this;
    }

    public MarkMovieFavoriteAsyncTask setUpdateUri(Uri updateUri) {
        this.mUpdateUri = updateUri;
        return this;
    }

    public MarkMovieFavoriteAsyncTask setContentValues(ContentValues contentValues) {
        this.mContentValues = contentValues;
        return this;
    }

    public MarkMovieFavoriteAsyncTask setContentResolver(ContentResolver contentResolver) {
        this.mContentResolver = contentResolver;
        return this;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Check for required parameter before doInBackground
        String msg;

        if (mContentResolver == null) {
            msg = "must set the mContentResolver for this task.";
            Log.e(TAG, msg);
            throw new IllegalArgumentException(msg);
        }

        if (mUpdateUri == null) {
            msg = "must set the mUpdateUri for this task.";
            Log.e(TAG, msg);
            throw new IllegalArgumentException(msg);
        }

        if (mContentValues == null) {
            msg = "must set the mContentValues for this task.";
            Log.e(TAG, msg);
            throw new IllegalArgumentException(msg);
        }
    }


    @Override
    protected Boolean doInBackground(Void... voids) {
        // Use the contentResolver to update the target uri with the parameter contentValues
        int rowsUpdated = mContentResolver.update(mUpdateUri, mContentValues, null, null);

        // Return a boolean indicating whether the update was successful.
        return rowsUpdated > 0;
    }

    @Override
    protected void onPostExecute(Boolean updateSuccess) {
        super.onPostExecute(updateSuccess);
        mListener.onPostExecute(updateSuccess);

        /*Null the parameter for cleanup*/
        mUpdateUri = null;
        mContentValues = null;
    }

    /**
     * Interface for callback to the listener at stages where UI change is required
     * in preExecute and postExecute.
     */
    public interface OnMarkMovieFavoriteInteractionListener {
        void onPostExecute(Boolean updateSuccess);
    }
}
