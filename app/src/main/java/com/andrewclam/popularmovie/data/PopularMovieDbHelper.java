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


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.andrewclam.popularmovie.data.PopularMovieDbContract.MovieListingEntry;

/**
 * Created by Andrew Chi Heng Lam on 8/31/2017.
 * Manages a local database for user's movie data.
 */

public class PopularMovieDbHelper extends SQLiteOpenHelper {

    /*
     * This is the name of our database. Database names should be descriptive and end with the
     * .db extension.
     */
    public static final String DATABASE_NAME = "movie_listing.db";

    /*
     * If you change the database schema, you must increment the database version or the onUpgrade
     * method will not be called.
     */
    private static final int DATABASE_VERSION = 2;

    public PopularMovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the creation of
     * tables and the initial population of the tables should happen.
     *
     * @param sqLiteDatabase The database.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        /*
         * This String will contain a simple SQL statement that will create a table that will
         * cache our movie data.
         */
        final String SQL_CREATE_WEATHER_TABLE =
                /*
                * Create a table with the given table name in the contract
                * */
                "CREATE TABLE " + MovieListingEntry.TABLE_NAME + " (" +

                        /*
                         * MovieListingEntry did not explicitly declare a column called "_ID". However,
                         * MovieListingEntry implements the interface, "BaseColumns", which does have a
                         * field named "_ID". We use that here to designate our table's primary key.
                         *
                         * (!) This field "_ID" only uniquely identifies the movie entry in the user's own
                         * database, not to be confused with MOVIE_ID, which is the movie's unique id on the
                         * TMDB.
                         */

                        MovieListingEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        MovieListingEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +

                        MovieListingEntry.COLUMN_TITLE + " TEXT NOT NULL, " +

                        MovieListingEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +

                        MovieListingEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +

                        MovieListingEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +

                        MovieListingEntry.COLUMN_VOTE_COUNT + " INTEGER NOT NULL, " +

                        MovieListingEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +

                        MovieListingEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +

                        // Favorite should contain boolean, but SQLite doesn't have this data type
                        // instead, use INTEGER 1 to represent true, and 0 as false, default to false (0)
                        MovieListingEntry.COLUMN_FAVORITE + " INTEGER DEFAULT 0, " +

                        /*
                         * To ensure this table can only contain one movie entry per id, we declare
                         * the movie id column to be unique. We also specify "ON CONFLICT IGNORE". This tells
                         * SQLite that if we have a movie entry for a id and we attempt to
                         * insert another movie entry with that movie id, we just ignore the old movie listing
                         * entry.
                         */

                        " UNIQUE (" + MovieListingEntry.COLUMN_MOVIE_ID + ") ON CONFLICT IGNORE);";

        /*
         * After we've spelled out our SQLite table creation statement above, we actually execute
         * that SQL with the execSQL method of our SQLite database object.
         */
        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);
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
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieListingEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}