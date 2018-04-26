package com.andrewclam.popularmovie.views.main;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MainPresenter implements MainContract.Presenter, MainContract.ItemViewHolderPresenter{

  @Override
  public void loadItems() {

  }

  @Override
  public void checkNetworkState() {

  }

  @Override
  public void setFilterType(@NonNull FilterType type) {

  }

  @Override
  public void setSortType(@NonNull SortType type) {

  }

  @Override
  public void setSortOrder(@NonNull SortOrder order) {

  }

  @NonNull
  @Override
  public FilterType getCurrentFilterType() {
    return null;
  }

  @NonNull
  @Override
  public FilterType getCurrentSortType() {
    return null;
  }

  @NonNull
  @Override
  public SortOrder getCurrentSortOrder() {
    return null;
  }

  @Override
  public void setView(@NonNull MainContract.View view) {

  }

  @Override
  public void onAdapterBindViewHolder(MainContract.ItemViewHolder holder, int position) {

  }

  @Override
  public void onAdapterItemClicked(int position) {

  }

  @Override
  public void dropView() {

  }

  @Override
  public int onAdapterRequestItemCount() {
    return 0;
  }
}
