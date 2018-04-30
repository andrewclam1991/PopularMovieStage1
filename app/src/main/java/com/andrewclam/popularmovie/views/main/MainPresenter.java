package com.andrewclam.popularmovie.views.main;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.andrewclam.popularmovie.data.Repository;
import com.andrewclam.popularmovie.data.source.api.ApiServiceDecorator;
import com.andrewclam.popularmovie.data.source.api.DiscoverMoviesApiService;
import com.andrewclam.popularmovie.data.source.api.TMDBApiServiceContract;
import com.andrewclam.popularmovie.data.model.Movie;
import com.andrewclam.popularmovie.util.schedulers.BaseSchedulerProvider;
import com.andrewclam.popularmovie.views.detail.DetailActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

class MainPresenter implements MainContract.Presenter, MainContract.ItemViewHolderPresenter {

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
  MainPresenter(@NonNull Repository<Movie> movieRepository,
                @NonNull BaseSchedulerProvider schedulerProvider) {
    mMovieRepository = new DiscoverMoviesApiService(movieRepository);
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
            this::handleOnNext,
            this::handleOnError
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

  private void handleOnError(@NonNull Throwable throwable){
    Log.e("MainPresenter", throwable.getLocalizedMessage());
    if (mView != null && mView.isActive()){
      mView.showLoadingMoviesError();
    }
  }

  @Override
  public void setFilterType(@NonNull FilterType type) {
    switch (type){
      case DEFAULT:
        break;
      case FAVORITES:
        break;
    }
  }

  @Override
  public void setSortType(@NonNull SortType type) {
    switch (type){
      case DEFAULT:
        break;
      case BY_POPULARITY:
        break;
      case BY_RATING:
        break;
    }
  }

  @Override
  public void setSortOrder(@NonNull SortOrder order) {
    switch (order){
      case DEFAULT:
        break;
      case ASC:
        break;
      case DESC:
        break;
    }
  }
  @NonNull
  private SortOrder mCurrentSortOrder = SortOrder.DEFAULT;
  @NonNull
  private SortType mCurrentSortType = SortType.DEFAULT;
  @NonNull
  private FilterType mCurrentFilterType = FilterType.DEFAULT;

  @NonNull
  @Override
  public FilterType getCurrentFilterType() {
    return mCurrentFilterType;
  }

  @NonNull
  @Override
  public SortType getCurrentSortType() {
    return mCurrentSortType;
  }

  @NonNull
  @Override
  public SortOrder getCurrentSortOrder() {
    return mCurrentSortOrder;
  }


  @Override
  public void onAdapterBindViewHolder(MainContract.ItemViewHolder holder, int position) {
    if (position < 0){
      return; // invalid position
    }
    Movie movie = mMovies.get(position);
    String posterPath = movie.getPosterPath();
    String posterUrl = TMDBApiServiceContract.TMDBImageContract.getMoviePosterImageUri(posterPath).toString();
    holder.loadMoviePoster(posterUrl);
  }

  @Override
  public void onAdapterItemClicked(int position) {
    if (position < 0){
      return; // invalid position
    }

    Movie movie = mMovies.get(position);
    String id = movie.getUid();
    // TODO launch detail activity with the id
    mView.showDetailUi(id,MainActivity.class);
  }

  @Override
  public int onAdapterRequestItemCount() {
    return mMovies.size();
  }
}
