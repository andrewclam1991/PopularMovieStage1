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

package com.andrewclam.popularmovie.views.main;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.andrewclam.popularmovie.views.detail.DetailActivity;
import com.andrewclam.popularmovie.R;
import com.andrewclam.popularmovie.adapters.MovieListingsAdapter;
import com.andrewclam.popularmovie.async.FetchMovieAsyncTask;
import com.andrewclam.popularmovie.async.PopularMovieDbSync;
import com.andrewclam.popularmovie.data.db.AppDbContract;
import com.andrewclam.popularmovie.data.model.MovieListing;
import com.andrewclam.popularmovie.util.LayoutManagerUtil;
import com.andrewclam.popularmovie.util.NetworkUtil;

import org.parceler.Parcels;

import java.util.ArrayList;

import static com.andrewclam.popularmovie.async.PopularMovieDbSync.parseEntriesFromCursor;
import static com.andrewclam.popularmovie.util.NetworkUtil.TMDB_PATH_POPULAR;
import static com.andrewclam.popularmovie.util.NetworkUtil.TMDB_PATH_TOP_RATED;

/**
 * Created by Andrew Chi Heng Lam on 8/19/2017.
 * <p>
 * MainActivity of the PopularMovie application, shows a user a default list of movies from TMDB
 * (first-page) in a recyclerView, user can select sort-value base on their preference and get a
 * different list of movie as a response.
 */

public class MainActivity extends AppCompatActivity implements
    LoaderManager.LoaderCallbacks<Cursor>, MovieListingsAdapter.OnMovieEntryClickListener {
  public static final String EXTRA_MOVIE_ENTRY_OBJECT = "extra_movie_entry_obj";
  /*
   * Start Activity For Result codes
   * Use this for the MainActivity to restartLoader and refresh the dataset
   */
  public static final int FAVORITE_CHANGE_REQUEST = 200;
  public static final int FAVORITE_CHANGED_RESULT = 201;
  /*Log Tag*/
  private static final String TAG = MainActivity.class.getSimpleName();
  /*Constant - Keys*/
  private static final String LIST_STATE_KEY = "list_state_key";

  private static final String LIST_TYPE_SELECTOR_KEY = "instance_sort_val";
  private static final String USER_SHOW_FAVORITES_KEY = "user_show_favorite_movies";
  /*
   * This ID will be used to identify the Loader responsible for loading our offline database. In
   * some cases, one Activity can deal with many Loaders. However, in our case, there is only one.
   * We will still use this ID to initialize the loader and create the loader for best practice.
   */
  private static final int ID_MOVIE_LISTING_LOADER = 123;

  private Context mContext;
  private ProgressBar mProgressBar;
  private LinearLayout mErrorMsgLayout;
  private RecyclerView mRecyclerView;
  private GridLayoutManager mLayoutManager;
  private MovieListingsAdapter mAdapter;
  private String mListType;
  private Parcelable mListState;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    // Init Context
    mContext = MainActivity.this;

    // UI Reference
    mProgressBar = findViewById(R.id.pb_loading_indicator);
    mErrorMsgLayout = findViewById(R.id.linear_layout_error_msg);
    mRecyclerView = findViewById(R.id.rv_movie_entries);

    // Init adapter
    mAdapter = new MovieListingsAdapter(this);

    // Init layout manager
    int spanSize = LayoutManagerUtil.getSpanSize(this);
    mLayoutManager = new GridLayoutManager(this, spanSize);

    // Attach the layout manager to the recyclerView
    mRecyclerView.setLayoutManager(mLayoutManager);

    // Back the recyclerView with the adapter
    mRecyclerView.setAdapter(mAdapter);

    // See if savedInstanceState exists and where we retained user's query selection
    if (savedInstanceState != null) {
      mListType = savedInstanceState.getString(LIST_TYPE_SELECTOR_KEY);
      mListState = savedInstanceState.getParcelable(LIST_STATE_KEY);
    } else {
      // Default to popular and the first position
      mListType = TMDB_PATH_POPULAR;
    }

    // Show the loading indicator
    mProgressBar.setVisibility(View.VISIBLE);

    // Call loadMovieData to load the latest data from the TMDB server
    loadMovieData(mListType);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    switch (id) {
      case R.id.action_sort_by_popularity:
        // Set the instance var sort by value to POPULAR
        mListType = TMDB_PATH_POPULAR;
        loadMovieData(mListType);
        return true;
      case R.id.action_sort_by_rating:
        // Set the sort by value to TOP_RATED
        mListType = TMDB_PATH_TOP_RATED;
        loadMovieData(mListType);
        return true;
      case R.id.action_show_favorites:
        // Set the sort by value to user's favorites
        mListType = USER_SHOW_FAVORITES_KEY;
        loadMovieData(mListType);
      default:
        return super.onOptionsItemSelected(item);
    }

  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    /* (!) Due to row limitations,  with grid layout view the find the first visible
     * would get the first column of the row in different orientation. More complicated because
     * gridsize is calculated in runtime, adapting to different screen sizes and ratios
     *
     * ex.
     * 1st vertical view (initial view, ROW 2 is visible, 4 is the first element)
     * [1] [2] [3]
     * [4#] [5] [6] <- visible row
     * [7] [8] [9]
     *
     * -> Rotate to horizontal
     * 1st Horizontal View (smooth scroll to row that contains 4,)
     * [1##] [2] [3] [4#] <- visible row
     * [5] [6] [7] [8]
     * [9]
     *
     *
     * Now, the get first visible would return 1, and on next rotation, the screen would
     * not return to the 1st vertical view
     * -> Rotate to vertical
     * 2nd vertical view
     *
     * [1##] [2] [3] <- becomes visible, first element changed to [1]
     * [4] [5] [6]
     * [7] [8] [9]
     */

    // get the layout manager's list state
    mListState = mLayoutManager.onSaveInstanceState();

    // Saved layout manager's list state parcelable
    savedInstanceState.putParcelable(LIST_STATE_KEY, mListState);

    // Save the user's current sort value and position
    savedInstanceState.putString(LIST_TYPE_SELECTOR_KEY, mListType);

    // Always call the superclass so it can save the view hierarchy state
    super.onSaveInstanceState(savedInstanceState);
  }

  /**
   * This method loads the movie entry data from a separate thread,
   * uses the MovieEntryAsyncTask
   *
   * @param mListType the user selected value for sorting the result entry
   *                  according to their preference of the type of list.
   */

  private void loadMovieData(String mListType) {
    /******************************
     * Check Device Network State *
     ******************************/
    boolean isConnected = NetworkUtil.getNetworkState(mContext);

    if (mListType.equals(USER_SHOW_FAVORITES_KEY) || !isConnected) {
      // NETWORK DISCONNECTED //

      /*
       * If not connected, possibly due to no network connectivity or down server
       * Try to use Loader to get the Cursor, to accessing the cached data from the database
       * and populate the entries.
       *
       * <p>
       * Ensures a loader is initialized and active. If the loader doesn't already exist, one
       * is created and (if the activity/fragment is currently started) starts the loader.
       * Otherwise the last created loader is re-used.
       *
       * <p>
       * OnLoadFinished handles the populating the UI with data
       */

      getSupportLoaderManager().restartLoader(ID_MOVIE_LISTING_LOADER, null, this);
      return;
    }

    // NETWORK CONNECTED //

    /******************************************
     * (!) Set API Key from the Resource file *
     ******************************************/
    final String mApiKey = getString(R.string.tmdb_api_key);

    // Create a new MovieEntryAsyncTask to fetch movie entry data from the server
    // on a background thread
    new FetchMovieAsyncTask()
        .setSortByValue(mListType)
        .setApiKey(mApiKey)
        .setListener(new FetchMovieAsyncTask.onMovieEntryTaskInteractionListener() {
          @Override
          public void onPreExecute() {
            // Show the loading indicator
            mProgressBar.setVisibility(View.VISIBLE);
          }

          @Override
          public void onPostExecute(@Nullable ArrayList<MovieListing> entries) {
            // Task complete, hide the loading indicator
            mProgressBar.setVisibility(View.GONE);

            // Check the entries
            if (entries == null || entries.size() == 0) {
              // show error msg
              showErrorMsg();
              return;
            }

            // Entries contain data, sync and cache data with the fresh data
            PopularMovieDbSync.syncDatabase(mContext, entries);

            // Bind the entries to the adapter for display
            mAdapter.setMovieEntryData(entries);

            showMovieData();
          }
        }).execute();
  }

  /**
   * This method shows the entry data when it is available and hides the error msg
   */
  private void showMovieData() {
    // Restore the layout manager list state if the state is saved from the
    // previous instance
    if (mListState != null) {
      mLayoutManager.onRestoreInstanceState(mListState);
    }

    mErrorMsgLayout.setVisibility(View.GONE);
    mRecyclerView.setVisibility(View.VISIBLE);
  }

  /**
   * This methods shows error msg and hides the data view
   */
  private void showErrorMsg() {
    mRecyclerView.setVisibility(View.GONE);
    mErrorMsgLayout.setVisibility(View.VISIBLE);
  }

  /**
   * Called by the {@link android.support.v4.app.LoaderManagerImpl} when a new Loader needs to be
   * created. This Activity only uses one loader, so we don't necessarily NEED to check the
   * loaderId, but this is certainly best practice.
   *
   * @param loaderId The loader ID for which we need to create a loader
   * @param args     Any arguments supplied by the caller
   * @return A new Loader instance that is ready to start loading.
   */
  @Override
  public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
    switch (loaderId) {
      case ID_MOVIE_LISTING_LOADER:
        // (!) Convert mListType for TMDB to the client database commands
        String selection = null;
        String selectionArgs[] = null;
        String mSortOrderStr = null;

        switch (mListType) {
          case TMDB_PATH_POPULAR:
            mSortOrderStr = AppDbContract.PopularMovieEntry.COLUMN_POPULARITY
                + " DESC";
            break;
          case TMDB_PATH_TOP_RATED:
            mSortOrderStr = AppDbContract.PopularMovieEntry.COLUMN_VOTE_AVERAGE
                + " DESC";
            break;
          case USER_SHOW_FAVORITES_KEY:
            selection = AppDbContract.PopularMovieEntry.COLUMN_FAVORITE + "=?";
            selectionArgs = new String[]{"1"};
            mSortOrderStr = null;
            break;
          default:
            // no conversion needed
        }

        return new CursorLoader(this,
            AppDbContract.PopularMovieEntry.CONTENT_URI,
            null,
            selection,
            selectionArgs,
            mSortOrderStr);
      default:
        throw new RuntimeException("Loader Not Implemented: " + loaderId);
    }
  }

  /**
   * Called when a Loader has finished loading its data.
   *
   * @param loader The Loader that has finished.
   * @param data   The data generated by the Loader.
   */
  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    // Task complete, hide the loading indicator
    mProgressBar.setVisibility(View.GONE);

    // Parse the cursor data into an ArrayList of movie listing
    ArrayList<MovieListing> entries = parseEntriesFromCursor(data);

    // Bind the entries to the adapter for display
    mAdapter.setMovieEntryData(entries);

    // only show the entryData if there are entries to show.
    if (entries.size() > 0) {
      showMovieData();
    } else {
      showErrorMsg();
    }
  }

  /**
   * Called when a previously created loader is being reset, and thus making its data unavailable.
   * The application should at this point remove any references it has to the Loader's data.
   *
   * @param loader The Loader that is being reset.
   */
  @Override
  public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    /*
     * Since this Loader's data is now invalid, we need to clear the Adapter that is
     * displaying the data.
     */
    mAdapter.setMovieEntryData(null);
  }

  /**
   * Callback when the entry item is clicked, callback from the MovieEntryAdapter
   *
   * @param entry the particular entry that is clicked by the user
   */
  @Override
  public void onItemClicked(MovieListing entry) {
    // Starts the DetailActivity with the entry item to populate the movie entry info
    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
    intent.putExtra(EXTRA_MOVIE_ENTRY_OBJECT, Parcels.wrap(entry));
    startActivityForResult(intent, FAVORITE_CHANGE_REQUEST);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      case FAVORITE_CHANGE_REQUEST:
        switch (resultCode) {
          case FAVORITE_CHANGED_RESULT:
            if (mListType.equals(USER_SHOW_FAVORITES_KEY)) {
              // Restart the loader to load new database entries to update the main view if the user
              // is in "MY FAVORITES"
              getSupportLoaderManager().restartLoader(ID_MOVIE_LISTING_LOADER, null, this);
            }
            break;
        }
        break;
      default:
        // call super's implementation in default
        super.onActivityResult(requestCode, resultCode, data);
    }
  }
}
