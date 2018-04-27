package com.andrewclam.popularmovie.views.main;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.andrewclam.popularmovie.data.Repository;
import com.andrewclam.popularmovie.data.model.Movie;
import com.andrewclam.popularmovie.util.schedulers.BaseSchedulerProvider;
import com.google.common.base.Optional;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class MainPresenter implements MainContract.Presenter, MainContract.ItemViewHolderPresenter{

  @NonNull
  private final Repository<Movie> mMovieRepository;

  @NonNull
  private final BaseSchedulerProvider mSchedulerProvider;

  @NonNull
  private final CompositeDisposable mCompositeDisposable;

  @Nullable
  private MainContract.View mView;

  @Inject
  MainPresenter(@NonNull Repository<Movie> movieRepository,
                @NonNull BaseSchedulerProvider schedulerProvider){
    mMovieRepository = movieRepository;
    mSchedulerProvider = schedulerProvider;
    mCompositeDisposable = new CompositeDisposable();
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

    Disposable disposable = mMovieRepository.getItems()
        .flatMap(Flowable::fromIterable)
        .toList()
        .subscribeOn(mSchedulerProvider.io())
        .observeOn(mSchedulerProvider.ui())
        .subscribe(
            this::handleOnNext
        );

    mCompositeDisposable.add(disposable);
  }

  private void handleOnNext(@NonNull List<Movie> movies){
    // TODO handle presenting the list of movies
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
  public void onAdapterBindViewHolder(MainContract.ItemViewHolder holder, int position) {

  }

  @Override
  public void onAdapterItemClicked(int position) {

  }

  @Override
  public int onAdapterRequestItemCount() {
    return 0;
  }
}
