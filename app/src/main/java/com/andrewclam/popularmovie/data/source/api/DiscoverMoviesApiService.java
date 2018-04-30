package com.andrewclam.popularmovie.data.source.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.andrewclam.popularmovie.data.DataSource;
import com.andrewclam.popularmovie.data.model.Movie;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.andrewclam.popularmovie.data.source.api.TMDBApiServiceContract.TMDBContract.BASE_TMDB_REQUEST_URL;

/**
 * Concrete implementation of a {@link ApiServiceDecorator <>} that is responsible
 * for providing the implementation to a list of {@link Movie} from the service api
 */
@Singleton
public class DiscoverMoviesApiService extends ApiServiceDecorator<Movie> {

  @Inject
  public DiscoverMoviesApiService(@NonNull DataSource<Movie> repository) {
    super(repository);
  }

  @NonNull
  @Override
  public final Call<List<Movie>> provideApiServiceCall(@NonNull String apiKey,
                                                 @Nullable Map<String, String> options) {
    // Create an service instance of the api service using retrofit
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(BASE_TMDB_REQUEST_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    TMDBApiServiceContract.DiscoverMoviesService service = retrofit.create(
        TMDBApiServiceContract.DiscoverMoviesService.class);

    return service.getItems(apiKey, options);
  }
}
