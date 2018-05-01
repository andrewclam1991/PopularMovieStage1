package com.andrewclam.popularmovie.views.main;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.andrewclam.popularmovie.R;
import com.andrewclam.popularmovie.di.annotations.ActivityScoped;
import com.andrewclam.popularmovie.util.LayoutManagerUtil;
import com.andrewclam.popularmovie.views.detail.DetailActivity;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import dagger.android.support.DaggerFragment;

/**
 * A simple {@link Fragment} and a {@link MainContract.View} implementation responsible for
 * handling lifecycle callbacks and delegating user interaction logic to its
 * {@link MainContract.Presenter}. This class holds only references to View classes and an
 * instance of {@link MainContract.Presenter}.
 *
 * Note: {@link MainContract.Presenter} is injected and with its lifecycle managed by Dagger
 * as defined in the {@link MainModule}
 */
@ActivityScoped
public class MainFragment extends DaggerFragment implements MainContract.View {

  @Inject
  MainContract.Presenter mPresenter;

  @Inject
  MoviesRvAdapter mRvAdapter;

  @Nullable
  private RecyclerView mItemsRv;

  @Nullable
  private RecyclerView.LayoutManager mLayoutManager;

  @Override
  public void onPause() {
    mPresenter.dropView();
    super.onPause();
  }

  @Override
  public void onResume() {
    mPresenter.setView(this);
    super.onResume();
  }

  @Inject
  public MainFragment() {
    // Required empty public constructor*
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_main, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    // Init layout manager
    int spanSize = LayoutManagerUtil.getSpanSize(getContext());
    mLayoutManager = new GridLayoutManager(getContext(), spanSize);
    mItemsRv = view.findViewById(R.id.items_rv);
    mItemsRv.setAdapter(mRvAdapter);
    mItemsRv.setLayoutManager(mLayoutManager);

    // Allow fragment to participate in creating menu options
    setHasOptionsMenu(true);
    setRetainInstance(true);

    super.onViewCreated(view, savedInstanceState);
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    // Save Presenter states
    outState.putString(MainContract.Presenter.FILTER_TYPE_KEY,
        mPresenter.getCurrentFilterType().toString());
    outState.putString(MainContract.Presenter.SORT_ORDER_KEY,
        mPresenter.getCurrentSortOrder().toString());
    outState.putString(MainContract.Presenter.SORT_TYPE_KEY,
        mPresenter.getCurrentSortType().toString());
    super.onSaveInstanceState(outState);
  }

  @Override
  public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
    // See if savedInstanceState exists and where we retained user's query selection
    if (savedInstanceState != null) {
      // Restore Presenter states
      String filterType = savedInstanceState.getString(MainContract.Presenter.FILTER_TYPE_KEY);
      mPresenter.setFilterType(MainContract.Presenter.FilterType.valueOf(filterType));

      String sortOrder = savedInstanceState.getString(MainContract.Presenter.SORT_ORDER_KEY);
      mPresenter.setSortOrder(MainContract.Presenter.SortOrder.valueOf(sortOrder));

      String sortType = savedInstanceState.getString(MainContract.Presenter.SORT_TYPE_KEY);
      mPresenter.setSortType(MainContract.Presenter.SortType.valueOf(sortType));
    }
    super.onViewStateRestored(savedInstanceState);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.menu_main, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    switch (id) {
      case R.id.action_sort_by_popularity:
        // TODO handles sort result by popularity
        return true;
      case R.id.action_sort_by_rating:
        // TODO handles sort result by rating
        return true;
      case R.id.action_show_favorites:
        // TODO handle show user favorites
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void showDetailUi(@NonNull String id, @NonNull Class<? extends DaggerAppCompatActivity> clazz) {
    Intent intent = new Intent(getActivity(), clazz);
    intent.putExtra(DetailActivity.ARG_DETAIL_ACT_ITEM_ID, id);
    intent.setAction(Intent.ACTION_VIEW);
    startActivity(intent);
  }

  @Override
  public void showLoadingMoviesError() {
    Toast.makeText(getContext(), getString(R.string.error_msg_unable_to_fetch),
        Toast.LENGTH_LONG).show();
  }

  @Override
  public void onDataSetChanged() {
    if (mRvAdapter != null) {
      mRvAdapter.notifyDataSetChanged();
    }
  }

  @Override
  public boolean isActive() {
    return isAdded();
  }
}
