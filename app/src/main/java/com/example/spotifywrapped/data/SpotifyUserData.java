package com.example.spotifywrapped.data;

import android.graphics.Bitmap;

import com.example.spotifywrapped.utils.SpotifyAPI;

import java.net.URL;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SpotifyUserData {
    private String username;
    private String profileImageURLString;
    private Bitmap cachedProfileImage;
    private final Lock imageLock = new ReentrantLock();

    public SpotifyUserData(String username, String profileImageURLString) {
        this.username = username;
        this.profileImageURLString = profileImageURLString;
    }

    public Bitmap getProfileImageAsync() {
        imageLock.lock();
        try {
            if (cachedProfileImage == null) {
                cachedProfileImage = SpotifyAPI.fetchImageFromURLAsync(profileImageURLString);
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
