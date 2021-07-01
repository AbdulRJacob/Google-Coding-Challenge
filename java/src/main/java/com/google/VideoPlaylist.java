package com.google;

import java.util.ArrayList;
import java.util.List;


/** A class used to represent a Playlist */
class VideoPlaylist {

  List<Video> videoPlaylist;
  private final String name;
  private int size;

  public VideoPlaylist(String name) {
    this.videoPlaylist = new ArrayList<>();
    this.name = name;
    size = 0;
  }

  // Adds Video to playlist
  public void addVideo(Video video) {
    videoPlaylist.add(video);
    size++;
  }

  // Remove Video From playlist
  public void removeVideo(Video video) {
    videoPlaylist.remove(video);
    size--;
  }

  // Removes all video from playlist
  public void removeAll(){
    videoPlaylist = new ArrayList<>();
    size = 0;
  }

  // Checks if the playlist contains a video
  public boolean contains(Video video) {
    return videoPlaylist.contains(video);
  }


  public List<Video> getVideoPlaylist(){
    return videoPlaylist;
  }

  public String getName() {
    return name;
  }

  public int getSize() {
    return size;
  }
}
