package com.andrewclam.popularmovie.views.detail;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.andrewclam.popularmovie.R;
import com.andrewclam.popularmovie.di.annotations.ActivityScoped;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

/**
 * A simple {@link Fragment} and a {@link DetailContract.View} implementation responsible for
 * handling lifecycle callbacks and delegating user interaction logic to its
 * {@link DetailContract.Presenter}. This class holds only references to View classes and an
 * instance of {@link DetailContract.Presenter}.
 *
 * Note: {@link DetailContract.Presenter} is injected and with its lifecycle managed by Dagger
 * as defined in the {@link DetailModule}
 */
@ActivityScoped
public class DetailFragment extends DaggerFragment implements DetailContract.View {

  @Inject
  DetailContract.Presenter mPresenter;

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
  public DetailFragment() {
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


    // Allow fragment to participate in creating menu options
    setHasOptionsMenu(true);
    setRetainInstance(true);

    super.onViewCreated(view, savedInstanceState);
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    // Save Presenter states
    super.onSaveInstanceState(outState);
  }

  @Override
  public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
    // See if savedInstanceState exists and where we retained user's query selection
    if (savedInstanceState != null) {
      // Restore Presenter states
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

      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void showLoadingMovieError() {
    Toast.makeText(getContext(), getString(R.string.error_msg_unable_to_fetch),
        Toast.LENGTH_LONG).show();
  }

  @Override
  public boolean isActive() {
    return isAdded();
  }
}
