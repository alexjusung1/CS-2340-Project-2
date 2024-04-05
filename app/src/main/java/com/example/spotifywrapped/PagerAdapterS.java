package com.example.spotifywrapped;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PagerAdapterS extends FragmentStateAdapter {
    public PagerAdapterS(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new SongFrag1();
            case 1:
                return new SongFrag2();
            case 2:
                return new SongFrag3();
            case 3:
                return new SongFrag4();
            case 4:
                return new SongFrag5();
            case 5:
                return new SongFrag6();
            case 6:
                return new SongFrag7();
            case 7:
                return new SongFrag8();
            case 8:
                return new SongFrag9();
            case 9:
                return new SongFrag10();
            default:
                return new SongFrag1();
        }
    }

    @Override
    public int getItemCount() {
        return 10; // Number of pages
    }
}
