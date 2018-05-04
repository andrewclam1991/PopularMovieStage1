package com.andrewclam.popularmovie.data.source;

import android.support.annotation.NonNull;

import com.andrewclam.popularmovie.data.DataSource;
import com.andrewclam.popularmovie.data.model.MovieResponse;
import com.andrewclam.popularmovie.data.model.Entity;
import com.google.common.base.Optional;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * Implementation of {@link DataSource <Entity>} that exposes methods to access a remote data source
 * Note: This implementation is a GET-only RESTful service api consumer, therefore POST, PUT and
 * DELETE operations are finalized and simply return completed.
 *
 * @param <E> type of {@link Entity}
 */
abstract class DataSourceRemote<E extends Entity> implements DataSource<E> {

  @Inject
  @ApiKey
  String mApiKey;

  DataSourceRemote() { }

  @Override
  public final void refresh() {
    // No implementation, repository handles data refresh
  }

  @NonNull
  @Override
  public abstract Flowable<List<E>> getItems(@NonNull Map<String, String> options);

  @NonNull
  @Override
  public abstract Flowable<List<E>> getItems();

  @NonNull
  @Override
  public abstract Flowable<Optional<E>> getItem(@NonNull String entityId);

  @NonNull
  @Override
  public final Completable add(@NonNull E item) {
    return Completable.complete();
  }

  @NonNull
  @Override
  public final Completable addAll(@NonNull List<E> items) {
    return Completable.complete();
  }

  @NonNull
  @Override
  public final Completable update(@NonNull E item) {
    return Completable.complete();
  }

  @NonNull
  @Override
  public final Completable remove(@NonNull String entityId) {
    return Completable.complete();
  }

  @NonNull
  @Override
  public final Completable removeAll() {
    return Completable.complete();
  }

}
