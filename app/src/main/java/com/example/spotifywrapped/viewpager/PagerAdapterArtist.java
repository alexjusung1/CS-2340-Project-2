package com.example.spotifywrapped.viewpager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.spotifywrapped.data.TimeRange;

public class PagerAdapterArtist extends FragmentStateAdapter {
    boolean isCurrent;
    int pastPosition;

    public PagerAdapterArtist(@NonNull FragmentActivity fragmentActivity,
                              boolean isCurrent, int pastPosition) {
        super(fragmentActivity);
        this.isCurrent = isCurrent;
        this.pastPosition = pastPosition;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        ArtistFrag fragment = new ArtistFrag();

        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putBoolean("isCurrent", isCurrent);
        args.putInt("summaryPosition", pastPosition);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getItemCount() {
        return 10; // Number of pages
    }
}
