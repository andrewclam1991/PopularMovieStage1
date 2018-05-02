package com.andrewclam.popularmovie.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

// This class contains all fetched movies and extra information
public class ApiResponse<E extends Entity> {
  @SerializedName("page")
  private int page;

  @SerializedName("results")
  private List<E> results;

  @SerializedName("total_results")
  private int totalResults;

  @SerializedName("total_pages")
  private int totalPages;

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public List<E> getResults() {
    return results;
  }

  public void setResults(List<E> results) {
    this.results = results;
  }

  public int getTotalResults() {
    return totalResults;
  }

  public void setTotalResults(int totalResults) {
    this.totalResults = totalResults;
  }

  public int getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(int totalPages) {
    this.totalPages = totalPages;
  }
}
