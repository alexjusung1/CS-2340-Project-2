package com.example.spotifywrapped.data;

public enum TimeRange {
    SHORT("short_term"),
    MEDIUM("medium_term"),
    LONG("long_term");

    private final String value;

    TimeRange(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
