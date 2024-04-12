package com.example.spotifywrapped.data;

import android.graphics.Bitmap;

import com.example.spotifywrapped.utils.SpotifyAPI;

import java.net.URL;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SpotifyUserData {
    private String username;
    private URL profileImageURL;
    private Bitmap cachedProfileImage;
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
