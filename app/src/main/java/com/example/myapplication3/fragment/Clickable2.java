package com.example.myapplication3.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication3.R;

public class Clickable2 extends Fragment {

    @SuppressLint({"MissingInflatedId", "SetJavaScriptEnabled"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_clickable2, container, false);

        TextView clickable1 = rootView.findViewById(R.id.clickable1);
        TextView clickable2 = rootView.findViewById(R.id.clickable2);
        TextView clickable3 = rootView.findViewById(R.id.clickable3);

        // Set click listeners for each clickable TextView
        clickable1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickable1Clicked();
            }
        });


        // Method to be executed when clickable2 is clicked
        return rootView;
    }


    private void clickable1Clicked() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, new DetailsFragment());
        fragmentTransaction.addToBackStack(null); // If you want to add the transaction to the back stack
        fragmentTransaction.commit();
    }
    // Method to be executed when clickable3 is clicked

}
