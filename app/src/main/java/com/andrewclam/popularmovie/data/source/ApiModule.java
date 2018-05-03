package com.andrewclam.popularmovie.data.source;

import android.content.Context;
import android.support.annotation.NonNull;

import com.andrewclam.popularmovie.R;

import dagger.Module;
import dagger.Provides;

/**
 * This is used by Dagger to inject the required arguments into the
 * {@link DataSourceRemote<>}.
 */
@Module
public abstract class ApiModule {

  @Provides
  @NonNull
  @ApiKey
  static String provideApiKey(@NonNull Context context) {
    return context.getString(R.string.tmdb_api_key);
  }
}
