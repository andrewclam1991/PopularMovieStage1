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


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.andrewclam.popularmovie.data.db.AppDbContract.MovieEntry;
import com.andrewclam.popularmovie.data.db.AppDbContract.MovieFavoriteEntry;
import com.andrewclam.popularmovie.data.model.Movie;

/**
 * Created by Andrew Chi Heng Lam on 8/31/2017.
 * Manages a local database for user's movie data.
 */

public class AppDbHelper extends SQLiteOpenHelper {

  /*
   * This is the name of our database. Database names should be descriptive and end with the
   * .db extension.
   */
  private static final String DATABASE_NAME = "movie_listing.db";

  /*
   * If you change the database schema, you must increment the database version or the onUpgrade
   * method will not be called.
   */
  private static final int DATABASE_VERSION = 1;

  AppDbHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }


  /**
   * SQL Statement to create a table for {@link MovieEntry} rows
   * <p>
   * Note: "_ID" only locally unique identifies a {@link MovieEntry} row in the user's own
   * database, not to be confused with MOVIE_ID, which is the {@link Movie}'s unique
   * id at the service api.
   * <p>
   * Note: To ensure that each {@link MovieEntry} is unique within this table, we will enforce
   * {@link MovieEntry#COLUMN_MOVIE_TMDB_ID} column to be unique with a "ON CONFLICT REPLACE"
   * clause.This clause commands SQLite that given there is an existing {@link MovieEntry},
   * if an attempt is made to insert a {@link MovieEntry} with same id, then SQLite
   * should just replace such row with the new data.
   * <p>
   * TODO Add Constraint to MovieFavorite column, if movie is marked as favorite, should not delete local movie record
   */
  private final static String SQL_CREATE_MOVIES_TABLE =
      "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
          AppDbContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
          AppDbContract.MovieEntry.COLUMN_MOVIE_TMDB_ID + " INTEGER NOT NULL, " +
          AppDbContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
          AppDbContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
          AppDbContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
          AppDbContract.MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
          AppDbContract.MovieEntry.COLUMN_VOTE_COUNT + " INTEGER NOT NULL, " +
          AppDbContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
          AppDbContract.MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
          AppDbContract.MovieEntry.COLUMN_NAME_DELETE_FLAG + " INTEGER NOT NULL, " +
          " UNIQUE (" + MovieEntry.COLUMN_MOVIE_TMDB_ID + ") ON CONFLICT REPLACE)";


  /**
   * TODO SQL Statement to create a table for {@link MovieFavoriteEntry} rows
   */
  private final String SQL_CREATE_MOVIE_FAVORITES_TABLE = "";


  /**
   * Called when the database is created for the first time. This is where the creation of
   * tables and the initial population of the tables should happen.
   *
   * @param sqLiteDatabase The database.
   */
  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
//    sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_FAVORITES_TABLE);
  }

  /**
   * This database is only a cache for online data, so its upgrade policy is simply to discard
   * the data and call through to onCreate to recreate the table. Note that this only fires if
   * you change the version number for your database (in our case, DATABASE_VERSION). It does NOT
   * depend on the version number for your application found in your app/build.gradle file. If
   * you want to update the schema without wiping data, commenting out the current body of this
   * method should be your top priority before modifying this method.
   *
   * @param sqLiteDatabase Database that is being upgraded
   * @param oldVersion     The old database version
   * @param newVersion     The new database version
   */
  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
    onCreate(sqLiteDatabase);
  }
}