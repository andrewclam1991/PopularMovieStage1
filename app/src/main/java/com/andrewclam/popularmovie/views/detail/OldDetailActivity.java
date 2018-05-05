/*
 * Copyright <2017> <ANDREW LAM>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons
 * to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT
 * OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.andrewclam.popularmovie.views.detail;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.andrewclam.popularmovie.R;
import com.andrewclam.popularmovie.adapters.RelatedVideosAdapter;
import com.andrewclam.popularmovie.adapters.UserReviewsAdapter;
import com.andrewclam.popularmovie.async.DbUpdateAsyncTask;
import com.andrewclam.popularmovie.async.FetchRelatedVideoAsyncTask;
import com.andrewclam.popularmovie.async.FetchUserReviewAsyncTask;
import com.andrewclam.popularmovie.data.db.AppDbContract;
import com.andrewclam.popularmovie.data.model.Movie;
import com.andrewclam.popularmovie.data.model.MovieVideo;
import com.andrewclam.popularmovie.util.NetworkUtil;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import dagger.android.support.DaggerAppCompatActivity;

import static com.andrewclam.popularmovie.data.db.AppDbContract.buildMovieUriWithId;

/**
 * DetailActivity is used to show user each movie entry in detail when a thumbnail is clicked in
 * MainActivity, detail includes the title, poster, rating and overview etc.
 */
public class OldDetailActivity extends DaggerAppCompatActivity {
  // Log Tag
  private static final String TAG = DetailActivity.class.getSimpleName();

  // QueryConstants
  public static final String ARG_DETAIL_ACT_ITEM_ID = "ITEM_DETAIL_ID";

  // UI views
  private View rootScrollView; // need to save instance state of
  private ImageView posterBannerIv;
  private ImageView posterIv;
  private TextView titleTv;
  private TextView releaseDateTv;
  private TextView voteAverageTv;
  private TextView voteCountTv;
  private TextView overViewTv;
  private FloatingActionButton favBtn;
  private boolean mFavStatus;
  private Context mContext;

  // RecyclerView for Related Videos
  private RecyclerView mRelatedVideosRv;
  private RelatedVideosAdapter mRelatedVideosAdapter;
  private TextView mEmptyViewRelatedVideosTv;
  private ProgressBar mRelatedVideoLoadingPb;

  // RecyclerView for Reviews
  private RecyclerView mUserReviewsRv;
  private UserReviewsAdapter mUserReviewsAdapter;
  private TextView mEmptyViewUserReviewsTv;
  private ProgressBar mUserReviewLoadingPb;

  // Share Function
  private URL mShareTrailerUrl;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    if (getIntent() != null && getIntent().hasExtra("1")) {
      // Unwrap the parcel to retrieve the entry object
      final Movie entry = new Movie();

      if (entry != null) {
        // Init context
        mContext = OldDetailActivity.this;

        // Reference the UI views
        rootScrollView = findViewById(R.id.detail_root_view);
        posterBannerIv = findViewById(R.id.iv_poster_banner);
        posterIv = findViewById(R.id.iv_poster);
        titleTv = findViewById(R.id.tv_title);
        releaseDateTv = findViewById(R.id.tv_release_date);
        voteAverageTv = findViewById(R.id.tv_vote_average);
        voteCountTv = findViewById(R.id.tv_vote_count);
        overViewTv = findViewById(R.id.tv_overview);
        favBtn = findViewById(R.id.fav_btn);

        // Initialize RelatedVideos RecyclerView and its Adapter
        initRelatedVideoRv();

        // Initialize Reviews RecyclerView and its Adapter
        initReviewsRv();

        // Populate the referenced UI with the entry object
        rootScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
          @Override
          public void onGlobalLayout() {
            rootScrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            // (!) Only populate the entry fields when the view are laid out, this is
            // because we need the dynamic width of the poster image view to be
            // calculated before loading the image in with picasso, picasso needs
            // the width in order to calculate the height of the image.
            populateEntryFields(entry);
          }
        });

        // Log posterIv size
        Log.d(TAG, "posterIv width: " + posterIv.getWidth() + ", height: " + posterIv.getHeight());
      }

    } else {
      Log.e(TAG, "Intent doesn't have the required movie entry");
      finish();
    }
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    // Always call the superclass so it can save the view hierarchy state
    super.onSaveInstanceState(savedInstanceState);
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_detail, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    switch (id) {
      case R.id.action_share_movie:
        // Share the first video of type trailer in the Related Video
        if (mShareTrailerUrl != null) {
          // Call shareTrailer
          String title = getString(R.string.action_share_this_movie_trailer);
          shareTrailer(title, mShareTrailerUrl.toString());
        } else {
          // No trailer to share
          Snackbar.make(rootScrollView, getString(R.string.error_no_trailer_to_share),
              Snackbar.LENGTH_SHORT).show();
        }
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  /**
   * a method to initialize the recycler view, adapter and the layout manager
   * for the related videos
   */
  private void initRelatedVideoRv() {
    mRelatedVideosRv = findViewById(R.id.related_video_rv);
    mRelatedVideosAdapter = new RelatedVideosAdapter(entry -> {
      // Get the video url from the entry object
//      URL videoUrl = entry.getVideoUrl();
      URL videoUrl = null;
      // launch an implicit intent to handle the video url

      // Build the intent
      Uri videoUri = Uri.parse(videoUrl.toString());
      Intent videoIntent = new Intent(Intent.ACTION_VIEW, videoUri);

      // Verify it resolves
      PackageManager packageManager = getPackageManager();
      List<ResolveInfo> activities = packageManager.queryIntentActivities(videoIntent, 0);
      boolean isIntentSafe = activities.size() > 0;

      // Start an activity if it's safe
      if (isIntentSafe) {
        startActivity(videoIntent);
      }

    });

    // Reference the empty view and the loading indicator
    mEmptyViewRelatedVideosTv = findViewById(R.id.tv_related_video_empty_view);
    mRelatedVideoLoadingPb = findViewById(R.id.pb_related_video_loading_indicator);

    // Init layout manager for horizontal scroll
    LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

    // Attach the layout manager to the recyclerView
    mRelatedVideosRv.setLayoutManager(layoutManager);

    // Back the recyclerView with the adapter
    mRelatedVideosRv.setAdapter(mRelatedVideosAdapter);
  }

  /**
   * a method to initialize the recycler view, adapter and the layout manager
   * for the related videos. This method is called in
   */
  private void initReviewsRv() {
    mUserReviewsRv = findViewById(R.id.user_reviews_rv);
    mUserReviewsAdapter = new UserReviewsAdapter(entry -> {
      // Do something when user clicks the review (??)
      // Direct user to read more with the url link
      String reviewUrlStr = entry.getUrl();
      // launch an implicit intent to handle the video url

      // Build the intent
      Uri reviewUri = Uri.parse(reviewUrlStr);
      Intent reviewUriIntent = new Intent(Intent.ACTION_VIEW, reviewUri);

      // Verify it resolves
      PackageManager packageManager = getPackageManager();
      List<ResolveInfo> activities = packageManager.queryIntentActivities(reviewUriIntent, 0);
      boolean isIntentSafe = activities.size() > 0;

      // Start an activity if it's safe
      if (isIntentSafe) {
        startActivity(reviewUriIntent);
      }
    });

    // Reference the empty view
    mEmptyViewUserReviewsTv = findViewById(R.id.tv_user_reviews_empty_view);
    mUserReviewLoadingPb = findViewById(R.id.pb_user_review_loading_indicator);

    // Init layout manager for horizontal scroll
    LinearLayoutManager layoutManager = new LinearLayoutManager(this,
        LinearLayoutManager.HORIZONTAL, false);

    // Attach the layout manager to the recyclerView
    mUserReviewsRv.setLayoutManager(layoutManager);

    // Back the recyclerView with the adapter
    mUserReviewsRv.setAdapter(mUserReviewsAdapter);
  }

  /**
   * This is a method to populate the entry UI fields with data
   *
   * @param entry the single parsed movie entry
   */
  private void populateEntryFields(@NonNull Movie entry) {
    /* MOVIE ID */
    // The id required to do favorite, video and comment's db and network io
    Long movieId = entry.getMovieId();

    /**********************
     * Movie General Info *
     **********************/

    /* TITLE */
    // Set movie title as the activity title
    String title = entry.getTitle();
    titleTv.setText(title);

    // CollapsingToolbarLayout set title
    CollapsingToolbarLayout mCollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
    mCollapsingToolbarLayout.setTitleEnabled(true);
    mCollapsingToolbarLayout.setTitle(title);
    mCollapsingToolbarLayout.setExpandedTitleTextColor(
        ColorStateList.valueOf(
            ContextCompat.getColor(this, R.color.colorTransparent)
        ));

    /* RELEASE DATE */
    // Set the release date at the field
    releaseDateTv.setText(entry.getReleaseDate());

    /* POSTER (and Banner) */
    // Load the entry's poster image into the imageView using picasso
    // Use NetworkUtil to form the query url, pass in the posterPath
    URL posterUrl = NetworkUtil.buildImageUrl(entry.getPosterPath());

    // Load into the posterBannerIv
    Picasso.get()
        .load(posterUrl.toString())
        .into(posterBannerIv);

    // Make it semi-transparent to aid readability
    posterBannerIv.setImageAlpha(126);

    // Load into the posterIv
    Picasso.get()
        .load(posterUrl.toString())
        .resize(posterIv.getWidth(), 0)
        .into(posterIv);

    // Log posterIv size
    Log.d(TAG, "posterIv width: " + posterIv.getWidth() + ", height: " + posterIv.getHeight());

    /* VOTE AVERAGE */
    // Set the vote average score at the field
    double voteAverage = entry.getVoteAverage();
    voteAverageTv.setText(getString(R.string.vote_average, voteAverage));

    /* VOTE COUNT */
    // Set the vote count at the field
    long voteCount = entry.getVoteCount();
    voteCountTv.setText(getString(R.string.vote_count, voteCount));

    /* OVERVIEW */
    // Set the movie overview at the field
    overViewTv.setText(entry.getOverview());

    /*******************
     * Favorite Button *
     *******************/
    // FAV button allows user to favorite / un-favorite the movie
    loadFavButton(movieId);

    /******************************************
     * Movie's Video Assets (Trailer or More) *
     ******************************************/
    // Show user all the movie's related videos on record in TMDB
    loadRelatedVideos(movieId);

    /*******************
     * Movie's Reviews *
     *******************/
    // Show user all the movie's related reviews on record in TMDB
    loadUserReviews(movieId);
  }

  /**
   * setUpFavButton is a sub method that handles setting up the fav button. This button
   * allows user to mark the movie as a favorite, or remove the movie from the favorite list
   * (if the movie is already in this list)
   *
   * @param movieId the unique id of a particular movie from TMDB, and also act as the unique
   *                identifier in the client's database.
   */
  private void loadFavButton(Long movieId) {
    // Set the color when the user clicks the button
    favBtn.setRippleColor(ContextCompat.getColor(this, R.color.colorSecondary));

    // Build the Uri that points to the movie base on the id, this Uri is required
    // for getting the fav the fav status
    final Uri updateUri = buildMovieUriWithId(movieId);

    // Get the current favorite status, the button changes appearance to indicate
    // the current favorite status base on this value
    mFavStatus = getCurrentFavoriteStatus(updateUri);

    // Create a onClickListener for the favorite button, this handles when a user clicks the
    // fav button
    View.OnClickListener onFavClickListener = view -> {
      // On Click, toggle the mMarkedFavorite and update with that value
      final ContentValues contentValues = new ContentValues();
      contentValues.put(AppDbContract.MovieEntry.COLUMN_MOVIE_TMDB_ID, !mFavStatus);

      // Get a reference to the contentResolver
      final ContentResolver contentResolver = mContext.getContentResolver();

      // Use the MarkMovieFavoriteAsyncTask to update the movie's favorite status
      // asynchronously.
      new DbUpdateAsyncTask()
          .setContentResolver(contentResolver)
          .setUpdateUri(updateUri)
          .setContentValues(contentValues)
          .setListener(rowsUpdated -> {
            // call getCurrentFavoriteStatus to update the current mFavStatus when
            // the database update operation is complete
            mFavStatus = getCurrentFavoriteStatus(updateUri);

            // Base on the current FavStatus, show user their update is done
            Snackbar.make(rootScrollView,
                mFavStatus ? R.string.action_added_to_favorite : R.string.action_removed_from_favorite,
                Snackbar.LENGTH_SHORT).show();

            // Set Result (when user exits this activity, to notify the starting activity
            // of user has changed favorite result) whenever the this activity finishes.
            setResult(1);
          }).execute();
    };

    // Set fav button onClick to favorite this movie
    favBtn.setOnClickListener(onFavClickListener);
  }

  /**
   * loadRelatedVideos uses the particular movie id to query the TMDB for its list of
   * related videos, and upon completion returns an arrayList of MovieVideo objects
   * to populate the related video recycler view.
   *
   * @param movieId the particular movie's unique id on TMDB
   */
  private void loadRelatedVideos(final Long movieId) {
    /******************************************
     * (!) Set API Key from the Resource file *
     ******************************************/
    final String mApiKey = getString(R.string.tmdb_api_key);

    // Show the related video loading indicator
    mRelatedVideoLoadingPb.setVisibility(View.VISIBLE);

    new FetchRelatedVideoAsyncTask()
        .setApiKey(mApiKey)
        .setMovieId(movieId)
        .setListener(entries -> {
          // Hide the loading indicator
          mRelatedVideoLoadingPb.setVisibility(View.GONE);

          // got related video entries?
          if (entries == null || entries.isEmpty()) {
            // entries is either null or empty, show empty view
            showRelatedVideos(false);
          } else {
            // has data to show, get the URL of the first video that is of type trailer
            mShareTrailerUrl = getFirstMatchingVideoTypeUrl(entries, "Trailer");

            // has data to show, bind it to the adapter
            mRelatedVideosAdapter.setRelatedVideoData(entries);
            showRelatedVideos(true);
          }
        }).execute();

  }

  private void loadUserReviews(final Long movieId) {
    /******************************************
     * (!) Set API Key from the Resource file *
     ******************************************/
    final String mApiKey = getString(R.string.tmdb_api_key);

    // Show the loading indicator
    mUserReviewLoadingPb.setVisibility(View.VISIBLE);

    new FetchUserReviewAsyncTask()
        .setApiKey(mApiKey)
        .setMovieId(movieId)
        .setListener(entries -> {
          // Hide the loading indicator
          mUserReviewLoadingPb.setVisibility(View.GONE);

          // got related user reviews?
          if (entries == null || entries.isEmpty()) {
            showUserReviews(false);
          } else {
            // has data to show, bind it to the adapter
            mUserReviewsAdapter.setUserReviewData(entries);
            showUserReviews(true);
          }
        }).execute();
  }

  /**
   * This method make queries with the user database to see if this movie is a favorite, and also
   * modifies the favorite button
   *
   * @param movieUri the specific Uri that points to the movie
   */
  private boolean getCurrentFavoriteStatus(Uri movieUri) {
    Cursor cursor = getContentResolver().query(movieUri,
        null,
        null,
        null,
        null);

    // Check for error
    if (cursor == null || !cursor.moveToNext()) {
      throw new RuntimeException("Error occurred");
    }

    // Get the user's favorite status from the cursor, with the column index
    int favoriteColIndex = cursor.getColumnIndex("1");
    int markedFavorite = cursor.getInt(favoriteColIndex);

    // Close cursor to prevent mem leak;
    cursor.close();

    switch (markedFavorite) {
      case 0:
        // Current is false, button should show (add to favorite)
        favBtn.setImageResource(R.drawable.ic_favorite_off_24dp);
        return false;
      case 1:
        // Current is true, button should show (added to favorite)
        favBtn.setImageResource(R.drawable.ic_favorite_on_24dp);
        return true;
      default:
        throw new SQLiteException("Error occurred, the stored fav value is out of range");
    }
  }

  private void showRelatedVideos(boolean hasRelatedVideos) {
    mRelatedVideosRv.setVisibility(hasRelatedVideos ? View.VISIBLE : View.GONE);
    mEmptyViewRelatedVideosTv.setVisibility(hasRelatedVideos ? View.GONE : View.VISIBLE);
  }

  private void showUserReviews(boolean hasUserReviews) {
    mUserReviewsRv.setVisibility(hasUserReviews ? View.VISIBLE : View.GONE);
    mEmptyViewUserReviewsTv.setVisibility(hasUserReviews ? View.GONE : View.VISIBLE);
  }

  /**
   * getFirstMatchingVideoTypeUrl() takes a whole list of returned related video entries and find the
   * first entry that matches the video type criteria, returns its URL for share. The parameters
   * can't be null.
   *
   * @param entries   the entire entries of the related videos of a particular movie
   * @param videoType the parameter videoType that we want to match.
   * @return the URL of the first video that is a trailer
   */
  private URL getFirstMatchingVideoTypeUrl(@NonNull ArrayList<MovieVideo> entries, @NonNull final String videoType) {
    // For each entry in the entries of the MovieVideo, find the first entry that
    // is of the type trailer, and then return the url of such entry.

    /** (!) JAVA 8 implementation - Android N or later */
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
      // Use Predicate to test the entry against the criteria, return its video url
      // Use lambda express to use the Predicate interface, use predicate for testing
      Predicate<MovieVideo> videoTypePredicate = video -> video.getType().equals(videoType);

      // Find the first element that fits the predicate criteria, return it, or else return null
      MovieVideo firstVideo = entries.stream().filter(videoTypePredicate).findFirst().orElse(null);

      if (firstVideo != null) {
        // the filter returned the first occurrence, return its URL.
        return null;
//        return firstVideo.getVideoUrl();
      } else {
        // filter returned no result, return null
        return null;
      }
    }

    /** Default Implementation */
    // For each entry in the arrayList, do that test, return the first entry's url if it matches
    for (MovieVideo entry : entries) {
      if (entry.getType().equals(videoType)) {
        return null;
//        return entry.getVideoUrl();
      }
    }

    // Exited for loop without any entry that matches the criteria
    return null;
  }

  /**
   * This method shares the trailer and allows the user to select which app they would like to use to
   * share the trailer. Using ShareCompat's IntentBuilder, we get some really cool functionality for
   * free. The chooser that is started using the {@link ShareCompat.IntentBuilder#startChooser()} method will
   * create a chooser when more than one app on the device can handle the Intent. This happens
   * when the user has, for example, both a messenger app and an email app. If only one Activity
   * on the phone can handle the Intent, it will automatically be launched.
   *
   * @param shareContent Text that will be shared
   * @param shareTitle   The movie's title and the origin
   */
  private void shareTrailer(String shareTitle, String shareContent) {
    // Create a String variable called mimeType and set it to "text/plain"
    /*
     * You can think of MIME types similarly to file extensions. They aren't the exact same,
     * but MIME types help a computer determine which applications can open which content. For
     * example, if you double click on a .pdf file, you will be presented with a list of
     * programs that can open PDFs. Specifying the MIME type as text/plain has a similar affect
     * on our implicit Intent. With text/plain specified, all apps that can handle text content
     * in some way will be offered when we call startActivity on this particular Intent.
     */
    String mimeType = "text/plain";

    // Use ShareCompat.IntentBuilder to build the Intent and start the chooser
    /* ShareCompat.IntentBuilder provides a fluent API for creating Intents */
    ShareCompat.IntentBuilder
        /* The from method specifies the Context from which this share is coming from */
        .from(this)
        .setType(mimeType)
        .setChooserTitle(shareTitle)
        .setText(shareContent)
        .startChooser();
  }
}
