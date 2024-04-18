package com.example.spotifywrapped.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifywrapped.R;
import com.example.spotifywrapped.databinding.PastRewrapsFragmentBinding;
import com.example.spotifywrapped.recyclerview.PastRewrapAdapter;
import com.example.spotifywrapped.utils.FirestoreDataHolder;
import com.google.android.material.appbar.MaterialToolbar;


public class PastRewrapPage extends AppCompatActivity {
    
    private PastRewrapsFragmentBinding binding;
    private RecyclerView recyclerView;
    private PastRewrapAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.past_rewraps_fragment);
        binding = PastRewrapsFragmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerView = binding.pastRewrapRecyclerview;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Add your artists to the list here
        // pastYears list will be more dynamic with the user's data in the long run
        FirestoreDataHolder.getPastSummaries()
                .thenAccept(pastSummaries -> runOnUiThread(() -> {
                    adapter = new PastRewrapAdapter(this, pastSummaries, this);
                    recyclerView.setAdapter(adapter);
                }));
        // Add more artists if needed

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setNavigationOnClickListener(v -> finish());
    }
}
