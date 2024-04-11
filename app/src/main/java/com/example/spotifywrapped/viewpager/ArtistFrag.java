package com.example.spotifywrapped.viewpager;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.spotifywrapped.data.ArtistData;
import com.example.spotifywrapped.databinding.ArtistFragBinding;
import com.example.spotifywrapped.utils.SpotifyAPI;

import java.net.URL;

public class ArtistFrag extends Fragment {
    ArtistFragBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = ArtistFragBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        int pos = requireArguments().getInt("position", -1) + 1;
        binding.number.setText(String.format("#%d",
                requireArguments().getInt("position", -1) + 1));
        if (pos != -1 && SpotifyAPI.topArtists != null) {
            ArtistData a = SpotifyAPI.topArtists.get(pos);
            binding.artistName.setText(a.getName());
            binding.streamNumber.setText(a.getFollowerCount());
        }
    }
}