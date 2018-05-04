package com.andrewclam.popularmovie.data.modelapi;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.common.base.Strings;

/**
 * Contract against the TMDB Image API
 */
public final class MovieImageContract {
  // Note: Contract class should never be instantiated
  private MovieImageContract() {
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
  public static String getImageUrl(@NonNull String posterPath)
      throws IllegalArgumentException {
    if (Strings.isNullOrEmpty(posterPath)) {
      throw new IllegalArgumentException("posterPath can't be empty or null");
    }

    Uri.Builder builder = BASE_TMDB_IMAGE_REQUEST_URI.buildUpon();
    builder.appendPath(PATH_T)
        .appendPath(PATH_P)
        .appendPath(PATH_IMAGE_SIZE_W500)
        .appendPath(posterPath);

    return builder.build().toString();

  }
}
