package com.example.spotifywrapped;

import com.google.gson.JsonObject;

public class TrackData {

    String name;
    String albumName;
    String albumImageUrl;
    String artistName;
    String audioURL;


    public TrackData(String name, 
        String albumName, String albumImageURL, String artistName, 
        String songPreviewURL) {

            this.name = name;
            this.albumName = albumName;
            this.albumImageUrl = albumImageURL;
            this.artistName = artistName;
            this.audioURL = songPreviewURL;
    }
}
