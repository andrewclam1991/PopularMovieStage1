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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrewclam.popularmovie.data.PopularMovieDbContract;
import com.andrewclam.popularmovie.models.MovieListing;
import com.andrewclam.popularmovie.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.net.URL;

import static com.andrewclam.popularmovie.MainActivity.EXTRA_MOVIE_ENTRY_OBJECT;
import static com.andrewclam.popularmovie.data.PopularMovieDbContract.MovieListingEntry.COLUMN_FAVORITE;
import static com.andrewclam.popularmovie.data.PopularMovieDbContract.buildMovieUriWithId;

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
    private Button favBtn;
    private boolean mFavStatus;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Init context
        mContext = DetailActivity.this;

        // Reference the UI views
        posterIv = findViewById(R.id.iv_poster);
        releaseDateTv = findViewById(R.id.tv_release_date);
        voteAverageTv = findViewById(R.id.tv_vote_average);
        voteCountTv = findViewById(R.id.tv_vote_count);
        overViewTv = findViewById(R.id.tv_overview);
        favBtn = findViewById(R.id.button_mark_favorite);

        if (getIntent() != null && getIntent().hasExtra(EXTRA_MOVIE_ENTRY_OBJECT)) {
            // Unwrap the parcel to retrieve the entry object
            final MovieListing entry = Parcels.unwrap(getIntent().getParcelableExtra(EXTRA_MOVIE_ENTRY_OBJECT));

            if (entry != null) {
                // Build the Uri that points to the movie base on the id
                final Uri uri = buildMovieUriWithId(entry.getId());

                // Populate the ui entries
                populateEntryFields(entry);

                // Get the current favorite status
                mFavStatus = getCurrentFavoriteStatus(uri);

                // Set fav button onClick to favorite this movie
                favBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // On Click, toggle between the mMarkedFavorite and store that value
                        final ContentValues contentValues = new ContentValues();
                        contentValues.put(PopularMovieDbContract.MovieListingEntry.COLUMN_FAVORITE, !mFavStatus);

                        new AsyncTask() {
                            @Override
                            protected Object doInBackground(Object[] objects) {
                                getContentResolver().update(uri, contentValues, null, null);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Object o) {
                                // call getCurrentFavoriteStatus to update the current mFavStatus when
                                // the database update operation is complete
                                mFavStatus = getCurrentFavoriteStatus(uri);
                                super.onPostExecute(o);
                            }
                        }.execute();
                    }
                });
            }

        } else {
            Log.e(TAG, "Intent doesn't have the required movie entry");
            finish();
        }
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

        if (cursor != null && cursor.moveToNext()) {
            int favoriteColIndex = cursor.getColumnIndex(COLUMN_FAVORITE);
            int markedFavorite = cursor.getInt(favoriteColIndex);

            cursor.close();

            switch (markedFavorite) {
                case 0:
                    favBtn.setText(getString(R.string.add_to_favorite_list));
                    return false;
                case 1:
                    favBtn.setText(getString(R.string.added_to_favorite_list));
                    return true;
                default:
                    throw new IllegalArgumentException("Favorite column value out of range");
            }
        } else {
            throw new RuntimeException("Error occurred");
        }
    }
    /**
     * This is a method to populate the entry UI fields with data
     *
     * @param entry the single parsed movie entry
     */
    private void populateEntryFields(@NonNull MovieListing entry) {
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
