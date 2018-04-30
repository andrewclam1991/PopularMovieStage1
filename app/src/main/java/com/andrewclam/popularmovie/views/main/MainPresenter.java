package com.andrewclam.popularmovie.views.main;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.andrewclam.popularmovie.data.model.Movie;
import com.andrewclam.popularmovie.data.ApiServiceDecorator;
import com.andrewclam.popularmovie.util.schedulers.BaseSchedulerProvider;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class MainPresenter implements MainContract.Presenter, MainContract.ItemViewHolderPresenter {

  @NonNull
  private final ApiServiceDecorator<Movie> mMovieRepository;

  @NonNull
  private final BaseSchedulerProvider mSchedulerProvider;

  @NonNull
  private final CompositeDisposable mCompositeDisposable;

  @Nullable
  private MainContract.View mView;

  @NonNull
  private final List<Movie> mMovies;

  @Inject
  MainPresenter(@NonNull ApiServiceDecorator<Movie> movieRepository,
                @NonNull BaseSchedulerProvider schedulerProvider) {
    mMovieRepository = new ApiServiceDecorator<>(movieRepository);
    mSchedulerProvider = schedulerProvider;
    mCompositeDisposable = new CompositeDisposable();
    mMovies = new ArrayList<>(0);
  }

  @Override
  public void setView(@NonNull MainContract.View view) {
    mView = view;
    loadItems();
  }

  @Override
  public void dropView() {
    mView = null;
  }

  @Override
  public void loadItems() {
    mMovieRepository.refresh();

    Disposable disposable = mMovieRepository
        .setQueryParams(()->null)
        .getItems()
        .flatMap(Flowable::fromIterable)
        .toList()
        .subscribeOn(mSchedulerProvider.io())
        .observeOn(mSchedulerProvider.ui())
        .subscribe(
            this::handleOnNext
        );

    mCompositeDisposable.add(disposable);
  }

  private void handleOnNext(@NonNull List<Movie> movies) {
    // refresh in-memory list
    mMovies.clear();
    if (!movies.isEmpty()){
      // has new content
      mMovies.addAll(movies);
    }

    if (mView != null && mView.isActive()) {
      mView.onDataSetChanged();
    }
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
  public void onAdapterBindViewHolder(MainContract.ItemViewHolder holder, int position) {
    if (position < 0){
      return; // invalid position
    }
  }

  @Override
  public void onAdapterItemClicked(int position) {

  }

  @Override
  public int onAdapterRequestItemCount() {
    return 0;
  }
}
