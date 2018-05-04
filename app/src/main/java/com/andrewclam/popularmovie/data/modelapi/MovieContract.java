package com.andrewclam.popularmovie.data.modelapi;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Contract against the TMDB Discover/Movies API
 * see https://developers.themoviedb.org/3/discover/movie-discover
 * <p>
 * Discover movies by different types of data like average rating, number of votes,
 * genres and certifications.
 */
public final class MovieContract {

  /**
   * {@link DiscoverMoviesParam}
   * <p>
   * Defines a list of app's supported api query parameters and its supported values
   * for the service api.
   */
  public static final class DiscoverMoviesParam {
    public static final String QUERY_SORT_BY_KEY = "sort_by";
    public static final String QUERY_PAGE_KEY = "page";

    @StringDef({QUERY_SORT_BY_KEY, QUERY_PAGE_KEY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {}
  }

  /**
   * {@link SortByArg} Defines a list of allowed values for
   * {@link DiscoverMoviesParam#QUERY_SORT_BY_KEY}, its default argument
   * is {@link SortByArg#POPULARITY_DESC}
   */
  public static final class SortByArg {
    public static final String POPULARITY_DESC = "popularity.desc";
    public static final String POPULARITY_ASC = "popularity.asc";
    public static final String VOTE_AVERAGE_DESC = "vote_average.desc";
    public static final String VOTE_AVERAGE_ASC = "vote_average.asc";
    public static final String DEFAULT = "popularity.desc";
    /**
     * StringDef to enforce {@link SortByArg} type inputs
     */
    @StringDef({POPULARITY_DESC, POPULARITY_ASC, VOTE_AVERAGE_DESC, VOTE_AVERAGE_ASC, DEFAULT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {}
  }
}
