package com.andrewclam.popularmovie.views.detail;

import android.support.annotation.NonNull;

import com.andrewclam.popularmovie.data.model.Movie;
import com.andrewclam.popularmovie.views.BasePresenter;
import com.andrewclam.popularmovie.views.BaseView;

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
     * Allows the View class to handle notifying user
     * when there is an error loading the {@link Movie} detail
     */
    void showLoadingMovieError();

    /**
     * Allows the View class to handle whether to show user the loading indicator
     * @param isVisible controls the loading indicator's visibility
     */
    void setLoadingIndicator(boolean isVisible);

    /**
     * Call to View class to handle showing user a share selection
     * @param mimeType type of media to share
     * @param title share title
     * @param content share content
     */
    void showShareCompat(@NonNull String mimeType, @NonNull String title, @NonNull String content);
  }


  /**
   * Interface that defines the Presenter class responsibilities
   */
  interface Presenter extends BasePresenter<View>{

    /**
     * Call to start loading {@link Movie} detail from the Model layer
     */
    void loadMovie();

    /**
     * Call to check the current movie's favorite status in the Model layer
     */
    void loadMovieFavoriteStatus();

  }

}
