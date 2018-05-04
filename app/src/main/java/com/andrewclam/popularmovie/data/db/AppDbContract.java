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
import com.andrewclam.popularmovie.data.model.MovieVideo;

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
   * is a valid path for looking at movie data
   */
  public static final String PATH_MOVIES = "movies";
  public static final String PATH_FAVORITES = "favorites";

  public static final String PATH_UID = "uid";


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
    public static final String COLUMN_MOVIE_TMDB_ID = "tmdb_movie_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_RELEASE_DATE = "release_date";
    public static final String COLUMN_POSTER_PATH = "poster_path";
    public static final String COLUMN_VOTE_AVERAGE = "vote_average";
    public static final String COLUMN_VOTE_COUNT = "vote_count";
    public static final String COLUMN_OVERVIEW = "overview";
    public static final String COLUMN_POPULARITY = "popularity";
    @Deprecated
    public static final String COLUMN_FAVORITE = "is_favorite";

    /* Valid values for boolean COLUMN_FAVORITE*/
    @Deprecated
    public static final int ARG_FAVORITE_IS_TRUE = 1;
    @Deprecated
    public static final int ARG_FAVORITE_IS_FALSE = 0;
  }

  /**
   * {@link MovieVideo}'s each row entry constants
   */
  public static final class MovieVideoEntry extends MaintainableColumn implements BaseColumns {
    public static final Uri CONTENT_URI_MOVIES =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

    public static final String TABLE_NAME = "movie_videos_table";
    public static final String COLUMN_MOVIE_VIDEO_ID = "id";
    public static final String COLUMN_KEY = "key";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SITE = "site";
    public static final String COLUMN_SIZE = "size";
    public static final String COLUMN_TYPE = "type";

    /* Valid values for int COLUMN_VIDEO_SIZE */
    public static final int ARG_SIZE_360 = 360;
    public static final int ARG_SIZE_480 = 480;
    public static final int ARG_SIZE_720 = 720;
    public static final int ARG_SIZE_1080 = 1080;

    /* Valid values for String COLUMN_VIDEO_TYPE */
    public static final String ARG_TYPE_TRAILER = "Trailer";
    public static final String ARG_TYPE_TEASER = "Teaser";
    public static final String ARG_TYPE_CLIP = "Clip";
    public static final String ARG_TYPE_FEATURETTE = "Featurette";
  }

  /**
   * TODO Migrate the MovieEntry#COLUMN_FAVORITE to this entry here, and reference the movie by id
   */
  public static final class MovieFavoriteEntry extends MaintainableColumn implements BaseColumns{
    public static final Uri CONTENT_URI_MOVIE_FAVORITES =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

    public static final String TABLE_NAME = "favorites_table";
    public static final String COLUMN_MOVIE_TMDB_ID = MovieEntry.COLUMN_MOVIE_TMDB_ID;
    public static final String COLUMN_FAVORITE = "is_favorite";

    /* Valid values for boolean COLUMN_FAVORITE*/
    public static final int ARG_FAVORITE_IS_TRUE = 1;
    public static final int ARG_FAVORITE_IS_FALSE = 0;
  }

  /**
   * All maintainable column with maintenance related flags
   */
  static abstract class MaintainableColumn {
    // delete flag
    public static final String COLUMN_NAME_DELETE_FLAG = "delete_flag";
  }
}
