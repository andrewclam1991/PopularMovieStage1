package com.andrewclam.popularmovie.di.modules;


import com.andrewclam.popularmovie.util.schedulers.BaseSchedulerProvider;
import com.andrewclam.popularmovie.util.schedulers.SchedulerProvider;

import dagger.Module;
import dagger.Provides;

@Module
abstract public class SchedulerProviderModule {

  @Provides
  static BaseSchedulerProvider provideSchedulerProvider() {
    return SchedulerProvider.getInstance();
  }
}
