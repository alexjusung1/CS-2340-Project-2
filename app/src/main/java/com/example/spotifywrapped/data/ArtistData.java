package com.example.spotifywrapped.data;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.spotifywrapped.utils.SpotifyAPI;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ArtistData {
    private String name;
    private int followerCount;
    private URL artistImageURL;

    public ArtistData() {
    }

    public ArtistData(JsonObject jsonObject) {
        name = jsonObject.get("name")
                .getAsString();

        followerCount = jsonObject.get("followers")
                .getAsJsonObject()
                .get("total")
                .getAsInt();

        JsonArray pictures = jsonObject.get("images").getAsJsonArray();
        try {
            artistImageURL = new URL(pictures.get(0).getAsJsonObject().get("url").getAsString());
        } catch (MalformedURLException e) {
            Log.e("ArtistData", "URL is invalid");
            artistImageURL = null;
            e.printStackTrace();
        }
    }

    // Getter for 'name' attribute
    public String getName() {
        return name;
    }

    // Getter for 'followerCount' attribute
    public int getFollowerCount() {
        return followerCount;
    }

    // Getter for 'artistImageURL' attribute
    public URL getArtistImageURL() {
        return artistImageURL;
    }
}
