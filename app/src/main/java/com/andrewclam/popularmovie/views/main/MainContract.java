package com.andrewclam.popularmovie.views.main;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.andrewclam.popularmovie.BasePresenter;
import com.andrewclam.popularmovie.BaseView;
import com.andrewclam.popularmovie.data.model.Entity;

/**
 * Interface contract that defines number of {@link View}s' and its
 * {@link Presenter}s' responsibilities
 */
public interface MainContract {

  /**
   * Interface that defines the View class responsibilities
   */
  interface View extends BaseView{
    /**
     * Allow the View to handle notifying user
     * when there is an error loading the movies
     */
    void showLoadingMoviesError();

  }

  /**
   * Interface that defines the ViewHolder class responsibilities
   * allows communication with a client {@link ItemViewHolderPresenter<ItemViewHolder>}
   */
  interface ItemViewHolder{
    // Define view holder methods here
  }

  /**
   * Interface that defines the Presenter class responsibilities
   */
  interface Presenter extends BasePresenter<View>{

    /**
     * Enum that defines the sort filter type
     */
    enum FilterType{
      DEFAULT, BY_POPULARITY, BY_RATING, FAVORITES_ONLY
    }

    /**
     * Enum that defines the filter order
     */
    enum FilterOrder{
      ASC, DESC
    }

    /**
     * Allow client to start loading {@link Entity}s
     */
    void loadItems();

    /**
     * Allow client to filter the result base on the filter type
     * Note: Also allow the {@link FilterType} instance state to be restored
     * @param type any of the valid {@link FilterType}
     */
    void setFilterType(@NonNull FilterType type);

    /**
     * Allow client to order the results base on the {@code order}
     * Note: Also allow the {@link FilterOrder} instance state to be restored
     * @param order any of the valid {@link FilterOrder}
     */
    void setFilterOrder(@NonNull FilterOrder order);


    /**
     * Allow client to get the current filter type set by the user,
     * Note: handles screen rotation to persist user selection.
     * @return the current filter type set by the user, if not set
     * a default is returned
     */
    @NonNull
    FilterType getCurrentFilterType();

    /**
     * Allow client to get the current {@link FilterOrder} set by the user,
     * Note: handles screen rotation to persist user selection.
     * @return the current {@link FilterOrder} set by the user, if not set
     * a default is returned
     */
    @NonNull
    FilterOrder getCurrentFilterOrder();
  }

  /**
   * Interface that defines a {@link ItemViewHolderPresenter} responsibilities
   * allows communication with a client {@link ItemViewHolder}
   * @param <I>
   */
  interface ItemViewHolderPresenter<I extends ItemViewHolder>{

    void onAdapterBindViewHolder(I holder, int position);

    void onAdapterItemClicked(int adapterPosition);

    int onAdapterRequestItemCount();

  }

}
