package com.andrewclam.popularmovie.views.main;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.andrewclam.popularmovie.R;
import com.andrewclam.popularmovie.di.annotations.ActivityScoped;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

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
    mItemsRv = view.findViewById(R.id.items_rv);
    mItemsRv.setAdapter(mRvAdapter);
    super.onViewCreated(view, savedInstanceState);
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
