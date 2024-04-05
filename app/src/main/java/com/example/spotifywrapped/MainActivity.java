package com.example.spotifywrapped;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.spotifywrapped.databinding.ActivityMainBinding;

import java.net.URL;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    Bitmap bmp = null;

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
                        bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        binding.imageView.setImageBitmap(bmp);
                    } catch (Exception e) {
                        Log.e("URL", e.getMessage());
                        e.printStackTrace();
                    }
                });
            }, TimeRange.SHORT, 1);
        }

        binding.button2.setOnClickListener(view -> {
            if (bmp != null) {
                binding.imageView.setImageBitmap(bmp);
            }
        });

        binding.button.setOnClickListener(view -> {
            try {
                startActivity(SpotifyAuth.getAuthorizationIntent());
            } catch (Exception e) {
                Toast.makeText(this, "Error in SpotifyAuth", Toast.LENGTH_SHORT).show();
            }
        });
    }


}