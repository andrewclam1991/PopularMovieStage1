package com.andrewclam.popularmovie.data.source;

import android.support.annotation.NonNull;

import com.andrewclam.popularmovie.data.DataSource;
import com.andrewclam.popularmovie.data.model.Movie;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * This is used by Dagger to inject the required arguments into the {@link Repository<Movie>}.
 */
@Module
public abstract class RepositoryModuleMovies {
  @Binds
  @NonNull
  @Singleton

  @Repo
  abstract DataSource<Movie> providesRepository(@NonNull Repository<Movie> repository);

  @Binds
  @NonNull
  @Singleton

  @Local
  abstract DataSource<Movie> providesLocalDataSource(@NonNull DataSourceLocalMovies dataSource);

  @Binds
  @NonNull
  @Singleton
  @Remote
  abstract DataSource<Movie> providesRemoteDataSource(@NonNull DataSourceRemoteMovies dataSource);

  @Provides
  @NonNull
  @Singleton
  static DataSourceRemoteMovies.ApiServiceMovies provideApiService(@NonNull Retrofit retrofit) {
    return retrofit.create(DataSourceRemoteMovies.ApiServiceMovies.class);
  }
}
