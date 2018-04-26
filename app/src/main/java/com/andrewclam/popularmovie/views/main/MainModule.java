package com.andrewclam.popularmovie.views.main;


import android.support.annotation.NonNull;


import com.andrewclam.popularmovie.di.annotations.ActivityScoped;

import dagger.Binds;
import dagger.Module;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link MainPresenter}.
 */
@Module
public abstract class MainModule {
    @NonNull
    @ActivityScoped
    @Binds
    abstract MainContract.Presenter injectMainPresenter(@NonNull MainPresenter presenter);
}
