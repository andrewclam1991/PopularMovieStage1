package com.andrewclam.popularmovie.data;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.andrewclam.popularmovie.data.api.TMDBApiContract;
import com.andrewclam.popularmovie.data.api.TMDBApiContract.TMDBContract.DiscoverMovie.SortByArgument;
import com.andrewclam.popularmovie.data.model.Entity;
import com.google.common.base.Optional;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public abstract class ServiceApiWrapper<E extends Entity> implements DataSource<E> {
  /**
   * Exposes a new behavior to a wrapped {@link DataSource<E>} which allows client to supply
   * a api request uri at runtime.
   */
  @NonNull
  public abstract Uri provideApiRequestUri();
}
