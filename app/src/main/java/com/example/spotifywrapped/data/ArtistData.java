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

@IgnoreExtraProperties
public class ArtistData {
    private String name;
    private int followerCount;
    private URL artistImageURL;
    @Exclude private Bitmap cachedArtistImage;
    private final Lock imageLock = new ReentrantLock();
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
            Log.e("ArtistData", "URL is invalid");
            artistImageURL = null;
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public Bitmap getArtistImageAsync() {
        imageLock.lock();
        try {
            if (cachedArtistImage == null) {
                cachedArtistImage = SpotifyAPI.fetchImageFromURLAsync(artistImageURL);
            }
            return cachedArtistImage;
        } catch (Exception e) {
            Log.e("Artist Data", "Exception in fetching artist image bitmap.");
            throw new RuntimeException();
        }
        finally {
            imageLock.unlock();
        }
    }

    public String getFollowerCount() {
        return followerCount + " Followers";
    }
}
