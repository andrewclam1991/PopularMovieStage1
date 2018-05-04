package com.andrewclam.popularmovie.data.source;

import android.support.annotation.NonNull;

import com.andrewclam.popularmovie.data.DataSource;
import com.andrewclam.popularmovie.data.model.MovieVideo;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * This is used by Dagger to inject the required arguments into the {@link Repository<MovieVideo>}.
 */
@Module
public abstract class RepositoryModuleMovieVideos {
  @Binds
  @NonNull
  @Singleton
  @Repo
  abstract DataSource<MovieVideo> providesRepository(@NonNull Repository<MovieVideo> repository);

  @Binds
  @NonNull
  @Singleton
  @Local
  abstract DataSource<MovieVideo> providesLocalDataSource(@NonNull DataSourceLocalMovieVideos dataSource);

  @Binds
  @NonNull
  @Singleton
  @Remote
  abstract DataSource<MovieVideo> providesRemoteDataSource(@NonNull DataSourceRemoteMovieVideos dataSource);

  @Provides
  @NonNull
  @Singleton
  static MovieVideo.ServiceApi provideServiceApi (@NonNull Retrofit retrofit) {
    return retrofit.create(MovieVideo.ServiceApi.class);
  }
}
