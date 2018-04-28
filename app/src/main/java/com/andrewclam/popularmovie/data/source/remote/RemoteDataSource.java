package com.andrewclam.popularmovie.data.source.remote;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.andrewclam.popularmovie.data.DataSource;
import com.andrewclam.popularmovie.data.model.Entity;
import com.google.common.base.Optional;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 * Implementation of {@link DataSource<Entity>} that exposes methods to access a remote data source
 * Note: This implementation is a GET-only RESTful service api consumer, therefore POST, PUT and
 * DELETE operations are finalized and simply return completed.
 * @param <E> type of {@link Entity}
 */
@Singleton
abstract class RemoteDataSource<E extends Entity> implements DataSource<E> {

  @Override
  public void refresh() {
    // No implementation, repository handles data refresh
  }

  @Inject
  RemoteDataSource(){}

  @Override
  public Flowable<List<E>> getItems() {
    // make the http request with the provided uri
    // get the json response
    // parse the json response into pojo
    // add each pojo into a list
    // return the list
    return getJsonResponse(null)
        .flatMap(this::parseList)
        .toFlowable();
  }

  @Override
  public Flowable<Optional<E>> getItem(@NonNull String entityId) {
    // parse the json response into pojo
    // add each pojo into a list
    // return the item
    return getJsonResponse(null)
        .flatMap(this::parse)
        .toFlowable();
  }

  /**
   * TODO Implement the http request steps
   * Responsible for making the http request with the provided uri
   * and returns a json response for processing
   * @return a json formatted String data
   */
  @NonNull
  private Single<String> getJsonResponse(@NonNull Uri uri){
    return null;
  }

  @NonNull
  abstract Single<Optional<E>> parse(String jsonResponse);

  @NonNull
  abstract Single<List<E>> parseList(String jsonResponse);

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

}
