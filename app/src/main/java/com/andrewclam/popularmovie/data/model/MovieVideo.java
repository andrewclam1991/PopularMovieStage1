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

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import static com.andrewclam.popularmovie.data.model.MovieVideo.QueryConstants.ARG_MOVIE_ID;
import static com.andrewclam.popularmovie.data.modelapi.BaseContract.Paths.PATH_MOVIE;
import static com.andrewclam.popularmovie.data.modelapi.BaseContract.QUERY_API_KEY;

/**
 * Model class to store a particular movies' associated video data
 */
public final class MovieVideo extends Entity {

  /**
   * Unique id that identifies the {@link Movie} in TMDB
   */
  private long movieId;

  /**
   * Unique id that identifies the particular {@link MovieVideo} in TMDB
   */
  @SerializedName("id")
  private String movieVideoId;

  @SerializedName("key")
  private String key;

  @SerializedName("name")
  private String name;

  @SerializedName("site")
  private String site;

  @SerializedName("size")
  private int size;

  @SerializedName("type")
  private String type;

  public long getMovieId() {
    return movieId;
  }

  public void setMovieId(long movieId) {
    this.movieId = movieId;
  }

  public String getMovieVideoId() {
    return movieVideoId;
  }

  public void setMovieVideoId(String movieVideoId) {
    this.movieVideoId = movieVideoId;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSite() {
    return site;
  }

  public void setSite(String site) {
    this.site = site;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  /**
   * Model class to store the serialized response data and holds a
   * list of returned {@link MovieVideo}s.
   * <p>
   * Example request:
   * https://api.themoviedb.org/3/movie/{movie_id}/videos?api_key=<<api_key>>&language=en-US
   */
  public class Response {
    @SerializedName("id")
    private int movieVideoResponseId;

    @SerializedName("results")
    private List<MovieVideo> results;

    public int getMovieVideoResponseId() {
      return movieVideoResponseId;
    }

    public void setMovieVideoResponseId(int movieVideoResponseId) {
      this.movieVideoResponseId = movieVideoResponseId;
    }

    public List<MovieVideo> getResults() {
      return results;
    }

    public void setResults(List<MovieVideo> results) {
      this.results = results;
    }
  }

  /**
   * QueryConstants for the {@link MovieVideo} api
   */
  public static final class QueryConstants {
    // path arg
    public final static String ARG_MOVIE_ID = "movie-id";

    // query keys
    public final static String QUERY_KEY_LANGUAGE = "language";
  }

  /**
   * Retrofit interface for generating an {@link MovieVideo} api client
   */
  public interface ServiceApi {
    /**
     * Query service api using the provided arguments and gets a json {@link MovieVideo.Response},
     * which contains a list of {@link MovieVideo}.
     *
     * @param apiKey  developer api key
     * @param movieId the unique id of the {@link Movie} to query a list of its {@link MovieVideo}s
     * @return an observable that when subscribed to, will return a {@link MovieVideo.Response}
     * if the call was successful.
     */
    @NonNull
    @GET(PATH_MOVIE + "/" + "{" + ARG_MOVIE_ID + "}")
    Flowable<MovieVideo.Response> getMovieVideos(@Query(QUERY_API_KEY) @NonNull String apiKey,
                                                 @Path(ARG_MOVIE_ID) int movieId);
  }

}

