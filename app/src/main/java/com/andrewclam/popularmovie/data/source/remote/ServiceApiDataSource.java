package com.andrewclam.popularmovie.data.source.remote;

import android.support.annotation.NonNull;

import com.andrewclam.popularmovie.data.DataSource;
import com.andrewclam.popularmovie.data.model.Entity;
import com.google.common.base.Optional;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * Implementation of the {@link DataSource}
 * Restful Api client data source that only supports GET
 * @param <E>
 */
public class ServiceApiDataSource<E extends Entity> implements DataSource<E>{

  @Override
  public void refresh() {

  }

  @Override
  public Flowable<List<E>> getItems() {
    return null;
  }

  @Override
  public Flowable<Optional<E>> getItem(@NonNull String entityId) {
    return null;
  }

  @Override
  public Completable add(@NonNull E item) {
    return Completable.complete();
  }

  @Override
  public Completable addAll(@NonNull List<E> items) {
    return Completable.complete();
  }

  @Override
  public Completable update(@NonNull E item) {
    return Completable.complete();
  }

  @Override
  public Completable remove(@NonNull String entityId) {
    return Completable.complete();
  }

  @Override
  public Completable removeAll() {
    return Completable.complete();
  }

}
