package com.example.spotifywrapped.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifywrapped.R;
import com.example.spotifywrapped.data.Artist;
import com.example.spotifywrapped.databinding.RecommendationsDisplayBinding;
import com.example.spotifywrapped.recyclerview.recommendationAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class recommendations extends AppCompatActivity {

    private RecommendationsDisplayBinding binding;

    private RecyclerView recyclerView;
    private recommendationAdapter adapter;
    private List<Artist> artistList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = RecommendationsDisplayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerView = binding.recommendationsRecycler;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        artistList = new ArrayList<>();
        // Add your artists to the list here
        artistList.add(new Artist("Artist 1", "Genre 1", "url_to_image_1"));
        artistList.add(new Artist("Artist 2", "Genre 2", "url_to_image_2"));
        // Add more artists if needed

        adapter = new recommendationAdapter(this, artistList);
        recyclerView.setAdapter(adapter);

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

        topAppBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(recommendations.this, Homepage.class));
            }
        });
    }
}
