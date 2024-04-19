package com.example.spotifywrapped.viewpager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.spotifywrapped.activities.TimeRangeViewModel;
import com.example.spotifywrapped.databinding.TrackFragBinding;
import com.example.spotifywrapped.utils.FirestoreDataHolder;
import com.example.spotifywrapped.utils.SpotifyAPI;
import com.example.spotifywrapped.utils.SpotifyDataHolder;

import java.util.concurrent.CompletableFuture;

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
        int pos = requireArguments().getInt("position", -1);
        binding.number.setText(String.format("#%d", pos + 1));

        TimeRangeViewModel vm = new ViewModelProvider(requireActivity())
                .get(TimeRangeViewModel.class);

        vm.getTimeRangeObserver().observe(getViewLifecycleOwner(), timeRange -> {
            if (requireArguments().getBoolean("isCurrent")) {
                CompletableFuture.supplyAsync(() -> SpotifyDataHolder.getCurrentTopTrackAsync(timeRange, pos))
                        .thenApply(trackData -> {
                            requireActivity().runOnUiThread(() -> {
                                binding.artistName.setText(trackData.getArtistName());
                                binding.albumName.setText(trackData.getAlbumName());
                                binding.songName.setText(trackData.getName());
                            });
                            return SpotifyAPI.fetchImageFromURLAsync(trackData.getAlbumImageURLString());
                        }).thenAccept(bitmap -> requireActivity().runOnUiThread(() -> binding.musicAlbumView.setImageBitmap(bitmap)));
            } else {
                FirestoreDataHolder.getPastSummary(requireArguments().getInt("summaryPosition"))
                        .thenApply(summary -> summary.getTopTrack(timeRange, pos))
                        .thenApply(trackData -> {
                            requireActivity().runOnUiThread(() -> {
                                binding.artistName.setText(trackData.getArtistName());
                                binding.albumName.setText(trackData.getAlbumName());
                                binding.songName.setText(trackData.getName());
                            });
                            return SpotifyAPI.fetchImageFromURLAsync(trackData.getAlbumImageURLString());
                        }).thenAccept(bitmap -> requireActivity().runOnUiThread(() -> binding.musicAlbumView.setImageBitmap(bitmap)));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}