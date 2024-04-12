package com.example.spotifywrapped.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spotifywrapped.FirestoreUpdate;
import com.example.spotifywrapped.R;
import com.example.spotifywrapped.data.RewrappedSummary;
import com.example.spotifywrapped.utils.SpotifyDataHolder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

public class RewrapInfoPage extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    TextInputEditText rewrapName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rewrap_info_page);

        Bundle passedData = getIntent().getExtras();

        ImageView btnBack = findViewById(R.id.back);
        ImageView saveBtn = findViewById(R.id.save_button);
        Button topArtist = findViewById(R.id.top_artist);
        Button topSongs = findViewById(R.id.top_song);
        rewrapName = findViewById(R.id.rewrap_name);

        if (passedData != null && passedData.getBoolean("isCurrent")) {
            CompletableFuture.runAsync(SpotifyDataHolder::prepareNewSummmaryAsync);
            CompletableFuture.supplyAsync(SpotifyDataHolder::getCurrentSummaryAsync)
                    .thenAccept(rewrappedSummary -> {
                        if (isFinishing()) { return; }
                        runOnUiThread(() -> rewrapName.setText(rewrappedSummary.getSummaryName()));
                    });
        }

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("RewrapPrefs", MODE_PRIVATE);

        btnBack.setOnClickListener(v -> finish());

        topArtist.setOnClickListener(v -> {
            Intent intent = new Intent(RewrapInfoPage.this, Top10Artists.class);
            intent.putExtra("isCurrent", passedData != null && passedData.getBoolean("isCurrent"));
            startActivity(intent);
        });

        topSongs.setOnClickListener(v -> startActivity(new Intent(RewrapInfoPage.this, Top10Songs.class)));

        saveBtn.setOnClickListener(v -> {
            FirebaseAuth fAuth = FirebaseAuth.getInstance();
            FirebaseFirestore fStore = FirebaseFirestore.getInstance();
            FirestoreUpdate firestoreUpdate = new FirestoreUpdate(fStore, fAuth.getUid());
            CompletableFuture.supplyAsync(SpotifyDataHolder::getCurrentSummaryAsync)
                    .thenAccept(firestoreUpdate::updateSpotifyFireStore);

        });

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
        Toast.makeText(RewrapInfoPage.this, "Rewrap Saved", Toast.LENGTH_LONG).show();
    }
}

