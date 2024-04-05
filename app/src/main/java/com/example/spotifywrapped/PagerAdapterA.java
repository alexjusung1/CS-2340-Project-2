package com.example.spotifywrapped;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PagerAdapterA extends FragmentStateAdapter {
    public PagerAdapterA(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ArtistFrag1();
            case 1:
                return new ArtistFrag2();
            case 2:
                return new ArtistFrag3();
            case 3:
                return new ArtistFrag4();
            case 4:
                return new ArtistFrag5();
            case 5:
                return new ArtistFrag6();
            case 6:
                return new ArtistFrag7();
            case 7:
                return new ArtistFrag8();
            case 8:
                return new ArtistFrag9();
            case 9:
                return new ArtistFrag10();
            default:
                return new ArtistFrag1();
        }
    }

    @Override
    public int getItemCount() {
        return 10; // Number of pages
    }
}
