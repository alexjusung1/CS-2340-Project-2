package com.example.spotifywrapped;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

public class Top10Songs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rewrapped_template_artist);

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        TextView numberTextView = findViewById(R.id.number);
        TextView songNameTextView = findViewById(R.id.song_name);
        TextView artistNameTextView = findViewById(R.id.artist_name);
        TextView albumNameTextView = findViewById(R.id.album_name);

        Spinner dropdown = findViewById(R.id.dropdownMenu);
        String[] items = new String[]{"Short Term", "Medium Term", "Long Term"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String number = extras.getString("number");
            String songName = extras.getString("songName");
            String artistName = extras.getString("artistName");
            String albumName = extras.getString("albumName");

            numberTextView.setText(number);
            songNameTextView.setText(songName);
            artistNameTextView.setText(artistName);
            albumNameTextView.setText(albumName);
        }

        topAppBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Top10Songs.this, RewrapInfoPage.class));
            }
        });
    }
}