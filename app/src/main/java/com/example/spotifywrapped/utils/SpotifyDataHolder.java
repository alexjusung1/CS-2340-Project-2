package com.example.spotifywrapped.utils;

import android.util.Log;

import com.example.spotifywrapped.data.RewrappedSummary;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SpotifyDataHolder {
    private static volatile RewrappedSummary currentSummary;
    private static final Lock currentSummaryLock = new ReentrantLock();
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


    public static void prepareNewSummmaryAsync() {
        currentSummaryLock.lock();
        try {
            String defaultName = String.format("%s -- %s",
                    getCurrentUsername().get(),
                    LocalDate.now().toString());
            currentSummary = new RewrappedSummary(defaultName);
        } catch (ExecutionException | InterruptedException e) {
            Log.e("SpotifyDataHolder", "Error while preparing new summary");
            throw new RuntimeException(e);
        } finally {
            currentSummaryLock.unlock();
        }
    }

    public static RewrappedSummary getCurrentSummaryAsync() {
        currentSummaryLock.lock();
        try {
            return currentSummary;
        } finally {
            currentSummaryLock.unlock();
        }
    }
}
