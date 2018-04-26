/*
 * Copyright <2017> <ANDREW LAM>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.andrewclam.popularmovie.data.model;

import org.parceler.Parcel;

/**
 * Model class use to store movie metadata.
 */
@Parcel(Parcel.Serialization.BEAN)
public class Movie extends Entity{

  private long movieId;
  private String title;
  private String releaseDate;
  private String posterPath;
  private double voteAverage;
  private long voteCount;
  private double popularity;
  private String overview;
  private boolean setFavorite;

  /* Public No-Arg constructor */
  public Movie() {

  }

  public long getMovieId() {
    return movieId;
  }

  public void setMovieId(long id) {
    super.setUid(String.valueOf(id));
    this.movieId = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getPosterPath() {
    return posterPath;
  }

  public void setPosterPath(String posterPath) {
    this.posterPath = posterPath;
  }

  public double getVoteAverage() {
    return voteAverage;
  }

  public void setVoteAverage(double voteAverage) {
    this.voteAverage = voteAverage;
  }

  public long getVoteCount() {
    return voteCount;
  }

  public void setVoteCount(long voteCount) {
    this.voteCount = voteCount;
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

  public double getPopularity() {
    return popularity;
  }

  public void setPopularity(double popularity) {
    this.popularity = popularity;
  }

  public boolean isSetFavorite() {
    return setFavorite;
  }

  public void setFavorite(boolean setFavorite) {
    this.setFavorite = setFavorite;
  }
}

