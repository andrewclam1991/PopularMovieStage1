package com.andrewclam.popularmovie.data.source.remote;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.andrewclam.popularmovie.data.api.TMDBApiContract;
import com.andrewclam.popularmovie.data.model.Movie;
import com.andrewclam.popularmovie.di.annotations.ApiKey;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Concrete implementation of a {@link RemoteDataSource} for {@link Movie}s
 */
@Singleton
public class MoviesRemoteDataSource extends RemoteDataSource<Movie,MoviesRemoteDataSource.MovieQueryParams> {

  @Inject
  MoviesRemoteDataSource(){}

  @NonNull
  @Override
  Uri.Builder provideBaseUriBuilder() {
    return TMDBApiContract.TMDBContract.getBaseMovieUriBuilder();
  }

  enum MovieQueryParams implements RemoteDataSource.QueryParams{
    DEFAULT,POPULARITY_DESC,POPULARITY_ASC,
  }

}
