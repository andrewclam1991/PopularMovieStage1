/*
 * Copyright <2017> <ANDREW LAM>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.andrewclam.popularmovie;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.andrewclam.popularmovie.models.MovieListing;
import com.andrewclam.popularmovie.sync.PopularMovieAsyncTask;
import com.andrewclam.popularmovie.sync.PopularMovieDbSync;
import com.andrewclam.popularmovie.utilities.LayoutManagerUtils;
import com.andrewclam.popularmovie.utilities.NetworkUtils;

import org.parceler.Parcels;

import java.util.ArrayList;

import static com.andrewclam.popularmovie.utilities.NetworkUtils.TMDB_PATH_POPULAR;
import static com.andrewclam.popularmovie.utilities.NetworkUtils.TMDB_PATH_TOP_RATED;

/**
 * Created by Andrew Chi Heng Lam on 8/19/2017.
 * <p>
 * MainActivity of the PopularMovie application, shows a user a default list of movies from TMDB
 * (first-page) in a recyclerView, user can select sort-value base on their preference and get a
 * different list of movie as a response.
 */

public class MainActivity extends AppCompatActivity implements MovieListingsAdapter.OnMovieEntryClickListener {

    /*Constants */
    public static final String EXTRA_MOVIE_ENTRY_OBJECT = "extra_movie_entry_obj";
    /*Log Tag*/
    private static final String TAG = MainActivity.class.getSimpleName();
    /*Constant*/
    private static final String SORT_VAL = "instance_sort_val";

    /*Instance Var*/
    private ProgressBar mProgressBar;
    private LinearLayout mErrorMsgLayout;
    private RecyclerView mRecyclerView;
    private MovieListingsAdapter mAdapter;
    private String mSortByValue;
    private Context mContext;

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
        int spanSize = LayoutManagerUtils.getSpanSize(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, spanSize);

        // Attach the layout manager to the recyclerView
        mRecyclerView.setLayoutManager(layoutManager);

        // Back the recyclerView with the adapter
        mRecyclerView.setAdapter(mAdapter);

        // See if savedInstanceState exists and where we retained user's query selection
        if (savedInstanceState != null) {
            mSortByValue = savedInstanceState.getString(SORT_VAL);
        } else {
            // Default to popular
            mSortByValue = TMDB_PATH_POPULAR;
        }

        // Call loadMovieData to populate the adapter with data from the TMDB server
        loadMovieData(mSortByValue);
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
                mSortByValue = TMDB_PATH_POPULAR;
                loadMovieData(mSortByValue);
                return true;
            case R.id.action_sort_by_rating:
                // Set the sort by value to TOP_RATED
                mSortByValue = TMDB_PATH_TOP_RATED;
                loadMovieData(mSortByValue);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current sort value
        savedInstanceState.putString(SORT_VAL, mSortByValue);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * This method loads the movie entry data from a separate thread,
     * uses the MovieEntryAsyncTask
     *
     * @param sortByValue the user selected value for sorting the result entry
     *                    according to their preference.
     */

    private void loadMovieData(String sortByValue) {
        // Set API Key from the Resource file
        String mApiKey = getString(R.string.tmdb_api_key);

        // Create a new MovieEntryAsyncTask to fetch movie entry data from the server
        // on a background thread
        new PopularMovieAsyncTask()
                .setApiKey(mApiKey)
                .setListener(new PopularMovieAsyncTask.onMovieEntryTaskInteractionListener() {
                    @Override
                    public void onPreExecute() {
                        // Show the loading indicator
                        mProgressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onPostExecute(@Nullable ArrayList<MovieListing> entries) {
                        // Task complete, hide the loading indicator
                        mProgressBar.setVisibility(View.GONE);

                        // Check Internet state
                        boolean isConnected = NetworkUtils.getNetworkState(mContext);
                        boolean isFromCache = false;
                        if (!isConnected) {
                            // If not connected, possibly due to no network connectivity or down server
                            // we would expect an empty entries.
                            // Try to get the cache from the database and populate the entries
                            entries = PopularMovieDbSync.getEntriesFromDatabase(mContext);
                            if (entries != null) {
                                // If the entries from the database is not null, set the flag
                                // is from Cache to true;
                                isFromCache = true;
                            }
                        }

                        // Check the entries
                        // AsyncTask returns a set of movie entries,
                        if (entries == null || entries.size() == 0) {

                            // If entries is still empty or null,
                            // show error msg
                            showErrorMsg();
                            return;

                        }

                        // Entries contain data, sync and cache data if the data is not from the cache
                        if (!isFromCache) {
                            PopularMovieDbSync.syncDatabase(mContext, entries);
                        }

                        // Bind the entries to the adapter for display
                        mAdapter.setMovieEntryData(entries);
                        showEntryData();
                    }
                }).execute(sortByValue);
    }

    /**
     * This method shows the entry data when it is available and hides the error msg
     */
    private void showEntryData() {
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
     * Callback when the entry item is clicked, callback from the MovieEntryAdapter
     *
     * @param entry the particular entry that is clicked by the user
     */
    @Override
    public void onItemClicked(MovieListing entry) {
        // Starts the DetailActivity with the entry item to populate the movie entry info
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(EXTRA_MOVIE_ENTRY_OBJECT, Parcels.wrap(entry));
        startActivity(intent);
    }

    // Register a broadcast receiver to detect network change, and do database sync
}
