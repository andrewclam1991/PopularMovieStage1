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

import android.net.Uri;
import android.provider.BaseColumns;

import com.andrewclam.popularmovie.data.model.Movie;

import static com.andrewclam.popularmovie.data.db.AppDbContract.MovieEntry.CONTENT_URI_MOVIES;

/**
 * Defines table and column names for the movie entry database. this contract class keeps
 * the SQLite code organized and easier to maintain
 */
public final class AppDbContract {

  private AppDbContract(){}
  /*
   * The "Content authority" is a name for the entire content provider, similar to the
   * relationship between a domain name and its website. A convenient string to use for the
   * content authority is the package name for the app, which is guaranteed to be unique on the
   * Play Store.
   */
  static final String CONTENT_AUTHORITY = "com.andrewclam.popularmovie";

  /*
   * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
   * the content provider for PopularMovie.
   */
  static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

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
  public static final String PATH_UID = "uid";
  public static final String PATH_FAVORITES = "favorites";

  /**
   * Convenience method that provides a {@link Uri} that points to a specific
   * movie given its unique movie id
   *
   * @param movieId the unique movie id as fetched from TMDB)
   * @return an Uri that points to that particular movie on the user's database
   */
  public static Uri buildMovieUriWithId(Long movieId) {
    String movieIdStr = String.valueOf(movieId);
    return CONTENT_URI_MOVIES.buildUpon().appendPath(movieIdStr).build();
  }

  /**
   * {@link Movie}'s each row entry constants
   */
  public static final class MovieEntry extends MaintainableColumn implements BaseColumns {
    public static final Uri CONTENT_URI_MOVIES =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

    public static final String TABLE_NAME = "movies_table";
    public static final String COLUMN_MOVIE_TMDB_ID = "tmdb_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_RELEASE_DATE = "release_date";
    public static final String COLUMN_POSTER_PATH = "poster_path";
    public static final String COLUMN_VOTE_AVERAGE = "vote_average";
    public static final String COLUMN_VOTE_COUNT = "vote_count";
    public static final String COLUMN_OVERVIEW = "overview";
    public static final String COLUMN_POPULARITY = "popularity";
    public static final String COLUMN_FAVORITE = "favorite";

    /* Valid values for boolean COLUMN Favorite*/
    public static final int ARG_MOVIE_FAVORITE_TRUE = 1;
    public static final int ARG_MOVIE_FAVORITE_FALSE = 0;
  }

  /**
   * All maintainable column with maintenance related flags
   */
  static abstract class MaintainableColumn {
    // delete flag
    public static final String COLUMN_NAME_DELETE_FLAG = "delete_flag";
  }
}
