package com.andrewclam.popularmovie.util;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.google.common.base.Strings;

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

  /**
   * Method to check whether an activity has a intent action of not
   * @param activity framework activity class
   * @return the intent action the activity was started one, else
   * throws exception
   */
  @NonNull
  public static String getIntentAction(@NonNull Activity activity) {
    final String action = activity.getIntent().getAction();
    if (Strings.isNullOrEmpty(action)) {
      throw new UnsupportedOperationException("Unable to handle intent without a set action");
    } else {
      return action;
    }
  }

}