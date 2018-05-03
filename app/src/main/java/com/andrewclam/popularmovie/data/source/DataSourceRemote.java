package com.andrewclam.popularmovie.data.source;

import android.support.annotation.NonNull;

import com.andrewclam.popularmovie.data.DataSource;
import com.andrewclam.popularmovie.data.model.Entity;
import com.google.common.base.Optional;

import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * Implementation of {@link DataSource <Entity>} that exposes methods to access a remote data source
 * Note: This implementation is a GET-only RESTful service api consumer, therefore POST, PUT and
 * DELETE operations are finalized and simply return completed.
 * @param <E> type of {@link Entity}
 */
abstract class DataSourceRemote<E extends Entity> implements DataSource<E> {

  @Override
  public void refresh() {
    // No implementation, repository handles data refresh
  }

  DataSourceRemote(){}

  @NonNull
  @Override
  public Flowable<List<E>> getItems(@NonNull Map<String, String> options) {
    return Flowable.empty();
  }

  @NonNull
  @Override
  public Flowable<List<E>> getItems() {
    return Flowable.empty();
  }

  @NonNull
  @Override
  public Flowable<Optional<E>> getItem(@NonNull String entityId) {
    return Flowable.empty();
  }

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
