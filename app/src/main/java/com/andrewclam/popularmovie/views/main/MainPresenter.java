package com.andrewclam.popularmovie.views.main;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.andrewclam.popularmovie.data.DataSource;
import com.andrewclam.popularmovie.data.Repository;
import com.andrewclam.popularmovie.data.api.TMDBApiService;
import com.andrewclam.popularmovie.data.model.Movie;
import com.andrewclam.popularmovie.data.ServiceApiDecorator;
import com.andrewclam.popularmovie.util.schedulers.BaseSchedulerProvider;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.andrewclam.popularmovie.data.api.TMDBApiContract.TMDBContract.BASE_TMDB_REQUEST_URL;

public class MainPresenter implements MainContract.Presenter, MainContract.ItemViewHolderPresenter {

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
  MainPresenter(@NonNull Repository<Movie> movieRepository,
                @NonNull BaseSchedulerProvider schedulerProvider) {
    mMovieRepository = new ServiceApiDecorator<>(movieRepository)
        .setApiService(this::provideApiService)
        .setApiKey(() -> "somekey");

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

  @NonNull
  private TMDBApiService provideApiService() {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(BASE_TMDB_REQUEST_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build();
    return retrofit.create(TMDBApiService.class);
  }

  private void handleOnNext(@NonNull List<Movie> movies) {
    // refresh in-memory list
    mMovies.clear();
    if (!movies.isEmpty()){
      // has content
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
