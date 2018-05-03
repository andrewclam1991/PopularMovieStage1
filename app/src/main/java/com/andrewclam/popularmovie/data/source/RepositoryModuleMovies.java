package com.andrewclam.popularmovie.data.source;

import android.support.annotation.NonNull;

import com.andrewclam.popularmovie.data.DataSource;
import com.andrewclam.popularmovie.data.model.Movie;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

/**
 * This is used by Dagger to inject the required arguments into the {@link Repository<Movie>}.
 */
@Module
public abstract class RepositoryModuleMovies {

  @NonNull
  @Singleton
  @Binds
  @Local
  abstract DataSource<Movie> providesLocalDataSource(@NonNull DataSourceLocalMovies dataSource);

  @NonNull
  @Singleton
  @Binds
  @Remote
  abstract DataSource<Movie> providesRemoteDataSource(@NonNull DataSourceLocalMovies dataSource);

  @NonNull
  @Singleton
  @Binds
  @Repo
  abstract DataSource<Movie> providesRepository(@NonNull Repository<Movie> repository);
}
