package com.andrewclam.popularmovie;

/**
 * Base interface to all View classes
 */
public interface BaseView {
  /**
   * Indicate whether this {@link BaseView} is active
   * @return true if the View is visible and active
   */
  boolean isActive();
}
