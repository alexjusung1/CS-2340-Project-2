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
public class TrackData {

    private String name;
    String albumName;
    String albumImageUrlString;

    URL albumImageURL;

    // @Exclude Bitmap cachedAlbumImage;

    private final Lock imageLock = new ReentrantLock();

    String artistName;
    String audioURL;

    public TrackData(JsonObject jsonObject) {
        JsonObject album = jsonObject.get("album").getAsJsonObject();
        JsonObject primaryArtist = jsonObject.get("artists").getAsJsonArray().get(0).getAsJsonObject();

        name = jsonObject.get("name").getAsString();
        albumName = album.get("name").getAsString();
        albumImageUrlString = album.get("images").getAsJsonArray().get(1).getAsJsonObject().get("url").getAsString();
        // for this one, album images has 3 different resolutions. index 2 is 300x300

        artistName = primaryArtist.get("name").getAsString();
        audioURL = jsonObject.get("preview_url").getAsString();

        try {
            albumImageURL = new URL(albumImageUrlString);
        } catch (MalformedURLException e) {
            albumImageURL = null;
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public String getAudioURL() {
        return audioURL;
    }

    public String getAlbumImageUrlString() {
        return albumImageUrlString;
    }

    public URL getAlbumImageURL() {
        return albumImageURL;
    }
    public String getAlbumName() {
        return albumName;
    }

    public String getArtistName() {
        return artistName;
    }

//    public Bitmap getAlbumImageAsync() {
//        imageLock.lock();
//        try {
//            if (cachedAlbumImage == null) {
//                cachedAlbumImage = SpotifyAPI.fetchImageFromURLAsync(albumImageURL);
//            }
//            return cachedAlbumImage;
//        } catch (Exception e) {
//            Log.e("Track Data", "Error in fetching album image bitmap");
//            throw new RuntimeException();
//        } finally {
//            imageLock.unlock();
//        }
//    }
}
