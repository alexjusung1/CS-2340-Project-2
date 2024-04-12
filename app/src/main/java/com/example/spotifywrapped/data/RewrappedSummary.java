package com.example.spotifywrapped.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RewrappedSummary {
    private String summaryName;
    private Map<String, List<ArtistData>> topTenArtists;
    private Map<String, List<TrackData>> topTenTracks;

    public RewrappedSummary(String summaryName) {
        this.summaryName = summaryName;
        topTenArtists = new HashMap<>();
        topTenTracks = new HashMap<>();
    }

    public RewrappedSummary() {
        this("apples");
    }
    public String getSummaryName() {
        return summaryName;
    }

    public void updateTopArtists(TimeRange timeRange, List<ArtistData> topArtists) {
        topTenArtists.put(timeRange.getValue(), topArtists);
    }

    public ArtistData getTopArtist(TimeRange timeRange, int position) {
        return topTenArtists.get(timeRange.getValue()).get(position);
    }

    public TrackData getTopTrack(TimeRange timeRange, int position) {
        return topTenTracks.get(timeRange.getValue()).get(position);
    }

    public List<ArtistData> getTopArtists(TimeRange timeRange) {
        return topTenArtists.get(timeRange.getValue());
    }

    public boolean hasArtistsFromTime(TimeRange timeRange) {
        return topTenArtists.containsKey(timeRange.getValue());
    }

    public boolean hasTracksFromTime(TimeRange timeRange) {
        return topTenTracks.containsKey(timeRange.getValue());
    }

    public void updateTopTracks(TimeRange timeRange, List<TrackData> topTracks) {
        topTenTracks.put(timeRange.getValue(), topTracks);
    }

    public Map<String, List<ArtistData>> getTopTenArtists() {
        return topTenArtists;
    }

    public Map<String, List<TrackData>> getTopTenTracks() {
        return topTenTracks;
    }
}
