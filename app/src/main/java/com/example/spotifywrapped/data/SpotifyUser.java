package com.example.spotifywrapped.data;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.spotifywrapped.utils.SpotifyAPI;

import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SpotifyUser {
    private String username;
    private URL profileImageURL;
    private Bitmap cachedProfileImage;
    private Lock imageLock = new ReentrantLock();

    public SpotifyUser(String username, URL profileImageURL) {
        this.username = username;
        this.profileImageURL = profileImageURL;
    }

    public Bitmap getProfileImageAsync() {
        imageLock.lock();
        try {
            if (cachedProfileImage == null) {
                cachedProfileImage = CompletableFuture.supplyAsync(
                        () -> SpotifyAPI.fetchImageFromURLAsync(profileImageURL)).get();
            }
            return cachedProfileImage;
        } catch (ExecutionException | InterruptedException e) {
            Log.e("SpotifyUser", "Failed to fetch image");
            throw new RuntimeException(e);
        } finally {
            imageLock.unlock();
        }
    }

    public String getUsername() {
        return username;
    }
}
