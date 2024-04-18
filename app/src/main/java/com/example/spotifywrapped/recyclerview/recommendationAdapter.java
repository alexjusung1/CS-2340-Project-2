package com.example.spotifywrapped.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifywrapped.R;
import com.example.spotifywrapped.data.Artist;
import com.example.spotifywrapped.data.ArtistData;
import com.example.spotifywrapped.data.TimeRange;
import com.example.spotifywrapped.utils.SpotifyAPI;
import com.example.spotifywrapped.utils.SpotifyDataHolder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class recommendationAdapter extends RecyclerView.Adapter<recommendationAdapter.recommendationsViewHolder> {
    private List<ArtistData> artistList;
    private Context context;
    private Activity activity;

    public recommendationAdapter(Context context, List<ArtistData> artistList, Activity activity) {
        this.context = context;
        this.artistList = artistList;
        this.activity = activity;
    }
    @Override
    public recommendationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommendation_item, parent, false);
        return new recommendationsViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull recommendationsViewHolder holder, int position) {
        if (artistList.size() <= position) {
            return;
        }
        ArtistData artist = artistList.get(position);
        holder.artistNameTextView.setText(artist.getName());
        holder.genreTextView.setText(Integer.toString(artist.getFollowerCount()) + " Followers");
        CompletableFuture.supplyAsync(() -> {
            return SpotifyAPI.fetchImageFromURLAsync(artist.getArtistImageURLString());
        }).thenAccept((bm) -> {activity.runOnUiThread(() ->{holder.artistImageView.setImageBitmap(bm);});});

        //new DownloadImageView(holder.artistImageView).execute(artist.getImageUrl());
    }

    public void setArtistList(List<ArtistData> l) {artistList = l;}
    @Override
    public int getItemCount() {
        return artistList.size();
    }

    public static class recommendationsViewHolder extends RecyclerView.ViewHolder {
        ImageView artistImageView;
        TextView artistNameTextView;
        TextView genreTextView;

        public recommendationsViewHolder(@NonNull View itemView) {
            super(itemView);
            artistImageView = itemView.findViewById(R.id.artistImageView);
            artistNameTextView = itemView.findViewById(R.id.artistNameTextView);
            genreTextView = itemView.findViewById(R.id.genreTextView);
        }
    }
}
