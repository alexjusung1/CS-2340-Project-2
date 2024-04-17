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
import com.example.spotifywrapped.utils.FirestoreDataHolder;
import com.example.spotifywrapped.utils.SpotifyDataHolder;
import com.example.spotifywrapped.viewpager.PagerAdapterTrack;
import com.example.spotifywrapped.R;
import com.google.android.material.appbar.MaterialToolbar;

import androidx.lifecycle.ViewModelProvider;
import android.media.MediaPlayer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import android.net.Uri;
import android.widget.Button;
import android.widget.Toast;


public class Top10Songs extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    String audioURLString;
    Uri audioURI;
    TimeRangeViewModel vm;

    MenuItem playButton;
    boolean isCurrent;
    int pastPosition;
    boolean hasAudioURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rewrapped_template_songs);

        vm = new ViewModelProvider(this).get(TimeRangeViewModel.class);

        Bundle passedData = getIntent().getExtras();
        isCurrent = passedData.getBoolean("isCurrent", true);
        pastPosition = passedData.getInt("pastPosition", 0);

        ViewPager2 vp = findViewById(R.id.viewPager);
        vp.setAdapter(new PagerAdapterTrack(this, isCurrent, pastPosition));

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

        Spinner dropdown = findViewById(R.id.dropdownMenu);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, TimeRange.descriptions);
        dropdown.setAdapter(adapter);

        playButton = topAppBar.getMenu().findItem(R.id.play_button);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vm.setCurrentTimeRange(TimeRange.values()[position]);

                // when you change the time range, a new media player is created with the new url.
                initializeMediaPlayer(vp.getCurrentItem());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        vp.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                initializeMediaPlayer(position);
            }
        });

        playButton.setOnMenuItemClickListener(item -> {
            Log.d("Aditya", "play button has been clicked");
            if (!hasAudioURL) {
                Toast.makeText(Top10Songs.this, "No preview for this track...", Toast.LENGTH_SHORT).show();
                return true;
            }

            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.start();
                }

            }
            return true;
        });
        topAppBar.setOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    private void initializeMediaPlayer(int position) {
        CompletableFuture.supplyAsync(() -> {
            TimeRange currentTimeRange = vm.getTimeRangeObserver().getValue();
            if (isCurrent) {
                return SpotifyDataHolder.getCurrentTopTrackAsync(currentTimeRange, position);
            } else {
                try {
                    return FirestoreDataHolder.getPastSummary(pastPosition).get().getTopTrack(currentTimeRange, position);
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).thenAccept(trackData -> {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }

            audioURLString = trackData.getAudioURL();
            if (audioURLString == null) {
                hasAudioURL = false;
                return;
            }

            Log.d("Aditya", audioURLString);
            hasAudioURL = true;
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
}
