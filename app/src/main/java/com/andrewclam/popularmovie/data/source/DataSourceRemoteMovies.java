package com.andrewclam.popularmovie.data.source;

import android.support.annotation.NonNull;

import com.andrewclam.popularmovie.data.model.ApiResponse;
import com.andrewclam.popularmovie.data.model.Movie;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Flowable;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

import static com.andrewclam.popularmovie.data.source.ApiServiceContract.TMDBContract.Paths.PATH_DISCOVER;
import static com.andrewclam.popularmovie.data.source.ApiServiceContract.TMDBContract.Paths.PATH_MOVIE;
import static com.andrewclam.popularmovie.data.source.ApiServiceContract.TMDBContract.QUERY_API_KEY;

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
    Flowable<ApiResponse<Movie>> getItems(@Query(QUERY_API_KEY) @NonNull String apiKey,
                                          @QueryMap @NonNull Map<String, String> options);

    @NonNull
    @GET(PATH_DISCOVER + "/" + PATH_MOVIE)
    Flowable<ApiResponse<Movie>> getItems(@Query(QUERY_API_KEY) @NonNull String apiKey);

    @NonNull
    @GET(PATH_DISCOVER + "/" + PATH_MOVIE)
    Flowable<ApiResponse<Movie>> getItem(@Query(QUERY_API_KEY) @NonNull String apiKey,
                                         @Query("id") @NonNull String itemId);
  }

  @NonNull
  private final ApiServiceMovies mApiService;

  @Inject
  DataSourceRemoteMovies(@NonNull Retrofit retrofit) {
    mApiService = retrofit.create(ApiServiceMovies.class);
  }

  @NonNull
  @Override
  Flowable<ApiResponse<Movie>> getApiResponse(@NonNull String apiKey) {
    return mApiService.getItems(apiKey);
  }

  @NonNull
  @Override
  Flowable<ApiResponse<Movie>> getApiResponse(@NonNull String apiKey,
                                              @NonNull Map<String, String> options) {
    return mApiService.getItems(apiKey,options);
  }

  @NonNull
  @Override
  Flowable<ApiResponse<Movie>> getApiResponse(@NonNull String apiKey, @NonNull String itemId) {
    return mApiService.getItem(apiKey,itemId);
  }
}