package com.example.spotifywrapped.viewpager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PagerAdapterTrack extends FragmentStateAdapter {
    boolean isCurrent;
    int pastPositon;

    public PagerAdapterTrack(@NonNull FragmentActivity fragmentActivity,
                             boolean isCurrent, int pastPosition) {
        super(fragmentActivity);
        this.isCurrent = isCurrent;
        this.pastPositon = pastPosition;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        TrackFrag fragment = new TrackFrag();

        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putBoolean("isCurrent", isCurrent);
        args.putInt("summaryPosition", pastPositon);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getItemCount() {
        return 10; // Number of pages
    }
}
