package com.example.spotifywrapped;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String action = intent.getAction();
        if (action == null || action.equals(Intent.ACTION_MAIN)) {
            // Normal app launch
            if (action == null) Log.w(TAG, "Null action");
        } else {
            // Returning from Spotify login
            Uri uri = intent.getData();
            Authorization.parseAuthorizationResponse(uri);
            // TODO: Reroute to previous layout/fragment
        }
    }
}