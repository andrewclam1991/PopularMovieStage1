package com.andrewclam.popularmovie.data.source;

import com.andrewclam.popularmovie.data.model.Movie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MovieMockRepositoryTest extends BaseMockRepositoryTest<Movie> {

  @Override
  List<Movie> provideTestItemsList() {
    List<Movie> list = new ArrayList<>(0);

    int count = 12345;
    while (count > 0){
      Movie item = new Movie();
      item.setUid(UUID.randomUUID().toString());
      list.add(item);
      count--;
    }
    return list;
  }

  @Override
  Movie provideTestItem() {
    Movie item = new Movie();
    item.setUid(UUID.randomUUID().toString());
    return item;
  }

  @Override
  Class<Movie> provideTestItemClass() {
    return Movie.class;
  }

  @Override
  Map<String, String> provideTestGetItemsOptions() {
    Map<String, String> options = new HashMap<>();
    options.put("key","value");
    return options;
  }
}
