package com.example.spotifywrapped;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifywrapped.databinding.RecommendationsDisplayBinding;

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
    }
}
