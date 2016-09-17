package com.testing.contactpicker.sampleagainformaps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.testing.contactpicker.NavigationDrawerActivity;
import com.testing.contactpicker.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements LocationListener {

    GoogleMap map;
    String mylocation = "";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Evacuation Areas");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        map = mapFragment.getMap();

        //sample test
        final String[] array_places = new String[]{"University of the Philippines - Diliman North Side",
                "Marikina Boys Town East Side","LRT 2 - Santolan Depot","Intramuros Golf Course","Ultra - Pasig",
                "Villamor Air Base Golf Club","Bonifacio Global City","Chateau Open Area","Kagitingan Executive Golf Course","El Salvador Open Area"};

        final Double[] array_latitude = new Double[] {14.618873,14.666823,14.622044,14.593188,14.577634,14.519300,14.540867,14.536991,14.529825,14.488050};
        final Double[] array_longitude = new Double[] {121.073970,121.118052,121.086042,120.996594,121.066295,121.025447,121.050318,121.044095,121.055017,121.016328};



        final ArrayAdapter<String> adapter;



        for(int counter = 0; counter < array_places.length; counter++){


            map.addMarker(new MarkerOptions()
                    .position (new LatLng(array_latitude[counter],array_longitude[counter]))
                    .title(array_places[counter])
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.house)));
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    returnOrigin returnDestination = new returnOrigin();

                    for(int count = 0; count < array_places.length; count++){
                        if(array_places[count].equals(marker.getTitle())){
                            Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());

                            try {
                                List<Address> addresses = geocoder.getFromLocation(array_latitude[count], array_longitude[count], 1);
                                if(addresses.size() > 0){
                                    Address address = addresses.get(0);

                                    String dcity = address.getAddressLine(0);
                                    String dcity2 = address.getAddressLine(1);
                                    String dcity1and2 = dcity + " " + dcity2;

                                    returnDestination.getDestination(dcity1and2);


                                    Intent intent = new Intent(MapsActivity.this, MapsPath.class);
                                    intent.putExtra("destination", dcity1and2);
                                    intent.putExtra("origin", mylocation);
                                    startActivity(intent);

                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    return false;
                }
            });
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }
    @Override
    public void onLocationChanged(Location location) {

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latlng = new LatLng(latitude, longitude);
        map.addMarker(new MarkerOptions().position(latlng).title("You are here!")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_you)));
        map.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        map.animateCamera(CameraUpdateFactory.zoomTo(12));

        returnOrigin returnOrigin = new returnOrigin();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if(addresses.size() > 0 ){
                Address address = addresses.get(0);

                String currentcity = address.getAddressLine(0);
                String currentcity1 = address.getAddressLine(1);

                String origin = currentcity + " " + currentcity1;

                returnOrigin.getOrigin(origin);

                mylocation = returnOrigin.setOrigin();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void onSearch(View view) {

        EditText location_tf = (EditText) findViewById(R.id.TFaddress);
        String location = location_tf.getText().toString();
        List<Address> addressList = null;
        if (location != null || !location.equals("")) {

            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Address address = addressList.get(0);
            LatLng latlng = new LatLng(address.getLatitude(), address.getLongitude());
            map.addMarker(new MarkerOptions().position(latlng).title("you've searched").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_search)));
            map.animateCamera(CameraUpdateFactory.newLatLng(latlng));
        }

    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Maps Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
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
            startActivity(new Intent(this, NavigationDrawerActivity.class));
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }
}
