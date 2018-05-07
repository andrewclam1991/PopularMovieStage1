package com.andrewclam.popularmovie.views.detail;


import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andrewclam.popularmovie.R;
import com.andrewclam.popularmovie.di.annotations.ActivityScoped;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

import static com.google.common.base.Preconditions.checkNotNull;

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

  // Host Activity listener
  @Nullable
  private Callback mCallback;

  // Views
  private ProgressBar mLoadingIndicator;
  private ImageView mPosterBannerIv;
  private ImageView mPosterIconIv;
  private TextView mTitleTv;
  private TextView mReleaseDateTv;
  private TextView mVoteAverageTv;
  private TextView mVoteCountTv;
  private TextView mOverViewTv;
  private FloatingActionButton mFavBtn;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof DetailFragment.Callback){
      mCallback = (DetailFragment.Callback)context;
    }else{
      throw new RuntimeException("Host activity of DetailFragment must implement Callback");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mCallback = null;
  }

  @Override
  public void onPause() {
    super.onPause();
    mPresenter.dropView();
  }

  @Override
  public void onResume() {
    super.onResume();
    mPresenter.setView(this);
    mPresenter.loadMovie();
    mPresenter.loadMovieFavoriteStatus();
  }


  @Inject
  public DetailFragment() {
    // Required empty public constructor*
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_detail, container, false);

    // Reference the UI views
    mLoadingIndicator = view.findViewById(R.id.pb_loading_indicator);
    mPosterBannerIv = view.findViewById(R.id.iv_poster_banner);
    mPosterIconIv = view.findViewById(R.id.iv_poster);
    mTitleTv = view.findViewById(R.id.tv_title);
    mReleaseDateTv = view.findViewById(R.id.tv_release_date);
    mVoteAverageTv = view.findViewById(R.id.tv_vote_average);
    mVoteCountTv = view.findViewById(R.id.tv_vote_count);
    mOverViewTv = view.findViewById(R.id.tv_overview);
    mFavBtn = view.findViewById(R.id.fav_btn);

    // Allow fragment to participate in creating menu options
    setHasOptionsMenu(true);
    setRetainInstance(true);

    return view;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.menu_detail,menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    switch (id) {
      case R.id.action_share_movie:
        mPresenter.handleOnShareClicked();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
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
  public void showLoadingMovieError() {
    Toast.makeText(getContext(), getString(R.string.error_msg_unable_to_fetch),
        Toast.LENGTH_LONG).show();
    if(isActive() && getActivity()!=null) {
      getActivity().finish();
    }
  }

  @Override
  public void showMissingMovieError() {
    Toast.makeText(getContext(), getString(R.string.error_msg_unable_to_fetch),
        Toast.LENGTH_LONG).show();
    if(isActive() && getActivity()!=null) {
      getActivity().finish();
    }
  }

  @Override
  public boolean isActive() {
    return isAdded();
  }

  @Override
  public void setLoadingIndicator(boolean isVisible) {
    mLoadingIndicator.setVisibility(isVisible? View.VISIBLE: View.GONE);
  }

  @Override
  public void showPosterBanner(@NonNull String url) {
    checkNotNull(mCallback).showPosterBanner(url);
  }

  @Override
  public void showPosterIcon(@NonNull String url) {
    Picasso.get().load(url).into(mPosterIconIv);
  }

  @Override
  public void hidePosters() {
    mPosterBannerIv.setVisibility(View.GONE);
    mPosterIconIv.setVisibility(View.GONE);
  }

  @Override
  public void showTitle(@NonNull String title) {
    checkNotNull(mCallback).showTitle(title);
    mTitleTv.setText(title);
  }

  @Override
  public void hideTitle() {
    checkNotNull(mCallback).showTitle(getString(R.string.no_title));
    mTitleTv.setText(getString(R.string.no_title));
  }

  @Override
  public void showReleaseDate(@NonNull String releaseDate) {
    mReleaseDateTv.setText(releaseDate);
  }

  @Override
  public void hideReleaseDate() {
    mReleaseDateTv.setText(getString(R.string.no_release_date));
  }

  @Override
  public void showVoteAverage(double voteAverage) {
    mVoteAverageTv.setText(String.valueOf(voteAverage));
  }

  @Override
  public void hideVoteAverage() {
    mVoteAverageTv.setText(getString(R.string.no_vote_average));
  }

  @Override
  public void showVoteCount(int voteCount) {
    Resources res = mVoteAverageTv.getContext().getResources();
    String voteCountStr = res.getQuantityString(R.plurals.vote,voteCount,voteCount);
    mVoteAverageTv.setText(voteCountStr);
  }

  @Override
  public void hideVoteCount() {
    mVoteCountTv.setText(getString(R.string.no_votes));
  }

  @Override
  public void showOverView(@NonNull String overview) {
    mOverViewTv.setText(overview);
  }

  @Override
  public void hideOverView() {
    mOverViewTv.setText(getString(R.string.no_overview));
  }

  @Override
  public void setFavoriteStatus(boolean isFavorite) {
    mFavBtn.setImageResource(isFavorite?
        R.drawable.ic_favorite_on_24dp:
        R.drawable.ic_favorite_off_24dp);
  }

  /**
   * Implementation Note:
   * This method shares the trailer and allows the user to select which app
   * they would like to use to share the trailer. Using ShareCompat's IntentBuilder, we get some
   * really cool functionality for free. The chooser that is started using the
   * {@link ShareCompat.IntentBuilder#startChooser()} method will create a chooser when more than
   * one app on the device can handle the Intent. This happens when the user has, for example,
   * both a messenger app and an email app. If only one Activity on the phone can handle the
   * Intent, it will automatically be launched.
   *
   * You can think of MIME types similarly to file extensions. They aren't the exact same,
   * but MIME types help a computer determine which applications can open which content. For
   * example, if you double click on a .pdf file, you will be presented with a list of
   * programs that can open PDFs. Specifying the MIME type as text/plain has a similar affect
   * on our implicit Intent. With text/plain specified, all apps that can handle text content
   * in some way will be offered when we call startActivity on this particular Intent.
   *
   * @param mimeType type of media to share
   * @param content share content
   */
  @Override
  public void showShare(@NonNull String mimeType, @NonNull String content) {
    if (isActive() && getActivity()!= null){
      String title = getString(R.string.action_share_this_movie_trailer);
      ShareCompat.IntentBuilder
          .from(getActivity())
          .setType(mimeType)
          .setChooserTitle(title)
          .setText(content)
          .startChooser();
    }
  }

  interface Callback {
    void showPosterBanner(@NonNull String url);

    void showTitle(@NonNull String title);
  }
}
