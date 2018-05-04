package com.andrewclam.popularmovie.data.source;

import android.support.annotation.NonNull;

import com.andrewclam.popularmovie.data.model.MovieResponse;
import com.andrewclam.popularmovie.data.model.Movie;
import com.google.common.base.Optional;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Flowable;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

import static com.andrewclam.popularmovie.data.modelapi.BaseContract.Paths.PATH_DISCOVER;
import static com.andrewclam.popularmovie.data.modelapi.BaseContract.Paths.PATH_MOVIE;
import static com.andrewclam.popularmovie.data.modelapi.BaseContract.QUERY_API_KEY;

/**
 * Concrete Implementation of get-only of {@link DataSourceRemote<Movie>}
 */
@Singleton
class DataSourceRemoteMovies extends DataSourceRemote<Movie> {
  /**
   * Interface class that defines the service api for {@link Retrofit#create(Class)};
   */
  interface ApiServiceMovies{
    @NonNull
    @GET(PATH_DISCOVER + "/" + PATH_MOVIE)
    Flowable<MovieResponse<Movie>> getItems(@Query(QUERY_API_KEY) @NonNull String apiKey,
                                            @QueryMap @NonNull Map<String, String> options);

    @NonNull
    @GET(PATH_DISCOVER + "/" + PATH_MOVIE)
    Flowable<MovieResponse<Movie>> getItems(@Query(QUERY_API_KEY) @NonNull String apiKey);
  }

  @NonNull
  private final ApiServiceMovies mApiService;

  @Inject
  DataSourceRemoteMovies(@NonNull ApiServiceMovies apiService) {
    mApiService = apiService;
  }

  @NonNull
  @Override
  public Flowable<List<Movie>> getItems(@NonNull Map<String, String> options) {
    return mApiService.getItems(super.mApiKey,options)
        .flatMap(movieResponse -> Flowable.just(movieResponse.getResults()));
  }

  @NonNull
  @Override
  public Flowable<List<Movie>> getItems() {
    return mApiService.getItems(super.mApiKey)
        .flatMap(movieResponse -> Flowable.just(movieResponse.getResults()));
  }

  // TODO implement get item by id from remote api
  @NonNull
  @Override
  public Flowable<Optional<Movie>> getItem(@NonNull String entityId) {
    return Flowable.empty();
  }
}