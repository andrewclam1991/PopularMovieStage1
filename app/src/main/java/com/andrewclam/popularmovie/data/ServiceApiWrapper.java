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

public class ServiceApiWrapper<E extends Entity> implements DataSource<E> {

  @NonNull
  private final DataSource<E> mDataSource;

  @Nullable
  private Uri mApiRequestUri;

  public ServiceApiWrapper(@NonNull DataSource<E> dataSource){
    mDataSource = dataSource;
  }

  @NonNull
  public DataSource<E> setApiRequestUri(@NonNull OnRequestApiUriCallback callback){
    mApiRequestUri = callback.provideApiUri();
    return this;
  }

  // TODO extend getItems() behavior
  // TODO issue how to only affect the remote datasource but not local, but this wrapper wrapped
  // at the repository level
  // repository handled the remote datasource abstract and is invisible to implementation
  // but implementation wants to add new behavior to the remote datasource
  // how to do this in such a way that doesn't change the existing interface?
  // (Open for extension, Close to modification)
  @Override
  public final Flowable<List<E>> getItems() {
    return mDataSource.getItems();
  }

  // TODO extend getItem() behavior
  @Override
  public final Flowable<Optional<E>> getItem(@NonNull String entityId) {
    return mDataSource.getItem(entityId);
  }

  public interface OnRequestApiUriCallback {
    Uri provideApiUri();
  }

  // Unmodified behaviors
  @Override
  public final void refresh() {
    mDataSource.refresh();
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
