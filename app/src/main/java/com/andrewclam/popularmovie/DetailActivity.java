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

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrewclam.popularmovie.adapters.RelatedVideosAdapter;
import com.andrewclam.popularmovie.adapters.ReviewsAdapter;
import com.andrewclam.popularmovie.data.PopularMovieDbContract;
import com.andrewclam.popularmovie.models.MovieListing;
import com.andrewclam.popularmovie.models.RelatedVideo;
import com.andrewclam.popularmovie.sync.DbUpdateAsyncTask;
import com.andrewclam.popularmovie.sync.FetchVideoInfoAsyncTask;
import com.andrewclam.popularmovie.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.andrewclam.popularmovie.MainActivity.EXTRA_MOVIE_ENTRY_OBJECT;
import static com.andrewclam.popularmovie.MainActivity.FAVORITE_CHANGED_RESULT;
import static com.andrewclam.popularmovie.data.PopularMovieDbContract.PopularMovieEntry.COLUMN_FAVORITE;
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

    // RecyclerView for Related Videos
    private RecyclerView mRelatedVideosRv;
    private RelatedVideosAdapter mRelatedVideosAdapter;

    // RecyclerView for Reviews
    private RecyclerView mReviewsRv;
    private ReviewsAdapter mReivewsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent() != null && getIntent().hasExtra(EXTRA_MOVIE_ENTRY_OBJECT)) {
            // Unwrap the parcel to retrieve the entry object
            final MovieListing entry = Parcels.unwrap(getIntent().getParcelableExtra(EXTRA_MOVIE_ENTRY_OBJECT));

            if (entry != null) {
                // Init context
                mContext = DetailActivity.this;

                // Reference the UI views
                posterIv = findViewById(R.id.iv_poster);
                releaseDateTv = findViewById(R.id.tv_release_date);
                voteAverageTv = findViewById(R.id.tv_vote_average);
                voteCountTv = findViewById(R.id.tv_vote_count);
                overViewTv = findViewById(R.id.tv_overview);
                favBtn = findViewById(R.id.button_mark_favorite);

                mRelatedVideosRv = findViewById(R.id.related_video_rv);
                mRelatedVideosAdapter = new RelatedVideosAdapter(new RelatedVideosAdapter.OnMovieEntryClickListener() {
                    @Override
                    public void onItemClicked(RelatedVideo entry) {
                        // Get the video url from the entry object
                        URL videoUrl = entry.getVideoUrl();
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

                    }
                });

                // Populate the referenced UI with the entry object
                populateEntryFields(entry);
            }

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
    private void populateEntryFields(@NonNull MovieListing entry) {
        /* MOVIE ID */
        // The id required to do favorite, video and comment's db and network io
        Long movieId = entry.getId();

        /**********************
         * Movie General Info *
         **********************/

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

        /*******************
         * Favorite Button *
         *******************/
        // FAV button allows user to favorite / un-favorite the movie
        setupFavButton(movieId);

        /******************************************
         * Movie's Video Assets (Trailer or More) *
         ******************************************/
        // Show user all the movie's related videos on record in TMDB
        setupRelatedVideos(movieId);

        /*******************
         * Movie's Reviews *
         *******************/
        // Show user all the movie's related videos on record in TMDB
        // todo implement the review api query, parse, store and display
    }


    // todo, set the options to show share icon, and get the first trailer from madapter

    /**
     * setupRelatedVideos
     *
     * @param movieId
     */
    private void setupRelatedVideos(final Long movieId) {
        /******************************************
         * (!) Set API Key from the Resource file *
         ******************************************/
        final String mApiKey = getString(R.string.tmdb_api_key);

        new FetchVideoInfoAsyncTask()
                .setApiKey(mApiKey)
                .setMovieId(movieId)
                .setListener(new FetchVideoInfoAsyncTask.OnFetchVideoInfoCompleteListener() {
                    @Override
                    public void onComplete(ArrayList<RelatedVideo> entries) {
                        // got related video entries?
                        if (entries != null) {
                            mRelatedVideosAdapter.setRelatedVideoData(entries);
                        } else {
                            // No related videos, show NoVideo in the view
                        }
                    }
                }).execute();

    }

    /**
     * setUpFavButton is a sub method that handles setting up the fav button. This button
     * allows user to mark the movie as a favorite, or remove the movie from the favorite list
     * (if the movie is already in this list)
     *
     * @param movieId the unique id of a particular movie from TMDB, and also act as the unique
     *                identifier in the client's database.
     */
    private void setupFavButton(Long movieId) {
        // Build the Uri that points to the movie base on the id, this Uri is required
        // for getting the fav the fav status
        final Uri updateUri = buildMovieUriWithId(movieId);

        // Get the current favorite status, the button changes appearance to indicate
        // the current favorite status base on this value
        mFavStatus = getCurrentFavoriteStatus(updateUri);

        // Create a onClickListener for the favorite button, this handles when a user clicks the
        // fav button
        View.OnClickListener onFavClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // On Click, toggle the mMarkedFavorite and update with that value
                final ContentValues contentValues = new ContentValues();
                contentValues.put(PopularMovieDbContract.PopularMovieEntry.COLUMN_FAVORITE, !mFavStatus);

                // Get a reference to the contentResolver
                final ContentResolver contentResolver = mContext.getContentResolver();

                // Use the MarkMovieFavoriteAsyncTask to update the movie's favorite status
                // asynchronously.
                new DbUpdateAsyncTask()
                        .setContentResolver(contentResolver)
                        .setUpdateUri(updateUri)
                        .setContentValues(contentValues)
                        .setListener(new DbUpdateAsyncTask.OnMovieUpdateActionListener() {
                            @Override
                            public void onUpdateComplete(Integer rowsUpdated) {
                                // call getCurrentFavoriteStatus to update the current mFavStatus when
                                // the database update operation is complete
                                mFavStatus = getCurrentFavoriteStatus(updateUri);

                                // Set Result (when user exits this activity, to notify the starting activity
                                // of user has changed favorite result) whenever the this activity finishes.
                                setResult(FAVORITE_CHANGED_RESULT);
                            }
                        }).execute();
            }
        };

        // Set fav button onClick to favorite this movie
        favBtn.setOnClickListener(onFavClickListener);
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
        int favoriteColIndex = cursor.getColumnIndex(COLUMN_FAVORITE);
        int markedFavorite = cursor.getInt(favoriteColIndex);

        // Close cursor to prevent mem leak;
        cursor.close();

        switch (markedFavorite) {
            case 0:
                // Current is false, button should show (add to favorite)
                favBtn.setText(getString(R.string.add_to_favorite_list));
                return false;
            case 1:
                // Current is true, button should show (added to favorite)
                favBtn.setText(getString(R.string.added_to_favorite_list));
                return true;
            default:
                throw new SQLiteException("Error occurred, the stored fav value is out of range");
        }
    }
}
