/*
 * Copyright <2017> <ANDREW LAM>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.andrewclam.popularmovie.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.andrewclam.popularmovie.data.MovieListingContract.CONTENT_AUTHORITY;
import static com.andrewclam.popularmovie.data.MovieListingContract.MovieListingEntry.COLUMN_FAVORITE;
import static com.andrewclam.popularmovie.data.MovieListingContract.MovieListingEntry.SELECTION_ARG_MOVIE_FAVORITE_TRUE;
import static com.andrewclam.popularmovie.data.MovieListingContract.MovieListingEntry.TABLE_NAME;

/**
 * Created by Andrew Chi Heng Lam on 8/31/2017.
 * <p>
 * This class serves as the ContentProvider for all of users' offline PopularMovie's data.
 * This class allows us to bulk insert, query, update movie listing.
 */

public class MovieListingProvider extends ContentProvider {
    /*
    * These constant will be used to match URIs with the data they are looking for. We will take
    * advantage of the UriMatcher class to make that matching MUCH easier than doing something
    * ourselves, such as using regular expressions.
    */
    public static final int CODE_MOVIE = 100;
    public static final int CODE_MOVIE_WITH_ID = 101;
    public static final int CODE_MOVIE_FAVORITE = 102;
    /*
    * The URI Matcher used by this content provider. The leading "s" in this variable name
    * signifies that this UriMatcher is a static member variable of WeatherProvider and is a
    * common convention in Android programming.
    */
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    /*
    * Reference a Movie listing dbHelper to get writable/readable databases for each provider
    * method to work with.
     */
    private MovieListingDbHelper mDbHelper;

    /**
     * Creates the UriMatcher that will match each URI to the CODE_MOVIE and
     * CODE_MOVIE_WITH_ID and CODE_MOVIE_FAVORITE constants defined above.
     *
     * @return A UriMatcher that correctly matches the constants for CODE_MOVIE, CODE_MOVIE_WITH_ID
     * and CODE_MOVIE_FAVORITE
     */
    private static UriMatcher buildUriMatcher() {
        /*
         * Init the matcher
         * All paths added to the UriMatcher have a corresponding code to return when a match is
         * found. The code passed into the constructor of UriMatcher here represents the code to
         * return for the root URI. It's common to use NO_MATCH as the code for this case.
         */
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CONTENT_AUTHORITY;

        /*
         This Uri: content://com.andrewclam.popularmovie/movies
         example uses: do operations on the whole movie listing, with user supplied parameters
         projection, selection, selectionArgs and sortOrder
        */
        matcher.addURI(authority, MovieListingContract.PATH_MOVIES, CODE_MOVIE);

        /*
         This Uri: content://com.andrewclam.popularmovie/movies/id
         example uses: do operation on a individual movie listing
        */
        matcher.addURI(authority, MovieListingContract.PATH_MOVIES + "/#", CODE_MOVIE_WITH_ID);

        /*
         This Uri: content://com.andrewclam.popularmovie/movies/favorite
         example uses: query the user's list of favorite movie
        */
        matcher.addURI(authority, MovieListingContract.PATH_MOVIES + "/" + MovieListingContract.PATH_FAVORITES, CODE_MOVIE_FAVORITE);

        return matcher;
    }

    /**
     * In onCreate, we initialize our content provider on startup. This method is called for all
     * registered content providers on the application main thread at application launch time.
     * It must not perform lengthy operations, or application startup will be delayed.
     * <p>
     * Nontrivial initialization (such as opening, upgrading, and scanning
     * databases) should be deferred until the content provider is used (via {@link #query},
     * {@link #bulkInsert(Uri, ContentValues[])}, etc).
     * <p>
     * Deferred initialization keeps application startup fast, avoids unnecessary work if the
     * provider turns out not to be needed, and stops database errors (such as a full disk) from
     * halting application launch.
     *
     * @return true if the provider was successfully loaded, false otherwise
     */
    @Override
    public boolean onCreate() {
        /*
         * As noted in the comment above, onCreate is run on the main thread, so performing any
         * lengthy operations will cause lag in your app. Since MovieListingDbHelper's constructor is
         * very lightweight, we are safe to perform that initialization here.
         */
        mDbHelper = new MovieListingDbHelper(getContext());
        return true;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        // Use uri matcher, the match determines how we handle the query()
        int match = sUriMatcher.match(uri);

        // Init a cursor for return
        Cursor cursor = null;

        switch (match) {
            case CODE_MOVIE_FAVORITE: {
                // (!) scoped this CODE_MOVIE_FAVORITE case to contain the SQLiteDatabase db var declaration and assignment
                // Get the readable database using the dbHelper
                final SQLiteDatabase db = mDbHelper.getReadableDatabase();

                // Set selection to select favorite, selectionArg handles the "=?" wildcard
                selection = MovieListingContract.MovieListingEntry.COLUMN_FAVORITE + "=?";

                // Set selectionArgs to select movie favorite that is true
                selectionArgs = new String[]{SELECTION_ARG_MOVIE_FAVORITE_TRUE};

                // Return a cursor of movie listings in the database that matches the
                // parameter criteria
                cursor = db.query(
                        MovieListingContract.MovieListingEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
            }
            break;

            case CODE_MOVIE_WITH_ID:
                /* Get the movie id from the Uri, if the uri matches this pattern,
                * the id should be appended at the end of the uri
                */
                String idStr = uri.getLastPathSegment();

                // Set selection to select movie id, selectionArg handles the "=?" wildcard
                selection = MovieListingContract.MovieListingEntry.COLUMN_MOVIE_ID + "=?";

                // Set selectionArgs to Use the idStr as the only argument
                selectionArgs = new String[]{idStr};

                // no break, continue to the shared logic in CODE_MOVIE
            case CODE_MOVIE:
                // Get the readable database using the dbHelper
                final SQLiteDatabase db = mDbHelper.getReadableDatabase();

                // Return a cursor of movie listings in the database that matches the
                // parameter criteria
                cursor = db.query(
                        MovieListingContract.MovieListingEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null, sortOrder
                );
                break;

            default:
                throw new UnsupportedOperationException("Unsupported or unknown Uri for query()");
        }

        return cursor;
    }

    /**
     * Handles requests to insert a set of new rows. In PopularMovie, we are only going to be
     * inserting multiple rows of data at a time from a JSON response from TMDB, which contains many
     * json objects.
     * <p>
     * There is no use case for inserting a single row of data into our ContentProvider, and so we
     * are only going to implement bulkInsert. In a normal ContentProvider's implementation,
     * you will probably want to provide proper functionality for the insert method as well.
     *
     * @param uri    The content:// URI of the insertion request.
     * @param values An array of sets of column_name/value pairs to add to the database.
     *               This must not be {@code null}.
     * @return The number of values that were inserted.
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        // Use uri matcher to make sure the call is pointing to the movie
        int match = sUriMatcher.match(uri);

        switch (match) {
            case CODE_MOVIE:
                // BulkInsert should only work in this case at the /movie path
                // any other case would resort to super's implementation

                // Get a writable database with the dbHelper
                final SQLiteDatabase db = mDbHelper.getWritableDatabase();

                // call beginTransaction() with the SQlite db to begin a potentially
                // long running transaction, remember to call endTransaction() when such transaction
                // is complete.
                db.beginTransaction();

                // Initialize a int to hold the number of rows inserted, this will be the return val
                int rowsInserted = 0;

                // Try-finally to do the operation, finally block should only execute when the try
                // block is complete or throws an error/exception
                try {

                    for (ContentValues value : values) {
                        long _id = db.insert(TABLE_NAME, null, value);
                        if (_id != -1) {
                            // If the insert is successful, increment the rowsInserted by one
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();

                } finally {

                    // Try block op ended, end this db transaction.
                    db.endTransaction();
                }

                // Notify the content resolver of modified dataset if there are rowsInserted
                if (rowsInserted > 0) {
                    if (getContext() != null) getContext().getContentResolver()
                            .notifyChange(uri, null);
                }

                return rowsInserted;
            default:
                // no matching uri found, use parent class's implementation
                return super.bulkInsert(uri, values);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        // Normally, insert function returns the uri to the data that is just inserted
        throw new UnsupportedOperationException("#SadFace, MovieListing Provider doesn't support " +
                "single insert(), use bulkInsert() instead");
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Normally, delete function returns an int of the rowsDeleted

        throw new UnsupportedOperationException("#SadFace, MovieListing Provider doesn't support " +
                "delete()");
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        if (contentValues == null) {
            // No content value, nothing to update, return 0
            return 0;
        }

        /*
        * Use uri matcher to make sure the call is pointing to a particular movie listing
        */
        int match = sUriMatcher.match(uri);

        int rowsUpdated = 0;

        switch (match) {
            case CODE_MOVIE_WITH_ID:
                /*
                * In this implementation we are only allowing user to update the favorite status
                * on a particular movie, each call to update's contentValues must contain a valid
                * favorite bool value. Other contentValues in the parameter are simply ignored at
                * the moment.
                */

                /* Get the movie id from the Uri, if the uri matches this pattern,
                * the id should be appended at the end of the uri
                */
                String idStr = uri.getLastPathSegment();

                // Set selection to select movie id, selectionArg handles the "=?" wildcard
                selection = MovieListingContract.MovieListingEntry.COLUMN_MOVIE_ID + "=?";

                // Set selectionArgs to Use the idStr as the only argument
                selectionArgs = new String[]{idStr};

                // Update only the favorite status, check if it is null before proceeding

                Boolean isMarkFavorite = contentValues.getAsBoolean(COLUMN_FAVORITE);

                if (isMarkFavorite != null) {
                    // create the cv, effectively ignored
                    ContentValues cv = new ContentValues();
                    cv.put(COLUMN_FAVORITE, isMarkFavorite);

                    // (!) Test what is actually put in the cv
                    Log.d("test", "" + cv.get(COLUMN_FAVORITE));

                    // call update on the particular movie with the cv that contains the favorite
                    rowsUpdated = update(uri, cv, selection, selectionArgs);

                } else {
                    throw new IllegalArgumentException("Illegal argument in ContentValues, doesn't " +
                            "contain valid a bool value for favorite ");
                }

                // Notify the content resolver of modified dataset if there are rowsUpdated
                if (rowsUpdated > 0) {
                    if (getContext() != null) getContext().getContentResolver()
                            .notifyChange(uri, null);
                }

                return rowsUpdated;
            default:
                throw new UnsupportedOperationException("Unsupported or unknown Uri for update()");
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
