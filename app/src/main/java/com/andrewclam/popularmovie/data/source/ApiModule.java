package com.andrewclam.popularmovie.data.source;

import android.content.Context;
import android.support.annotation.NonNull;

import com.andrewclam.popularmovie.R;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.andrewclam.popularmovie.data.modelapi.BaseContract.BASE_TMDB_REQUEST_URL;

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

  @Provides
  @NonNull
  @Singleton
  static Retrofit providesRetrofit(){
    return new Retrofit.Builder()
        .baseUrl(BASE_TMDB_REQUEST_URL.concat("/"))
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build();
  }
}
