package com.example.spotifywrapped.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spotifywrapped.data.RewrappedSummary;
import com.example.spotifywrapped.utils.FirestoreDataHolder;
import com.example.spotifywrapped.utils.FirestoreUpdate;
import com.example.spotifywrapped.R;
import com.example.spotifywrapped.data.TimeRange;
import com.example.spotifywrapped.utils.SpotifyDataHolder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RewrapInfoPage extends AppCompatActivity {
    TextInputEditText rewrapName;
    boolean isCurrent;
    int pastPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rewrap_info_page);

        Bundle passedData = getIntent().getExtras();
        isCurrent = passedData.getBoolean("isCurrent", true);
        pastPosition = passedData.getInt("pastPosition", 0);

        ImageView btnBack = findViewById(R.id.back);
        ImageView saveBtn = findViewById(R.id.save_button);
        Button topArtist = findViewById(R.id.top_artist);
        Button topSongs = findViewById(R.id.top_song);
        rewrapName = findViewById(R.id.rewrap_name);

        if (isCurrent) {
            CompletableFuture.runAsync(SpotifyDataHolder::prepareNewSummmaryAsync);
            CompletableFuture.supplyAsync(SpotifyDataHolder::getCurrentSummaryAsync)
                    .thenAccept(rewrappedSummary -> {
                        if (isFinishing()) { return; }
                        runOnUiThread(() -> rewrapName.setText(rewrappedSummary.getSummaryName()));
                    });
        } else {
            rewrapName.setEnabled(false);
            // Loading past data
            FirestoreDataHolder.getPastSummary(pastPosition)
                    .thenAccept(summary -> {
                        if (isFinishing()) { return; }
                        runOnUiThread(() -> {
                            rewrapName.setText(summary.getSummaryName());
                            rewrapName.setEnabled(false);
                        });
                    });
        }

        rewrapName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (isCurrent) {
                    CompletableFuture.runAsync(() ->
                            SpotifyDataHolder.updateCurrentSummaryNameAsync(s.toString()));
                }
            }
        });

        btnBack.setOnClickListener(v -> finish());

        topArtist.setOnClickListener(v -> {
            Intent intent = new Intent(RewrapInfoPage.this, Top10Artists.class);
            intent.putExtra("isCurrent", isCurrent);
            intent.putExtra("pastPosition", pastPosition);
            startActivity(intent);
        });

        topSongs.setOnClickListener(v -> {
            Intent intent = new Intent(RewrapInfoPage.this, Top10Songs.class);
            intent.putExtra("isCurrent", isCurrent);
            intent.putExtra("isCurrent", pastPosition);
            startActivity(intent);
        });

        saveBtn.setOnClickListener(v -> {
            if (!isCurrent) {
                Toast.makeText(this, "Does not support saving past summaries", Toast.LENGTH_SHORT).show();
                Log.d("XXXXXX", String.valueOf(isCurrent));
                return;
            }
            FirebaseAuth fAuth = FirebaseAuth.getInstance();
            FirebaseFirestore fStore = FirebaseFirestore.getInstance();
            FirestoreUpdate firestoreUpdate = new FirestoreUpdate(fStore, fAuth.getUid());

            List<CompletableFuture<?>> fetchDataActions = new ArrayList<>();
            for (TimeRange range: TimeRange.values()) {
                fetchDataActions.add(CompletableFuture.runAsync(() ->
                        SpotifyDataHolder.getCurrentTopArtistAsync(range, 0)));
                fetchDataActions.add(CompletableFuture.runAsync(() ->
                        SpotifyDataHolder.getCurrentTopTrackAsync(range, 0)));
            }

            CompletableFuture.allOf(fetchDataActions.stream().toArray(CompletableFuture<?>[]::new))
                    .thenRun(() -> {
                        FirestoreDataHolder.addNewRewrappedSummaryAsync(firestoreUpdate, SpotifyDataHolder.getCurrentSummaryAsync());
                    })
                    .thenRun(() -> runOnUiThread(() -> Toast.makeText(
                            RewrapInfoPage.this,
                            "Successfully saved summary",
                            Toast.LENGTH_SHORT).show()));
        });
    }

}

