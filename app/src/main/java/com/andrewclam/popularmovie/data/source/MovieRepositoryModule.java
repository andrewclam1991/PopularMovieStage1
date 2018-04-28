package com.andrewclam.popularmovie.data.source;

import android.support.annotation.NonNull;

import com.andrewclam.popularmovie.data.DataSource;
import com.andrewclam.popularmovie.data.Repository;
import com.andrewclam.popularmovie.data.ServiceApiDataSourceDecorator;
import com.andrewclam.popularmovie.data.model.Movie;
import com.andrewclam.popularmovie.data.source.local.MoviesLocalDataSource;
import com.andrewclam.popularmovie.di.annotations.Local;
import com.andrewclam.popularmovie.di.annotations.Remote;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

/**
 * This is used by Dagger to inject the required arguments into the {@link Repository<Movie>}.
 */
@Module
public abstract class MovieRepositoryModule {

  @NonNull
  @Singleton
  @Binds
  @Local
  abstract DataSource<Movie> providesLocalDataSource(MoviesLocalDataSource dataSource);

  @NonNull
  @Singleton
  @Binds
  @Remote
  // TODO implement MoviesRemoteDataSource
  abstract DataSource<Movie> providesRemoteDataSource(MoviesLocalDataSource dataSource);

}
