package com.example.myapplication3.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication3.Login;
import com.example.myapplication3.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class AccountFragment extends Fragment {

    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);

        mAuth = FirebaseAuth.getInstance();
        TextView logoutview = rootView.findViewById(R.id.logoutview);
        TextView profile = rootView.findViewById(R.id.profile);
        TextView upload = rootView.findViewById(R.id.upload);

        logoutview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                signOutUser();
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               ProfileMethod();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadMethod();
            }
        });

        return rootView;
    }

    private void ProfileMethod() {
        // Get the activity associated with this fragment
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        // Check if the activity is not null and if it has a valid fragment manager
        if (activity != null) {
            activity.getSupportFragmentManager();// Begin a fragment transaction
            FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();

            // Replace the fragment in the activity's layout with ProfileFragment
            fragmentTransaction.replace(R.id.fragment_container, new ProfileFragment());

            // Add the transaction to the back stack (optional)
            fragmentTransaction.addToBackStack(null);

            // Commit the transaction
            fragmentTransaction.commit();
        }
    }
    private void UploadMethod()
    {        AppCompatActivity activity = (AppCompatActivity) getActivity();

        // Check if the activity is not null and if it has a valid fragment manager
        if (activity != null) {
            activity.getSupportFragmentManager();// Begin a fragment transaction
            FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();

            // Replace the fragment in the activity's layout with ProfileFragment
            fragmentTransaction.replace(R.id.fragment_container, new UploadFragment());

            // Add the transaction to the back stack (optional)
            fragmentTransaction.addToBackStack(null);
            // Commit the transaction
            fragmentTransaction.commit();
        }
    }


    private void signOutUser() {
        Intent loginIntent = new Intent(getActivity(), Login.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        requireActivity().finish();
    }
}
