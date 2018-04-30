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

    /**
     * Allow the View to handle adapter notification
     * when the underlying data set is changed
     */
    void onDataSetChanged();

  }

  /**
   * Interface that defines the ViewHolder class responsibilities
   * allows communication with a client {@link ItemViewHolderPresenter<ItemViewHolder>}
   */
  interface ItemViewHolder{
    // Define view holder methods here
    void loadMoviePoster(@NonNull String posterUrl);
  }

  /**
   * Interface that defines the Presenter class responsibilities
   */
  interface Presenter extends BasePresenter<View>{

    /**
     * Allow client to start loading {@link Entity}s
     */
    void loadItems();

    /**
     * Enum that defines the filter types
     */
    enum FilterType{
      DEFAULT, FAVORITES
    }

    /**
     * Enum that defines the sort types
     */
    enum SortType{
      DEFAULT, BY_POPULARITY, BY_RATING,
    }

    /**
     * Enum that defines the sort order
     */
    enum SortOrder {
      DEFAULT, ASC, DESC
    }

    /**
     * Allow client to filter the result base on a type
     * Note: Also allow the {@link FilterType} instance state to be restored
     * @param type any of the valid {@link FilterType}
     */
    void setFilterType(@NonNull FilterType type);

    /**
     * Allow client to sort the result base on a type
     * Note: Also allow the {@link SortType} instance state to be restored
     * @param type any of the valid {@link SortType}
     */
    void setSortType(@NonNull SortType type);

    /**
     * Allow client to order the results base on the {@code order}
     * Note: Also allow the {@link SortOrder} instance state to be restored
     * @param order any of the valid {@link SortOrder}
     */
    void setSortOrder(@NonNull SortOrder order);

    /**
     * Allow client to get the current {@link FilterType} set by the user,
     * Note: handles screen rotation to persist user selection.
     * @return the current {@link FilterType} set by the user, if not set
     * a {@link FilterType#DEFAULT} is returned
     */
    @NonNull
    FilterType getCurrentFilterType();

    /**
     * Allow client to get the current {@link SortType} set by the user,
     * Note: handles screen rotation to persist user selection.
     * @return the current {@link SortType} set by the user, if not set
     * a {@link SortType#DEFAULT} is returned
     */
    @NonNull
    SortType getCurrentSortType();

    /**
     * Allow client to get the current {@link SortOrder} set by the user,
     * Note: handles screen rotation to persist user selection.
     * @return the current {@link SortOrder} set by the user, if not set
     * a {@link SortOrder#DEFAULT} is returned
     */
    @NonNull
    SortOrder getCurrentSortOrder();

    // Constants
    String FILTER_ORDER_KEY = "FILTER_ORDER_KEY";
    String SORT_TYPE_KEY = "SORT_TYPE_KEY";
    String SORT_ORDER_KEY = "SORT_ORDER_KEY";
  }

  /**
   * Interface that defines a {@link ItemViewHolderPresenter} responsibilities
   * allows communication with a client {@link ItemViewHolder}
   * @param <I>
   */
  interface ItemViewHolderPresenter<I extends ItemViewHolder>{

    /**
     * Method called when the adapter is ready to populate a {@link ItemViewHolder}
     * with model data.
     * @param holder the {@link ItemViewHolder} that is ready to be populated
     * @param position the corresponding absolute position of the data within the list.
     */
    void onAdapterBindViewHolder(I holder, int position);

    /**
     * Method called when the adapter registered a user click on an element item.
     * @param position the corresponding absolute position of the data within the list.
     */
    void onAdapterItemClicked(int position);

    /**
     * Method called when the adapter requests the current count of the items
     * @return the current count of items
     */
    int onAdapterRequestItemCount();

  }

}
