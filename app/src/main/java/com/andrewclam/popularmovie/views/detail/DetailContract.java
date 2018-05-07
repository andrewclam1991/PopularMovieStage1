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
   * Interface that defines the View class responsibilities and affordances
   */
  interface View extends BaseView{
    /**
     * Let the View handle notifying user when
     * there is an error loading the {@link Movie} detail
     */
    void showLoadingMovieError();

    /**
     * Let the View handle notify user when the item information is missing
     * or not found
     */
    void showMissingMovieError();

    /**
     * Let the View handle whether to show user the loading indicator
     * @param isVisible controls the loading indicator's visibility
     */
    void setLoadingIndicator(boolean isVisible);

    /**
     * Let the View show the poster image as a banner with the provided url
     * @param url image url to the poster banner
     */
    void showPosterBanner(@NonNull String url);

    /**
     * Let the View show the poster image as an icon with the provided url
     * @param url image url to the poster banner
     */
    void showPosterIcon(@NonNull String url);

    /**
     * Let the View hide the poster(s) when the image url is not
     * available
     */
    void hidePosters();

    /**
     * Let the View show the title
     * @param title the item title
     */
    void showTitle(@NonNull String title);

    /**
     * Let the View hide the title
     */
    void hideTitle();

    /**
     * Let the View show the release date
     * @param releaseDate formatted release date
     */
    void showReleaseDate(@NonNull String releaseDate);

    /**
     * Let the View hide the release date
     * when it is not available
     */
    void hideReleaseDate();

    /**
     * Let the View show the average vote score
     * @param voteAverage computed average vote score
     */
    void showVoteAverage(double voteAverage);

    /**
     * Let the View hide the vote average
     * when it is not available
     */
    void hideVoteAverage();

    /**
     * Let the View show the number of votes
     * @param voteCount computed counts of votes
     */
    void showVoteCount(int voteCount);

    /**
     * Let the View hide the vote count
     * when it is not available
     */
    void hideVoteCount();

    /**
     * Let the View show the item's overview
     * @param overview a description or overview of an item
     */
    void showOverView(@NonNull String overview);

    /**
     * Let the View hide the overview view
     * when it is not available
     */
    void hideOverView();

    /**
     * Let the View handle showing the user whether the
     * item is marked as a favorite
     * @param isFavorite controls whether to mark the item as a favorite
     */
    void setFavoriteStatus(boolean isFavorite);

    /**
     * Let the View handle showing user a share dialog intent
     *
     * @param mimeType type of media to share
     * @param content share content
     */
    void showShare(@NonNull String mimeType, @NonNull String content);
  }


  /**
   * Interface that defines the Presenter class responsibilities
   */
  interface Presenter extends BasePresenter<View>{

    /**
     * Let Presenter know to start loading {@link Movie} detail from the Model layer.
     */
    void loadMovie();

    /**
     * Let Presenter know when to communicate with the Model layet to
     * check the current movie's favorite status.
     */
    void loadMovieFavoriteStatus();

    /**
     * Let Presenter known when the user has clicked the Share button
     */
    void handleOnShareClicked();

  }

}
