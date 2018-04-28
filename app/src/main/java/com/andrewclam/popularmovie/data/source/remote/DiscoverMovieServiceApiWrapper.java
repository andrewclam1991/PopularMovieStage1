package com.andrewclam.popularmovie.data.source.remote;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.andrewclam.popularmovie.data.DataSource;
import com.andrewclam.popularmovie.data.ServiceApiWrapper;
import com.andrewclam.popularmovie.data.model.Movie;
import com.google.common.base.Optional;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class DiscoverMovieServiceApiWrapper implements DataSource<Movie> {

  public interface OnRequestApiUriCallback {
    Uri provideApiUri();
  }

  @NonNull
  private final DataSource<Movie> mDataSource;

  @Nullable
  private Uri mRequestUri;

  public DiscoverMovieServiceApiWrapper(@NonNull DataSource<Movie> dataSource){
    mDataSource = dataSource;
  }

  @NonNull
  public DataSource<Movie> setApiRequestUri(@NonNull OnRequestApiUriCallback callback){
    mRequestUri = callback.provideApiUri();
    return mDataSource;
  }

  // TODO extend getItems() behavior
  // TODO issue how to only affect the remote datasource but not local, but this wrapper wrapped
  // at the repository level
  // repository handled the remote datasource abstract and is invisible to implementation
  // but implementation wants to add new behavior to the remote datasource
  // how to do this in such a way that doesn't change the existing interface?
  // (Open for extension, Close to modification)
  @Override
  public final Flowable<List<Movie>> getItems() {
    return mDataSource.getItems();
  }

  // TODO extend getItem() behavior
  @Override
  public final Flowable<Optional<Movie>> getItem(@NonNull String entityId) {
    return mDataSource.getItem(entityId);
  }

  // Unmodified behaviors
  @Override
  public final void refresh() {
    mDataSource.refresh();
  }

  @Override
  public final Completable add(@NonNull Movie item) {
    return mDataSource.add(item);
  }

  @Override
  public final Completable addAll(@NonNull List<Movie> items) {
    return mDataSource.addAll(items);
  }

  @Override
  public final Completable update(@NonNull Movie item) {
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
