package com.andrewclam.popularmovie.util;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This provides methods to help Activities load their UI.
 */
public final class ActivityUtils {

  /**
   * The {@code fragment} is added to the container view with id {@code frameId}. The operation is
   * performed by the {@code fragmentManager}.
   *
   */
  public static void addFragmentToActivity (@NonNull FragmentManager fragmentManager,
                                            @NonNull Fragment fragment, int frameId) {
    checkNotNull(fragmentManager);
    checkNotNull(fragment);
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    transaction.replace(frameId, fragment);
    transaction.commit();
  }

  /**
   * The {@code fragment} is removed from the host activity. The operation is
   * performed by the {@code fragmentManager}.
   *
   */
  public static void removeFragmentFromActivity (@NonNull FragmentManager fragmentManager,
                                                 @NonNull Fragment fragment) {
    checkNotNull(fragmentManager);
    checkNotNull(fragment);
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    transaction.remove(fragment);
    transaction.commit();
  }


}