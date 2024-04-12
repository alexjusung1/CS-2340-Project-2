package com.example.spotifywrapped.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotifywrapped.activities.PastRewrapPage;
import com.example.spotifywrapped.R;
import com.example.spotifywrapped.activities.RewrapInfoPage;
import com.example.spotifywrapped.data.RewrappedSummary;

import java.util.List;

public class PastRewrapAdapter extends RecyclerView.Adapter<PastRewrapAdapter.PastRewrapViewHolder> {

    private List<RewrappedSummary> pastyearCountList;
    private Context context;
    private PastRewrapPage parentPage;

    public PastRewrapAdapter(Context context, List<RewrappedSummary> pastyearCountList, PastRewrapPage parentPage) {
        this.context = context;
        this.pastyearCountList = pastyearCountList;
        this.parentPage = parentPage;
    }
    @Override
    public PastRewrapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.past_rewraps_listitem, parent, false);
        TextView textView = view.findViewById(R.id.past_year);

        PastRewrapViewHolder viewHolder = new PastRewrapViewHolder(view);

        textView.setOnClickListener(view1 -> {
            Intent intent = new Intent(context, RewrapInfoPage.class);
            intent.putExtra("isCurrent", false);
            intent.putExtra("pastPosition", viewHolder.getAdapterPosition());
            parentPage.startActivity(intent);
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PastRewrapViewHolder holder, int position) {
        RewrappedSummary pastYears = pastyearCountList.get(position);
        holder.pastYearTextView.setText(pastYears.getSummaryName());
    }

    @Override
    public int getItemCount() {
        return pastyearCountList.size();
    }

    public static class PastRewrapViewHolder extends RecyclerView.ViewHolder {
        TextView pastYearTextView;

        public PastRewrapViewHolder(@NonNull View itemView) {
            super(itemView);

            pastYearTextView = itemView.findViewById(R.id.past_year);
        }
    }
}
