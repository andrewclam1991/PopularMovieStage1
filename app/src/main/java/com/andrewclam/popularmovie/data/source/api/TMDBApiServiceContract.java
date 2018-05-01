package com.andrewclam.popularmovie.data.source.api;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import com.andrewclam.popularmovie.data.model.Movie;
import com.google.common.base.Strings;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

import static com.andrewclam.popularmovie.data.source.api.TMDBApiServiceContract.TMDBContract.Paths.PATH_DISCOVER;
import static com.andrewclam.popularmovie.data.source.api.TMDBApiServiceContract.TMDBContract.Paths.PATH_MOVIE;
import static com.andrewclam.popularmovie.data.source.api.TMDBApiServiceContract.TMDBContract.QUERY_API_KEY;

/**
 * Defines a set of constants against the supported TMDB APIs
 * - {@link TMDBContract} Defines the contract constants against the TMDB Movies API
 * - {@link TMDBImageContract} Defines the contract constants against the Image database of TMDB
 */
public final class TMDBApiServiceContract {

  /**
   * Contract against the TMDB Movies API
   * Note: requires a developer service api key, set by {@link #QUERY_API_KEY}
   * see https://www.themoviedb.org/documentation/api
   */
  static final class TMDBContract {
    // Note: Contract class should never be instantiated
    private TMDBContract() {
    }

    // Scheme, authority and base content uri
    private static final String SCHEME = "https";
    private static final String AUTHORITY = "api.themoviedb.org";
    private static final String API_VERSION = "3";
    static final String BASE_TMDB_REQUEST_URL = new Uri.Builder()
        .scheme(SCHEME)
        .authority(AUTHORITY)
        .appendPath(API_VERSION)
        .build().toString();

    // Paths
    public static final class Paths{
      static final String PATH_DISCOVER = "discover";
      static final String PATH_MOVIE = "movie";
      public static final String PATH_VIDEO = "videos";
      public static final String PATH_REVIEWS = "reviews";
    }

    // Query parameters
    static final String QUERY_API_KEY = "api_key";

  }

  // TODO design movie reviews service api
  public interface MovieReviewsSerivce{

  }

  // TODO design movie related videos service api
  public interface MovieRelatedVideosService{

  }

  /**
   * {@link DiscoverMoviesParam}
   * see https://developers.themoviedb.org/3/discover/movie-discover
   * Discover movies by different types of data like average rating, number of votes,
   * genres and certifications.
   * <p>
   * Defines a list of app's supported api query parameters and its supported values
   * for the service api.
   */
  public static final class DiscoverMoviesParam {
    public static final String QUERY_SORT_BY_KEY = "sort_by";
    public static final String QUERY_PAGE = "page";

    @StringDef({QUERY_SORT_BY_KEY,QUERY_PAGE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {}
  }

  /**
   * {@link SortByArg} Defines a list of allowed values for {@link DiscoverMoviesParam#QUERY_SORT_BY_KEY},
   * its default argument is {@link SortByArg#POPULARITY_DESC}
   */
  public static final class SortByArg {
    public static final String POPULARITY_DESC = "popularity.desc";
    public static final String POPULARITY_ASC = "popularity.asc";
    public static final String VOTE_AVERAGE_DESC = "vote_average.desc";
    public static final String VOTE_AVERAGE_ASC = "vote_average.asc";
    /**
     * StringDef to enforce {@link SortByArg} type inputs
     */
    @StringDef({POPULARITY_DESC, POPULARITY_ASC, VOTE_AVERAGE_DESC, VOTE_AVERAGE_ASC})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {}
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
    private static final String AUTHORITY = "image.tmdb.org";
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
    @NonNull
    public static Uri getMoviePosterImageUri(@NonNull String posterPath)
        throws IllegalArgumentException {
      if (Strings.isNullOrEmpty(posterPath)) {
        throw new IllegalArgumentException("posterPath can't be empty or null");
      }

      Uri.Builder builder = BASE_TMDB_IMAGE_REQUEST_URI.buildUpon();
      builder.appendPath(PATH_T)
          .appendPath(PATH_P)
          .appendPath(PATH_IMAGE_SIZE_W500)
          .appendPath(posterPath);

      return builder.build();

    }

  }

}
