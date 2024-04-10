package com.example.spotifywrapped.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spotifywrapped.R;
import com.example.spotifywrapped.utils.SpotifyAuth;

public class Homepage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        Button rewrapInfo = findViewById(R.id.rewrapinfo);
        Button pastRewrap = findViewById(R.id.past_rewrap);
        ImageView settings = findViewById(R.id.setting);
        Button recommendations = findViewById(R.id.recommendation);
//        change later

        rewrapInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SpotifyAuth.isLoggedOut()) {
                    startActivity(new Intent(Homepage.this, ConnectToSpotifyActivity.class));
                } else {
                    Intent intent = new Intent(Homepage.this, RewrapInfoPage.class);
                    intent.putExtra("isCurrent", true);

                    startActivity(intent);
                }
            }
        });

        pastRewrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Homepage.this, PastRewrapPage.class));
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Homepage.this, SettingsActivity.class));
            }
        });

        recommendations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Homepage.this, com.example.spotifywrapped.activities.recommendations.class));
            }
        });

//        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//
//            }
//        });
    }
}