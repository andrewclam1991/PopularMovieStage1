package com.andrewclam.popularmovie.data.api;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.util.Pair;
import android.view.View;

import com.andrewclam.popularmovie.data.model.Movie;
import com.andrewclam.popularmovie.data.model.RelatedVideo;
import com.andrewclam.popularmovie.data.model.UserReview;
import com.google.common.base.Strings;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URL;
import java.util.List;

import static com.andrewclam.popularmovie.data.api.TMDBApiContract.TMDBContract.DiscoverMovie.SortByArg.POPULARITY_ASC;
import static com.andrewclam.popularmovie.data.api.TMDBApiContract.TMDBContract.DiscoverMovie.SortByArg.POPULARITY_DESC;
import static com.andrewclam.popularmovie.data.api.TMDBApiContract.TMDBContract.DiscoverMovie.SortByArg.VOTE_AVERAGE_ASC;
import static com.andrewclam.popularmovie.data.api.TMDBApiContract.TMDBContract.DiscoverMovie.SortByArg.VOTE_AVERAGE_DESC;

/**
 * Defines a set of constants against the supported TMDB APIs
 * - {@link TMDBContract} Defines the contract constants against the TMDB Movies API
 * - {@link TMDBImageContract} Defines the contract constants against the Image database of TMDB
 */
public final class TMDBApiContract {

  /**
   * Contract against the TMDB Movies API
   * Note: requires a developer service api key, set by {@link #QUERY_API_KEY}
   * see https://www.themoviedb.org/documentation/api
   */
  public static final class TMDBContract {
    // Note: Contract class should never be instantiated
    private TMDBContract() {
    }

    // Scheme, authority and base content uri
    private static final String SCHEME = "https";
    private static final String AUTHORITY = "api.themoviedb.org";
    private static final String API_VERSION = "3";
    private static final Uri BASE_TMDB_REQUEST_URI = new Uri.Builder()
        .scheme(SCHEME)
        .authority(AUTHORITY)
        .appendPath(API_VERSION)
        .build();

    // Paths
    static final String PATH_DISCOVER = "discover";
    static final String PATH_MOVIE = "movies";
    static final String PATH_VIDEO = "videos";
    static final String PATH_REVIEWS = "reviews";

    // Query parameters
    static final String QUERY_API_KEY = "api_key";

    /**
     * {@link DiscoverMovie}
     * see https://developers.themoviedb.org/3/discover/movie-discover
     * Discover movies by different types of data like average rating, number of votes,
     * genres and certifications.
     * <p>
     * Defines a list of app's supported api query parameters and its supported values
     * for the service api.
     */
    public static final class DiscoverMovie {
      static final Uri REQUEST_URI_DISCOVER_MOVIE = BASE_TMDB_REQUEST_URI.buildUpon()
          .appendPath(PATH_DISCOVER).appendPath(PATH_MOVIE).build();

      public static final String QUERY_SORT_BY_KEY = "sort_by";
      public static final String QUERY_PAGE = "page";

      /**
       * {@link SortByArg} Defines a list of allowed values for {@link #QUERY_SORT_BY_KEY},
       * its default argument is {@link SortByArg#POPULARITY_DESC}
       */
      @StringDef({POPULARITY_DESC, POPULARITY_ASC, VOTE_AVERAGE_DESC, VOTE_AVERAGE_ASC})
      @Retention(RetentionPolicy.SOURCE)
      public @interface SortByArgument {}

      public static final class SortByArg {
        public static final String POPULARITY_DESC = "popularity.desc";
        public static final String POPULARITY_ASC = "popularity.asc";
        public static final String VOTE_AVERAGE_DESC = "vote_average.desc";
        public static final String VOTE_AVERAGE_ASC = "vote_average.asc";
      }

      /**
       * Builds a compliant request {@link Uri} for using {@link DiscoverMovie} api base on the
       * template:
       * https://api.themoviedb.org/3/discover/movie?api_key=<<YOUR_API_KEY>>&sort_by=popularity.desc
       * <p>
       * the request would:
       * - GET /discover/movie
       * - and then given the api key value
       * - and then sort result base on popularity
       * - and then return result in descending order.
       * <p>
       * see https://developers.themoviedb.org/3/discover/movie-discover
       *
       * @param apiKey required developer api key
       * @return a base {@link Uri} that can be built upon, or converted to {@link URL}
       * @throws IllegalArgumentException when {@code apiKey} is not supplied
       */
      @NonNull
      public static Uri getRequestUri(@NonNull String apiKey) throws IllegalArgumentException {
        if (Strings.isNullOrEmpty(apiKey)) {
          throw new IllegalArgumentException("apiKey can't be empty or null");
        }
        Uri.Builder builder = REQUEST_URI_DISCOVER_MOVIE.buildUpon();
        builder.appendQueryParameter(QUERY_API_KEY, apiKey);

        return builder.build();
      }

      @NonNull
      public static Uri getRequestUriWithParams(@NonNull String apiKey,
                                                @Nullable List<Pair<String, String>> params) {
        if (Strings.isNullOrEmpty(apiKey)) {
          throw new IllegalArgumentException("apiKey can't be empty or null");
        }
        Uri.Builder builder = getRequestUri(apiKey).buildUpon();
        for (Pair<String, String> param : params) {
          builder.appendQueryParameter(param.first, param.second);
        }
        return builder.build();
      }
    }


    /**
     * Builds a compliant request {@link Uri} for a single {@link Movie}'s list of
     * {@link RelatedVideo}s keys according to the following template:
     * <p>
     * https://api.themoviedb.org/3/movie/211672/videos?api_key=[key]
     * [SCHEME] [AUTHORITY] / [PATH_MOVIE] / [PATH_MOVIE_ID] / [PATH_VIDEO] [?Query = Argument]
     *
     * @param apiKey  required developer api key
     * @param movieId the unique id that represents a {@link Movie} at the service api
     * @return a base {@link Uri} that can be built upon, or converted to {@link URL}
     * @throws IllegalArgumentException when {@code apiKey} is not supplied,
     */
    public static Uri getMovieRelatedVidoesKeysUri(@NonNull String apiKey, @NonNull Long movieId)
        throws IllegalArgumentException {
      if (Strings.isNullOrEmpty(apiKey)) {
        throw new IllegalArgumentException("apiKey can't be empty or null");
      }

      Uri.Builder builder = new Uri.Builder();
      builder.scheme(SCHEME)
          .authority(AUTHORITY)
          .appendPath(PATH_MOVIE)
          .appendPath(String.valueOf(movieId))
          .appendPath(PATH_VIDEO)
          .appendQueryParameter(QUERY_API_KEY, apiKey);

      return builder.build();
    }

    /**
     * Builds a compliant request {@link Uri} for a single {@link Movie}'s list of
     * {@link UserReview}s according to the following template:
     * <p>
     * https://api.themoviedb.org/3/movie/211672/reviews?api_key=[key]
     * [SCHEME] [AUTHORITY] / [PATH_MOVIE] / [PATH_MOVIE_ID] / [PATH_REVIEWS] [?Query = Argument]
     *
     * @param apiKey  required developer api key
     * @param movieId the unique id that represents a {@link Movie} at the service api
     * @return a base {@link Uri} that can be built upon, or converted to {@link URL}
     * @throws IllegalArgumentException when {@code apiKey} is not supplied,
     */
    public static Uri getMovieUserReviewsUri(@NonNull String apiKey, @NonNull Long movieId)
        throws IllegalArgumentException {
      if (Strings.isNullOrEmpty(apiKey)) {
        throw new IllegalArgumentException("apiKey can't be empty or null");
      }

      Uri.Builder builder = new Uri.Builder();
      builder.scheme(SCHEME)
          .authority(AUTHORITY)
          .appendPath(PATH_MOVIE)
          .appendPath(String.valueOf(movieId))
          .appendPath(PATH_REVIEWS)
          .appendQueryParameter(QUERY_API_KEY, apiKey);

      return builder.build();
    }
  }

  /**
   * Contract against the TMDB Image API
   * see https://www.themoviedb.org/documentation/api
   */
  public static final class TMDBImageContract {
    // Note: Contract class should never be instantiated
    private TMDBImageContract() {
    }

    private static final String SCHEME = "https";
    private static final String AUTHORITY = "https://image.tmdb.org/";
    private static final Uri BASE_TMDB_IMAGE_REQUEST_URI = new Uri.Builder()
        .scheme(SCHEME)
        .authority(AUTHORITY)
        .build();

    // Paths
    private static final String PATH_T = "t";
    private static final String PATH_P = "p";
    private static final String PATH_IMAGE_SIZE_W500 = "w500";

    /**
     * Builds a request string base on the request template
     *
     * @param posterPath the path to the poster image
     * @return a String url
     * @throws IllegalArgumentException when {@code posterPath} is not supplied
     */
    public static Uri getMoviePosterImageUri(@NonNull String posterPath)
        throws IllegalArgumentException {
      if (Strings.isNullOrEmpty(posterPath)) {
        throw new IllegalArgumentException("posterPath can't be empty or null");
      }

      Uri.Builder builder = new Uri.Builder();
      builder.scheme(SCHEME)
          .authority(AUTHORITY)
          .appendPath(PATH_T)
          .appendPath(PATH_P)
          .appendPath(PATH_IMAGE_SIZE_W500)
          .appendPath(posterPath);

      return builder.build();

    }

  }

}
