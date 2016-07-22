package com.testing.contactpicker.navigation_drawer_fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.testing.contactpicker.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class EarthquakesFragment extends Fragment {

    ArrayList<HashMap<String, String>> earthQuakes;
    public static final String everyday_url = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.geojson";
    public EarthquakesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_earthquakes, container, false);

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        earthQuakes = new ArrayList<HashMap<String, String>>();

    }
}
