package com.example.spotifywrapped;

import com.google.gson.JsonObject;

public class TrackData {

    String name;
    String albumName;
    String albumImageUrl;
    String artistName;
    String audioURL;

    public TrackData(JsonObject jsonObject) {
        JsonObject album = jsonObject.get("album").getAsJsonObject();
        JsonObject primaryArtist = jsonObject.get("artists").getAsJsonArray().get(0).getAsJsonObject();

        name = jsonObject.get("name").getAsString();
        albumName = album.get("name").getAsString();
        albumImageUrl = album.get("images").getAsJsonArray().get(1).getAsJsonObject().get("url").getAsString();
        // for this one, album images has 3 different resolutions. index 2 is 300x300

        artistName = primaryArtist.get("name").getAsString();
        audioURL = jsonObject.get("preview_url").getAsString();
    }
}
