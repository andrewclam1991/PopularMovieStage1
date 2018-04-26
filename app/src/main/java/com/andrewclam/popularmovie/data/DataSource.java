package com.andrewclam.popularmovie.data;

import android.support.annotation.NonNull;

import com.andrewclam.popularmovie.data.model.Entity;
import com.google.common.base.Optional;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * Exposes a generic interface to the application’s underlying Model layer,
 * this design uses RxJava types for handling concurrency and implements the observer pattern.
 */
public interface DataSource<E extends Entity> {

  /**
   * interface to force data source to refresh its in memory cache
   */
  void refresh();

  /**
   * interface to get a collection of items of the type from the model layer
   * @return observable stream that returns a list of items
   */
  Flowable<List<E>> getItems();

  /**
   * interface to get item data from the model layer
   * @param entityId unique id of the {@link Entity}
   * @return observable stream that returns a {@link Optional} item, may not exist
   */
  Flowable<Optional<E>> getItem(@NonNull String entityId);

  /**
   * Interface to add one item into the model layer
   * @param item the item data to be inserted
   * @return a completable that emits when the item was saved or in case of error.
   */
  Completable add(@NonNull E item);

  /**
   * Interface to add a collection of items into the model layer
   * @param items the collection of items to be inserted
   * @return a completable that emits when all the items were saved or in case of error.
   */
  Completable addAll(@NonNull List<E> items);

  /**
   * Interface to update one item inside the model layer
   * @param item the item data to be updated
   * @return a completable that emits when the item was updated or in case of error.
   */
  Completable update(@NonNull E item);

  /**
   * Interface to remove one item from the model layer
   * @param entityId unique id of the {@link Entity} that is to be removed
   * @return a completable that emits when the item was removed or in case of error.
   */
  Completable remove(@NonNull String entityId);

  /**
   * Interface to remove all items of the type from the model layer
   * @return a completable that emits when all the items were removed or in case of error.
   */
  Completable removeAll();

}
