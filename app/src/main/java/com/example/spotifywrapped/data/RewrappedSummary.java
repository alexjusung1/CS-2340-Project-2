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

    public RewrappedSummary() {
        this("apples");
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

    public TrackData getTopTrack(TimeRange timeRange, int position) {
        return topTenTracks.get(timeRange).get(position);
    }

    public List<ArtistData> getTopArtists(TimeRange timeRange) {
        return topTenArtists.get(timeRange);
    }

    public boolean hasArtistsFromTime(TimeRange timeRange) {
        return topTenArtists.containsKey(timeRange);
    }

    public boolean hasTracksFromTime(TimeRange timeRange) {
        return topTenTracks.containsKey(timeRange);
    }

    public void updateTopTracks(TimeRange timeRange, List<TrackData> topTracks) {
        topTenTracks.put(timeRange, topTracks);
    }

    public Map<TimeRange, List<ArtistData>> getTopTenTracks() {
        return topTenArtists;
    }

    public Map<TimeRange, List<TrackData>> getTopTenArtists() {
        return topTenTracks;
    }
}
