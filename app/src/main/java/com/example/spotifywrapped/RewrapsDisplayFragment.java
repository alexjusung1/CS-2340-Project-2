package com.example.spotifywrapped;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class RewrapsDisplayFragment extends Fragment {
    private RecyclerView recyclerView;
    private Button backButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.past_rewraps_fragment, container, false);

        // Find views
        recyclerView = view.findViewById(R.id.recyclerViewRewraps);
        backButton = view.findViewById(R.id.backButtonRewraps);

        // Set click listener for back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        // Set up RecyclerView
        List<String> dataList = generateDummyData(); // Replace with your actual data
        RewrapListAdapter adapter = new RewrapListAdapter(dataList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        return view;
    }

    private List<String> generateDummyData() {
        List<String> dataList = new ArrayList<>();
        // Add dummy data here
        dataList.add("Item 1");
        dataList.add("Item 2");
        dataList.add("Item 3");
        return dataList;
    }
}