package com.andrewclam.popularmovie.views.main;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.andrewclam.popularmovie.data.DataSource;
import com.andrewclam.popularmovie.data.Repository;
import com.andrewclam.popularmovie.data.api.TMDBApiContract;
import com.andrewclam.popularmovie.data.model.Movie;
import com.andrewclam.popularmovie.data.ServiceApiDataSourceDecorator;
import com.andrewclam.popularmovie.util.schedulers.BaseSchedulerProvider;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class MainPresenter implements MainContract.Presenter, MainContract.ItemViewHolderPresenter {

  @NonNull
  private final DataSource<Movie> mMovieRepository;

  @NonNull
  private final BaseSchedulerProvider mSchedulerProvider;

  @NonNull
  private final CompositeDisposable mCompositeDisposable;

  @Nullable
  private MainContract.View mView;

  @Inject
  MainPresenter(@NonNull Repository<Movie> movieRepository,
                @NonNull BaseSchedulerProvider schedulerProvider) {
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

    Disposable disposable =
        new ServiceApiDataSourceDecorator<>(mMovieRepository)
            .setApiRequestUri(this::getInstanceApiRequestUri)
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

  /**
   * Determines the uri to use for querying the service api
   * this method can vary in runtime and change uri base on user selection
   * @return runtime-generated uri
   */
  private Uri getInstanceApiRequestUri(){
    return TMDBApiContract.TMDBContract.DiscoverMovie.getRequestUriWithParams("",null);
  }

  private void handleOnNext(@NonNull List<Movie> movies) {
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
