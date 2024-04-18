package com.example.spotifywrapped.data;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.net.MalformedURLException;
import java.net.URL;

public class ArtistData {
    private String name;
    private int followerCount;
    private String artistImageURLString;
    private String artistID;

    public ArtistData() {
    }

    public ArtistData(JsonObject jsonObject) {
        name = jsonObject.get("name")
                .getAsString();

        followerCount = jsonObject.get("followers")
                .getAsJsonObject()
                .get("total")
                .getAsInt();
        artistID = jsonObject.get("id").getAsString();
        JsonArray pictures = jsonObject.get("images").getAsJsonArray();
        artistImageURLString = pictures.get(0).getAsJsonObject().get("url").getAsString();
    }

    // Getter for 'name' attribute
    public String getName() {
        return name;
    }

    public String getArtistID() {return artistID;}
    // Getter for 'followerCount' attribute
    public int getFollowerCount() {
        return followerCount;
    }

    // Getter for 'artistImageURL' attribute
    public String getArtistImageURLString() {
        return artistImageURLString;
    }
}
