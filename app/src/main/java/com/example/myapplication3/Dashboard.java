package com.example.myapplication3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.myapplication3.fragment.AccountFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication3.fragment.AboutFragment;
import com.example.myapplication3.fragment.DetailsFragment;
import com.example.myapplication3.fragment.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Dashboard extends AppCompatActivity {

    BottomNavigationView bottomNavigation;
    TextView result, confidence;
    ImageView imageView;
    Button picture;
    int imageSize = 224;

    //camera initialize
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        replaceHomeFragment();
        bottomNavigation = findViewById(R.id.bottomnavigation);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if (item.getItemId() == R.id.about) {
                    replaceAboutFragment();
                } else if (item.getItemId() == R.id.home) {
                    replaceHomeFragment();
                } else if (item.getItemId() == R.id.blog) {
                    replacepostfragment();
                } else if (item.getItemId() == R.id.account) {
                    replaceAccountFragment();
                }
                return true; // Change this to true
            }
        }); // Add closing bracket here

    }

    // Replace HomeFragment
    private void replaceHomeFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, new HomeFragment());
        fragmentTransaction.commit();
    }

    // Replace AboutFragment
    private void replaceAboutFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, new AboutFragment());
        fragmentTransaction.commit();
    }

    // Replace postfragment
    private void replacepostfragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, new DetailsFragment());
        fragmentTransaction.commit();
    }
    public void replaceAccountFragment()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, new AccountFragment());
        fragmentTransaction.commit();
    }
}
