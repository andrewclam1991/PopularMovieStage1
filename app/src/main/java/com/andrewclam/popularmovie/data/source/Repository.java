package com.andrewclam.popularmovie.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.andrewclam.popularmovie.data.DataSource;
import com.andrewclam.popularmovie.data.model.Entity;
import com.google.common.base.Optional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Flowable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Model class for storing, retrieving, updating and deleting implementing {@link E}'s data
 * This class implements the Repository design pattern to offer support n number of data sources
 * while abstracting data sources into a single interface with the implementing Presenter class.
 * This design aids Model's future scalability and usability.
 * <p>
 * This class implements the Template (Generic) design pattern to abstract handling entity
 * datasource CRUD and caching operations from concrete {@link Repository<E>} implementations.
 * This design further aids Model's implementation scalability by reducing redundant code on
 * maintenance and upgrade.
 * <p>
 * For simplicity, this implements a synchronization between locally persisted data and data
 * obtained from the remote server(s), by using the remote data source(s) only if the data from the
 * local data source doesn't exist or is empty. Results are cached in memory for even faster
 * retrieval during usage.
 */
@Singleton
class Repository<E extends Entity> implements DataSource<E> {

  @NonNull
  private final DataSource<E> mRemoteDataSource;

  @NonNull
  private final DataSource<E> mLocalDataSource;

  /**
   * This variable has package local visibility so it can be accessed from tests.
   */
  @VisibleForTesting
  @NonNull
  final Map<String, E> mCachedItems;

  /**
   * Marks the cache as invalid, to force an update the next time data is requested. This variable
   * has package local visibility so it can be accessed from tests.
   * Note: default set flag to false, so at init (with mCachedItems empty),
   * repository will always try local-first
   */
  @VisibleForTesting
  boolean mCacheIsDirty = false;

  /**
   * By marking the constructor with {@code @Inject}, Dagger will try to inject the dependencies
   * required to create an instance of the TasksRepository. Because {@link DataSource<E>} is an
   * interface, we must provide to Dagger a way to buildAs those arguments, this is done in
   * {@link Repository<E>}.
   * <p>
   * When two arguments or more have the same type, we must provide to Dagger a way to
   * differentiate them. This is done using a qualifier.
   * <p>
   * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
   * with {@code @Nullable} values.
   */
  @VisibleForTesting
  @Inject
  Repository(@NonNull @Remote DataSource<E> remoteDataSource,
             @NonNull @Local DataSource<E> localDataSource) {
    mRemoteDataSource = checkNotNull(remoteDataSource, "remoteDataSource can't be null!");
    mLocalDataSource = checkNotNull(localDataSource, "localDataSource can't be null!");
    mCachedItems = new LinkedHashMap<>();
  }

  /**
   * Gets properties from cache, local data source (SQLite) or remote data source, whichever is
   * available first.
   */
  @NonNull
  @Override
  public Flowable<List<E>> getItems() {
    // Respond immediately with cache if available and not dirty
    if (!mCachedItems.isEmpty() && !mCacheIsDirty) {
      return Flowable.fromIterable(mCachedItems.values()).toList().toFlowable();
    }

    // Repository starts with clean cache (mCacheIsDirty = false);
    // Queries local first
    // if local empty, try remote,
    // if local has data, add each item to cache, return data.
    // if remote empty, return no data.
    // if remote has data, add each item to local and cache, mark cache as clean return data.

    Flowable<List<E>> remoteItems = saveRemoteItems(mRemoteDataSource.getItems());

    if (mCacheIsDirty) {
      // refresh local data with remote
      return remoteItems;
    } else {
      // query local and remote data sources, emit the first result
      Flowable<List<E>> localItems = cacheLocalItems(mLocalDataSource.getItems());
      return Flowable.concat(localItems, remoteItems)
          .filter(items -> !items.isEmpty())
          .firstOrError()
          .toFlowable();
    }
  }

  @NonNull
  @Override
  public Flowable<List<E>> getItems(@NonNull Map<String, String> options) {
    // Repository starts with clean cache (mCacheIsDirty = false);
    // Queries local first
    // if local empty, try remote,
    // if local has data, add each item to cache, return data.
    // if remote empty, return no data.
    // if remote has data, add each item to local and cache, mark cache as clean return data.

    Flowable<List<E>> localItems = cacheLocalItems(mLocalDataSource.getItems(options));
    Flowable<List<E>> remoteItems = saveRemoteItems(mRemoteDataSource.getItems(options));

    return Flowable.concat(localItems, remoteItems)
        .filter(items -> !items.isEmpty())
        .firstOrError()
        .toFlowable();
  }

  @NonNull
  private Flowable<List<E>> cacheLocalItems(@NonNull Flowable<List<E>> localItems) {
    return localItems.flatMap(items -> Flowable.fromIterable(items)
        .doOnNext(this::saveItemToCache)
        .toList()
        .toFlowable()
    ).takeWhile(items -> !items.isEmpty());// this completes the stream when the list becomes empty
  }

  @NonNull
  private Flowable<List<E>> saveRemoteItems(@NonNull Flowable<List<E>> remoteItems) {
    return remoteItems.flatMap(items -> Flowable.fromIterable(items)
        .doOnNext(item -> mLocalDataSource.add(item).andThen(saveItemToCache(item)))
        .toList()
        .toFlowable()
    ).doOnComplete(() -> mCacheIsDirty = false);
  }

  @NonNull
  @Override
  public Flowable<Optional<E>> getItem(@NonNull final String itemId) {
    checkNotNull(itemId);

    final E cachedItem = getItemWithIdFromCache(itemId);

    // Respond with the if it is available in cache
    if (cachedItem != null) {
      return Flowable.just(Optional.of(cachedItem));
    }

    // Create an Observable to query the item in the local data source
    Flowable<Optional<E>> localItem = getLocalItemById(itemId);

    // Create an Observable to query the item in the remote data source, and download it
    Flowable<Optional<E>> remoteItem = getRemoteItemById(itemId);

    // Concat the local and remote sources into one,
    return Flowable.concat(localItem, remoteItem)
        .firstElement()
        .toFlowable();
  }

  @NonNull
  private Flowable<Optional<E>> getLocalItemById(@NonNull final String itemId) {
    return mLocalDataSource.getItem(itemId)
        .takeWhile(Optional::isPresent)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .flatMap(item -> saveItemToCache(item).andThen(Flowable.just(Optional.of(item))));
  }

  @NonNull
  private Flowable<Optional<E>> getRemoteItemById(@NonNull final String itemId) {
    return mRemoteDataSource.getItem(itemId)
        .flatMap(itemOptional -> {
          if (itemOptional.isPresent()) {
            E item = itemOptional.get();
            return mLocalDataSource.add(item)
                .andThen(saveItemToCache(item))
                .andThen(Flowable.just(itemOptional));
          } else {
            return Flowable.just(Optional.absent());
          }
        });
  }

  @NonNull
  @Override
  public Completable add(@NonNull E item) {
    mCachedItems.put(item.getUid(), checkNotNull(item));
    return mLocalDataSource.add(item).andThen(mRemoteDataSource.add(item));
  }

  @NonNull
  @Override
  public Completable addAll(@NonNull List<E> items) {
    for (E item : items) {
      mCachedItems.put(item.getUid(), checkNotNull(item));
    }
    return mLocalDataSource.addAll(items).andThen(mRemoteDataSource.addAll(items));
  }

  @NonNull
  @Override
  public Completable update(@NonNull E item) {
    mCachedItems.put(item.getUid(), item);
    return mLocalDataSource.update(item).andThen(mRemoteDataSource.update(item));
  }

  @NonNull
  @Override
  public Completable remove(@NonNull String entityId) {
    if (!mCachedItems.isEmpty() && mCachedItems.containsKey(entityId)) {
      mCachedItems.remove(entityId);
    }
    return mLocalDataSource.remove(entityId).andThen(mRemoteDataSource.remove(entityId));
  }

  @NonNull
  @Override
  public Completable removeAll() {
    mCachedItems.clear();
    return mLocalDataSource.removeAll().andThen(mRemoteDataSource.removeAll());
  }

  @Nullable
  private E getItemWithIdFromCache(@NonNull final String itemId) {
    checkNotNull(itemId);

    if (mCachedItems.isEmpty()) {
      return null;
    } else {
      // still might not be able to find the item with id, return a nullable item
      return mCachedItems.get(itemId);
    }
  }

  @Override
  public void refresh() {
    mCacheIsDirty = true;
  }

  @VisibleForTesting
  private Completable saveItemToCache(@NonNull E item) {
    mCachedItems.put(item.getUid(), item);
    return Completable.complete();
  }

}
