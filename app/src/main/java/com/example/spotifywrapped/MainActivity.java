package com.example.spotifywrapped;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.spotifywrapped.databinding.ActivityMainBinding;

import java.security.NoSuchAlgorithmException;

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

            binding.textView.setText("Account login");
        }

        binding.button.setOnClickListener(view -> {
            try {
                startActivity(SpotifyAuth.getAuthorizationIntent());
            } catch (Exception e) {
                Log.e(TAG, "some error??");
            }
        });
    }
}