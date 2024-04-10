package com.example.spotifywrapped.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifywrapped.R;
import com.example.spotifywrapped.data.PastYears;
import com.example.spotifywrapped.databinding.PastRewrapsFragmentBinding;
import com.example.spotifywrapped.recyclerview.PastRewrapAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;


public class PastRewrapPage extends AppCompatActivity {
    
    private PastRewrapsFragmentBinding binding;
    private RecyclerView recyclerView;
    private PastRewrapAdapter adapter;
    private List<PastYears> pastYears;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.past_rewraps_fragment);
        binding = PastRewrapsFragmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerView = binding.pastRewrapRecyclerview;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        pastYears = new ArrayList<>();
        // Add your artists to the list here
        pastYears.add(new PastYears("chamilliyu ", "December 2021"));
        pastYears.add(new PastYears("chamilliyu ", "November 2021"));
        pastYears.add(new PastYears("chamilliyu ", "October 2021"));
        pastYears.add(new PastYears("chamilliyu ", "September 2021"));
        // Add more artists if needed

        adapter = new PastRewrapAdapter(this, pastYears, this);
        recyclerView.setAdapter(adapter);

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

        topAppBar.setOnClickListener(v -> startActivity(new Intent(PastRewrapPage.this, Homepage.class)));

    }
}
