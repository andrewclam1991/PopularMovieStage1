package com.andrewclam.popularmovie.data.api;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.andrewclam.popularmovie.data.model.Movie;
import com.andrewclam.popularmovie.data.model.RelatedVideo;
import com.andrewclam.popularmovie.data.model.UserReview;
import com.google.common.base.Strings;

import java.net.URL;

/**
 * Defines a set of constants against the TMDB APIs
 */
public class TMDBApiContract {

  /**
   * Contract against the TMDB Movies API
   * Note: requires a developer service api key
   * see https://www.themoviedb.org/documentation/api
   */
  public static class TMDBContract {
    private static final String SCHEME = "https";
    private static final String AUTHORITY = "api.themoviedb.org/3";

    // Paths
    private static final String PATH_MOVIE = "movies";
    private static final String PATH_VIDEO = "videos";
    private static final String PATH_REVIEWS = "reviews";

    // Query parameter keys
    private static final String QUERY_API_KEY = "api_key";

    /**
     * Builds a compliant request {@link Uri} for a list of {@link Movie}s base on the template
     * https://api.themoviedb.org/3/discover/movie?api_key=<<YOUR_API_KEY>>&sort_by=popularity.desc
     * <p>
     * the request would:
     * discovers (gets) movies
     * and given the api key value
     * (optional)
     * and sort result base on popularity
     * and outputs result in descending order.
     * <p>
     * see https://www.themoviedb.org/documentation/api
     *
     * @param apiKey required developer api key
     * @return a base {@link Uri} that can be built upon, or converted to {@link URL}
     * @throws IllegalArgumentException when {@code apiKey} is not supplied
     */
    public static Uri getBaseMoviesUri(@NonNull String apiKey) throws IllegalArgumentException {
      if (Strings.isNullOrEmpty(apiKey)) {
        throw new IllegalArgumentException("apiKey can't be empty or null");
      }
      Uri.Builder builder = getBaseMovieUriBuilder();
      builder.scheme(SCHEME)
          .authority(AUTHORITY)
          .appendPath(PATH_MOVIE)
          .appendQueryParameter(QUERY_API_KEY, apiKey);

      return builder.build();
    }

    public static Uri.Builder getBaseMovieUriBuilder(){
      Uri.Builder builder = new Uri.Builder();
      return builder.scheme(SCHEME)
          .authority(AUTHORITY)
          .appendPath(PATH_MOVIE);
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
  public static class TMDBImageContract {
    private static final String SCHEME = "https";
    private static final String AUTHORITY = "https://image.tmdb.org/";

    // Paths
    // TODO need documentation from service api
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
    public static Uri getMoviePosterImageUri(@NonNull String posterPath) throws IllegalArgumentException {
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
