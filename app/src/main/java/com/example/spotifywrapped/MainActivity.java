package com.example.spotifywrapped;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.spotifywrapped.R.id;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setContentView(R.layout.past_rewraps_fragment);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fragmentManager = getSupportFragmentManager();

        Fragment initialFragment = new RewrapsDisplayFragment();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, initialFragment).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            if (item.getItemId() == id.past_rewraps_disp) {
                selectedFragment = new RewrapsDisplayFragment();
            }
            if (selectedFragment != null) {
                fragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                return true;
            }
            return false;
        });
    }
}