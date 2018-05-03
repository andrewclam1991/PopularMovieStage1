package com.andrewclam.popularmovie.views;

import android.support.annotation.NonNull;

/**
 * Base interface for all Presenter classes to enforce support
 * for {@link BaseView}
 * @param <V> type of {@link BaseView} that the {@link BasePresenter} supports
 */
public interface BasePresenter<V extends BaseView> {

  /**
   * Allow implementing {@link BasePresenter} to set its supporting
   * View class
   * @param view the View to attach a controlling {@link BasePresenter} to
   */
  void setView(@NonNull V view);

  /**
   * Allow implementing {@link BasePresenter} to drop its
   * reference to its {@link BaseView} to prevent leaking
   * View class.
   */
  void dropView();
}
