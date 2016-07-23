package com.testing.contactpicker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.testing.contactpicker.navigation_drawer_fragments.AboutFragment;
import com.testing.contactpicker.navigation_drawer_fragments.EarthquakesFragment;
import com.testing.contactpicker.navigation_drawer_fragments.HelpFragment;
import com.testing.contactpicker.navigation_drawer_fragments.HowToResponseFragment;
import com.testing.contactpicker.navigation_drawer_fragments.SettingsFragment;

import com.testing.contactpicker.sampleagainformaps.MapsActivity;

public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback{
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
        toast.setDuration(Toast.LENGTH_SHORT);
        for(int i=0;i<20;i++){
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
        if(Preferences.getPrimaryContactNumber(getApplicationContext()).isEmpty() && Preferences.getSecondaryContactNumber(getApplicationContext()).isEmpty()){
            fragmentManager.beginTransaction().replace(R.id.content_frame_for_fragments,new SettingsFragment()).commit();
            getSupportActionBar().setTitle("Settings");
        }
        else{
            fragmentManager.beginTransaction().replace(R.id.content_frame_for_fragments,new EarthquakesFragment()).commit();
            getSupportActionBar().setTitle("Earthquakes");
        }

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
            startActivity(new Intent(NavigationDrawerActivity.this, MapsActivity.class));
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
        mMap = googleMap;
        double[][] valleyFaultSystemCoordinates = {
                {15.0929081,121.0613609},
                {14.8740942,121.1427638},
                {14.8236989,121.0948389},
                {14.760122,121.2003971},
                {14.6774533,121.1591888},
                {14.6750431,121.0402429},
                {14.6504042,121.1019699},
                {14.5772737,121.0857342},
                {14.5546368,121.0241139},
                {14.5170481,121.047546},
                {14.4067315,121.0365698},
                {14.3072134,121.0116093},
                {14.2971985,121.0387576},
                {14.2191259,120.9692234},
                {14.3488148,121.0342489},
                {14.3034756,121.0759443},
                {14.283444,121.084726},
                {14.2471016,121.1360729},
                {14.1870326,121.1235034}};
        String[] cities = getApplicationContext().getResources().getStringArray(R.array.cities);
        int[] populations = getApplicationContext().getResources().getIntArray(R.array.populations);
        for(int i=0;i<valleyFaultSystemCoordinates.length;i++){
            LatLng places = new LatLng(valleyFaultSystemCoordinates[i][0],valleyFaultSystemCoordinates[i][1]);
            String population = String.format("%,.2f",(double)populations[i]);
            mMap.addMarker(new MarkerOptions().position(places).title(cities[i]).snippet("Population : " + population.replace(".00","")));
        }
        PolylineOptions line = new PolylineOptions()
                .add(new LatLng(15.0929081,121.0613609),
                        new LatLng(14.8740942,121.1427638),
                        new LatLng(14.8236989,121.0948389),
                        new LatLng(14.760122,121.2003971),
                        new LatLng(14.6774533,121.1591888),
                        new LatLng(14.6750431,121.0402429),
                        new LatLng(14.6504042,121.1019699),
                        new LatLng(14.5772737,121.0857342),
                        new LatLng(14.5546368,121.0241139),
                        new LatLng(14.5170481,121.047546),
                        new LatLng(14.4067315,121.0365698),
                        new LatLng(14.3072134,121.0116093),
                        new LatLng(14.2971985,121.0387576),
                        new LatLng(14.2191259,120.9692234),
                        new LatLng(14.3488148,121.0342489),
                        new LatLng(14.3034756,121.0759443),
                        new LatLng(14.283444,121.084726),
                        new LatLng(14.2471016,121.1360729),
                        new LatLng(14.1870326,121.1235034))
                .width(5)
                .color(Color.RED).geodesic(true);
        mMap.addPolyline(line);
        for(int j=0;j<valleyFaultSystemCoordinates.length;j++){
            CircleOptions circle = new CircleOptions()
                    .center(new LatLng(valleyFaultSystemCoordinates[j][0],valleyFaultSystemCoordinates[j][1]))
                    .radius((int)Math.sqrt(populations[j])*2)
                    .strokeWidth(2)
                    .strokeColor(Color.RED)
                    .fillColor(Color.argb(100,255,0,0));
            mMap.addCircle(circle);
        }
        LatLng center = new LatLng(14.5946358,121.0064731);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center,9));

    }

}
