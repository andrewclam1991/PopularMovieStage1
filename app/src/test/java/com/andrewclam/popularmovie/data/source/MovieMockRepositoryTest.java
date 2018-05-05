package com.andrewclam.popularmovie.data.source;

import com.andrewclam.popularmovie.data.model.Movie;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MovieMockRepositoryTest extends BaseMockRepositoryTest<Movie> {

  @Override
  List<Movie> provideTestItemsList() {
    List<Movie> list = new ArrayList<>();
    int count = 30;
    while (count > 0){
      Movie item = new Movie();
      item.setUid(UUID.randomUUID().toString());
      list.add(item);
      count--;
    }
    return list;
  }

  @Override
  Class<Movie> provideTestItemClass() {
    return Movie.class;
  }
}
