package com.example.spotifywrapped.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.spotifywrapped.R;
import com.example.spotifywrapped.data.TimeRange;
import com.example.spotifywrapped.viewpager.PagerAdapterArtist;
import com.google.android.material.appbar.MaterialToolbar;

public class Top10Artists extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rewrapped_template_artist);

        TimeRangeViewModel viewModel = new ViewModelProvider(this).get(TimeRangeViewModel.class);

        Bundle passedData = getIntent().getExtras();
        boolean isCurrent = passedData.getBoolean("isCurrent", true);
        int pastPosition = passedData.getInt("pastPosition", 0);

        ViewPager2 vp = findViewById(R.id.viewPager);
        vp.setAdapter(new PagerAdapterArtist(this, isCurrent, pastPosition));

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        Spinner dropdown = findViewById(R.id.dropdownMenu);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                TimeRange.descriptions);
        dropdown.setAdapter(adapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.setCurrentTimeRange(TimeRange.values()[position]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        topAppBar.setOnClickListener(v -> finish());
    }
}
