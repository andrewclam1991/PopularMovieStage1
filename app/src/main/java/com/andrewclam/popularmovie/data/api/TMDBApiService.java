package com.andrewclam.popularmovie.data.api;

import com.andrewclam.popularmovie.data.model.Entity;
import com.andrewclam.popularmovie.data.model.Movie;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

import static com.andrewclam.popularmovie.data.api.TMDBApiContract.*;
import static com.andrewclam.popularmovie.data.api.TMDBApiContract.DiscoverMoviesParam.QUERY_SORT_BY_KEY;
import static com.andrewclam.popularmovie.data.api.TMDBApiContract.TMDBContract.QUERY_API_KEY;

public interface TMDBApiService<E extends Entity> {

  /**
   * https://api.themoviedb.org/3/discover/movie?api_key=<<YOUR_API_KEY>>&sort_by=popularity.desc
   *
   * @param apiKey
   * @return
   */
  @GET("discover/movies")
  Call<List<E>> getItems(@Query(QUERY_API_KEY) String apiKey);

  /**
   * https://api.themoviedb.org/3/discover/movie?api_key=<<YOUR_API_KEY>>&sort_by=popularity.desc
   *
   * @param apiKey
   * @return
   */
  @GET("discover/movies")
  Call<List<E>> getItems(@Query(QUERY_API_KEY) String apiKey,
                         @QueryMap Map<String, String> options);

}
