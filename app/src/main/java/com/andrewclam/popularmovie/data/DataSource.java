package com.andrewclam.popularmovie.data;

import android.support.annotation.NonNull;

import com.andrewclam.popularmovie.data.model.Entity;
import com.google.common.base.Optional;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * Common interface to the app model layer
 */
public interface DataSource<E extends Entity> {

  /**
   * Allow client to get all items of type {@link E}
   * from the model layer
   * @return an observable list of object of type {@link E}
   */
  Flowable<List<E>> getItems();

  /**
   * Allow client to get a single item by {@link E}'s id
   * from the model layer
   * @return a single {@link Flowable} observable object of type {@link E}
   */
  Flowable<Optional<E>> getItem(@NonNull String id);

  Completable update(@NonNull E item);

  Completable delete(@NonNull String id);

  Completable deleteAll();

  void refresh();

}
