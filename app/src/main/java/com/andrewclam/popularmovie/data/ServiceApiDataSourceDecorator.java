package com.andrewclam.popularmovie.data;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.andrewclam.popularmovie.data.DataSource;
import com.andrewclam.popularmovie.data.model.Entity;
import com.google.common.base.Optional;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class ServiceApiDataSourceDecorator<E extends Entity> implements DataSource<E> {

  @NonNull
  private final DataSource<E> mDataSource;

  @Nullable
  private Uri mApiRequestUri;

  public ServiceApiDataSourceDecorator(@NonNull DataSource<E> dataSource){
    mDataSource = dataSource;
  }

  /**
   * add a behavior that allows client to supply a uri
   * @param callback when client sets a uri
   * @return an instance of this class to maintain api fluency.
   */
  @NonNull
  public DataSource<E> setApiRequestUri(@NonNull OnRequestApiUriCallback callback){
    mApiRequestUri = callback.provideApiUri();
    return this;
  }

  /**
   * Functional interface for client to provide an api request {@link Uri}
   */
  public interface OnRequestApiUriCallback {
    Uri provideApiUri();
  }

  // Unmodified behaviors
  @Override
  public final Flowable<List<E>> getItems() {
    return mDataSource.getItems();
  }

  @Override
  public final void refresh() {
    mDataSource.refresh();
  }

  @Override
  public final Flowable<Optional<E>> getItem(@NonNull String entityId) {
    return mDataSource.getItem(entityId);
  }

  @Override
  public final Completable add(@NonNull E item) {
    return mDataSource.add(item);
  }

  @Override
  public final Completable addAll(@NonNull List<E> items) {
    return mDataSource.addAll(items);
  }

  @Override
  public final Completable update(@NonNull E item) {
    return mDataSource.update(item);
  }

  @Override
  public final Completable remove(@NonNull String entityId) {
    return mDataSource.remove(entityId);
  }

  @Override
  public final Completable removeAll() {
    return mDataSource.removeAll();
  }

}
