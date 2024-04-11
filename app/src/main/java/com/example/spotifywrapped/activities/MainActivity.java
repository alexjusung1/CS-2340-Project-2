package com.example.spotifywrapped.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spotifywrapped.utils.SpotifyAPI;
import com.example.spotifywrapped.utils.SpotifyAuth;
import com.example.spotifywrapped.data.TimeRange;
import com.example.spotifywrapped.databinding.ActivityMainBinding;

import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String action = intent.getAction();
        if (action == null || action.equals(Intent.ACTION_MAIN)) {
            // Normal app launch
            if (action == null) Log.w(TAG, "Null action");
        } else {
            // Returning from Spotify login
            Uri uri = intent.getData();
            SpotifyAuth.parseAuthorizationResponse(uri);
            // TODO: Reroute to previous layout/fragment

//            SpotifyAPI.getTopArtists(topArtists -> {
//                if (isFinishing()) { return; }
//                runOnUiThread(() -> {
//                    binding.textView.setText(topArtists.get(0).getName());
//                    try {
//                        Log.d("MainActivity", topArtists.get(0).getArtistImageURI());
//                        URL url = new URL(topArtists.get(0).getArtistImageURI());
//                        SpotifyAPI.fetchImageFromURL(bitmap -> {
//                            if (isFinishing()) { return; }
//                            runOnUiThread(() -> binding.imageView.setImageBitmap(bitmap));
//                        }, url);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                });
//            }, TimeRange.SHORT, 1);

//            SpotifyAPI.getTopTracks(topTracks -> {
//                if (isFinishing()) { return; }
//                runOnUiThread(() -> {
//                    binding.textView3.setText(topTracks.get(0).getName());
//                    try {
//                        Log.d("MainActivity", topTracks.get(0).getAlbumImageUrl());
//                        URL url = new URL(topTracks.get(0).getAlbumImageUrl());
//                        SpotifyAPI.fetchImageFromURL(bitmap -> {
//                            if (isFinishing()) { return; }
//                            runOnUiThread(() -> binding.imageView2.setImageBitmap(bitmap));
//                        }, url);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                });
//            }, TimeRange.SHORT, 1);
        }

        binding.button.setOnClickListener(view -> {
            try {
                startActivity(SpotifyAuth.getAuthorizationIntent());
            } catch (Exception e) {
                Toast.makeText(this, "Error in SpotifyAuth", Toast.LENGTH_SHORT).show();
            }
        });

//        binding.button2.setOnClickListener(view -> {
//            SpotifyAuth.debugForceRefresh();
//        });
    }
}
