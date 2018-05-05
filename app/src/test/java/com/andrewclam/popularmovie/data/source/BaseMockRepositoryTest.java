package com.andrewclam.popularmovie.data.source;

import com.andrewclam.popularmovie.data.DataSource;
import com.andrewclam.popularmovie.data.model.Entity;
import com.google.common.base.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the implementation of the in-memory {@link Repository<>} with cache
 * using {@link org.mockito.Mockito}
 */
public abstract class BaseMockRepositoryTest<E extends Entity> {

  private List<E> ITEMS;

  private E ITEM;

  private Class<E> ITEM_CLASS;

  private Map<String,String> OPTIONS;

  private Repository<E> mRepository;

  @Mock
  private DataSource<E> mRemoteDataSource;

  @Mock
  private DataSource<E> mLocalDataSource;

  @Before
  public void setupRepository(){
    // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
    // inject the mocks in the test the initMocks method needs to be called.
    MockitoAnnotations.initMocks(this);

    // Get a reference to the class under test
    mRepository = new Repository<>(mRemoteDataSource,mLocalDataSource);

    // Generate a test items
    ITEMS = provideTestItemsList();
    ITEM = provideTestItem();
    ITEM_CLASS = provideTestItemClass();
    OPTIONS = provideTestGetItemsOptions();
  }

  @After
  public void cleanup(){
    mRepository = null;
  }

  /**
   * Allow test subclass to provide a list of test items of type {@link E}
   * @return provides a list of test items
   */
  abstract List<E> provideTestItemsList();

  /**
   * Allow test subclass to provide a test item of type {@link E}
   * @return a test item
   */
  abstract E provideTestItem();

  /**
   * Allow test subclass to provide the test item's Class
   * @return a test item 's Class
   */
  abstract Class<E> provideTestItemClass();

  abstract Map<String,String> provideTestGetItemsOptions();

  /**
   * Tests - Model Create
   */
  @Test
  public void saveItems_savesItemsToRemoteDataSource() {
    // Given when items are saved, they will save successfully in local and remote data sources
    new ArrangeBuilder()
        .withItemsAdded(mLocalDataSource, ITEMS)
        .withItemsAdded(mRemoteDataSource, ITEMS);

    // When a list of items are saved to the repository
    TestObserver testObserver = new TestObserver();
    mRepository.addAll(ITEMS).subscribe(testObserver);

    // Verify addAll(items) is called at the remote data source
    verify(mRemoteDataSource).addAll(ITEMS);

    // Then observed issue completes without error
    testObserver.assertComplete();
    testObserver.assertNoErrors();
  }
  @Test
  public void saveItem_savesItemToRemoteDataSource() {
    // Given when an item is saved, it will save successfully in local and remote data sources
    new ArrangeBuilder()
        .withItemAdded(mLocalDataSource, ITEM)
        .withItemAdded(mRemoteDataSource, ITEM);

    // When an item is saved to the repository
    TestObserver testObserver = new TestObserver();
    mRepository.add(ITEM).subscribe(testObserver);

    // Verify add(item) is called at the remote data source
    verify(mRemoteDataSource).add(ITEM);

    // Then completable completes without error
    testObserver.assertComplete();
    testObserver.assertNoErrors();

    // And that cache is updated
    assertThat(mRepository.mCachedItems.size(), is(1));
  }

  @Test
  public void saveItems_savesItemsToLocalDataSource() {
    // Given when items are saved, they will save successfully in local and remote data sources
    new ArrangeBuilder()
        .withItemsAdded(mLocalDataSource, ITEMS)
        .withItemsAdded(mRemoteDataSource, ITEMS);

    // When a list of items are saved to the repository
    TestObserver testObserver = new TestObserver();
    mRepository.addAll(ITEMS).subscribe(testObserver);

    // Verify addAll(items) is called at the local data source
    verify(mLocalDataSource).addAll(ITEMS);

    // Then completable completes without error
    testObserver.assertComplete();
    testObserver.assertNoErrors();
  }

  @Test
  public void saveItem_savesItemToLocalDataSource() {
    // Given when an item is saved, it will save successfully in local and remote data sources
    new ArrangeBuilder()
        .withItemAdded(mLocalDataSource, ITEM)
        .withItemAdded(mRemoteDataSource, ITEM);

    // When an item is saved to the repository
    TestObserver testObserver = new TestObserver();
    mRepository.add(ITEM).subscribe(testObserver);

    // Verify add(item) is called at the local data source
    verify(mLocalDataSource).add(ITEM);

    // Then completable completes without error
    testObserver.assertComplete();
    testObserver.assertNoErrors();
  }

  /**
   * Tests - Model Retrieve
   */
  @Test
  public void getItem_getItemFromRemoteDataSource_whenItemAvailableInRemoteDataSource(){
    // Given a stub item is not available in the local data source
    // And that the stub item is available in the remote data source
    // And that when any item is added, it will complete successfully in local
    new ArrangeBuilder()
        .withItemNotAvailable(mLocalDataSource,ITEM.getUid())
        .withItemAvailable(mRemoteDataSource,ITEM)
        .withItemAddedAny(mLocalDataSource);

    // When an item is requested from the repository
    TestSubscriber<Optional<E>> testSubscriber = new TestSubscriber<>();
    mRepository.getItem(ITEM.getUid()).subscribe(testSubscriber);

    // Then the item is loaded from the local
    verify(mRemoteDataSource).getItem(eq(ITEM.getUid()));
    testSubscriber.assertValue(Optional.of(ITEM));
  }

  @Test
  public void getItem_getItemFromLocalDataSource_whenItemAvailableInLocalDataSource() {
    // Given a stub item is available in the local datasource
    // and that the stub item is not available in the remote datasource
    new ArrangeBuilder()
        .withItemAvailable(mLocalDataSource,ITEM)
        .withItemNotAvailable(mRemoteDataSource,ITEM.getUid());

    // When an item is requested from the repository
    TestSubscriber<Optional<E>> testSubscriber = new TestSubscriber<>();
    mRepository.getItem(ITEM.getUid()).subscribe(testSubscriber);

    // Then the item is loaded from the local
    verify(mLocalDataSource).getItem(eq(ITEM.getUid()));
    testSubscriber.assertValue(Optional.of(ITEM));
  }

  @Test
  public void getItem_getItemFromInMemoryCache_whenItemAvailableInCache(){
    // Given when any items are saved in local and remote, they completes successfully
    // And the local data source has no data available
    // And the remote data source has no data available
    new ArrangeBuilder()
        .withItemAddedAny(mLocalDataSource)
        .withItemAddedAny(mRemoteDataSource)
        .withItemNotAvailable(mLocalDataSource,ITEM.getUid())
        .withItemNotAvailable(mRemoteDataSource,ITEM.getUid());

    // When an item is added
    TestObserver testObserver = new TestObserver();
    mRepository.add(ITEM).subscribe(testObserver);

    // And then an item is requested from the repository
    TestSubscriber<Optional<E>> testSubscriber = new TestSubscriber<>();
    mRepository.getItem(ITEM.getUid()).subscribe(testSubscriber);

    // Verify that neither the local nor remote's getItem() is called
    verify(mRemoteDataSource,never()).getItem(ITEM.getUid());
    verify(mLocalDataSource,never()).getItem(ITEM.getUid());
    // Then the item is loaded from the cache
    testSubscriber.assertValue(Optional.of(ITEM));
  }

  @Test
  public void getItems_getItemsFromRemoteDataSource_whenItemsAvailableInRemoteDataSource() {
    // Given that the remote data source has data available
    // And that the local data source has no data available
    // And when any item is added in local (caching remote), they complete successfully
    new ArrangeBuilder()
        .withItemsAvailable(mRemoteDataSource, ITEMS)
        .withItemsNotAvailable(mLocalDataSource)
        .withItemAddedAny(mLocalDataSource);

    // When items are requested from the tasks repository
    TestSubscriber<List<E>> testSubscriber = new TestSubscriber<>();
    mRepository.getItems().subscribe(testSubscriber);

    // Then items are loaded from the remote data source
    verify(mRemoteDataSource).getItems();
    testSubscriber.assertValue(ITEMS);
  }

  @Test
  public void getItems_getItemsFromLocalDataSource_whenItemsAvailableInLocalDataSource() {
    // Given that the local data source has data available
    // and that the remote data source has no data available
    new ArrangeBuilder()
        .withItemsNotAvailable(mRemoteDataSource)
        .withItemsAvailable(mLocalDataSource, ITEMS);

    // When items are requested from the tasks repository
    TestSubscriber<List<E>> testSubscriber = new TestSubscriber<>();
    mRepository.getItems().subscribe(testSubscriber);

    // Then items are loaded from the local data source
    verify(mLocalDataSource).getItems();
    testSubscriber.assertValue(ITEMS);
  }

  @Test
  public void getItems_getItemsFromInMemoryCache_whenItemsAvailableInCache() {
    // Given when any items are saved in local and remote, they completes successfully
    new ArrangeBuilder()
        .withItemsAddedAny(mLocalDataSource)
        .withItemsAddedAny(mRemoteDataSource);

    // When items are added to repository
    TestObserver testObserver = new TestObserver();
    mRepository.addAll(ITEMS).subscribe(testObserver);
    // And that one subscription is set to get items
    TestSubscriber<List<E>> testSubscriber1 = new TestSubscriber<>();
    mRepository.getItems().subscribe(testSubscriber1);

    // Verify neither the local nor remote data sources' getItems() was called
    verify(mLocalDataSource, never()).getItems();
    verify(mRemoteDataSource, never()).getItems();
    // And that the subscriber has received the items
    testSubscriber1.assertValue(ITEMS);
  }

  @Test
  public void getItems_getItemsFromRemoteDataSource_whenItemsNotAvailableLocalDataSource(){
    // Given that the local data source has no data available
    // and tht the remote data source has data available
    // and when added item to local, operation completes successfully.
    new ArrangeBuilder()
        .withItemsAvailable(mRemoteDataSource,ITEMS)
        .withItemsNotAvailable(mLocalDataSource)
        .withItemAddedAny(mLocalDataSource);

    // When items are retrieved from the repository
    TestSubscriber<List<E>> testSubscriber = new TestSubscriber<>();
    mRepository.getItems().subscribe(testSubscriber);

    // Verify the remote getItems() was called
    verify(mRemoteDataSource).getItems();
    // And that the local add() was called to save each item from remote
    verify(mLocalDataSource,times(ITEMS.size())).add(any(ITEM_CLASS));
    // And that the subscriber has received the items
    testSubscriber.assertValue(ITEMS);
  }

  @Test
  public void getItem_cachesItemToLocalDataSource_whenItemAvailableInRemoteDataSource() {
    // Given when a specific item is saved in local, it completes successfully
    // And the local data source has no data available
    // And the remote data source has data available
    new ArrangeBuilder()
        .withItemAdded(mLocalDataSource, ITEM)
        .withItemNotAvailable(mLocalDataSource, ITEM.getUid())
        .withItemAvailable(mRemoteDataSource, ITEM);

    // When one subscription is set to get a single item
    TestSubscriber<Optional<E>> testSubscriber1 = new TestSubscriber<>();
    mRepository.getItem(ITEM.getUid()).subscribe(testSubscriber1);

    // Verify the remote was called to get the item
    verify(mRemoteDataSource).getItem(ITEM.getUid());
    // And that the local was called to save the item from remote
    verify(mLocalDataSource).add(ITEM);
    // And the subscriber has received the optional item
    testSubscriber1.assertValue(Optional.of(ITEM));
    // And the cache is not dirty (data up-to-date)
    assertFalse(mRepository.mCacheIsDirty);
  }

  @Test
  public void getItems_cachesItemsToLocalDataSource_whenItemsAvailableInRemoteDataSource() {
    // Given when any item is saved in local, it completes successfully
    // And the local data source has no data available
    // And the remote data source has data available
    new ArrangeBuilder()
        .withItemAddedAny(mLocalDataSource)
        .withItemsNotAvailable(mLocalDataSource)
        .withItemsAvailable(mRemoteDataSource, ITEMS);

    // When one subscription is set to get a list of items
    TestSubscriber<List<E>> testSubscriber1 = new TestSubscriber<>();
    mRepository.getItems().subscribe(testSubscriber1);

    // Verify items were requested from remote data source
    verify(mRemoteDataSource).getItems();
    // And the local data source was called to save each item
    verify(mLocalDataSource, times(ITEMS.size())).add(any(ITEM_CLASS));
    // And the subscriber has received the items
    testSubscriber1.assertValue(ITEMS);
    // And the cache is not dirty
    assertFalse(mRepository.mCacheIsDirty);
  }

  @Test
  public void getItem_cachesAfterFirstSubscription_whenItemsAvailableInLocalDataSource() {
    // Given that the local data source has data available
    // And the remote data source does not have any data available
    new ArrangeBuilder()
        .withItemsAvailable(mLocalDataSource, ITEMS)
        .withItemsNotAvailable(mRemoteDataSource);

    // When two subscriptions are set
    TestSubscriber<List<E>> testSubscriber1 = new TestSubscriber<>();
    mRepository.getItems().subscribe(testSubscriber1);
    TestSubscriber<List<E>> testSubscriber2 = new TestSubscriber<>();
    mRepository.getItems().subscribe(testSubscriber2);

    /*
     * TODO Question: why would remote getItems() be ever called?
     * 0. First subscription
     * 1. Repo start with mCacheIsDirty = false
     * 2. Repo start with emptyCache
     * 3. Repo falls to Flowable.concat(localItems,remoteItems)
     * 4. mLocalDataSource.getItems() called
     * 5. for each localItem -> cacheItems.put(id,localItem);
     * 6. return ITEMS
     *
     * 0. Second subscription
     * 1. Repo start with mCacheIsDirect = false
     * 2. Repo start with non-emptyCache
     * 3. Repo returns cache ITEMS immediately
     */

    // Verify that the items were only requested once from remote, once from local:
    verify(mLocalDataSource).getItems();
    verify(mRemoteDataSource).getItems();
    assertFalse(mRepository.mCacheIsDirty);
    testSubscriber1.assertValue(ITEMS);
    testSubscriber2.assertValue(ITEMS);
  }

  @Test
  public void getItems_CachesAfterFirstSubscription_whenItemsAvailableInRemoteDataSource() {
    // Given that the remote data source has data available
    // And the local data source does not have any data available
    // And when any item is saved in the local, its completes successfully
    new ArrangeBuilder()
        .withItemsAvailable(mRemoteDataSource, ITEMS)
        .withItemsNotAvailable(mLocalDataSource)
        .withItemAddedAny(mLocalDataSource);

    // When two subscriptions are set
    TestSubscriber<List<E>> testSubscriber1 = new TestSubscriber<>();
    mRepository.getItems().subscribe(testSubscriber1);

    TestSubscriber<List<E>> testSubscriber2 = new TestSubscriber<>();
    mRepository.getItems().subscribe(testSubscriber2);

    // Then tasks were only requested once from remote and local sources
    verify(mRemoteDataSource).getItems();
    verify(mLocalDataSource).getItems();
    assertFalse(mRepository.mCacheIsDirty);
    testSubscriber1.assertValue(ITEMS);
    testSubscriber2.assertValue(ITEMS);
  }

  @Test
  public void getItem_failsWithEmptyOptional_whenItemNotAvailable(){
    // Given a stub item is not available in the remote
    // And the sub item is not available in the local
    // And when any item is saved in the local, it completes successfully
    new ArrangeBuilder()
        .withItemNotAvailable(mRemoteDataSource,ITEM.getUid())
        .withItemNotAvailable(mLocalDataSource,ITEM.getUid())
        .withItemAddedAny(mLocalDataSource);

    // When an item is requested from the repository
    TestSubscriber<Optional<E>> testSubscriber = new TestSubscriber<>();
    mRepository.getItem(ITEM.getUid()).subscribe(testSubscriber);

    // Verify that an empty Optional is returned
    testSubscriber.assertValue(Optional.absent());
  }

  @Test
  public void getItems_failsWithError_whenItemsNotAvailable(){
    // Given that the local data source has no data available
    // and that the remote data source has no data available
    new ArrangeBuilder()
        .withItemsNotAvailable(mLocalDataSource)
        .withItemsNotAvailable(mRemoteDataSource);

    // When calling getItems in the repository
    TestSubscriber<List<E>> testSubscriber = new TestSubscriber<>();
    mRepository.getItems().subscribe(testSubscriber);

    // Verify no data is returned
    testSubscriber.assertNoValues();
    // and that error is returned
    testSubscriber.assertError(NoSuchElementException.class);
  }


  @Test
  public void getItems_itemsAreRetrievedFromRemote_whenCacheIsDirty() {
    // Given that the remote data source has data available
    // and that when add item to local, operation completes successfully
    new ArrangeBuilder()
        .withItemsAvailable(mRemoteDataSource,ITEMS)
        .withItemAddedAny(mLocalDataSource);

    // When calling getItems() in the repository with dirty cache
    mRepository.refresh();
    TestSubscriber<List<E>> testSubscriber = new TestSubscriber<>();
    mRepository.getItems().subscribe(testSubscriber);

    // Verify the tasks from the remote data source are returned, not the local
    verify(mLocalDataSource, never()).getItems();
    verify(mRemoteDataSource).getItems();
    testSubscriber.assertValue(ITEMS);
  }

  @Test
  public void getItemsWithOptions_getItemsFromRemoteDataSource_whenItemsAvailableInRemoteDataSource(){
    // Given that the remote data source has data available with options
    // And that the local data source has no data available
    // And when any item is added in local (caching remote), they complete successfully
    new ArrangeBuilder()
        .withItemsAvailableWithOptions(mRemoteDataSource, ITEMS, OPTIONS)
        .withItemsNotAvailableWithOptions(mLocalDataSource, OPTIONS)
        .withItemAddedAny(mLocalDataSource);

    // When getItems(Options) are requested from the repository
    TestSubscriber<List<E>> testSubscriber = new TestSubscriber<>();
    mRepository.getItems(OPTIONS).subscribe(testSubscriber);

    // Then items are loaded from the remote data source
    verify(mRemoteDataSource).getItems(OPTIONS);
    testSubscriber.assertValue(ITEMS);
  }

  @Test
  public void getItemsWithOptions_getItemsFromLocalDataSource_whenItemsAvailableInLocalDataSource(){
    // Given that the remote data source has no data available with options
    // And that the local data source has data available with options
    new ArrangeBuilder()
        .withItemsAvailableWithOptions(mLocalDataSource, ITEMS, OPTIONS)
        .withItemsNotAvailableWithOptions(mRemoteDataSource, OPTIONS);

    // When getItems(Options) are requested from the repository
    TestSubscriber<List<E>> testSubscriber = new TestSubscriber<>();
    mRepository.getItems(OPTIONS).subscribe(testSubscriber);

    // Then items are loaded from the local data source
    verify(mLocalDataSource).getItems(OPTIONS);
    testSubscriber.assertValue(ITEMS);
  }

  /**
   * Tests - Model Update
   */
  @Test
  public void updateItem_updatesLocalDataSource_updatesRemoteDataSource() {
    // Given when any item is updated in local or remote, it completes successfully
    // And the local data source has data available
    // And the remote data source has data available
    new ArrangeBuilder()
        .withItemUpdatedAny(mLocalDataSource)
        .withItemUpdatedAny(mRemoteDataSource)
        .withItemsAvailable(mLocalDataSource, ITEMS)
        .withItemsAvailable(mRemoteDataSource, ITEMS);

    // When an item is updated
    TestObserver testObserver = new TestObserver();
    mRepository.update(ITEM).subscribe(testObserver);

    // Verify that update() is called in local and remote data source
    verify(mRemoteDataSource).update(ITEM);
    verify(mLocalDataSource).update(ITEM);
  }

  @Test
  public void updateItem_repoGetItemById_getsCorrectUpdatedItem() {
    // Given when any item is updated in local or remote, it completes successfully
    // And the local data source has data available
    // And the remote data source has data available
    new ArrangeBuilder()
        .withItemUpdatedAny(mLocalDataSource)
        .withItemUpdatedAny(mRemoteDataSource)
        .withItemsAvailable(mLocalDataSource, ITEMS)
        .withItemsAvailable(mRemoteDataSource, ITEMS);

    // When an item is updated
    TestObserver testObserver = new TestObserver();
    mRepository.update(ITEM).subscribe(testObserver);

    // And When get the item that was updated
    TestSubscriber<Optional<E>> testSubscriber = new TestSubscriber<>();
    mRepository.getItem(ITEM.getUid()).subscribe(testSubscriber);

    // Verify that the test subscriber's got the item that has the correct uid
    testSubscriber.assertValue(itemOptional -> itemOptional.get().getUid().equals(ITEM.getUid()));
  }

  /**
   * Tests - Model Delete
   */
  @Test
  public void deleteItem_addItemThenRemoveIt_itemRemovedFromAllDataSources(){
    // Given when add any item is called in local or remote, the process completes successfully
    // and when remove item is called in local or remote, the process completes successfully
    new ArrangeBuilder()
        .withItemAddedAny(mLocalDataSource)
        .withItemAddedAny(mRemoteDataSource)
        .withItemRemoved(mLocalDataSource,ITEM.getUid())
        .withItemRemoved(mRemoteDataSource,ITEM.getUid());

    // When the item is added to the repository
    TestObserver testObserver = new TestObserver();
    mRepository.add(ITEM).subscribe(testObserver);
    // And the item is deleted from the repository
    TestObserver testObserver1 = new TestObserver();
    mRepository.remove(ITEM.getUid()).subscribe(testObserver1);

    // Verify that the data sources were called to remove item
    verify(mRemoteDataSource).remove(ITEM.getUid());
    verify(mLocalDataSource).remove(ITEM.getUid());
    assertThat(mRepository.mCachedItems.size(),is(0));

    // and operation completes without error
    testObserver.assertComplete();
    testObserver.assertNoErrors();
  }

  @Test
  public void deleteItems_addItemsThenRemoveThem_itemsRemovedFromAllDataSources(){
    // Given when add all items is called, the process completes successfully
    // and when remove all is called, the process completes successfully
    new ArrangeBuilder()
        .withItemsAddedAny(mLocalDataSource)
        .withItemsAddedAny(mRemoteDataSource)
        .withItemsRemovedAll(mLocalDataSource)
        .withItemsRemovedAll(mRemoteDataSource);

    // When the items are added to the repository
    TestObserver testObserver = new TestObserver();
    mRepository.addAll(ITEMS).subscribe(testObserver);
    // and then removed all items from the repository
    TestObserver testObserver1 = new TestObserver();
    mRepository.removeAll().subscribe(testObserver1);

    // Verify the data sources were called to remove all items
    verify(mRemoteDataSource).removeAll();
    verify(mLocalDataSource).removeAll();
    assertThat(mRepository.mCachedItems.size(),is(0));
    // and operation completes without error
    testObserver.assertComplete();
    testObserver.assertNoErrors();
  }


  class ArrangeBuilder {

    ArrangeBuilder withItemsNotAvailable(DataSource<E> dataSource) {
      when(dataSource.getItems()).thenReturn(Flowable.just(Collections.emptyList()));
      return this;
    }

    ArrangeBuilder withItemsNotAvailableWithOptions(DataSource<E> dataSource, Map<String,String> options) {
      when(dataSource.getItems(options)).thenReturn(Flowable.just(Collections.emptyList()));
      return this;
    }

    ArrangeBuilder withItemsAvailable(DataSource<E> dataSource, List<E> items) {
      // don't allow the data sources to complete. ??
      when(dataSource.getItems()).thenReturn(Flowable.just(items).concatWith(Flowable.never()));
      return this;
    }

    ArrangeBuilder withItemsAvailableWithOptions(DataSource<E> dataSource, List<E> items, Map<String,String> options) {
      when(dataSource.getItems(options)).thenReturn(Flowable.just(items).concatWith(Flowable.never()));
      return this;
    }

    ArrangeBuilder withItemNotAvailable(DataSource<E> dataSource, String id) {
      when(dataSource.getItem(eq(id))).thenReturn(Flowable.just(Optional.absent()));
      return this;
    }

    ArrangeBuilder withItemAvailable(DataSource<E> dataSource, E item) {
      Optional<E> itemOptional = Optional.of(item);
      when(dataSource.getItem(eq(itemOptional.get().getUid())))
          .thenReturn(Flowable.just(itemOptional).concatWith(Flowable.never()));
      return this;
    }

    ArrangeBuilder withItemAdded(DataSource<E> dataSource, E item) {
      when(dataSource.add(item)).thenReturn(Completable.complete());
      return this;
    }

    ArrangeBuilder withItemAddedAny(DataSource<E> dataSource) {
      when(dataSource.add(any())).thenReturn(Completable.complete());
      return this;
    }

    ArrangeBuilder withItemsAdded(DataSource<E> dataSource, List<E> items) {
      when(dataSource.addAll(items)).thenReturn(Completable.complete());
      return this;
    }

    ArrangeBuilder withItemsAddedAny(DataSource<E> dataSource) {
      when(dataSource.addAll(anyList())).thenReturn(Completable.complete());
      return this;
    }

    ArrangeBuilder withItemUpdatedAny(DataSource<E> dataSource) {
      when(dataSource.update(any())).thenReturn(Completable.complete());
      return this;
    }

    ArrangeBuilder withItemsRemovedAll(DataSource<E> dataSource) {
      when(dataSource.removeAll()).thenReturn(Completable.complete());
      return this;
    }

    ArrangeBuilder withItemRemoved(DataSource<E> dataSource, String id){
      when(dataSource.remove(id)).thenReturn(Completable.complete());
      return this;
    }
  }
}
