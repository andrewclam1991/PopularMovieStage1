package com.andrewclam.popularmovie.views.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.andrewclam.popularmovie.R;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Concrete implementation of a {@link RecyclerView.Adapter<RecyclerView.ViewHolder>}. Within the
 * context of MVP architecture pattern, this class is implemented as a "dumb" View class
 * where it delegates actual data binding responsibility of each {@link MainContract.ItemViewHolder}
 * to its owning {@link MainContract.ItemViewHolderPresenter<>}. This class only holds reference
 * to framework View components, and contains no data.
 */
class MoviesRvAdapter extends RecyclerView.Adapter<MoviesRvAdapter.MovieItemViewHolder> {

  @NonNull
  private final MainContract.ItemViewHolderPresenter<MainContract.ItemViewHolder> mPresenter;

  @Inject
  MoviesRvAdapter(@NonNull MainContract.ItemViewHolderPresenter<MainContract.ItemViewHolder> presenter){
    mPresenter = presenter;
  }

  @NonNull
  @Override
  public MovieItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    // Get the layout inflater (required to inflate the itemView)
    Context context = parent.getContext(); // Context required by the layoutInflater
    LayoutInflater inflater = LayoutInflater.from(context);

    int layoutResId = R.layout.entry_list_item;
    boolean shouldAttachToParentImmediately = false; // optional parameter to indicate exactly what it says

    // Uses the layoutId, viewGroup, boolean signature of the inflater.inflate()
    @SuppressWarnings("ConstantConditions") View view = inflater.inflate(layoutResId, parent,
        shouldAttachToParentImmediately);

    return new MovieItemViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull MovieItemViewHolder holder, int position) {
    mPresenter.onAdapterBindViewHolder(holder,position);
  }

  @Override
  public int getItemCount() {
    return mPresenter.onAdapterRequestItemCount();
  }

  /**
   * Concrete implementation of a {@link RecyclerView.ViewHolder}, responsible for
   * setting up View fields and handling data setting methods calls defined in the
   * {@link MainContract.ItemViewHolder}.
   *
   * Note: Within a MVP pattern, this implementation of a {@link MainContract.ItemViewHolder}
   * is treated as a "dumb" View class where it handles View responsibilities lazily, and as
   * instructed by its Presenter class {@link MainContract.ItemViewHolderPresenter<>}.
   */
  class MovieItemViewHolder extends RecyclerView.ViewHolder implements MainContract.ItemViewHolder{

    @NonNull
    private final ImageView mMoviePosterIv;

    MovieItemViewHolder(View itemView) {
      super(itemView);
      mMoviePosterIv = itemView.findViewById(R.id.iv_poster);
      itemView.setOnClickListener(view -> mPresenter.onAdapterItemClicked(getAdapterPosition()));
    }

    @Override
    public void loadMoviePoster(@NonNull String posterUrl) {
      Picasso.get().load(posterUrl).into(mMoviePosterIv);
    }
  }
}
