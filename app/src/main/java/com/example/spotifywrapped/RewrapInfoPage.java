package com.example.spotifywrapped;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class RewrapInfoPage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rewrap_info_page);

        ImageView btnBack = findViewById(R.id.back);
        Button topArtist = findViewById(R.id.top_artist);
        Button topSongs = findViewById(R.id.top_song);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Homepage.pastPage == 0) {
                    startActivity(new Intent(RewrapInfoPage.this, Homepage.class));
                } else if (Homepage.pastPage == 1) {
                    startActivity(new Intent(RewrapInfoPage.this, RewrapPage.class));
                }
            }
        });

        topArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RewrapInfoPage.this, Top10Artists.class));
            }
        });

        topSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RewrapInfoPage.this, Top10Songs.class));
            }
        });
    }

}
