package com.example.spotifywrapped;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class recommendationAdapter extends RecyclerView.Adapter<recommendationAdapter.recommendationsViewHolder> {
    private List<Artist> artistList;
    private Context context;

    public recommendationAdapter(Context context, List<Artist> artistList) {
        this.context = context;
        this.artistList = artistList;
    }
    @Override
    public recommendationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommendation_item, parent, false);
        return new recommendationsViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull recommendationsViewHolder holder, int position) {
        Artist artist = artistList.get(position);
        holder.artistNameTextView.setText(artist.getName());
        holder.genreTextView.setText(artist.getGenre());
        new DownloadImageView(holder.artistImageView).execute(artist.getImageUrl());
    }

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
