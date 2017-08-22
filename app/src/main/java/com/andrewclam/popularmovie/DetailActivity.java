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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrewclam.popularmovie.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.net.URL;

import static com.andrewclam.popularmovie.MainActivity.EXTRA_MOVIE_ENTRY_OBJECT;

/**
 * Created by Andrew Chi Heng Lam on 8/19/2017.
 * <p>
 * DetailActivity is used to show user each movie entry in detail when a thumbnail is clicked in
 * MainActivity, detail includes the title, poster, rating and overview etc.
 */

public class DetailActivity extends AppCompatActivity {
    // Log Tag
    private static final String TAG = DetailActivity.class.getSimpleName();

    // UI views
    private ImageView posterIv;
    private TextView releaseDateTv;
    private TextView voteAverageTv;
    private TextView voteCountTv;
    private TextView overViewTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Reference the UI views
        posterIv = findViewById(R.id.iv_poster);
        releaseDateTv = findViewById(R.id.tv_release_date);
        voteAverageTv = findViewById(R.id.tv_vote_average);
        voteCountTv = findViewById(R.id.tv_vote_count);
        overViewTv = findViewById(R.id.tv_overview);

        if (getIntent() != null && getIntent().hasExtra(EXTRA_MOVIE_ENTRY_OBJECT)) {
            // Unwrap the parcel to retrieve the entry object
            MovieEntry entry = Parcels.unwrap(getIntent().getParcelableExtra(EXTRA_MOVIE_ENTRY_OBJECT));
            if (entry != null) populateEntryFields(entry);
        } else {
            Log.e(TAG, "Intent doesn't have the required movie entry");
            finish();
        }
    }

    /**
     * This is a method to populate the entry UI fields with data
     *
     * @param entry the single parsed movie entry
     */
    private void populateEntryFields(@NonNull MovieEntry entry) {
        /* TITLE */
        // Set movie title as the activity title
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(entry.getTitle());

        /* RELEASE DATE */
        // Set the release date at the field
        releaseDateTv.setText(String.valueOf(entry.getReleaseDate()));

        /* POSTER */
        // Load the entry's poster image into the imageView using picasso
        // Use NetworkUtils to form the query url, pass in the posterPath
        URL posterUrl = NetworkUtils.buildImageUrl(entry.getPosterPath());

        Picasso.with(this)
                .load(posterUrl.toString())
                .into(posterIv);

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
    }
}
