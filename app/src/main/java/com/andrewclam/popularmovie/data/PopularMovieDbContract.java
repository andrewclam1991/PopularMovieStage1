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

import android.net.Uri;
import android.provider.BaseColumns;

import static com.andrewclam.popularmovie.data.PopularMovieDbContract.PopularMovieEntry.CONTENT_URI;

/**
 * Defines table and column names for the movie entry database. this contract class keeps
 * the SQLite code organized and easier to maintain
 */
public class PopularMovieDbContract {

    /*
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website. A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * Play Store.
     */
    public static final String CONTENT_AUTHORITY = "com.andrewclam.popularmovie";

    /*
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider for PopularMovie.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /*
     * Possible paths that can be appended to BASE_CONTENT_URI to form valid URI's that Sunshine
     * can handle. For instance,
     *
     *     content://com.andrewclam.popularmovie/favorites/
     *     [           BASE_CONTENT_URI         ][ PATH_FAVORITES ]
     *
     * is a valid path for looking at weather data.
     */
    public static final String PATH_MOVIES = "movies";

    public static final String PATH_FAVORITES = "favorites";
//
//    /**
//     * Returns just the selection part of the query from a unique movie id
//     * This is used to get the Uri of the movie from the database given its unique movie id
//     *
//     * @param id the unique movie's id as loaded from TMDB
//     * @return a Uri that points to the particular movie with the id
//     */
//    public static Uri buildMovieUriWithId(long id) {
//        return CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build();
//    }

    /**
     * Easy to use helper method to build the uri that points to a specific movie given its unique
     * movie id
     *
     * @param movieId the unique movie id as fetched from TMDB)
     * @return an Uri that points to that particular movie on the user's database
     */
    public static Uri buildMovieUriWithId(Long movieId) {
        String movieIdStr = String.valueOf(movieId);
        return CONTENT_URI.buildUpon().appendPath(movieIdStr).build();
    }

    /* Inner class that defines the table contents of the movie table */
    public static final class PopularMovieEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the movie table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIES)
                .build();

        /* Used internally as the name of our database table table. */
        public static final String TABLE_NAME = "movie_listing_tb";

        /* Unique id that identifies a particular movie on the TMDB*/
        public static final String COLUMN_MOVIE_ID = "id";

        /* Movie's title */
        public static final String COLUMN_TITLE = "title";

        /* Movie Release Date*/
        public static final String COLUMN_RELEASE_DATE = "release_date";

        /* Movie's image poster path*/
        public static final String COLUMN_POSTER_PATH = "poster_path";

        /* Movie's average vote rating */
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        /* Movie's rating's vote count*/
        public static final String COLUMN_VOTE_COUNT = "vote_count";

        /* Movie's overview, synopsis*/
        public static final String COLUMN_OVERVIEW = "overview";

        /* Movie's popularity index */
        public static final String COLUMN_POPULARITY = "popularity";

        /* Movie's favorite status, client toggle this selection */
        public static final String COLUMN_FAVORITE = "favorite";

        /* Constant String for boolean COLUMN Favorite*/
        public static final String SELECTION_ARG_MOVIE_FAVORITE_TRUE = "1";
        public static final String SELECTION_ARG_MOVIE_FAVORITE_FALSE = "0";
    }
}
