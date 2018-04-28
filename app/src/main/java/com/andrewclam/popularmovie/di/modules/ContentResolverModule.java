package com.andrewclam.popularmovie.di.modules;


import android.content.ContentResolver;
import android.content.Context;
import android.support.annotation.NonNull;

import com.andrewclam.popularmovie.data.DataSource;
import com.andrewclam.popularmovie.data.ServiceApiDataSourceDecorator;
import com.andrewclam.popularmovie.data.model.Movie;
import com.andrewclam.popularmovie.util.schedulers.BaseSchedulerProvider;
import com.squareup.sqlbrite3.BriteContentResolver;
import com.squareup.sqlbrite3.SqlBrite;

import dagger.Module;
import dagger.Provides;

@Module
abstract public class ContentResolverModule {

  /**
   * Provides app-wide contentResolver, responsible for resolving uri to the right content
   *
   * @param context app context
   * @return app-wide contentResolver
   */
  @Provides
  @NonNull
  static ContentResolver provideContentResolver(@NonNull Context context) {
    return context.getContentResolver();
  }

  /**
   * Provides a Sqlbrite wrapped version of the ContentResolver, used querying database
   * as a stream of items using RxJava
   *
   * @param contentResolver   app-wide contentResolver, responsible for resolving uri to the right content
   * @param schedulerProvider app-wide schedulerProvider, manager for threads.
   * @return a Sqlbrite wrapped version of the ContentResolver
   */
  @Provides
  @NonNull
  static BriteContentResolver provideBriteContentResolver(@NonNull ContentResolver contentResolver,
                                                          @NonNull BaseSchedulerProvider schedulerProvider) {
    SqlBrite sqlBrite = new SqlBrite.Builder().build();
    return sqlBrite.wrapContentProvider(contentResolver, schedulerProvider.io());
  }

}
