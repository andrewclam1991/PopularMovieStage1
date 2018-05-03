package com.andrewclam.popularmovie.data.source;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.andrewclam.popularmovie.BuildConfig;
import com.andrewclam.popularmovie.data.DataSource;
import com.andrewclam.popularmovie.data.model.Entity;
import com.google.common.base.Optional;
import com.squareup.sqlbrite3.BriteContentResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.functions.Function;

import static com.andrewclam.popularmovie.data.db.AppDbContract.PATH_UID;
import static com.google.common.base.Preconditions.checkNotNull;

abstract class DataSourceLocal<E extends Entity> implements DataSource<E> {

  // LOG TAG
  private static final String LOG_TAG = DataSourceLocal.class.getSimpleName();

  @Override
  public void refresh() {
    // no implementation, it is handled in the repository
  }

  @Inject
  ContentResolver mContentResolver;

  @Inject
  BriteContentResolver mBriteContentResolver;

  @NonNull
  private final Uri mContentUri;

  @NonNull
  private final Function<Cursor, E> mMapperFunction;

  @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
  DataSourceLocal() {
    mContentUri = setContentUri();
    mMapperFunction = this::mapToItem;
  }

  // TODO implement local data query base on options
  @NonNull
  @Override
  public Flowable<List<E>> getItems(@NonNull Map<String, String> options) {
    return getItems();
  }

  @NonNull
  @Override
  public Flowable<List<E>> getItems() {
    checkNotNull(mBriteContentResolver, "mBriteContentResolver cannot be null!");
    return mBriteContentResolver.createQuery(mContentUri,
        null,
        null,
        null,
        null,
        true)
        .mapToList(mMapperFunction)
        .toFlowable(BackpressureStrategy.BUFFER);
  }

  @NonNull
  @Override
  public Flowable<Optional<E>> getItem(@NonNull String entityId) {
    checkNotNull(entityId, "item id can't be null for getDocumentUri()");
    checkNotNull(mBriteContentResolver, "mBriteContentResolver cannot be null!");

    Uri uri = mContentUri.buildUpon()
        .appendPath(PATH_UID)
        .appendPath(entityId)
        .build();

    return mBriteContentResolver.createQuery(uri,
        null,
        null,
        null,
        null,
        true)
        .mapToOneOrDefault(cursor -> Optional.of(mMapperFunction.apply(cursor)),
            Optional.<E>absent())
        .toFlowable(BackpressureStrategy.BUFFER);
  }

  @NonNull
  @Override
  public Completable add(@NonNull E item) {
    return Completable.create(emitter -> {
      Uri uri = mContentResolver.insert(mContentUri, from(item));
      if (uri != null) {
        if (BuildConfig.DEBUG) Log.d(LOG_TAG, "Save item success.");
        emitter.onComplete();
      } else {
        if (BuildConfig.DEBUG) Log.e(LOG_TAG, "Save item failure.");
        emitter.onError(new IOException());
      }
    });
  }

  @NonNull
  @Override
  public Completable addAll(@NonNull List<E> items) {
    return Completable.create(emitter -> {
      List<ContentValues> cvList = new ArrayList<>();
      for (E item : items) {
        ContentValues cv = from(item);
        cvList.add(cv);
      }
      ContentValues[] cvArray = (ContentValues[]) cvList.toArray();
      int numInserted = mContentResolver.bulkInsert(mContentUri, cvArray);
      if (numInserted > 0) {
        if (BuildConfig.DEBUG) Log.d(LOG_TAG, "Bulk insert item success.");
        emitter.onComplete();
      } else {
        if (BuildConfig.DEBUG) Log.d(LOG_TAG, "Bulk insert items failed.");
        emitter.onError(new IOException());
      }
    });
  }

  @NonNull
  @Override
  public Completable update(@NonNull E item) {
    return Completable.create(emitter -> {
      final Uri updateUri = mContentUri.buildUpon().appendPath(PATH_UID)
          .appendPath(item.getUid()).build();

      int rowsUpdated = mContentResolver.update(updateUri, from(item),
          null, null);
      if (rowsUpdated > 0) {
        if (BuildConfig.DEBUG) Log.d(LOG_TAG, "Update item success.");
        emitter.onComplete();
      } else {
        if (BuildConfig.DEBUG) Log.e(LOG_TAG, "Update item failure.");
        emitter.onError(new IOException());
      }
    });
  }

  @NonNull
  @Override
  public Completable remove(@NonNull String entityId) {
    return Completable.create(emitter -> {
      final Uri deleteUri = mContentUri.buildUpon().appendPath(PATH_UID)
          .appendPath(entityId).build();
      int rowDeleted = mContentResolver.delete(deleteUri, null, null);
      if (rowDeleted > 0) {
        if (BuildConfig.DEBUG) Log.d(LOG_TAG, "Delete item success.");
        emitter.onComplete();
      } else {
        if (BuildConfig.DEBUG) Log.e(LOG_TAG, "Delete item failure.");
        emitter.onError(new IOException());
      }
    });
  }

  @NonNull
  @Override
  public Completable removeAll() {
    return Completable.create(emitter -> {
      int rowDeleted = mContentResolver.delete(mContentUri, null, null);
      if (rowDeleted > 0) {
        if (BuildConfig.DEBUG) Log.d(LOG_TAG, "Delete all items success. deleted: " + rowDeleted);
        emitter.onComplete();
      } else {
        if (BuildConfig.DEBUG) Log.e(LOG_TAG, "Delete all items failure.");
        emitter.onError(new IOException());
      }
    });
  }

  /**
   * abstract method that sub-class must implement to correctly map
   * the content values from an item
   *
   * @param item item that is to be parsed into {@link ContentValues} for
   *             persistence
   */
  @VisibleForTesting
  @NonNull
  abstract ContentValues from(@NonNull final E item);

  /**
   * abstract method that sub-class must implement to correctly map
   * an object item from a {@link Cursor} object
   *
   * @param c cursor that contains the stored data
   * @return a mapped object of type {@link E}
   */
  @VisibleForTesting
  @NonNull
  abstract E mapToItem(@NonNull final Cursor c);

  /**
   * abstract method that sub-class must implement to provide the
   * correct contentUri
   *
   * @return sub-class supplied content uri
   */
  @VisibleForTesting
  @NonNull
  abstract Uri setContentUri();

}
