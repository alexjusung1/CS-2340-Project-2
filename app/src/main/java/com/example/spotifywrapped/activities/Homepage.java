package com.example.spotifywrapped.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.spotifywrapped.R;
import com.example.spotifywrapped.databinding.HomepageBinding;
import com.example.spotifywrapped.utils.SpotifyAuth;
import com.example.spotifywrapped.utils.SpotifyDataHolder;

import java.util.concurrent.CompletableFuture;

public class Homepage extends AppCompatActivity {
    HomepageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = HomepageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.rewrapinfo.setOnClickListener(v -> {
            if (SpotifyAuth.isLoggedOut()) {
                startActivity(new Intent(Homepage.this, ConnectToSpotifyActivity.class));
            } else {
                Intent intent = new Intent(Homepage.this, RewrapInfoPage.class);
                intent.putExtra("isCurrent", true);

                startActivity(intent);
            }
        });

        binding.pastRewrap.setOnClickListener(v -> startActivity(new Intent(Homepage.this, PastRewrapPage.class)));

        binding.setting.setOnClickListener(v -> startActivity(new Intent(Homepage.this, SettingsActivity.class)));

        binding.recommendation.setOnClickListener(v -> startActivity(new Intent(Homepage.this, recommendations.class)));

        updateUserNameImage();

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Toast.makeText(Homepage.this, "Back press is disabled in this screen", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUserNameImage();
    }

    private void updateUserNameImage() {
        if (SpotifyAuth.isLoggedOut()) {
            binding.userImage.setImageResource(R.drawable.user_placeholder);
            binding.username.setText(R.string.sample_username);
        }
        SpotifyDataHolder.getCurrentUserData()
                .thenApplyAsync(userData -> {
                    runOnUiThread(() -> binding.username.setText(userData.getUsername()));
                    return userData.getProfileImageAsync();
                }).thenAccept(bitmap -> runOnUiThread(() -> binding.userImage.setImageBitmap(bitmap)));
    }
}