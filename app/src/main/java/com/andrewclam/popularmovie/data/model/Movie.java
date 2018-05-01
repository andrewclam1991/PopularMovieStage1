
package com.andrewclam.popularmovie.data.model;

import com.google.gson.annotations.SerializedName;


/**
 * Model class use to store movie metadata.
 */
public class Movie extends Entity{

  @SerializedName("poster_path")
  private String posterPath;

  @SerializedName("adult")
  private boolean adult;

  @SerializedName("overview")
  private String overview;

  @SerializedName("release_date")
  private String releaseDate;

  @SerializedName("id")
  private long movieId;

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

  /* Public No-Arg constructor */
  public Movie() {}

  public long getMovieId() {
    return movieId;
  }

  public void setMovieId(long id) {
    super.setUid(String.valueOf(id));
    this.movieId = id;
  }

  public String getPosterPath() {
    return posterPath;
  }

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

