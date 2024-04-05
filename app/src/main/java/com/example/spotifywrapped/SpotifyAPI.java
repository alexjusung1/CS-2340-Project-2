package com.example.spotifywrapped;

import java.util.ArrayList;
import java.util.List;

public class SpotifyAPI {
    private static String topItemURL = "https://api.spotify.com/v1/me/top";

    public static List<ArtistData> getTopArtists(TimeRange range, int count) {
        return new ArrayList<>(count);
    }

    public static List<TrackData> getTopTracks(TimeRange range, int count) {
        return new ArrayList<>(count);
    }
}
