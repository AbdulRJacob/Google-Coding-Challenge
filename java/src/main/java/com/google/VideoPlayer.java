package com.google;

import java.util.*;
import java.util.stream.Collectors;

public class VideoPlayer {

  private final VideoLibrary videoLibrary;
  private Video currentVideo;
  private List<VideoPlaylist> videoPlaylist;
  private State state = State.STOPPED;

  public VideoPlayer() {
    this.videoLibrary = new VideoLibrary();
    this.videoPlaylist = new ArrayList<>();
  }

  public void numberOfVideos() {
    System.out.printf("%s videos in the library%n", videoLibrary.getVideos().size());
  }

  public void showAllVideos() {
    System.out.println("Here's a list of all available videos:");
    List<Video> videos = videoLibrary.getVideos();
    videos = sortVideos(videos);

    for (Video video : videos) {
      System.out.println(printInfo(video));
    }
  }

  // Print video information formatted
  private String printInfo(Video video) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < video.getTags().size(); i++) {
      sb.append(video.getTags().get(i)).append(" ");
    }
    if (sb.toString().length() != 0) {
      sb.deleteCharAt(sb.toString().length() - 1);
    }

    if (video.flag().isFlagged()){
      return video.getTitle()
          + " ("
          + video.getVideoId()
          + ") ["
          + sb.toString()
          + "] - FLAGGED (reason: " + video.flag().getReason() + ")";
    }

    return video.getTitle() + " (" + video.getVideoId() + ") [" + sb.toString() + "]";
  }

  // Sorts a list of video in lexicographical order
  private List<Video> sortVideos(List<Video> videoList) {
    List<Video> sortedList = videoList;
    sortedList =
        sortedList.stream()
            .sorted(Comparator.comparing(Video::getTitle))
            .collect(Collectors.toList());

    return sortedList;
  }

  public void playVideo(String videoId) {
    Video previousVideo = currentVideo;
    currentVideo = videoLibrary.getVideo(videoId);
    if (currentVideo == null) {
      System.out.println("Cannot play video: Video does not exist");
      return;
    }

    if (currentVideo.flag().isFlagged()) {
      System.out.println(
          "Cannot play video: Video is currently flagged (reason: "
              + currentVideo.flag().getReason()
              + ")");
      return;
    }

    if (previousVideo != null && state == State.PLAYING || state == State.PAUSED) {
      assert previousVideo != null;
      System.out.println("Stopping video: " + previousVideo.getTitle());
    }

    state = State.PLAYING;
    System.out.println("Playing video: " + currentVideo.getTitle());
  }

  public void stopVideo() {
    if (currentVideo == null || state == State.STOPPED) {
      System.out.println("Cannot stop video: No video is currently playing");
      return;
    }

    System.out.println("Stopping video: " + currentVideo.getTitle());
    state = State.STOPPED;
  }

  public void playRandomVideo() {
    Random random = new Random();
    if (allFlagged()){
      System.out.println("No videos available");
      return;
    }
    int next = random.nextInt(videoLibrary.getVideos().size());
    Video randomVid = videoLibrary.getVideos().get(next);
    boolean pick = false;

    while (!(randomVid.flag().isFlagged()) && pick){
      next = random.nextInt(videoLibrary.getVideos().size());
      randomVid = videoLibrary.getVideos().get(next);
      if (randomVid != null){
        pick = true;
      }
    }

    playVideo(randomVid.getVideoId());
  }

  // Checks if all videos are flagged
  private boolean allFlagged(){
    for (Video vid : videoLibrary.getVideos()) {
      if (!vid.flag().isFlagged()){
        return false;
      }
    }

    return true;
  }

  public void pauseVideo() {
    if (currentVideo == null || state == State.STOPPED) {
      System.out.println("Cannot pause video: No video is currently playing");
      return;
    }

    if (state == State.PAUSED) {
      System.out.println("Video already paused: " + currentVideo.getTitle());
    } else if (state == State.PLAYING) {
      System.out.println("Pausing video: " + currentVideo.getTitle());
      state = State.PAUSED;
    }
  }

  public void continueVideo() {
    if (currentVideo == null) {
      System.out.println("Cannot continue video: No video is currently playing");
      return;
    } else if (state == State.PLAYING || state == State.STOPPED) {
      System.out.println("Cannot continue video: Video is not paused");
      return;
    }

    System.out.println("Continuing video: " + currentVideo.getTitle());
    state = State.PLAYING;
  }

  public void showPlaying() {
    if (currentVideo == null || state == State.STOPPED) {
      System.out.println("No video is currently playing");
    } else if (state == State.PLAYING) {
      System.out.println("Currently playing: " + printInfo(currentVideo));
    } else if (state == State.PAUSED) {
      System.out.println("Currently playing: " + printInfo(currentVideo) + " - PAUSED");
    }
  }

  public void createPlaylist(String playlistName) {
    if (duplicatePlaylist(playlistName)) {
      System.out.println("Cannot create playlist: A playlist with the same name already exists");
      return;
    }
    VideoPlaylist playlist = new VideoPlaylist(playlistName);
    videoPlaylist.add(playlist);
    System.out.println("Successfully created new playlist: " + playlistName);
    sort();
  }

  private boolean duplicatePlaylist(String name) {
    for (VideoPlaylist p : videoPlaylist) {
      if (p.getName().toLowerCase().equals(name.toLowerCase())) {
        return true;
      }
    }
    return false;
  }

  public void addVideoToPlaylist(String playlistName, String videoId) {
    if (!duplicatePlaylist(playlistName)) {
      System.out.println("Cannot add video to " + playlistName + ": Playlist does not exist");
      return;
    }
    Video vid = videoLibrary.getVideo(videoId);

    if (vid == null) {
      System.out.println("Cannot add video to my_playlist: Video does not exist");
      return;
    }

    if (vid.flag().isFlagged()) {
      System.out.println(
              "Cannot add video to " + playlistName + ": Video is currently flagged (reason: "
                      + vid.flag().getReason()
                      + ")");
      return;
    }


    VideoPlaylist playlist = getPlaylist(playlistName);
    assert playlist != null;
    if (playlist.contains(vid)) {
      System.out.println("Cannot add video to " + playlistName + ": Video already added");
      return;
    }
    playlist.addVideo(vid);
    System.out.println("Added video to " + playlistName + ": " + vid.getTitle());
  }

  // Sorts Playlist in lexicographical order
  private void sort() {
    videoPlaylist =
        videoPlaylist.stream()
            .sorted(Comparator.comparing(VideoPlaylist::getName))
            .collect(Collectors.toList());
  }

  private VideoPlaylist getPlaylist(String playlistName) {
    for (VideoPlaylist playlist : videoPlaylist) {
      if (playlist.getName().toLowerCase().equals(playlistName.toLowerCase())) {
        return playlist;
      }
    }
    return null;
  }

  public void showAllPlaylists() {
    if (videoPlaylist.isEmpty()) {
      System.out.println("No playlists exist yet");
    } else {
      System.out.println("Showing all playlists: ");
      for (VideoPlaylist v : videoPlaylist) {
        System.out.println(v.getName());
      }
    }
  }

  public void showPlaylist(String playlistName) {
    if (!duplicatePlaylist(playlistName)) {
      System.out.println("Cannot show playlist " + playlistName + ": Playlist does not exist");
      return;
    }

    VideoPlaylist playlist = getPlaylist(playlistName);
    System.out.println("Showing playlist: " + playlistName);

    assert playlist != null;
    if (playlist.getSize() == 0) {
      System.out.println("No videos here yet");
    } else {
      for (Video vid : playlist.getVideoPlaylist()) {
        System.out.println(printInfo(vid));
      }
    }
  }

  public void removeFromPlaylist(String playlistName, String videoId) {
    if (!duplicatePlaylist(playlistName)) {
      System.out.println("Cannot remove video from " + playlistName + ": Playlist does not exist");
      return;
    }

    VideoPlaylist playlist = getPlaylist(playlistName);
    Video vid = videoLibrary.getVideo(videoId);

    if (vid == null) {
      System.out.println("Cannot remove video from " + playlistName + ": Video does not exist");
      return;
    }

    assert playlist != null;
    if (!playlist.contains(vid)) {
      System.out.println("Cannot remove video from " + playlistName + ": Video is not in playlist");
      return;
    }

    playlist.removeVideo(vid);
    System.out.println("Removed video from " + playlistName + ": " + vid.getTitle());
  }

  public void clearPlaylist(String playlistName) {
    if (!duplicatePlaylist(playlistName)) {
      System.out.println("Cannot clear playlist " + playlistName + ": Playlist does not exist");
      return;
    }

    VideoPlaylist playlist = getPlaylist(playlistName);
    assert playlist != null;
    playlist.removeAll();

    System.out.println("Successfully removed all videos from " + playlistName);
  }

  public void deletePlaylist(String playlistName) {
    if (!duplicatePlaylist(playlistName)) {
      System.out.println("Cannot delete playlist " + playlistName + ": Playlist does not exist");
      return;
    }

    videoPlaylist.removeIf(
        playlist -> playlist.getName().toLowerCase().equals(playlistName.toLowerCase()));
    System.out.println("Deleted playlist: " + playlistName);
  }

  public void searchVideos(String searchTerm) {
    HashMap<Integer, Video> videoHashMap = new HashMap<>();
    int i = 1;
    for (Video video : videoLibrary.getVideos()) {
      if (checkTitle(video, searchTerm)) {
        videoHashMap.put(i, video);
        i++;
      }
    }

    if (i == 1) {
      System.out.println("No search results for " + searchTerm);
      return;
    }

    videoHashMap = sortHashMap(videoHashMap);
    System.out.println("Here are the results for " + searchTerm + ":");
    for (int j = 1; j < videoHashMap.size() + 1; j++) {
      System.out.println(j + ") " + printInfo(videoHashMap.get(j)));
    }
    System.out.println(
        "Would you like to play any of the above? If yes, specify the number of the video.");
    System.out.println("If your answer is not a valid number, we will assume it's a no.");

    Scanner scanner = new Scanner(System.in);
    String input = scanner.nextLine();

    try {
      int res = Integer.parseInt(input);
      if (res < i && res >= 1) {
        playVideo(videoHashMap.get(res).getVideoId());
      }
    } catch (NumberFormatException ignored) {

    }
  }


  private boolean checkTitle(Video video, String searchTerm) {
    if (video.flag().isFlagged()){
      return false;
    }
    return video.getTitle().toLowerCase().contains(searchTerm.toLowerCase());
  }

  private HashMap<Integer, Video> sortHashMap(HashMap<Integer, Video> videoHashMap) {
    HashMap<Integer, Video> sortedHash = new HashMap<>();
    List<Video> videoList = sortVideos(videoHashMap.values().stream().collect(Collectors.toList()));
    int i = 1;
    for (Video v : videoList) {
      sortedHash.put(i, v);
      i++;
    }

    return sortedHash;
  }

  public void searchVideosWithTag(String videoTag) {
    HashMap<Integer, Video> videoHashMap = new HashMap<>();
    int val = 1;
    for (Video video : videoLibrary.getVideos()) {
      if (checkTag(video, videoTag)) {
        videoHashMap.put(val, video);
        val++;
      }
    }

    if (val == 1) {
      System.out.println("No search results for " + videoTag);
      return;
    }

    videoHashMap = sortHashMap(videoHashMap);
    System.out.println("Here are the results for " + videoTag + ":");
    for (int j = 1; j < videoHashMap.size() + 1; j++) {
      System.out.println(j + ") " + printInfo(videoHashMap.get(j)));
    }
    System.out.println(
        "Would you like to play any of the above? If yes, specify the number of the video.");
    System.out.println("If your answer is not a valid number, we will assume it's a no.");

    Scanner scanner = new Scanner(System.in);
    String input = scanner.nextLine();

    try {
      int res = Integer.parseInt(input);
      if (res < val && res >= 1) {
        playVideo(videoHashMap.get(res).getVideoId());
      }
    } catch (NumberFormatException ignored) {

    }
  }

  private boolean checkTag(Video video, String tag) {
    if (video.flag().isFlagged()){
      return false;
    }
    for (String t : video.getTags()) {
      if (t.toLowerCase().equals(tag.toLowerCase())) {
        return true;
      }
    }
    return false;
  }

  public void flagVideo(String videoId) {
    Video video = videoLibrary.getVideo(videoId);
    if (video == null) {
      System.out.println("Cannot flag video: Video does not exist");
      return;
    }

    if (video.flag().isFlagged()) {
      System.out.println("Cannot flag video: Video is already flagged");
    }

    if ((state == State.PLAYING || state == State.PAUSED) && video.equals(currentVideo)){
      stopVideo();
    }

    video.flag().setFlag(true);
    System.out.println(
        "Successfully flagged video: "
            + video.getTitle()
            + " (reason: "
            + video.flag().getReason()
            + ")");
  }

  public void flagVideo(String videoId, String reason) {
    Video video = videoLibrary.getVideo(videoId);
    if (video == null) {
      System.out.println("Cannot flag video: Video does not exist");
      return;
    }

    if (video.flag().isFlagged()) {
      System.out.println("Cannot flag video: Video is already flagged");
      return;
    }

    if ((state == State.PLAYING || state == State.PAUSED) && video.equals(currentVideo)){
      stopVideo();
    }
    video.flag().setFlag(true);
    video.flag().setReason(reason);
    System.out.println(
        "Successfully flagged video: "
            + video.getTitle()
            + " (reason: "
            + video.flag().getReason()
            + ")");
  }

  public void allowVideo(String videoId) {
    Video video = videoLibrary.getVideo(videoId);

    if(video == null){
      System.out.println("Cannot remove flag from video: Video does not exist");
    }else if (!video.flag().isFlagged()){
      System.out.println("Cannot remove flag from video: Video is not flagged");
    }else{
      video.flag().setFlag(false);
      video.flag().setReason("Not supplied");
      System.out.println("Successfully removed flag from video: " + video.getTitle());
    }
  }

  public enum State {
    PLAYING,
    PAUSED,
    STOPPED;

    State() {}
  }
}
