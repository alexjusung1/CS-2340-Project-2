package com.example.spotifywrapped.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RewrappedSummary {
    private String summaryName;
    private Map<TimeRange, List<ArtistData>> topTenArtists;
    private Map<TimeRange, List<TrackData>> topTenTracks;

    public RewrappedSummary(String summaryName) {
        this.summaryName = summaryName;
        topTenArtists = new HashMap<>();
        topTenTracks = new HashMap<>();
    }

    public String getSummaryName() {
        return summaryName;
    }

    public void updateTopArtists(TimeRange timeRange, List<ArtistData> topArtists) {
        topTenArtists.put(timeRange, topArtists);
    }

    public ArtistData getTopArtist(TimeRange timeRange, int position) {
        return topTenArtists.get(timeRange).get(position);
    }

    public List<ArtistData> getTopArtists(TimeRange timeRange) {
        return topTenArtists.get(timeRange);
    }

    public boolean hasArtistsFromTime(TimeRange timeRange) {
        return topTenArtists.containsKey(timeRange);
    }

    public void updateTopTracks(TimeRange timeRange, List<TrackData> topTracks) {
        topTenTracks.put(timeRange, topTracks);
    }
}
