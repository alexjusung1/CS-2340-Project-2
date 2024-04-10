package com.example.spotifywrapped.utils;

import com.example.spotifywrapped.data.ArtistData;

import java.util.List;

@FunctionalInterface
public interface TopArtistsAction {
    void performAction(List<ArtistData> topArtists);
}
