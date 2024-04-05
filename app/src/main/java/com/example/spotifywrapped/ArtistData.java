package com.example.spotifywrapped;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ArtistData {
    String name;
    int followerCount;
    String artistImageURI;
    int popularity;

    public ArtistData(JsonObject jsonObject) {
        name = jsonObject.get("name")
                .getAsString();

        followerCount = jsonObject.get("followers")
                .getAsJsonObject()
                .get("total")
                .getAsInt();

        JsonArray pictures = jsonObject.get("images").getAsJsonArray();
        artistImageURI = pictures.get(0).getAsJsonObject().get("url").toString();
    }
}
