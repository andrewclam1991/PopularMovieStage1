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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.andrewclam.popularmovie.data.PopularMovieDbContract;
import com.andrewclam.popularmovie.models.MovieListing;

import java.util.ArrayList;

import static com.andrewclam.popularmovie.data.PopularMovieDbContract.buildMovieUriWithId;

/**
 * Created by Andrew Chi Heng Lam on 8/31/2017.
 * <p>
 * <p>
 * This class contains helper method to sync user's offline database with the TMDB's
 * on a schedule, so user would always have the latest data.
 */

public class PopularMovieDbSync {
    /* Debug Tag*/
    private static final String TAG = PopularMovieDbSync.class.getSimpleName();
    /*
    todo store the sInitialized value in sharedPreferences
     Database initialization flag, to indicate whether the first initial
     download and caching of data occurred.
     */
    private static boolean sInitialized = false;

    // Private no arg constructor
    private PopularMovieDbSync() {
    }

    /**
     * Method to return an ArrayList of MovieListing from the client database to show the data
     * when the user is not online.
     * (!) This method needs to run on a separate thread
     *
     * @param context activity's context required for getting contentResolver()
     */
    public static ArrayList<MovieListing> getEntriesFromDatabase(final Context context) {

        Cursor cursor = context.getContentResolver().query(PopularMovieDbContract.MovieListingEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        if (cursor == null) {
            return null;
        }

        ArrayList<MovieListing> entries = new ArrayList<>();

        while (cursor.moveToNext()) {
            // Create a new entry to store the database
            MovieListing entry = new MovieListing();

            // Get the index of each column from the cursor
            int idColIndex = cursor.getColumnIndex(PopularMovieDbContract.MovieListingEntry.COLUMN_MOVIE_ID);
            int titleColIndex = cursor.getColumnIndex(PopularMovieDbContract.MovieListingEntry.COLUMN_TITLE);
            int releaseDateColIndex = cursor.getColumnIndex(PopularMovieDbContract.MovieListingEntry.COLUMN_RELEASE_DATE);
            int posterPathColIndex = cursor.getColumnIndex(PopularMovieDbContract.MovieListingEntry.COLUMN_POSTER_PATH);
            int voteAvgColIndex = cursor.getColumnIndex(PopularMovieDbContract.MovieListingEntry.COLUMN_VOTE_AVERAGE);
            int voteCountColIndex = cursor.getColumnIndex(PopularMovieDbContract.MovieListingEntry.COLUMN_VOTE_COUNT);
            int popularityColIndex = cursor.getColumnIndex(PopularMovieDbContract.MovieListingEntry.COLUMN_POPULARITY);
            int overviewColIndex = cursor.getColumnIndex(PopularMovieDbContract.MovieListingEntry.COLUMN_OVERVIEW);
            int favColIndex = cursor.getColumnIndex(PopularMovieDbContract.MovieListingEntry.COLUMN_FAVORITE);

            // Set each field to the entry
            entry.setId(cursor.getLong(idColIndex));
            entry.setTitle(cursor.getString(titleColIndex));
            entry.setReleaseDate(cursor.getString(releaseDateColIndex));
            entry.setPosterPath(cursor.getString(posterPathColIndex));
            entry.setVoteAverage(cursor.getDouble(voteAvgColIndex));
            entry.setVoteCount(cursor.getLong(voteCountColIndex));
            entry.setPopularity(cursor.getDouble(popularityColIndex));
            entry.setMarkedFavorite(cursor.getInt(favColIndex) == 1);
            entry.setOverview(cursor.getString(overviewColIndex));

            // Add the populated entry into the entry list
            entries.add(entry);
        }

        // Close the cursor after the loop
        cursor.close();

        return entries;
    }

    /**
     * syncDatabase takes in the freshly downloaded entry and see if the local database needs an
     * update or create new entries.
     *
     * @param context application context
     * @param entries the downloaded entries, MovieListings
     */
    public static void syncDatabase(final Context context, @Nullable final ArrayList<MovieListing> entries) {
        // Check if the entries is null or empty, return
        if (entries == null || entries.isEmpty()) {
            return;
        }

        // Check if the database is initialized, if not run it first
        if (!sInitialized) {
            // run initialization
            initDatabase(context, entries);
            return;
        }

        /*********************
         * Local Database Sync
         *********************/
        // For each downloaded entry, see if an entry with the same movie id exists already
        // on the database.

        Thread syncDbThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (MovieListing entry : entries) {
                        // Parse the entry
                        ContentValues contentValues = parseEntry(entry);

                        // load the cursor from using a particular movie id
                        Cursor cursor = context.getContentResolver().query(
                                buildMovieUriWithId(entry.getId()),
                                null,
                                null,
                                null,
                                null);

                        if (cursor == null || cursor.getCount() == 0) {
                            // The cursor doesn't exist, insert an entry in the db
                            context.getContentResolver().insert(PopularMovieDbContract.MovieListingEntry.CONTENT_URI, contentValues);

                        } else if (cursor.moveToNext()) {
                            // The cursor is valid, update the current entry in the db
                            Uri uriToUpdate = buildMovieUriWithId(entry.getId());
                            context.getContentResolver().update(uriToUpdate, contentValues, null, null);

                            // Close up the cursor after update
                            cursor.close();
                        }
                    }
                } catch (Exception ex) {
                    Log.e(TAG, "Exception occurred");
                    ex.printStackTrace();
                }
            }
        });

        // Start the syncDbThread on a separate thread
        syncDbThread.start();

    }

    private static void initDatabase(final Context context, final ArrayList<MovieListing> entries) {
        /*
        * After successfully showing the data, cache the data on a separate thread
        * call contentResolver->contentProvider
        * to bulkInsert the entries into client's database.
        */
        Thread initDbThread = new Thread(new Runnable() {

            @Override
            public void run() {
                // Initialize the contentValuesArray to have the size of all entries
                ContentValues[] contentValuesArray = new ContentValues[entries.size()];
                try {
                    // set a count index for the foreach loop, this index value is for referencing the
                    // correct ContentValue in the ContentValue[] to store each MovieListing entry.

                    int index = 0;
                    for (MovieListing entry : entries) {

                        // Create a new contentValue object to store the entry data
                        ContentValues contentValues = parseEntry(entry);

                        // Assign the constructed contentValues to the contentValuesArray[index]
                        contentValuesArray[index] = contentValues;

                        // increment the index after each completed iteration to access the next array
                        index++;
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                } finally {
                    // use the contentResolver bulkInsert to insert all the cv values
                    context.getContentResolver().bulkInsert(PopularMovieDbContract.MovieListingEntry.CONTENT_URI, contentValuesArray);
                }
            }

        });

        // Start the initDbThread on a separate thread
        initDbThread.start();
    }

    /**
     * parseEntry takes an movie listing entry and creates an array of ContentValues
     * that is used for insert, update and bulkInsert
     * Just A sub method to reduce duplicates
     *
     * @param entry a movie listing entry model object
     * @return a contentValue representation of the model object
     */
    private static ContentValues parseEntry(MovieListing entry) {
        // Create a new contentValue object to store the entry data
        ContentValues contentValues = new ContentValues();

        contentValues.put(PopularMovieDbContract.MovieListingEntry.COLUMN_MOVIE_ID, entry.getId());
        contentValues.put(PopularMovieDbContract.MovieListingEntry.COLUMN_TITLE, entry.getTitle());
        contentValues.put(PopularMovieDbContract.MovieListingEntry.COLUMN_RELEASE_DATE, entry.getReleaseDate());
        contentValues.put(PopularMovieDbContract.MovieListingEntry.COLUMN_POSTER_PATH, entry.getPosterPath());
        contentValues.put(PopularMovieDbContract.MovieListingEntry.COLUMN_VOTE_AVERAGE, entry.getVoteAverage());
        contentValues.put(PopularMovieDbContract.MovieListingEntry.COLUMN_VOTE_COUNT, entry.getVoteCount());
        contentValues.put(PopularMovieDbContract.MovieListingEntry.COLUMN_OVERVIEW, entry.getOverview());
        contentValues.put(PopularMovieDbContract.MovieListingEntry.COLUMN_POPULARITY, entry.getPopularity());

        return contentValues;
    }
}
