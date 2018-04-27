package com.andrewclam.popularmovie.data.source.remote;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.andrewclam.popularmovie.data.DataSource;
import com.andrewclam.popularmovie.data.model.Entity;
import com.andrewclam.popularmovie.data.model.Movie;
import com.andrewclam.popularmovie.di.annotations.Remote;
import com.google.common.base.Optional;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * Implementation of {@link DataSource<Entity>} that exposes methods to access a remote data source
 * Note: This implementation is a GET-only RESTful service api consumer, therefore
 * POST, PUT and DELETE operations are finalized and simply ignored.
 * @param <E> type of {@link Entity}
 */
@Singleton
abstract class RemoteDataSource<E extends Entity, Q extends RemoteDataSource.QueryParams>
    implements DataSource<E> {

  @Override
  public void refresh() {

  }

  @Inject
  RemoteDataSource(){}

  @Override
  public Flowable<List<E>> getItems() {
    return null;
  }

  @Override
  public Flowable<Optional<E>> getItem(@NonNull String entityId) {
    return null;
  }

  /**
   * Allow subclass to implement and provide the {@link Entity} specific query {@link URL},
   * according to the service api requirement.
   * @return an {@link Uri.Builder} that is specific to the {@link Entity} type
   */
  @NonNull
  abstract Uri.Builder provideBaseUriBuilder();

  private Uri appendQueryParameter(@NonNull Uri.Builder builder,
                                   @NonNull Q queryParam,
                                   @NonNull String value){
    return builder.appendQueryParameter(queryParam.toString(),value).build();
  }

  @Override
  public final Completable add(@NonNull E item) {
    return Completable.complete();
  }

  @Override
  public final Completable addAll(@NonNull List<E> items) {
    return Completable.complete();
  }

  @Override
  public final Completable update(@NonNull E item) {
    return Completable.complete();
  }

  @Override
  public final Completable remove(@NonNull String entityId) {
    return Completable.complete();
  }

  @Override
  public final Completable removeAll() {
    return Completable.complete();
  }

  interface QueryParams {

  }
}
