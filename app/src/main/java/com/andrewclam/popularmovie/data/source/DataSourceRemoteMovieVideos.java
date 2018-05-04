package com.andrewclam.popularmovie.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.andrewclam.popularmovie.data.model.Movie;
import com.andrewclam.popularmovie.data.model.MovieVideo;
import com.google.common.base.Optional;
import com.google.common.base.Strings;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Flowable;

/**
 * Concrete Implementation of get-only of {@link DataSourceRemote<Movie>}
 */
@Singleton
class DataSourceRemoteMovieVideos extends DataSourceRemote<MovieVideo> {

  @NonNull
  private final MovieVideo.ServiceApi mApiService;

  @Inject
  DataSourceRemoteMovieVideos(@NonNull MovieVideo.ServiceApi apiService) {
    mApiService = apiService;
  }

  @NonNull
  @Override
  public Flowable<List<MovieVideo>> getItems(@NonNull Map<String, String> options) {
    @Nullable
    String movieId = options.get(MovieVideo.QueryConstants.ARG_MOVIE_ID);

    if (Strings.isNullOrEmpty(movieId)){
      return Flowable.error(new IllegalArgumentException("must supply a non-null movie-id argument"));
    }else {
      return mApiService.getMovieVideos(super.mApiKey, Integer.parseInt(movieId))
          .flatMap(response -> Flowable.just(response.getResults()));
    }
  }

  @NonNull
  @Override
  public Flowable<List<MovieVideo>> getItems() {
    return Flowable.error(new UnsupportedOperationException("getItems() is not supported"));
  }

  @NonNull
  @Override
  public Flowable<Optional<MovieVideo>> getItem(@NonNull String entityId) {
    return Flowable.error(new UnsupportedOperationException("getItem(String) is not supported"));
  }
}