/*
 * Copyright <2017> <ANDREW LAM>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.andrewclam.popularmovie.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.andrewclam.popularmovie.R;
import com.andrewclam.popularmovie.models.MovieListing;
import com.andrewclam.popularmovie.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Andrew Chi Heng Lam on 8/19/2017.
 * <p>
 * MovieEntryAdapter is an implementation of the RecyclerViewAdapter, used to back the recyclerView
 * with the data, also contains an inner class that hold the cache of views.
 */

public class MovieListingsAdapter extends RecyclerView.Adapter<MovieListingsAdapter.MovieEntryAdapterViewHolder> {

    /*Log Tag*/
    private final static String TAG = MovieListingsAdapter.class.getSimpleName();
    private final OnMovieEntryClickListener mOnItemClickedListener; // Activity pass this
    /*Instance Vars*/
    private ArrayList<MovieListing> mEntries;

    /*Default Constructor*/
    public MovieListingsAdapter(OnMovieEntryClickListener mOnItemClickedListener) {
        mEntries = new ArrayList<>();
        this.mOnItemClickedListener = mOnItemClickedListener;
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param parent   The ViewGroup that these ViewHolders are contained within.
     * @param viewType If your RecyclerView has more than one type of item (which ours doesn't) you
     *                 can use this viewType integer to provide a different layout. See
     *                 {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                 for more details.
     * @return A new ForecastAdapterViewHolder that holds the View for each list item
     */
    @Override
    public MovieEntryAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // P: To create a viewHolder, RelatedVideoAdapterViewHolder needs an inflated itemView to work with
        // R: Needs a layout inflater to inflate the list item view
        // S: Layout inflater can be obtains from context, use LayoutInflater.from(context)
        // R: Needs a res id for the inflater to know which view to inflate
        // S: assign the layoutResId with the list item view

        // Get the layout inflater (required to inflate the itemView)
        Context context = parent.getContext(); // Context required by the layoutInflater
        LayoutInflater inflater = LayoutInflater.from(context);

        int layoutResId = R.layout.entry_list_item;
        boolean shouldAttachToParentImmediately = false; // optional parameter to indicate exactly what it says

        // Uses the layoutId, viewGroup, boolean signature of the inflater.inflate()
        @SuppressWarnings("ConstantConditions") View view = inflater.inflate(layoutResId, parent, shouldAttachToParentImmediately);

        return new MovieEntryAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the movie entry
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param holder   The ViewHolder which should be updated to represent the
     *                 contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(MovieEntryAdapterViewHolder holder, int position) {
        // This is where we bind data to the ViewHolder

        // Get the posterPath info from the entry item at the adapter position
        String posterPath = mEntries.get(position).getPosterPath();
        holder.loadPoster(posterPath);
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our movie entries
     */
    @Override
    public int getItemCount() {
        if (mEntries.isEmpty()) return 0;
        return mEntries.size();
    }

    /**
     * setMovieEntryData() updates the adapter's current data set
     *
     * @param mEntries the new dataset that we want to update the adapter with
     */
    public void setMovieEntryData(ArrayList<MovieListing> mEntries) {
        this.mEntries = mEntries;
        notifyDataSetChanged();
    }

    /**
     * Callback Interface
     * Handle on itemClick event in each itemView inside the RecyclerView
     */
    public interface OnMovieEntryClickListener {
        void onItemClicked(MovieListing entry);
    }

    /**
     * RelatedVideoAdapterViewHolder
     * an implementation of the RecyclerView.ViewHolder class that act as a cache fo the children
     * views for a single movie entry item.
     * <p>
     * Current implementation only shows the thumbnail of the movie entry and handles a click on
     * this item.
     */
    public class MovieEntryAdapterViewHolder extends RecyclerView.ViewHolder {

        private final ImageView mPosterIv;

        public MovieEntryAdapterViewHolder(View itemView) {
            super(itemView);
            // Reference the thumbnail Iv ui element
            mPosterIv = itemView.findViewById(R.id.iv_poster);
            // Set an onClickListener onto the itemView
            itemView.setOnClickListener(view -> {
                // 1) Get the current adapter position
                int adapterPosition = getAdapterPosition();

                // 2) Find the corresponding clicked entry in the entries
                MovieListing entry = mEntries.get(adapterPosition);

                // 3) Use onClickHandler to notify the activity of a onClick event
                // pass in the retrieved object
                mOnItemClickedListener.onItemClicked(entry);

                // Let the listening activity to handle starting the detailActivity
                // with the movie entry parameter.
            });
        }

        /**
         * loadPoster method loads the entry poster image with the parameter url
         *
         * @param posterPath the downloaded entry poster image's path (needs to set base url)
         */
        public void loadPoster(String posterPath) {
            // Use NetworkUtils to form the query url, pass in the posterPath
            URL posterUrl = NetworkUtils.buildImageUrl(posterPath);

            Picasso.with(mPosterIv.getContext())
                    .load(posterUrl.toString())
                    .into(mPosterIv);
        }
    }
}
