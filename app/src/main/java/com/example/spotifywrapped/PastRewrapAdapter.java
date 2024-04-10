package com.example.spotifywrapped;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PastRewrapAdapter extends RecyclerView.Adapter<PastRewrapAdapter.pastrewrapViewHolder> {

    private List<PastYears> pastyearCountList;
    private Context context;
    private PastRewrapPage parentPage;

    public PastRewrapAdapter(Context context, List<PastYears> pastyearCountList, PastRewrapPage parentPage) {
        this.context = context;
        this.pastyearCountList = pastyearCountList;
        this.parentPage = parentPage;
    }
    @Override
    public pastrewrapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.past_rewraps_listitem, parent, false);
        TextView textView = view.findViewById(R.id.past_year);
        textView.setOnClickListener(view1 -> {
            Intent intent = new Intent(context, RewrapInfoPage.class);
            parentPage.startActivity(intent);
        });

        return new pastrewrapViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull pastrewrapViewHolder holder, int position) {
        PastYears pastYears = pastyearCountList.get(position);
        holder.pastYearTextView.setText(pastYears.getUser() + " - " + pastYears.getMonth_year());
    }

    @Override
    public int getItemCount() {
        return pastyearCountList.size();
    }

    public static class pastrewrapViewHolder extends RecyclerView.ViewHolder {
        TextView pastYearTextView;

        public pastrewrapViewHolder(@NonNull View itemView) {
            super(itemView);

            pastYearTextView = itemView.findViewById(R.id.past_year);
        }


    }
}
