package com.andrewclam.popularmovie.data.source;

import android.content.Context;
import android.support.annotation.NonNull;

import com.andrewclam.popularmovie.R;
import com.andrewclam.popularmovie.data.DataSource;
import com.andrewclam.popularmovie.data.Repository;
import com.andrewclam.popularmovie.data.ApiServiceDecorator;
import com.andrewclam.popularmovie.data.api.TMDBApiService;
import com.andrewclam.popularmovie.data.model.Entity;
import com.andrewclam.popularmovie.data.model.Movie;
import com.andrewclam.popularmovie.data.source.local.MoviesLocalDataSource;
import com.andrewclam.popularmovie.di.annotations.ApiKey;
import com.andrewclam.popularmovie.di.annotations.Local;
import com.andrewclam.popularmovie.di.annotations.Remote;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.andrewclam.popularmovie.data.api.TMDBApiContract.TMDBContract.BASE_TMDB_REQUEST_URL;

/**
 * This is used by Dagger to inject the required arguments into the {@link Repository<Movie>}.
 */
@Module
public abstract class MovieRepositoryModule {

  @NonNull
  @Singleton
  @Binds
  @Local
  abstract DataSource<Movie> providesLocalDataSource(@NonNull MoviesLocalDataSource dataSource);

  @NonNull
  @Singleton
  @Binds
  @Remote
  abstract DataSource<Movie> providesRemoteDataSource(@NonNull MoviesLocalDataSource dataSource);

  @NonNull
  @Singleton
  static ApiServiceDecorator<Movie> provideWrappedRepository(@NonNull Repository<Movie> movieRepository,
                                                             @NonNull @ApiKey String apiKey,
                                                             @NonNull TMDBApiService<Movie> apiService){
    return new ApiServiceDecorator<>(movieRepository)
        .setApiService(()->apiService)
        .setApiKey(()->apiKey);
  }
  
  @NonNull
  @Singleton
  static TMDBApiService<?> provideApiService(){
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(BASE_TMDB_REQUEST_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    return retrofit.create(TMDBApiService.class);
  }

  @NonNull
  @ApiKey
  static String provideApiKey(@NonNull Context context){
    return context.getString(R.string.tmdb_api_key);
  }

}
