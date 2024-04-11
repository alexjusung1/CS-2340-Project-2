package com.example.spotifywrapped.data;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.spotifywrapped.utils.SpotifyAPI;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@IgnoreExtraProperties
public class SpotifyUserData {
    private String username;
    private URL profileImageURL;
    @Exclude private Bitmap cachedProfileImage;
    private final Lock imageLock = new ReentrantLock();

    public SpotifyUserData(String username, URL profileImageURL) {
        this.username = username;
        this.profileImageURL = profileImageURL;
    }

    public Bitmap getProfileImageAsync() {
        imageLock.lock();
        try {
            if (cachedProfileImage == null) {
                cachedProfileImage = SpotifyAPI.fetchImageFromURLAsync(profileImageURL);
            }
            return cachedProfileImage;
        } finally {
            imageLock.unlock();
        }
    }

    public String getUsername() {
        return username;
    }
}
