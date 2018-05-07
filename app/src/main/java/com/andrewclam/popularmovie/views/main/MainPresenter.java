package com.andrewclam.popularmovie.views.main;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.andrewclam.popularmovie.data.DataSource;
import com.andrewclam.popularmovie.data.model.Movie;
import com.andrewclam.popularmovie.data.source.Repo;
import com.andrewclam.popularmovie.util.schedulers.BaseSchedulerProvider;
import com.andrewclam.popularmovie.views.detail.DetailActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static com.andrewclam.popularmovie.data.modelapi.MovieImageContract.getImageUrl;

/**
 * Implementation of a {@link MainContract.Presenter} responsible for
 * interfacing {@link MainContract.View} with the underlying Model layer
 */
@Singleton
class MainPresenter implements MainContract.Presenter, MainContract.MovieItemPresenter {

  @NonNull
  private final DataSource<Movie> mMovieRepository;

  @NonNull
  private final BaseSchedulerProvider mSchedulerProvider;

  @NonNull
  private final CompositeDisposable mCompositeDisposable;

  @Nullable
  private MainContract.View mView;

  @NonNull
  private List<Movie> mMovies;

  @Inject
  MainPresenter(@NonNull @Repo DataSource<Movie> movieRepository,
                @NonNull BaseSchedulerProvider schedulerProvider) {
    mMovieRepository = movieRepository;
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
    mCompositeDisposable.clear();
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
            this::handleOnNext,
            this::handleOnError
        );

    mCompositeDisposable.add(disposable);
  }

  private void handleOnNext(@NonNull List<Movie> movies) {
    mMovies = movies;
    if (mView != null && mView.isActive()) {
      mView.onDataSetChanged();
    }
  }

  private void handleOnError(@NonNull Throwable throwable){
    Log.e("Pop", throwable.getLocalizedMessage());
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
    String posterUrl = getImageUrl(posterPath);
    holder.loadMoviePoster(posterUrl);
  }

  @Override
  public void onAdapterItemClicked(int position) {
    if (mView == null || !mView.isActive() || position < 0){
      return; // invalid position
    }
    Movie movie = mMovies.get(position);
    long id = movie.getMovieId();
    mView.showDetailUi(id, DetailActivity.class);
  }

  @Override
  public int onAdapterRequestItemCount() {
    return mMovies.size();
  }
}
