package com.example.spotifywrapped.activities;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.spotifywrapped.data.TimeRange;
import com.example.spotifywrapped.utils.SpotifyDataHolder;
import com.example.spotifywrapped.viewpager.PagerAdapterTrack;
import com.example.spotifywrapped.R;
import com.google.android.material.appbar.MaterialToolbar;

import androidx.lifecycle.ViewModelProvider;
import android.media.MediaPlayer;

import java.util.concurrent.CompletableFuture;
import android.net.Uri;
import android.widget.Button;


public class Top10Songs extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    String audioURLString;
    Uri audioURI;

    MenuItem playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rewrapped_template_songs);

        ViewPager2 vp = findViewById(R.id.viewPager);
        vp.setAdapter(new PagerAdapterTrack(this));

        TimeRangeViewModel vm  = new ViewModelProvider(this).get(TimeRangeViewModel.class);
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

        Spinner dropdown = findViewById(R.id.dropdownMenu);
        String[] items = new String[]{"Short Term", "Medium Term", "Long Term"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, TimeRange.descriptions);
        dropdown.setAdapter(adapter);


        playButton = topAppBar.getMenu().findItem(R.id.play_button);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vm.setCurrentTimeRange(TimeRange.values()[position]);

                // when you change the time range, a new media player is created with the new url.
                CompletableFuture.supplyAsync(() -> {
                    return SpotifyDataHolder.getCurrentTopTrackAsync(vm.getTimeRangeObserver().getValue(), position);

                }).thenAccept(trackData -> {
                    audioURLString = trackData.getAudioURL();
                    Log.d("Aditya", audioURLString);
                    audioURI = Uri.parse(audioURLString);
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), audioURI);
                    mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build());


                    mediaPlayer.setVolume(1, 1);
                    // mediaPlayer.start();
                });

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        playButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                Log.d("Aditya", "play button has been clicked");
                if (!(mediaPlayer == null)) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    } else {
                        mediaPlayer.start();
                    }

                }
                return true;
            }
        });
        topAppBar.setOnClickListener(v -> finish());
    }
}
