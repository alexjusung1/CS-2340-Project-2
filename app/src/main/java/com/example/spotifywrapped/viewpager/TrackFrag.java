package com.example.spotifywrapped.viewpager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.spotifywrapped.data.ArtistData;
import com.example.spotifywrapped.data.TrackData;
import com.example.spotifywrapped.databinding.TrackFragBinding;
import com.example.spotifywrapped.utils.SpotifyAPI;

public class TrackFrag extends Fragment {
    TrackFragBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = TrackFragBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        int pos = requireArguments().getInt("position", -1) + 1;
        binding.number.setText(String.format("#%d",
                requireArguments().getInt("position", -1) + 1));
    }
}