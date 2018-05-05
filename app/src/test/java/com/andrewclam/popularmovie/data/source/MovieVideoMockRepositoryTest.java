package com.andrewclam.popularmovie.data.source;

import com.andrewclam.popularmovie.data.model.MovieVideo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MovieVideoMockRepositoryTest extends BaseMockRepositoryTest<MovieVideo> {

  @Override
  List<MovieVideo> provideTestItemsList() {
    List<MovieVideo> list = new ArrayList<>(0);

    int count = 12345;
    while (count > 0){
      MovieVideo item = new MovieVideo();
      item.setUid(UUID.randomUUID().toString());
      list.add(item);
      count--;
    }
    return list;
  }

  @Override
  MovieVideo provideTestItem() {
    MovieVideo item = new MovieVideo();
    item.setUid(UUID.randomUUID().toString());
    return item;
  }

  @Override
  Class<MovieVideo> provideTestItemClass() {
    return MovieVideo.class;
  }

  @Override
  Map<String, String> provideTestGetItemsOptions() {
    Map<String, String> options = new HashMap<>();
    options.put("key","value");
    return options;
  }
}


