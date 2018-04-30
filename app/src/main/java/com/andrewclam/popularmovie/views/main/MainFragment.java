package com.andrewclam.popularmovie.views.main;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

import static com.andrewclam.popularmovie.util.NetworkUtil.TMDB_PATH_POPULAR;
import static com.andrewclam.popularmovie.util.NetworkUtil.TMDB_PATH_TOP_RATED;

/**
 * A simple {@link Fragment} subclass.
 */
@ActivityScoped
public class MainFragment extends DaggerFragment implements MainContract.View {

  @Inject
  MainContract.Presenter mPresenter;

  @Inject
  MoviesRvAdapter mRvAdapter;

  @Nullable
  private RecyclerView mItemsRv;
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

  public MainFragment() {
    // Required empty public constructor
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

    // Allow fragement to participate in creating menu options
    setHasOptionsMenu(true);
    setRetainInstance(true);

    super.onViewCreated(view, savedInstanceState);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.menu_main,menu);
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
  public void onSaveInstanceState(@NonNull Bundle outState) {
    mLayoutManager.onSaveInstanceState(); // TODO persist and restore the layout state
    super.onSaveInstanceState(outState);
  }

  @Override
  public void showLoadingMoviesError() {
    Toast.makeText(getContext(),getString(R.string.error_msg_unable_to_fetch),Toast.LENGTH_LONG)
        .show();
  }

  @Override
  public void onDataSetChanged() {
    if (mRvAdapter != null){
      mRvAdapter.notifyDataSetChanged();
    }
  }

  @Override
  public boolean isActive() {
    return isAdded();
  }
}
