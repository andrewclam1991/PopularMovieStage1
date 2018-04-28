package com.andrewclam.popularmovie.data.source.local;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.andrewclam.popularmovie.data.db.AppDbContract;
import com.andrewclam.popularmovie.data.model.Movie;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.andrewclam.popularmovie.data.db.AppDbContract.MovieEntry.ARG_MOVIE_FAVORITE_FALSE;
import static com.andrewclam.popularmovie.data.db.AppDbContract.MovieEntry.ARG_MOVIE_FAVORITE_TRUE;
import static com.andrewclam.popularmovie.data.db.AppDbContract.MovieEntry.CONTENT_URI_MOVIES;

/**
 * {@link MoviesLocalDataSource}
 * <p>
 * This class is only responsible for providing the CONTENT_URI_MOVIES and
 * implementing the from() and mapToItem(), its super class take care of the rest of
 * CRUD methods details. {@link LocalDataSource<>}
 * <p>
 * from() object mapping to ContentValues for saving item to local storage
 * mapToItem() parsing from a Cursor object to an item for retrieving an item from local storage.
 */

@Singleton
public class MoviesLocalDataSource extends LocalDataSource<Movie> {

  @Inject
  @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
  MoviesLocalDataSource() {}

  @VisibleForTesting
  @NonNull
  @Override
  ContentValues from(@NonNull Movie item) {
    ContentValues values = new ContentValues();
    values.put(AppDbContract.MovieEntry.COLUMN_MOVIE_TMDB_ID, item.getMovieId());
    values.put(AppDbContract.MovieEntry.COLUMN_TITLE, item.getTitle());
    values.put(AppDbContract.MovieEntry.COLUMN_RELEASE_DATE, item.getReleaseDate());
    values.put(AppDbContract.MovieEntry.COLUMN_VOTE_AVERAGE, item.getVoteAverage());
    values.put(AppDbContract.MovieEntry.COLUMN_VOTE_COUNT, item.getVoteCount());
    values.put(AppDbContract.MovieEntry.COLUMN_POPULARITY, item.getPopularity());
    values.put(AppDbContract.MovieEntry.COLUMN_OVERVIEW, item.getOverview());
    values.put(AppDbContract.MovieEntry.COLUMN_FAVORITE, item.isSetFavorite()?
        ARG_MOVIE_FAVORITE_TRUE : ARG_MOVIE_FAVORITE_FALSE);
    values.put(AppDbContract.MovieEntry.COLUMN_NAME_DELETE_FLAG, item.isSetDelete() ? 1 : 0);
    return values;
  }

  @VisibleForTesting
  @NonNull
  @Override
  Movie mapToItem(@NonNull Cursor c) {
    long id = c.getLong(c.getColumnIndexOrThrow(AppDbContract.MovieEntry.COLUMN_MOVIE_TMDB_ID));
    String title = c.getString(c.getColumnIndexOrThrow(AppDbContract.MovieEntry.COLUMN_TITLE));
    String releaseDate = c.getString(c.getColumnIndexOrThrow(AppDbContract.MovieEntry.COLUMN_RELEASE_DATE));
    double voteAverage = c.getDouble(c.getColumnIndexOrThrow(AppDbContract.MovieEntry.COLUMN_VOTE_AVERAGE));
    long voteCount = c.getInt(c.getColumnIndexOrThrow(AppDbContract.MovieEntry.COLUMN_VOTE_COUNT));
    double popularity = c.getDouble(c.getColumnIndexOrThrow(AppDbContract.MovieEntry.COLUMN_POPULARITY));
    String overview = c.getString(c.getColumnIndexOrThrow(AppDbContract.MovieEntry.COLUMN_OVERVIEW));
    boolean setFavorite = c.getInt(c.getColumnIndexOrThrow(AppDbContract.MovieEntry.COLUMN_FAVORITE))
        == ARG_MOVIE_FAVORITE_TRUE;
    boolean setDelete = c.getInt(c.getColumnIndexOrThrow(AppDbContract.MovieEntry.COLUMN_NAME_DELETE_FLAG)) == 1;


    Movie item = new Movie();
    item.setMovieId(id);
    item.setTitle(title);
    item.setReleaseDate(releaseDate);
    item.setVoteAverage(voteAverage);
    item.setVoteCount(voteCount);
    item.setPopularity(popularity);
    item.setOverview(overview);
    item.setFavorite(setFavorite);
    item.setDelete(setDelete);
    return item;
  }

  @VisibleForTesting
  @NonNull
  @Override
  Uri setContentUri() {
    return CONTENT_URI_MOVIES;
  }
}
