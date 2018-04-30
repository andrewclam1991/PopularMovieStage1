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

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.andrewclam.popularmovie.R;
import com.andrewclam.popularmovie.util.NetworkUtil;

import dagger.android.support.DaggerAppCompatActivity;

/**
 * Entry point of the PopularMovie application, responsible for showing a user a list of
 * current popular movies from TMDB.
 */

public class MainActivity extends DaggerAppCompatActivity{

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    // See if savedInstanceState exists and where we retained user's query selection
    if (savedInstanceState != null) {
    //  mListType = savedInstanceState.getString(LIST_TYPE_SELECTOR_KEY);
    //   mListState = savedInstanceState.getParcelable(LIST_STATE_KEY);
    } else {
      // Default to popular and the first position
    //  mListType = NetworkUtil.TMDB_PATH_POPULAR;
    }
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    /*
     * (!) Due to row limitations,  with grid layout view the find the first visible
     * would get the first column of the row in different orientation. More complicated because
     * gridsize is calculated in runtime, adapting to different screen sizes and ratios
     *
     * ex.
     * 1st vertical view (initial view, ROW 2 is visible, 4 is the first element)
     * [1*] [2] [3]
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
    // mListState = mLayoutManager.onSaveInstanceState();

    // Saved layout manager's list state parcelable
   // savedInstanceState.putParcelable(LIST_STATE_KEY, mListState);

    // Save the user's current sort value and position
   // savedInstanceState.putString(LIST_TYPE_SELECTOR_KEY, mListType);

    // Always call the superclass so it can save the view hierarchy state
    super.onSaveInstanceState(savedInstanceState);
  }


}
