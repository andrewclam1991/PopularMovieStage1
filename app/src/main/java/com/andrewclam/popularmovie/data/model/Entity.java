package com.andrewclam.popularmovie.data.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Base model class for storing data
 */
public class Entity {

  private String mEntityId;
  private boolean mSetDelete;

  public Entity(){
    // Note: No-arg constructor
  }

  @Nullable
  public String getmEntityId() {
    return mEntityId;
  }

  public void setmEntityId(@NonNull String mEntityId) {
    this.mEntityId = mEntityId;
  }

  public boolean ismSetDelete() {
    return mSetDelete;
  }

  public void setmSetDelete(boolean mSetDelete) {
    this.mSetDelete = mSetDelete;
  }
}
