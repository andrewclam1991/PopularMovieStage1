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

import java.net.URL;

/**
 * Model class to store a particular movies' associated video data.
 */

@Parcel(Parcel.Serialization.BEAN)
public class RelatedVideo extends Entity{

  private String relatedVideoId;
  private String key;
  private String name;
  private String providerSite;
  private int size;
  private String videoType;
  private URL videoUrl;
  private URL thumbnailUrl;

  public RelatedVideo() {

  }

  public String getRelatedVideoId() {
    return relatedVideoId;
  }

  public void setRelatedVideoId(String relatedVideoId) {
    this.relatedVideoId = relatedVideoId;
  }

  public String getKey() {
    return key;
  }

  public void setProviderKey(String key) {
    this.key = key;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getProviderSite() {
    return providerSite;
  }

  public void setProviderSite(String providerSite) {
    this.providerSite = providerSite;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public String getVideoType() {
    return videoType;
  }

  public void setVideoType(String videoType) {
    this.videoType = videoType;
  }

  public URL getVideoUrl() {
    return videoUrl;
  }

  public void setVideoUrl(URL videoUrl) {
    this.videoUrl = videoUrl;
  }

  public URL getThumbnailUrl() {
    return thumbnailUrl;
  }

  public void setThumbnailUrl(URL thumbnailUrl) {
    this.thumbnailUrl = thumbnailUrl;
  }
}
