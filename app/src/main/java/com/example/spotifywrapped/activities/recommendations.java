package com.example.spotifywrapped.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifywrapped.R;
import com.example.spotifywrapped.data.Artist;
import com.example.spotifywrapped.data.ArtistData;
import com.example.spotifywrapped.databinding.RecommendationsDisplayBinding;
import com.example.spotifywrapped.recyclerview.recommendationAdapter;
import com.example.spotifywrapped.utils.SpotifyAPI;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class recommendations extends AppCompatActivity {

    private RecommendationsDisplayBinding binding;

    private RecyclerView recyclerView;
    private recommendationAdapter adapter;
    private List<ArtistData> artistList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = RecommendationsDisplayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerView = binding.recommendationsRecycler;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SpotifyAPI.getRecommendations()
                .thenAccept((al) -> {
                    runOnUiThread( () -> {
                        adapter.setArtistList(al);
                        adapter.notifyDataSetChanged();});
                });
        artistList = new ArrayList<>();
        // Add your artists to the list here
        //artistList.add(new Artist("Artist 1", "Genre 1", "url_to_image_1"));
        //artistList.add(new Artist("Artist 2", "Genre 2", "url_to_image_2"));
        // Add more artists if needed

        adapter = new recommendationAdapter(this, artistList, this);
        recyclerView.setAdapter(adapter);

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(recommendations.this, Homepage.class));
            }
        });
    }
}
