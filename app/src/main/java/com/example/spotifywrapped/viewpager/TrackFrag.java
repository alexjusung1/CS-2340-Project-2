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
import com.example.spotifywrapped.data.ArtistData;
import com.example.spotifywrapped.data.TrackData;
import com.example.spotifywrapped.databinding.TrackFragBinding;
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
        int pos = requireArguments().getInt("position", -1) + 1;
        binding.number.setText(String.format("#%d",
                requireArguments().getInt("position", -1) + 1));

        TimeRangeViewModel vm = new ViewModelProvider(requireActivity())
                .get(TimeRangeViewModel.class);

        vm.getTimeRangeObserver().observe(getViewLifecycleOwner(), timeRange -> {
            CompletableFuture.supplyAsync(() -> SpotifyDataHolder.getCurrentTopTrackAsync(timeRange, pos))
                    .thenAccept(trackData -> {
                        CompletableFuture.supplyAsync(() -> SpotifyAPI.fetchImageFromURLAsync(trackData.getAlbumImageURL()))
                                        .thenAccept(bitmap -> {
                                            requireActivity().runOnUiThread(() ->
                                                    binding.musicAlbumView.setImageBitmap(bitmap));
                                            });

                            requireActivity().runOnUiThread(() -> {
                                binding.artistName.setText(trackData.getName());
                                binding.albumName.setText(trackData.getAlbumName());
                                binding.songName.setText(trackData.getName());
                            });

                    });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}