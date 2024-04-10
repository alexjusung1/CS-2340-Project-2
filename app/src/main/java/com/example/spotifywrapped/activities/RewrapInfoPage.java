package com.example.spotifywrapped.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import com.example.spotifywrapped.R;

public class RewrapInfoPage extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    TextInputEditText rewrapName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rewrap_info_page);

        ImageView btnBack = findViewById(R.id.back);
        ImageView saveBtn = findViewById(R.id.save_button);
        Button topArtist = findViewById(R.id.top_artist);
        Button topSongs = findViewById(R.id.top_song);
        rewrapName = findViewById(R.id.rewrap_name);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("RewrapPrefs", MODE_PRIVATE);

        btnBack.setOnClickListener(v -> startActivity(new Intent(RewrapInfoPage.this, PastRewrapPage.class)));

        rewrapName.setOnClickListener(v -> rewrapName.setText(""));

        topArtist.setOnClickListener(v -> startActivity(new Intent(RewrapInfoPage.this, Top10Artists.class)));

        topSongs.setOnClickListener(v -> startActivity(new Intent(RewrapInfoPage.this, Top10Songs.class)));

        saveBtn.setOnClickListener(v -> saveRewrapName());

        // Load saved rewrap name
        String savedRewrapName = sharedPreferences.getString("rewrapName", "");
        rewrapName.setText(savedRewrapName);
    }

    private void saveRewrapName() {
        String name = rewrapName.getText().toString();

        // Save rewrap name to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("rewrapName", name);
        editor.apply();
    }
}

