package com.andrewclam.popularmovie.data.source;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.andrewclam.popularmovie.data.db.AppDbContract;
import com.andrewclam.popularmovie.data.model.MovieVideo;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.andrewclam.popularmovie.data.db.AppDbContract.MovieEntry.CONTENT_URI_MOVIES;

/**
 * {@link DataSourceLocalMovieVideos}
 * <p>
 * This class is only responsible for providing the CONTENT_URI_MOVIES and
 * implementing the from() and mapToItem(), its super class take care of the rest of
 * CRUD methods details. {@link DataSourceLocal <>}
 * <p>
 * from() object mapping to ContentValues for saving item to local storage
 * mapToItem() parsing from a Cursor object to an item for retrieving an item from local storage.
 */

@Singleton
class DataSourceLocalMovieVideos extends DataSourceLocal<MovieVideo> {

  @Inject
  @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
  DataSourceLocalMovieVideos() {}

  @VisibleForTesting
  @NonNull
  @Override
  ContentValues from(@NonNull MovieVideo item) {
    ContentValues values = new ContentValues();
    values.put(AppDbContract.MovieVideoEntry.COLUMN_MOVIE_TMDB_ID,item.getMovieId());
    values.put(AppDbContract.MovieVideoEntry.COLUMN_ID,item.getMovieVideoId());
    values.put(AppDbContract.MovieVideoEntry.COLUMN_KEY,item.getKey());
    values.put(AppDbContract.MovieVideoEntry.COLUMN_NAME,item.getName());
    values.put(AppDbContract.MovieVideoEntry.COLUMN_SITE,item.getSite());
    values.put(AppDbContract.MovieVideoEntry.COLUMN_SIZE,item.getSize());
    values.put(AppDbContract.MovieVideoEntry.COLUMN_TYPE,item.getType());
    values.put(AppDbContract.MovieVideoEntry.COLUMN_NAME_DELETE_FLAG,item.isSetDelete()? 1:0);
    return values;
  }

  @VisibleForTesting
  @NonNull
  @Override
  MovieVideo mapToItem(@NonNull Cursor c) {
    long movieId = c.getLong(c.getColumnIndexOrThrow(AppDbContract.MovieVideoEntry.COLUMN_MOVIE_TMDB_ID));
    String movieVideoId = c.getString(c.getColumnIndexOrThrow(AppDbContract.MovieVideoEntry.COLUMN_ID));
    String key = c.getString(c.getColumnIndexOrThrow(AppDbContract.MovieVideoEntry.COLUMN_KEY));
    String name = c.getString(c.getColumnIndexOrThrow(AppDbContract.MovieVideoEntry.COLUMN_NAME));
    String site = c.getString(c.getColumnIndexOrThrow(AppDbContract.MovieVideoEntry.COLUMN_SITE));
    int size = c.getInt(c.getColumnIndexOrThrow(AppDbContract.MovieVideoEntry.COLUMN_SIZE));
    String type = c.getString(c.getColumnIndexOrThrow(AppDbContract.MovieVideoEntry.COLUMN_TYPE));
    boolean setDelete = c.getInt(c.getColumnIndexOrThrow(AppDbContract.MovieEntry.COLUMN_NAME_DELETE_FLAG)) == 1;

    MovieVideo item = new MovieVideo();
    item.setMovieId(movieId);
    item.setMovieVideoId(movieVideoId);
    item.setKey(key);
    item.setName(name);
    item.setSite(site);
    item.setSize(size);
    item.setType(type);
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
