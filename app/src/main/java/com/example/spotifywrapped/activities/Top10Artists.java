package com.example.spotifywrapped.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.spotifywrapped.viewpager.PagerAdapterArtist;
import com.example.spotifywrapped.R;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.InputStream;

public class Top10Artists extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rewrapped_template_artist);

        ViewPager2 vp = findViewById(R.id.viewPager);
        vp.setAdapter(new PagerAdapterArtist(this));

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
//        TextView numberTextView = findViewById(R.id.number);
//        TextView songNameTextView = findViewById(R.id.song_name);
//        TextView artistNameTextView = findViewById(R.id.artist_name);
//        TextView albumNameTextView = findViewById(R.id.album_name);
        Spinner dropdown = findViewById(R.id.dropdownMenu);
        String[] items = new String[]{"Short Term", "Medium Term", "Long Term"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);



//        Bundle extras = getIntent().getExtras();
//        if (extras != null) {
//            String number = extras.getString("number");
//            String songName = extras.getString("songName");
//            String artistName = extras.getString("artistName");
//            String albumName = extras.getString("albumName");
//
//              numberTextView.setText(number);
//              songNameTextView.setText(songName);
//              artistNameTextView.setText(artistName);
//              albumNameTextView.setText(albumName);
//        }

        topAppBar.setOnClickListener(v -> finish());
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
