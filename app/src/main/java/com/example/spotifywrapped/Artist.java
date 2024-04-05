package com.example.spotifywrapped;

public class Artist {
    private String name;
    private String genre;
    private String imageUrl;

    public Artist(String name, String genre, String imageUrl) {
        this.name = name;
        this.genre = genre;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getGenre() {
        return genre;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
