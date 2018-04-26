package com.andrewclam.popularmovie.data.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.UUID;

/**
 * Base model class for storing app data
 */
public class Entity {

  /**
   * The unique id that represents this {@link Entity}
   */
  private String entityId;

  /**
   * Flag that indicates whether this {@link Entity} is marked
   * for deletion.
   */
  private boolean setDelete;

  Entity(){}

  @Nullable
  public String getId() {
    return entityId;
  }

  public void setId(@NonNull String id) {
    this.entityId = id;
  }

  public boolean isSetDelete() {
    return setDelete;
  }

  public void setSetDelete(boolean setDelete) {
    this.setDelete = setDelete;
  }

  /**
   * Method to generate uuid classified by clazz type
   * @param clazz sub-class type of {@link Entity}
   * @param <E> type that is supported
   * @return an uuid suitable to use as the {@link Entity#entityId}
   */
  public static <E extends Entity> String generateUUID (Class<E> clazz){
    return clazz.getSimpleName().concat("-").concat(UUID.randomUUID().toString());
  }
}
