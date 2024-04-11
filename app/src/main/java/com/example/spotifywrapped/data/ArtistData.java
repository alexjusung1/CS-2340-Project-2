package com.example.spotifywrapped.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.net.MalformedURLException;
import java.net.URL;

public class ArtistData {
    private String name;
    private int followerCount;
    private URL artistImageURL;
    int popularity;

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
            artistImageURL = null;
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public URL getArtistImageURL() {
        return artistImageURL;
    }

    public String getFollowerCount() {
        return followerCount + " Followers";
    }
}
