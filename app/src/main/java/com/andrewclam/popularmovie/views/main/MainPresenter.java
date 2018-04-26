package com.andrewclam.popularmovie.views.main;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MainPresenter implements MainContract.Presenter, MainContract.ItemViewHolderPresenter{

  @Nullable
  private MainContract.View mView;

  @Override
  public void loadItems() {

  }

  @Override
  public void setFilterType(@NonNull FilterType type) {

  }

  @Override
  public void setFilterOrder(@NonNull FilterOrder order) {

  }

  @Override
  public void setView(@NonNull MainContract.View view) {
    mView = view;
  }

  @Override
  public void dropView() {
    mView = null;
  }

  @Override
  public void onAdapterBindViewHolder(MainContract.ItemViewHolder holder, int position) {

  }

  @Override
  public void onAdapterItemClicked(int adapterPosition) {

  }

  @Override
  public int onAdapterRequestItemCount() {
    return 0;
  }
}
