package com.andrewclam.popularmovie.views.main;


import android.support.annotation.NonNull;

import com.andrewclam.popularmovie.di.annotations.ActivityScoped;
import com.andrewclam.popularmovie.di.annotations.FragmentScoped;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link MainPresenterMovie}.
 */
@Module
public abstract class MainModule {
  @NonNull
  @ActivityScoped
  @Binds
  abstract MainContract.Presenter providePresenter(@NonNull MainPresenterMovie presenter);

  @NonNull
  @ActivityScoped
  @Binds
  abstract MainContract.MovieItemPresenter<MainContract.ItemViewHolder> provideItemViewHolderPresenter
      (@NonNull MainPresenterMovie presenter);

  @NonNull
  @FragmentScoped
  @ContributesAndroidInjector
  abstract MainFragment provideFragment();
}
