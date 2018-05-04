package com.andrewclam.popularmovie.data.modelapi;

import android.net.Uri;

/**
 * Defines a set of constants against the supported TMDB APIs
 */
public final class BaseContract {

  // Note: Contract class should never be instantiated
  private BaseContract() {
  }

  // Scheme, authority and base content uri
  private static final String SCHEME = "https";
  private static final String AUTHORITY = "api.themoviedb.org";
  private static final String API_VERSION = "3";
  public static final String BASE_TMDB_REQUEST_URL = new Uri.Builder()
      .scheme(SCHEME)
      .authority(AUTHORITY)
      .appendPath(API_VERSION)
      .build().toString();

  // Paths
  public static final class Paths {
    public static final String PATH_DISCOVER = "discover";
    public static final String PATH_MOVIE = "movie";
    protected static final String PATH_VIDEO = "videos";
    protected static final String PATH_REVIEWS = "reviews";
  }

  // Query parameters
  public static final String QUERY_API_KEY = "api_key";
}
