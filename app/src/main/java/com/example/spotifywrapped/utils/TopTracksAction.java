package com.example.spotifywrapped.utils;

import com.example.spotifywrapped.data.TrackData;

import java.util.List;

@FunctionalInterface
public interface TopTracksAction {
    void performAction(List<TrackData> topTracks);
}
