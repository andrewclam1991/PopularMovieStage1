
package com.andrewclam.popularmovie.data.model;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

/**
 * Model class use to store movie metadata.
 * see https://developers.themoviedb.org/3/discover/movie-discover
 */
public class Movie extends Entity{

  @SerializedName("id")
  private long movieId;

  @SerializedName("poster_path")
  private String posterPath;

  @SerializedName("adult")
  private boolean adult;

  @SerializedName("overview")
  private String overview;

  @SerializedName("release_date")
  private String releaseDate;

  @SerializedName("original_title")
  private String originalTitle;

  @SerializedName("original_language")
  private String originalLanguage;

  @SerializedName("title")
  private String title;

  @SerializedName("backdrop_path")
  private String backdropPath;

  @SerializedName("popularity")
  private double popularity;

  @SerializedName("vote_count")
  private long voteCount;

  @SerializedName("video")
  private boolean video;

  @SerializedName("vote_average")
  private double voteAverage;

  private boolean isFavorite;

  public long getMovieId() {
    return movieId;
  }

  public void setMovieId(long id) {
    super.setUid(String.valueOf(id));
    this.movieId = id;
  }

  public String getPosterPath() {
    posterPath = posterPath.replace("/","");
    return posterPath;
  }

  /**
   * Sets the path to the poster, including the file extension. Builds the image URL
   * To build an image URL, you will need 3 pieces of data. The base_url, size and file_path.
   * Simply combine them all and you will have a fully qualified URL. Here’s an example URL:
   * https://image.tmdb.org/t/p/w500/8uO0gUM8aNqYLs1OsTBQiXu0fEv.jpg
   * [base_url]                /[size]/[path]
   *
   * Note: Sanitize poster path because it contains forward slash
   * this doesn't play well with {@link Uri.Builder#appendPath(String)}
   * @param posterPath the fully qualified path to the poster, including the file extension
   */
  public void setPosterPath(String posterPath) {
    this.posterPath = posterPath;
  }

  public boolean isAdult() {
    return adult;
  }

  public void setAdult(boolean adult) {
    this.adult = adult;
  }

  public String getOverview() {
    return overview;
  }

  public void setOverview(String overview) {
    this.overview = overview;
  }

  public String getReleaseDate() {
    return releaseDate;
  }

  public void setReleaseDate(String releaseDate) {
    this.releaseDate = releaseDate;
  }

  public String getOriginalTitle() {
    return originalTitle;
  }

  public void setOriginalTitle(String originalTitle) {
    this.originalTitle = originalTitle;
  }

  public String getOriginalLanguage() {
    return originalLanguage;
  }

  public void setOriginalLanguage(String originalLanguage) {
    this.originalLanguage = originalLanguage;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getBackdropPath() {
    return backdropPath;
  }

  public void setBackdropPath(String backdropPath) {
    this.backdropPath = backdropPath;
  }

  public double getPopularity() {
    return popularity;
  }

  public void setPopularity(double popularity) {
    this.popularity = popularity;
  }

  public long getVoteCount() {
    return voteCount;
  }

  public void setVoteCount(long voteCount) {
    this.voteCount = voteCount;
  }

  public boolean isVideo() {
    return video;
  }

  public void setVideo(boolean video) {
    this.video = video;
  }

  public double getVoteAverage() {
    return voteAverage;
  }

  public void setVoteAverage(double voteAverage) {
    this.voteAverage = voteAverage;
  }

  public boolean isFavorite() {
    return isFavorite;
  }

  public void setFavorite(boolean favorite) {
    isFavorite = favorite;
  }
}

