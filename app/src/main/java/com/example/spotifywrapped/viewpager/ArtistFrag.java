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
import com.example.spotifywrapped.databinding.ArtistFragBinding;
import com.example.spotifywrapped.utils.SpotifyAPI;
import com.example.spotifywrapped.utils.SpotifyDataHolder;

import java.util.concurrent.CompletableFuture;

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
        int pos = requireArguments().getInt("position", -1);
        binding.number.setText(String.format("#%d", pos + 1));

        TimeRangeViewModel viewModel = new ViewModelProvider(requireActivity())
                .get(TimeRangeViewModel.class);

        viewModel.getTimeRangeObserver()
                .observe(getViewLifecycleOwner(), timeRange -> CompletableFuture.supplyAsync(() ->
                                SpotifyDataHolder.getCurrentTopArtistAsync(timeRange, pos))
                        .thenApply(artistData -> {
                            requireActivity().runOnUiThread(() -> {
                                binding.artistName.setText(artistData.getName());
                                binding.followerNumber.setText(String.format("%d Followers", artistData.getFollowerCount()));
                            });
                            return SpotifyAPI.fetchImageFromURLAsync(artistData.getArtistImageURL());
                        })
                        .thenAccept(bitmap -> requireActivity().runOnUiThread(() -> binding.musicAlbumView.setImageBitmap(bitmap)))
                );

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}