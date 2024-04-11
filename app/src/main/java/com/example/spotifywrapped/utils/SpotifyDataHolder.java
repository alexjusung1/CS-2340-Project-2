package com.example.spotifywrapped.utils;

import android.util.Log;

import com.example.spotifywrapped.data.RewrappedSummary;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SpotifyDataHolder {
    private static RewrappedSummary currentSummary;
    private static volatile String username;

    private static final Lock usernameLock = new ReentrantLock();

    public static void updateUsernameAsync() {
        usernameLock.lock();
        try {
            username = SpotifyAPI.getUserData().get();
        } catch (ExecutionException | InterruptedException e) {
            Log.d("SpotifyDataHolder", "test");
            throw new RuntimeException(e);
        } finally {
            usernameLock.unlock();
        }
    }

    public static CompletableFuture<String> getCurrentUsername() {
        return CompletableFuture.supplyAsync(() -> {
            usernameLock.lock();
            try {
                return username;
            } finally {
                usernameLock.unlock();
            }
        });
    }
}
