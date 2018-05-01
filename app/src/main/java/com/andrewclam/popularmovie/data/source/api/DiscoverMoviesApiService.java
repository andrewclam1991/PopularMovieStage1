package com.andrewclam.popularmovie.data.source.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.andrewclam.popularmovie.data.DataSource;
import com.andrewclam.popularmovie.data.model.Movie;
import com.andrewclam.popularmovie.data.model.ApiResponse;
import com.andrewclam.popularmovie.di.annotations.ApiKey;
import com.google.common.base.Strings;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

import static com.andrewclam.popularmovie.data.source.api.TMDBApiServiceContract.TMDBContract.BASE_TMDB_REQUEST_URL;
import static com.andrewclam.popularmovie.data.source.api.TMDBApiServiceContract.TMDBContract.Paths.PATH_DISCOVER;
import static com.andrewclam.popularmovie.data.source.api.TMDBApiServiceContract.TMDBContract.Paths.PATH_MOVIE;
import static com.andrewclam.popularmovie.data.source.api.TMDBApiServiceContract.TMDBContract.QUERY_API_KEY;

/**
 * Concrete implementation of a {@link ApiServiceDecorator <>} that is responsible
 * for providing the implementation to a list of {@link Movie} from the service api
 */
public class DiscoverMoviesApiService extends ApiServiceDecorator<Movie> {

  @Inject
  @ApiKey
  String mApiKey;

  public DiscoverMoviesApiService(@NonNull DataSource<Movie> repository) {
    super(repository);
  }

  @NonNull
  @Override
  public final Call<ApiResponse<Movie>> provideApiServiceCall(@Nullable Map<String, String> options) {
    if (Strings.isNullOrEmpty(mApiKey)){
      throw new IllegalArgumentException("mApiKey can't be null or empty");
    }

    // Create an service instance of the api service using retrofit
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(BASE_TMDB_REQUEST_URL+"/")
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    DiscoverMoviesService service = retrofit.create(DiscoverMoviesService.class);

    if (options != null){
      return service.getItems(mApiKey, options);
    }else{
      return service.getItems(mApiKey);
    }
  }

  /**
   * API Service interface for discovering {@link Movie}
   * example request:
   * https://api.themoviedb.org/3/discover/movie?api_key=<<YOUR_API_KEY>>&sort_by=popularity.desc
   */
  interface DiscoverMoviesService {

    /**
     * Gets a list of {@link Movie}s base on the supplied query parameters and their arguments.
     * @param apiKey TMDB developer api key
     * @return list of {@link Movie}s
     */
    @GET(PATH_DISCOVER + "/" + PATH_MOVIE)
    @NonNull
    Call<ApiResponse<Movie>> getItems(@Query(QUERY_API_KEY) @NonNull String apiKey,
                                      @QueryMap @NonNull Map<String, String> options);


    @GET(PATH_DISCOVER + "/" + PATH_MOVIE)
    Call<ApiResponse<Movie>> getItems(@Query(QUERY_API_KEY) @NonNull String apiKey);
  }

}
