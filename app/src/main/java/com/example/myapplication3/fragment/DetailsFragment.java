package com.example.myapplication3.fragment;
import com.example.myapplication3.fragment.CustomAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication3.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class DetailsFragment extends Fragment {

    private ListView ListViewId;
    private List<UploadWithKey> studentList;
    private CustomAdapter customAdapter;
    private DatabaseReference databaseReference;
    private int currentPage = 1; // Keep track of the current page
    private int itemsPerPage = 20; // Number of items per page

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details2, container, false);

        ListViewId = view.findViewById(R.id.ListViewId);
        studentList = new ArrayList<>();
        customAdapter = new CustomAdapter(getActivity(), studentList); // Pass activity
        ListViewId.setAdapter(customAdapter); // Set the adapter

        // Set button click listeners
        Button prevButton = view.findViewById(R.id.prevButton);
        Button nextButton = view.findViewById(R.id.nextButton);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPreviousPage();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNextPage();
            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference("upload");
        // Fetch data for the initial page
        fetchData();
        return view;
    }
    private void fetchData() {
        int start = (currentPage - 1) * itemsPerPage; // Calculate the start index
        Query query = databaseReference.orderByKey().startAt(Integer.toString(start)).limitToFirst(itemsPerPage);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                studentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey(); // Retrieve the key
                    Upload upload = snapshot.getValue(Upload.class);
                    UploadWithKey uploadWithKey = new UploadWithKey(key, upload); // Create UploadWithKey object
                    studentList.add(uploadWithKey);
                }
                customAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
    // Method to load next page
    private void loadNextPage() {
        // currentPage++;
        // Navigate to the NextPageActivity
        // Intent intent = new Intent(DetailsFragment.this, .class);
        // startActivity(intent);
    }

    // Method to load previous page
    private void loadPreviousPage() {
        if (currentPage > 1) {
            currentPage--;
            fetchData();
        }
    }
}
