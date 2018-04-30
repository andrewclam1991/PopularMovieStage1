package com.andrewclam.popularmovie;

import android.app.Application;
import android.support.annotation.NonNull;

import com.andrewclam.popularmovie.di.AppComponent;
import com.andrewclam.popularmovie.di.DaggerAppComponent;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;

/**
 * We create a custom {@link Application} class that extends  {@link DaggerApplication}.
 * We then override applicationInjector() which tells Dagger how to make our @Singleton Component
 * We never have to call `component.inject(this)` as {@link DaggerApplication} will do that for us.
 */
public class MyApplication extends DaggerApplication {

  @NonNull
  @Override
  protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
    AppComponent appComponent = DaggerAppComponent.builder().application(this).build();
    appComponent.inject(this);
    return appComponent;
  }
}
