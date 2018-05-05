package com.andrewclam.popularmovie.views.detail.movievideos;

import com.andrewclam.popularmovie.data.model.MovieVideo;
import com.andrewclam.popularmovie.views.BasePresenter;
import com.andrewclam.popularmovie.views.BaseView;
import com.andrewclam.popularmovie.views.detail.moviereviews.MovieReviewsContract;

public interface MovieVideosContract {

  interface View extends BaseView {
    /**
     * Call to notify View class when the {@link MovieVideo} data set has changed.
     */
    void onDataSetChanged();
  }

  interface Presenter extends BasePresenter<MovieReviewsContract.View> {
    /**
     * Call to start loading a list of {@link MovieVideo} from the Model layer
     */
    void loadMovieVideos();
  }

  /**
   * Interface that defines the {@link MovieVideo}'s adapter View and Presenter
   * class responsibilities
   */
  interface RecyclerViewAdapter{

    interface ItemViewHolder{

    }

    interface Presenter<I extends ItemViewHolder> {
      void onAdapterBindViewHolder(I holder, int position);

      void onAdapterItemClicked(int position);

      int onAdapterRequestItemCount();
    }

  }
}
