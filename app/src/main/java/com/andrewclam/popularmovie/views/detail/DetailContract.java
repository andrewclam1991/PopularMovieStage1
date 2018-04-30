package com.andrewclam.popularmovie.views.detail;

import android.support.annotation.NonNull;

import com.andrewclam.popularmovie.BasePresenter;
import com.andrewclam.popularmovie.BaseView;
import com.andrewclam.popularmovie.data.model.Entity;
import com.andrewclam.popularmovie.data.model.Movie;

import dagger.android.support.DaggerAppCompatActivity;

/**
 * Interface contract that defines number of {@link View}s'and its
 * {@link Presenter}s' responsibilities
 */
interface DetailContract {

  /**
   * Interface that defines the View class responsibilities
   */
  interface View extends BaseView{
    /**
     * Allow the View to handle notifying user
     * when there is an error loading the {@link Movie} detail
     */
    void showLoadingMovieError();
  }

  /**
   * Interface that defines the Presenter class responsibilities
   */
  interface Presenter extends BasePresenter<View>{

    /**
     * Allow client to start loading {@link Movie}
     */
    void loadItem();
  }

}
