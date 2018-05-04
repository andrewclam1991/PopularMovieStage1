package com.andrewclam.popularmovie.data.source;

import android.support.annotation.NonNull;

import com.andrewclam.popularmovie.data.DataSource;
import com.andrewclam.popularmovie.data.model.Movie;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.andrewclam.popularmovie.data.source.ApiServiceContract.TMDBContract.BASE_TMDB_REQUEST_URL;

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
  abstract DataSource<Movie> providesRemoteDataSource(@NonNull DataSourceRemoteMovies dataSource);

  @NonNull
  @Singleton
  @Binds
  @Repo
  abstract DataSource<Movie> providesRepository(@NonNull Repository<Movie> repository);

  @Provides
  @NonNull
  @Singleton
  static Retrofit providesRetrofit(){
    return new Retrofit.Builder()
        .baseUrl(BASE_TMDB_REQUEST_URL.concat("/"))
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build();
  }
}
