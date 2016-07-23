package com.testing.contactpicker.navigation_drawer_fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.testing.contactpicker.R;


/**
 * Created by Glenwin18 on 7/23/2016.
 */
public class EarthQuakeIndividual extends AppCompatActivity implements OnMapReadyCallback {
    SupportMapFragment supportMapFragment;
    private GoogleMap mMap;
    double latitude,longitude;
    String magnitude="",place="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportMapFragment = SupportMapFragment.newInstance();
        setContentView(R.layout.activity_earthquakes_individual);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Details");
        Intent intent =getIntent();
        place = intent.getStringExtra("place");
        magnitude = intent.getStringExtra("magnitude");
        String time = intent.getStringExtra("time");
        String date = intent.getStringExtra("date");
        latitude = intent.getDoubleExtra("lat",0);
        longitude = intent.getDoubleExtra("long",0);
        ((TextView)findViewById(R.id.location)).setText("Place : " + place );
        ((TextView)findViewById(R.id.magnitude)).setText("Magnitude : " + magnitude  );
        ((TextView)findViewById(R.id.timee)).setText("Time : " + time  );
        ((TextView)findViewById(R.id.date)).setText("Date : " + date  );
        ((TextView)findViewById(R.id.coordinates)).setText("Coordinates : \n" + latitude+", "+longitude);

        supportMapFragment.getMapAsync(this);
        FragmentManager mapFragmentManager = getSupportFragmentManager();
        if(!supportMapFragment.isAdded())
            mapFragmentManager.beginTransaction().add(R.id.maps,supportMapFragment).commit();
        else
            mapFragmentManager.beginTransaction().show(supportMapFragment).commit();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng specific_earthquake = new LatLng(latitude,longitude);
        mMap.addMarker(new MarkerOptions().position(specific_earthquake).title("Magnitude : " + magnitude).snippet("Place : " + place));
        LatLng center = new LatLng(latitude,longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center,9));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_back_earthquake_individual, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.backButton) {
            finish();
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }
}
