package com.andrewclam.popularmovie.data.model;

import android.support.annotation.NonNull;

import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Model class supertype
 * Provides common methods and states for all subtypes of {@link Entity}
 */
public class Entity {
  private String mUid;
  private boolean mSetDelete;

  /**
   * Default constructor for the class
   * Note: Require at least one no arg constructor for
   * Firebase
   */
  Entity() { }

  /**
   * @return the set unique identifier of this {@link Entity}
   */
  @NonNull
  public final String getUid() {
    return mUid;
  }

  /**
   * Set an unique identifier of this particular {@link Entity}
   *
   * @param uid unique identifier of this {@link Entity}
   */
  public final void setUid(@NonNull String uid) {
    mUid = checkNotNull(uid, "uid can't be null or empty");
  }

  /**
   * Control flag whether the entity record should be deleted from
   * the underlying data sources
   *
   * @return whether this {@link Entity} is marked for deletion
   */
  public final boolean isSetDelete() {
    return mSetDelete;
  }

  /**
   * Mark this {@link Entity} for deletion
   *
   * @param setDelete whether to mark this {@link Entity} for deletion
   */
  public final void setDelete(boolean setDelete) {
    mSetDelete = setDelete;
  }
}
