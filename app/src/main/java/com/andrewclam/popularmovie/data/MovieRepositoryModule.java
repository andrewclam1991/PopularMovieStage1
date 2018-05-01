package com.andrewclam.popularmovie.data;

import android.content.Context;
import android.support.annotation.NonNull;

import com.andrewclam.popularmovie.R;
import com.andrewclam.popularmovie.data.DataSource;
import com.andrewclam.popularmovie.data.Repository;
import com.andrewclam.popularmovie.data.model.Movie;
import com.andrewclam.popularmovie.data.source.api.ApiServiceDecorator;
import com.andrewclam.popularmovie.data.source.api.DiscoverMoviesApiService;
import com.andrewclam.popularmovie.data.source.local.MoviesLocalDataSource;
import com.andrewclam.popularmovie.di.annotations.ApiKey;
import com.andrewclam.popularmovie.di.annotations.Local;
import com.andrewclam.popularmovie.di.annotations.Remote;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

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
  @Provides
  @ApiKey
  static String provideApiKey(@NonNull Context context) {
    return context.getString(R.string.tmdb_api_key);
  }

}
