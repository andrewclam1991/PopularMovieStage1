package com.andrewclam.popularmovie.views.detail;


import android.support.annotation.NonNull;

import com.andrewclam.popularmovie.di.annotations.ActivityScoped;
import com.andrewclam.popularmovie.di.annotations.FragmentScoped;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link DetailPresenter}.
 */
@Module
public abstract class DetailModule {
  @NonNull
  @ActivityScoped
  @Binds
  abstract DetailContract.Presenter providePresenter(@NonNull DetailPresenter presenter);

  @Provides
  @NonNull
  @ActivityScoped
  static Integer provideMovieId(@NonNull DetailActivity activity){
    return 0;
  }

  @NonNull
  @FragmentScoped
  @ContributesAndroidInjector
  abstract DetailFragment provideFragment();
}
