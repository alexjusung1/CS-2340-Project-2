package com.example.spotifywrapped.utils;

@FunctionalInterface
public interface AccessTokenAction {
    void performAction(String accessToken);
}
