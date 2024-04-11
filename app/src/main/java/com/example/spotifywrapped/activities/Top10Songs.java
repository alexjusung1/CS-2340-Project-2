package com.example.spotifywrapped.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.spotifywrapped.viewpager.PagerAdapterTrack;
import com.example.spotifywrapped.R;
import com.google.android.material.appbar.MaterialToolbar;

public class Top10Songs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rewrapped_template_songs);

        ViewPager2 vp = findViewById(R.id.viewPager);
        vp.setAdapter(new PagerAdapterTrack(this));

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

        Spinner dropdown = findViewById(R.id.dropdownMenu);
        String[] items = new String[]{"Short Term", "Medium Term", "Long Term"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        topAppBar.setOnClickListener(v -> finish());
    }
}
