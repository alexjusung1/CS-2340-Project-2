package com.example.spotifywrapped.utils;

import android.graphics.Bitmap;

@FunctionalInterface
public interface FetchImageAction {
    void performAction(Bitmap image);
}
