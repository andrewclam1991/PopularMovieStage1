package com.andrewclam.popularmovie.data.source;

import android.support.annotation.NonNull;

import com.andrewclam.popularmovie.data.model.ApiResponse;
import com.andrewclam.popularmovie.data.model.Movie;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Flowable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

import static com.andrewclam.popularmovie.data.source.ApiServiceContract.TMDBContract.BASE_TMDB_REQUEST_URL;
import static com.andrewclam.popularmovie.data.source.ApiServiceContract.TMDBContract.Paths.PATH_DISCOVER;
import static com.andrewclam.popularmovie.data.source.ApiServiceContract.TMDBContract.Paths.PATH_MOVIE;
import static com.andrewclam.popularmovie.data.source.ApiServiceContract.TMDBContract.QUERY_API_KEY;

/**
 * Concrete Implementation of get-only of {@link DataSourceRemote<Movie>}
 */
@Singleton
class DataSourceRemoteMovies extends DataSourceRemote<Movie> {

  @NonNull
  private final ApiServiceMovies mApiService;

  @NonNull
  private final String mApiKey;

  @Inject
  DataSourceRemoteMovies(@NonNull @ApiKey String apiKey) {
    mApiService = provideRetrofitService();
    mApiKey = apiKey;
  }

  @NonNull
  private ApiServiceMovies provideRetrofitService() {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(BASE_TMDB_REQUEST_URL.concat("/"))
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build();
    return retrofit.create(ApiServiceMovies.class);
  }

  @NonNull
  @Override
  public Flowable<List<Movie>> getItems(@NonNull Map<String, String> options) {
    return mApiService.getItems(mApiKey, options)
        .flatMap(apiResponse -> Flowable.just(apiResponse.getResults()));
  }

  @NonNull
  @Override
  public Flowable<List<Movie>> getItems() {
    return mApiService.getItems(mApiKey)
        .flatMap(apiResponse -> Flowable.just(apiResponse.getResults()));
  }

  /**
   * Interface class that defines the service api for {@link Retrofit#create(Class)};
   */
  private interface ApiServiceMovies{
    @NonNull
    @GET(PATH_DISCOVER + "/" + PATH_MOVIE)
    Flowable<ApiResponse<Movie>> getItems(@Query(QUERY_API_KEY) @NonNull String apiKey,
                                          @QueryMap @NonNull Map<String, String> options);

    @NonNull
    @GET(PATH_DISCOVER + "/" + PATH_MOVIE)
    Flowable<ApiResponse<Movie>> getItems(@Query(QUERY_API_KEY) @NonNull String apiKey);
  }
}
