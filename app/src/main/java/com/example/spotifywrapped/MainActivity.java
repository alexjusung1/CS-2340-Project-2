package com.example.spotifywrapped;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

            SpotifyAPI.getTopArtists(topArtists -> {
                if (isFinishing()) { return; }
                runOnUiThread(() -> {
                    binding.textView.setText(topArtists.get(0).name);
                    try {
                        Log.d("MainActivity", topArtists.get(0).artistImageURI);
                        URL url = new URL(topArtists.get(0).artistImageURI);
                        SpotifyAPI.fetchImageFromUrl(bitmap -> {
                            if (isFinishing()) { return; }
                            runOnUiThread(() -> binding.imageView.setImageBitmap(bitmap));
                        }, url);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }, TimeRange.SHORT, 1);

            SpotifyAPI.getTopTracks(topTracks -> {
                if (isFinishing()) { return; }
                runOnUiThread(() -> {
                    binding.textView3.setText(topTracks.get(0).name);
                    try {
                        Log.d("MainActivity", topTracks.get(0).albumImageUrl);
                        URL url = new URL(topTracks.get(0).albumImageUrl);
                        SpotifyAPI.fetchImageFromUrl(bitmap -> {
                            if (isFinishing()) { return; }
                            runOnUiThread(() -> binding.imageView2.setImageBitmap(bitmap));
                        }, url);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }, TimeRange.SHORT, 1);
        }

//        binding.button2.setOnClickListener(view -> {
//            if (bmp != null) {
//                binding.imageView.setImageBitmap(bmp);
//            }
//        });

        binding.button.setOnClickListener(view -> {
            try {
                startActivity(SpotifyAuth.getAuthorizationIntent());
            } catch (Exception e) {
                Toast.makeText(this, "Error in SpotifyAuth", Toast.LENGTH_SHORT).show();
            }
        });
    }


}