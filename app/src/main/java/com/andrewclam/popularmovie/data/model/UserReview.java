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
 * Created by Andrew Chi Heng Lam on 9/2/2017.
 * A Model Class to store a particular movies' associated user review, includes the unique id,
 * author and the review content
 * It uses Parceler to take care of its parcelable implementation, when needed.
 */

@Parcel(Parcel.Serialization.BEAN)
public class UserReview {
  private String reviewId;
  private String author;
  private String content;
  private String reviewUrl;
  private String contentSnippet;

  public UserReview() {}

  public String getReviewId() {
    return reviewId;
  }

  public void setReviewId(String reviewId) {
    this.reviewId = reviewId;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getReviewUrl() {
    return reviewUrl;
  }

  public void setReviewUrl(String reviewUrl) {
    this.reviewUrl = reviewUrl;
  }

  public String getContentSnippet() {
    return contentSnippet;
  }

  public void setContentSnippet(String contentSnippet) {
    this.contentSnippet = contentSnippet;
  }
}
