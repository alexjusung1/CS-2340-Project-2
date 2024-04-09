package com.example.spotifywrapped;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ConnectToSpotifyActivity extends AppCompatActivity {
    Button connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conntect_to_spotify_fragment);

        connect = findViewById(R.id.connect_button);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ConnectToSpotifyActivity.this, Homepage.class));
            }
        });
    }
}
