package com.testing.contactpicker;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.testing.contactpicker.navigation_drawer_fragments.AboutFragment;
import com.testing.contactpicker.navigation_drawer_fragments.AddEvacuationAreasFragment;
import com.testing.contactpicker.navigation_drawer_fragments.EarthquakesFragment;
import com.testing.contactpicker.navigation_drawer_fragments.HelpFragment;
import com.testing.contactpicker.navigation_drawer_fragments.HowToResponseFragment;
import com.testing.contactpicker.navigation_drawer_fragments.SettingsFragment;

public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback,GoogleMap.OnMarkerClickListener {
    SupportMapFragment supportMapFragment;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportMapFragment = SupportMapFragment.newInstance();
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LayoutInflater inflater = getLayoutInflater();
        View myView = inflater.inflate(R.layout.earth_popup,(ViewGroup)findViewById(R.id.popup_earth));
        Toast toast = new Toast(this);
        toast.setView(myView);
        toast.setDuration(Toast.LENGTH_LONG);
        for(int i=0;i<30;i++){
            toast.show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        android.app.FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame_for_fragments,new EarthquakesFragment()).commit();
        getSupportActionBar().setTitle("Earthquakes");

        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        android.app.FragmentManager fragmentManager = getFragmentManager();
        FragmentManager mapFragmentManager = getSupportFragmentManager();
        String title = "";
        int id = item.getItemId();
        if(supportMapFragment.isAdded())
            mapFragmentManager.beginTransaction().hide(supportMapFragment).commit();
        if (id == R.id.nav_earthquakes) {
            fragmentManager.beginTransaction().replace(R.id.content_frame_for_fragments,new EarthquakesFragment()).commit();
            title = "Earthquakes";
        } else if (id == R.id.nav_add_evac) {
            fragmentManager.beginTransaction().replace(R.id.content_frame_for_fragments,new AddEvacuationAreasFragment()).commit();
            title = "Add Evacuation Areas";
        } else if (id == R.id.nav_vfs) {
            if(!supportMapFragment.isAdded())
                mapFragmentManager.beginTransaction().add(R.id.maps,supportMapFragment).commit();
            else
                mapFragmentManager.beginTransaction().show(supportMapFragment).commit();
            title = "Valley Fault System";
        } else if (id == R.id.nav_response) {
            fragmentManager.beginTransaction().replace(R.id.content_frame_for_fragments,new HowToResponseFragment()).commit();
            title = "Tutorials";
        } else if (id == R.id.nav_about) {
            fragmentManager.beginTransaction().replace(R.id.content_frame_for_fragments,new AboutFragment()).commit();
            title = "About";
        } else if (id == R.id.nav_help) {
            fragmentManager.beginTransaction().replace(R.id.content_frame_for_fragments,new HelpFragment()).commit();
            title = "Help";
        }
        else if (id == R.id.nav_settings) {
            fragmentManager.beginTransaction().replace(R.id.content_frame_for_fragments,new SettingsFragment()).commit();
            title = "Settings";
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        getSupportActionBar().setTitle(title);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        return false;
    }

}
