/*
 * Copyright <2017> <ANDREW LAM>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons
 * to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT
 * OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.andrewclam.popularmovie.data.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Strings;

import static com.andrewclam.popularmovie.data.db.AppDbContract.CONTENT_AUTHORITY;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class is an implementation of a {@link ContentProvider},
 * it exposes CRUD methods to interface with the local SQLite database.
 */
public class AppContentProvider extends ContentProvider {

  /* Log Tag */
  private static final String TAG = AppContentProvider.class.getSimpleName();
  /*
   * Reference a Movie listing dbHelper to get writable/readable databases for each provider
   * method to work with.
   */
  private AppDbHelper mAppDbHelper;
  
  /*
   * These constant will be used to match URIs with the data they are looking for. We will take
   * advantage of the UriMatcher class to make that matching MUCH easier than doing something
   * ourselves, such as using regular expressions.
   */
  private static final int CODE_MOVIE = 100;
  private static final int CODE_MOVIE_WITH_SERVICE_ID = 101;
  private static final int CODE_MOVIE_FAVORITE = 103;

  /*
   * The URI Matcher used by this content provider. The leading "s" in this variable name
   * signifies that this UriMatcher is a static member variable of it is a
   * common convention in Android programming.
   */
  private static final UriMatcher sUriMatcher = buildUriMatcher();
  
  /**
   * Creates the UriMatcher that will match each {@link Uri} to a 
   * @return A UriMatcher that correctly matches the uri to a provided constants
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
     * content://[AUTHORITY]/movies
     */
    matcher.addURI(authority, AppDbContract.PATH_MOVIES, CODE_MOVIE);

    /*
     * content://[AUTHORITY]/movies/uid/[id]
     * Note: the id specified by the service api, it is a numeric id and it is suppose to 
     * uniquely identifies the movie at the service api. Communication with the service api
     * must use this Uri.
     */
    matcher.addURI(authority, AppDbContract.PATH_MOVIES + "/" + AppDbContract.PATH_UID + "/#",
        CODE_MOVIE_WITH_SERVICE_ID);
    
    /*
     * content://[AUTHORITY]/movies/favorite
     */
    matcher.addURI(authority, AppDbContract.PATH_MOVIES + "/" + AppDbContract.PATH_FAVORITES, 
        CODE_MOVIE_FAVORITE);

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
   * Deferred lazy initialization keeps application startup fast, avoids unnecessary work if the
   * provider turns out not to be needed, and stops database errors (such as a full disk) from
   * halting application launch.
   *
   * @return true if the provider was successfully loaded, false otherwise
   */
  @Override
  public boolean onCreate() {
    /*
     * As noted in the comment above, onCreate is run on the main thread, so performing any
     * lengthy operations will cause lag in your app. Since AppDbHelper's constructor is
     * very lightweight, we are safe to perform that initialization here.
     */
    mAppDbHelper = new AppDbHelper(getContext());
    return true;
  }

  /**
   * Insert() implementation to handle single-row data insert into the client database
   *
   * @param uri           the content {@link Uri}
   * @param contentValues contentValues to be inserted into the data
   * @return the newly inserted data row's Uri
   */
  @Nullable
  @Override
  public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
    // Argument sanity check
    if (checkInsertArgs(uri, contentValues)) {
      throw new IllegalArgumentException("invalid arguments for insert()");
    }

    // Gets match id base on the supplied uri using the sUriMatcher
    final int match = sUriMatcher.match(uri);

    // Stores the matched database table
    final String inTables;

    switch (match) {
      // Movies
      case CODE_MOVIE:
        inTables = AppDbContract.MovieEntry.TABLE_NAME;
        break;
      default:
        throw new UnsupportedOperationException("Unknown or Unsupported Uri for Insert()");
    }

    /*
     * Gets an instance of a writable database, then inserts a row into the matched database
     * table (inTables), after insert then releases db resources.
     */
    final SQLiteDatabase db = checkNotNull(mAppDbHelper).getWritableDatabase();
    final long id = db.insert(inTables, null, contentValues);
    db.close();

    // Notify the resolver if the uri has been changed, and return the newly inserted URI
    if (id < 0) {
      throw new SQLException("Failed to insert row into " + uri.toString());
    } else {
      checkNotNull(getContext()).getContentResolver().notifyChange(uri, null);
      return ContentUris.withAppendedId(uri, id);
    }

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
    // Argument sanity check
    checkNotNull(uri, "uri can't be null");

    // Gets match id that would determine which table to bulk insert
    final int match = sUriMatcher.match(uri);

    // Stores the matched database table
    final String inTables;

    switch (match) {
      // Movies
      case CODE_MOVIE:
        inTables = AppDbContract.MovieEntry.TABLE_NAME;
        break;

      default:
        throw new UnsupportedOperationException("Unknown or Unsupported Uri for bulkInsert()");
    }

    /*
     * Gets an instance of a writable database, then starts bulk insert as a single transaction
     * into the matched database table (inTables), after transaction then releases db resources.
     */
    final SQLiteDatabase db = checkNotNull(mAppDbHelper).getWritableDatabase();
    final int rowInserted;
    db.beginTransaction();
    try {
      for (ContentValues value : values) {
        long newID = db.insertOrThrow(inTables, null, value);
        if (newID < 0) {
          throw new SQLException("Failed to insert row into " + uri);
        }
      }
      db.setTransactionSuccessful();
      checkNotNull(getContext()).getContentResolver().notifyChange(uri, null);
      rowInserted = values.length;
    } finally {
      // release db resources
      db.endTransaction();
      db.close();
    }

    return rowInserted;
  }

  /**
   * Handles query requests from clients. We will use this method in PopularMovie to query for all
   * of our movie data as well as to query for the movie listing with a particular id.
   *
   * @param uri           The URI to query
   * @param projection    The list of columns to put into the cursor. If null, all columns are
   *                      included.
   * @param selection     A selection criteria to apply when filtering rows. If null, then all
   *                      rows are included.
   * @param selectionArgs You may include ?s in selection, which will be replaced by
   *                      the values from selectionArgs, in order that they appear in the
   *                      selection.
   * @param sortOrder     How the rows in the cursor should be sorted.
   * @return A Cursor containing the results of the query. In our implementation,
   */
  @Nullable
  @Override
  public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                      @Nullable String[] selectionArgs, @Nullable String sortOrder) {

    // Gets match id that would determine which table and how to query
    final int match = sUriMatcher.match(uri);

    // Stores the matched database table
    final String inTables;

    switch (match) {
      // Movies
      case CODE_MOVIE:
        // Want to get the list of all movies
        inTables = AppDbContract.MovieEntry.TABLE_NAME;
        break;

      case CODE_MOVIE_FAVORITE:
        // Want to get the list of all movies that are marked favorite
        inTables = AppDbContract.MovieEntry.TABLE_NAME;
        // TODO move to favorites table
//        selection = AppDbContract.MovieEntry.COLUMN_FAVORITE.concat("=?");
//        selectionArgs = new String[]{String.valueOf(AppDbContract.MovieEntry.ARG_FAVORITE_IS_TRUE)};
        break;

      case CODE_MOVIE_WITH_SERVICE_ID:
        // Want to get the particular movie by its service id
        inTables = AppDbContract.MovieEntry.TABLE_NAME;
        selection = AppDbContract.MovieEntry.COLUMN_MOVIE_TMDB_ID.concat("=?");
        selectionArgs = new String[]{uri.getLastPathSegment()};
        break;

      default:
        throw new UnsupportedOperationException("Unknown or Unsupported Uri for query()");
    }

    // Get the readable database using the dbHelper
    final SQLiteDatabase db = mAppDbHelper.getReadableDatabase();

    // Init a cursor for return
    final Cursor cursor;
    cursor = db.query(
        inTables,
        projection,
        selection,
        selectionArgs,
        null,
        null,
        sortOrder
    );

    return cursor;
  }

  /**
   * Update() implementation to allow database to sync and update each movie, and allow user to
   * favorite and un-favorite a movie
   *
   * @param uri           the {@link Uri} that points to some row within a databae
   * @param contentValues the content values that contains columns of data to be updated
   * @param selection     selection for the column to be updated
   * @param selectionArgs the argument for column selection parameter
   * @return the number of rows updated
   */
  @Override
  public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection,
                    @Nullable String[] selectionArgs) {

    if (checkInsertArgs(uri,contentValues)) {
      // No content value to update, return rows updated 0
      return 0;
    }

    // Gets match id that would determine which table and how to update a row
    final int match = sUriMatcher.match(uri);

    // Stores the matched database table(s)
    final String inTables;

    switch (match) {
      case CODE_MOVIE_WITH_SERVICE_ID:
        inTables = AppDbContract.MovieEntry.TABLE_NAME;
        selection = AppDbContract.MovieEntry.COLUMN_MOVIE_TMDB_ID + "=?";
        selectionArgs = new String[]{uri.getLastPathSegment()};
        break;

      default:
        throw new UnsupportedOperationException("Unsupported or unknown Uri for update()");
    }

    /*
     * Gets an instance of a writable database, then starts update with the provided
     * into the matched database table (inTables), then release database source with close()
     */
    final SQLiteDatabase db = mAppDbHelper.getWritableDatabase();
    final int rowsUpdated = db.update(inTables, contentValues, selection, selectionArgs);
    db.close();

    // Check if there are rows updated, notify the content resolver of change if so.
    if (rowsUpdated > 0) {
      checkNotNull(getContext()).getContentResolver().notifyChange(uri, null);
    }
    // Return the number of rows updated, positive number indicate rows updated
    return rowsUpdated;
  }

  /**
   * delete() is not supported in this app
   */
  @Override
  public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
    throw new UnsupportedOperationException("delete() is not supported");
  }

  @Nullable
  @Override
  public String getType(@NonNull Uri uri) {
    return null;
  }

  /**
   * Helper method to check {@link #insert(Uri, ContentValues)} or
   * {@link #update(Uri, ContentValues, String, String[])}'s provided arg(s)
   *
   * @param uri           arg uri for {@link #insert(Uri, ContentValues)}
   * @param contentValues arg {@link #insert(Uri, ContentValues)}
   * @return true if arg(s) are good, false otherwise
   */
  private boolean checkInsertArgs(@NonNull Uri uri, @Nullable ContentValues contentValues) {
    boolean hasNoErrors = true;
    // check for null, and then check if content is empty
    if (Strings.isNullOrEmpty(checkNotNull(uri, "uri can't be null for insert()")
        .toString())) {
      hasNoErrors = false;
    }

    // Check for null, and then check if it is empty
    if (checkNotNull(contentValues, "contentValues can'ts be null for insert()")
        .size() == 0) {
      hasNoErrors = false;
    }
    return !hasNoErrors;
  }
}
