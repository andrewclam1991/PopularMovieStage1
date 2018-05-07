package com.andrewclam.popularmovie.views.detail.moviereviews;


import com.andrewclam.popularmovie.data.model.MovieReview;
import com.andrewclam.popularmovie.views.BasePresenter;
import com.andrewclam.popularmovie.views.BaseView;

/**
 * Interface that defines the {@link MovieReview}'s adapter View and Presenter
 * class responsibilities
 */
public interface MovieReviewsContract{

  interface View extends BaseView{
    /**
     * Call to notify View class when the {@link MovieReview} data set has changed.
     */
    void onDataSetChanged();
  }

  interface Presenter extends BasePresenter<View>{
    /**
     * Call to start loading a list of {@link MovieReview} from the Model layer
     */
    void loadMovieReviews();
  }


  /**
   * Interface that defines the {@link MovieReview}'s adapter View and Presenter
   * class responsibilities
   */
  interface ListAdapter {

    interface ItemViewHolder {
      // TODO define the view setters for each movie review
    }

    interface Presenter<I extends ItemViewHolder> {
      void onAdapterBindViewHolder(I holder, int position);

      void onAdapterItemClicked(int position);

      int onAdapterRequestItemCount();
    }

  }
}
