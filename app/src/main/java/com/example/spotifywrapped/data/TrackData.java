package com.example.spotifywrapped.data;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.gson.JsonObject;

@IgnoreExtraProperties
public class TrackData {

    private String name;
    String albumName;

    String albumImageURLString;

    String artistName;
    String audioURL;

    public TrackData() {
        //
    }

    public TrackData(JsonObject jsonObject) {
        JsonObject album = jsonObject.get("album").getAsJsonObject();
        JsonObject primaryArtist = jsonObject.get("artists").getAsJsonArray().get(0).getAsJsonObject();

        name = jsonObject.get("name").getAsString();
        albumName = album.get("name").getAsString();
        albumImageURLString = album.get("images").getAsJsonArray().get(1).getAsJsonObject().get("url").getAsString();
        // for this one, album images has 3 different resolutions. index 2 is 300x300

        artistName = primaryArtist.get("name").getAsString();
        try {
            audioURL = jsonObject.get("preview_url").getAsString();
        } catch (RuntimeException e) {
            audioURL = null;
        }
    }

    public String getName() {
        return name;
    }

    public String getAudioURL() {
        return audioURL;
    }

    public String getAlbumImageURLString() {
        return albumImageURLString;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getArtistName() {
        return artistName;
    }
}
