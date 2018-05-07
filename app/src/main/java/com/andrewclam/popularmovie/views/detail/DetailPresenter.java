package com.andrewclam.popularmovie.views.detail;

import android.support.annotation.NonNull;
import android.util.Log;

import com.andrewclam.popularmovie.BuildConfig;
import com.andrewclam.popularmovie.data.DataSource;
import com.andrewclam.popularmovie.data.model.Movie;
import com.andrewclam.popularmovie.data.model.MovieId;
import com.andrewclam.popularmovie.data.source.Repo;
import com.andrewclam.popularmovie.util.idlingresource.EspressoIdlingResource;
import com.andrewclam.popularmovie.util.schedulers.BaseSchedulerProvider;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.primitives.Ints;

import javax.annotation.Nullable;
import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static com.andrewclam.popularmovie.data.modelapi.MovieImageContract.getImageUrl;

class DetailPresenter implements DetailContract.Presenter {

  private final static String LOG_TAG = DetailPresenter.class.getSimpleName();

  @Nullable
  private DetailContract.View mView;

  @NonNull
  private final BaseSchedulerProvider mSchedulerProvider;

  @NonNull
  private final CompositeDisposable mCompositeDisposable;

  @NonNull
  private final DataSource<Movie> mRepository;

  @NonNull
  private final String mMovieId;

  @Nullable
  private Movie mMovie;

  @Inject
  DetailPresenter(@NonNull @Repo DataSource<Movie> repository,
                  @NonNull BaseSchedulerProvider schedulerProvider,
                  @MovieId long movieId){
    mRepository = repository;
    mSchedulerProvider = schedulerProvider;
    mMovieId = String.valueOf(movieId);
    mCompositeDisposable = new CompositeDisposable();
  }

  @Override
  public void setView(@NonNull DetailContract.View view) {
    mView = view;
  }

  @Override
  public void dropView() {
    mView = null;
    mMovie = null;
    mCompositeDisposable.clear();
  }

  @Override
  public void loadMovie() {
    if (mView == null || !mView.isActive()) {
      return; // View is already dead, no need to process
    }

    if (Strings.isNullOrEmpty(mMovieId)) {
      mView.showMissingMovieError();
      return;
    }

    // The network request might be handled in a different thread so make sure Espresso knows
    // that the app is busy until the response is handled.
    EspressoIdlingResource.increment(); // App is busy until further notice

    mView.setLoadingIndicator(true);

    mRepository.refresh();

    Log.e(LOG_TAG,"Start loading movie with id: " + mMovieId);

    Disposable disposable = mRepository.getItem(mMovieId)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .subscribeOn(mSchedulerProvider.computation())
        .observeOn(mSchedulerProvider.ui())
        .doFinally(() -> {
          if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
            EspressoIdlingResource.decrement(); // Set app as idle.
          }
        })
        .subscribe(
            this::handleOnNext,
            this::handleOnError
        );

    mCompositeDisposable.add(disposable);
  }

  /**
   * Handles when {@link Movie} item is loaded
   * @param movie the item that contains the data
   */
  private void handleOnNext(@NonNull Movie movie){
    Log.e(LOG_TAG,"Got movieUid: " + movie.getUid() + " movieId: " + movie.getMovieId());

    if (mView == null || !mView.isActive()){
      return; // View is dead, no presentation
    }

    // Store the movie data as a instance
    mMovie = movie;

    // Get data from item
    String posterPath = movie.getPosterPath();
    String posterUrl = getImageUrl(posterPath);
    String title = movie.getTitle();
    String releaseDate = movie.getReleaseDate();
    double voteAverage = movie.getVoteAverage();
    long voteCount = movie.getVoteCount();
    String overView = movie.getOverview();

    // Call View to show/hide item data depending on availability
    if (Strings.isNullOrEmpty(posterUrl)) {
      mView.hidePosters();
    }else {
      mView.showPosterBanner(posterUrl);
      mView.showPosterIcon(posterUrl);
    }

    if (Strings.isNullOrEmpty(title)){
      mView.hideTitle();
    }else {
      mView.showTitle(title);
    }

    if (Strings.isNullOrEmpty(releaseDate)){
      mView.hideReleaseDate();
    }else {
      mView.showReleaseDate(releaseDate);
    }

    if (voteAverage <= 0){
      mView.hideVoteAverage();
    }else {
      mView.showVoteAverage(voteAverage);
    }

    if (voteCount <= 0) {
      mView.hideVoteCount();
    }else {
      try {
        int voteCountInt = Ints.checkedCast(voteCount);
        mView.showVoteCount(voteCountInt);
      }catch (IllegalArgumentException e){
        if(BuildConfig.DEBUG)Log.e(LOG_TAG,e.getLocalizedMessage());
        mView.hideVoteCount();
      }
    }

    if(Strings.isNullOrEmpty(overView)){
      mView.hideOverView();
    }else {
      mView.showOverView(overView);
    }

    mView.setLoadingIndicator(false);
  }

  /**
   * Handles when error occurred in querying {@link Movie}
   * @param t throwable exception
   */
  private void handleOnError(@NonNull Throwable t){
    if (mView != null && mView.isActive()) {
      mView.showLoadingMovieError();
    }
    if(BuildConfig.DEBUG){
      Log.e(LOG_TAG,t.getLocalizedMessage());
      t.printStackTrace();
    }
  }

  @Override
  public void loadMovieFavoriteStatus() {
    // TODO load favorite status from the model and call view to set the favorite status
  }

  @Override
  public void handleOnShareClicked() {
    if (mView == null || !mView.isActive()){
      return; // View is dead
    }

    if (mMovie == null){
      mView.showMissingMovieError();
      loadMovie();
    }else {
      String mimeType = "text/plain";
      String content = ""; // TODO load trailer url
      mView.showShare(mimeType,content);
    }
  }


}
