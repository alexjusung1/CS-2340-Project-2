package com.example.spotifywrapped.data;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum TimeRange {
    SHORT("short_term", "Last 4 weeks"),
    MEDIUM("medium_term", "Last 6 months"),
    LONG("long_term", "Last year");

    private final String value;
    private final String description;

    public static String[] descriptions = Arrays.stream(values())
            .map(TimeRange::getDescription)
            .toArray(String[]::new);

    TimeRange(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}
