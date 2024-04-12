package com.example.spotifywrapped.utils;

import android.util.Log;

import com.example.spotifywrapped.data.ArtistData;
import com.example.spotifywrapped.data.RewrappedSummary;
import com.example.spotifywrapped.data.SpotifyUserData;
import com.example.spotifywrapped.data.TimeRange;
import com.example.spotifywrapped.data.TrackData;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SpotifyDataHolder {
    private static volatile RewrappedSummary currentSummary;
    private static final Lock currentSummaryLock = new ReentrantLock();
    private static volatile SpotifyUserData userData;

    private static final Lock userDataLock = new ReentrantLock();
    private static final Condition isAvailable = userDataLock.newCondition();

    public static void updateUserDataAsync() {
        userDataLock.lock();
        try {
            userData = SpotifyAPI.getUserData().get();
            isAvailable.signalAll();
        } catch (ExecutionException | InterruptedException e) {
            Log.d("SpotifyDataHolder", "test");
            throw new RuntimeException(e);
        } finally {
            userDataLock.unlock();
        }
    }

    public static CompletableFuture<SpotifyUserData> getCurrentUserData() {
        return CompletableFuture.supplyAsync(() -> {
            userDataLock.lock();
            try {
                if (userData == null) isAvailable.await();
                return userData;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                userDataLock.unlock();
            }
        });
    }


    public static void prepareNewSummmaryAsync() {
        currentSummaryLock.lock();
        try {
            String defaultName = String.format("%s -- %s",
                    getCurrentUserData().get().getUsername(),
                    LocalDate.now().toString());
            currentSummary = new RewrappedSummary(defaultName);
        } catch (ExecutionException | InterruptedException e) {
            Log.e("SpotifyDataHolder", "Error while preparing new summary");
            throw new RuntimeException(e);
        } finally {
            currentSummaryLock.unlock();
        }
    }

    public static ArtistData getCurrentTopArtistAsync(TimeRange timeRange, int position) {
        currentSummaryLock.lock();
        try {
            if (!currentSummary.hasArtistsFromTime(timeRange)) {
                currentSummary.updateTopArtists(timeRange,
                        SpotifyAPI.getTopArtists(timeRange, 10).get());
            }
            return currentSummary.getTopArtist(timeRange, position);
        } catch (ExecutionException | InterruptedException e) {
            Log.e("SpotifyDataHolder", "Failed getting top artists");throw new RuntimeException(e);
        } finally {
            currentSummaryLock.unlock();
        }
    }

    public static TrackData getCurrentTopTrackAsync(TimeRange timeRange, int position) {
        currentSummaryLock.lock();

        try {
            if (!currentSummary.hasTracksFromTime(timeRange)) {
                currentSummary.updateTopTracks(timeRange, SpotifyAPI.getTopTracks(timeRange, 10).get());
            }

            return currentSummary.getTopTrack(timeRange, position);
        } catch (ExecutionException e) {
            Log.e("SpotifyDataHolder", "Execution Error in getting top tracks");
            // e.printStackTrace();
            throw new RuntimeException();
        } catch (InterruptedException e) {
            Log.e("SpotifyDataHolder", "Interrupted Error in getting top tracks");
            // e.printStackTrace();
            throw new RuntimeException();
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
