package com.example.spotifywrapped.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spotifywrapped.R;
import com.example.spotifywrapped.utils.SpotifyAuth;
import com.example.spotifywrapped.utils.SpotifyDataHolder;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.CompletableFuture;

public class ConnectToSpotifyActivity extends AppCompatActivity {
    Button connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conntect_to_spotify_fragment);

        Intent intent = getIntent();
        String action = intent.getAction();
        if (action == null) {
            connect = findViewById(R.id.connect_button);
            connect.setOnClickListener(v -> startActivity(SpotifyAuth.getAuthorizationIntent()));
        } else if (action.equals(Intent.ACTION_VIEW)) {
            SpotifyAuth.parseAuthorizationResponse(intent.getData());

            Intent intentRewrapInfo = new Intent(this, RewrapInfoPage.class);
            intentRewrapInfo.putExtra("isCurrent", true);

            startActivity(intentRewrapInfo);
        }
    }
}
