package com.andrewclam.popularmovie.data;

import android.support.annotation.NonNull;

import com.andrewclam.popularmovie.data.model.Entity;

import java.util.List;

/**
 * Common interface to the app model layer
 */
public interface DataSource<E extends Entity> {

  /**
   * Allow client to get all items of type {@link E}
   * from the model layer
   * @return a list of object of type {@link E}
   */
  List<E> getItems();

  /**
   * Allow client to get a single item by {@link E}'s id
   * from the model layer
   * @return a single object of type {@link E}
   */
  E getItem(@NonNull String id);


}
