package com.andrewclam.popularmovie.data.source.remote;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.andrewclam.popularmovie.data.api.TMDBApiContract;
import com.andrewclam.popularmovie.data.model.Movie;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Concrete implementation of a {@link RemoteDataSource} for {@link Movie}s
 */
@Singleton
public class MoviesRemoteDataSource extends RemoteDataSource<Movie> {

  @Inject
  MoviesRemoteDataSource(){}

  @NonNull
  @Override
  Uri provideRequestUri() {
    return TMDBApiContract.TMDBContract.DiscoverMovie.getRequestUri("");
  }
}
