package com.andrewclam.popularmovie.views.detail;


import android.content.Intent;
import android.support.annotation.NonNull;

import com.andrewclam.popularmovie.data.model.MovieId;
import com.andrewclam.popularmovie.di.annotations.ActivityScoped;
import com.andrewclam.popularmovie.di.annotations.FragmentScoped;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

import static com.andrewclam.popularmovie.util.ActivityUtils.getIntentAction;

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
  @ActivityScoped
  @MovieId
  static long provideMovieId(@NonNull DetailActivity activity){
    switch (getIntentAction(activity)) {
      case Intent.ACTION_VIEW:
        final long itemId = activity.getIntent()
            .getLongExtra(DetailActivity.ARG_DETAIL_ACT_ITEM_ID, -1L);

        if (itemId < 0) {
          throw new IllegalArgumentException("intent extra must contain an itemId");
        }

        return itemId;
      default:
        // Unable to handle action
        throw new UnsupportedOperationException("Unable to handle the intent's set action");
    }
  }

  @NonNull
  @FragmentScoped
  @ContributesAndroidInjector
  abstract DetailFragment provideFragment();
}
