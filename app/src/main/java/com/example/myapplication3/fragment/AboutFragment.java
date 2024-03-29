package com.example.myapplication3.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication3.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AboutFragment extends Fragment implements OnMapReadyCallback {

    private WebView webView;
    private GoogleMap gMap;

    @SuppressLint({"MissingInflatedId", "SetJavaScriptEnabled"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        // Find the WebView within the inflated layout
        webView = rootView.findViewById(R.id.webview);
        // Enable JavaScript
        webView.getSettings().setJavaScriptEnabled(true);
        String videoUrl = "https://www.youtube.com/embed/cT_sMWI97PY";
        String html = "<html><body style=\"margin:0;padding:0;\"><iframe width=\"100%\" height=\"100%\" src=\"" + videoUrl + "\" frameborder=\"0\" allowfullscreen></iframe></body></html>";
        webView.loadData(html, "text/html", "utf-8");

        // Create a new instance of SupportMapFragment
        SupportMapFragment mapFragment = new SupportMapFragment();
        // Begin a fragment transaction
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        // Replace the map_container with the SupportMapFragment
        transaction.replace(R.id.map_container, mapFragment);
        // Commit the transaction
        transaction.commit();
        // Set a callback for when the map is ready
        mapFragment.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        LatLng chittagong = new LatLng(22.4716, 91.7877);
        gMap.addMarker(new MarkerOptions().position(chittagong).title("Chittagong University"));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(chittagong, 12f));
    }
}
