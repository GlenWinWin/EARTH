package com.testing.contactpicker.multipickercontacts;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.testing.contactpicker.R;
import com.testing.contactpicker.navigation_drawer_fragments.AboutFragment;
import com.testing.contactpicker.navigation_drawer_fragments.AddEvacuationAreasFragment;
import com.testing.contactpicker.navigation_drawer_fragments.EarthquakesFragment;
import com.testing.contactpicker.navigation_drawer_fragments.FragmentDrawer;
import com.testing.contactpicker.navigation_drawer_fragments.HelpFragment;
import com.testing.contactpicker.navigation_drawer_fragments.HowToResponseFragment;
import com.testing.contactpicker.navigation_drawer_fragments.SettingsFragment;

import java.util.ArrayList;

public class ContactPickerActivity extends AppCompatActivity{
    private GoogleMap mMap;
    public static final String PICKER_TYPE = "type";
    public static final String PICKER_TYPE_PHONE = "phone";
    public static final String CONTACT_PICKER_RESULT = "contacts";

    public static final int RESULT_ERROR = RESULT_FIRST_USER;
    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    SupportMapFragment supportMapFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getAction() != null && !Intent.ACTION_PICK.equals(getIntent().getAction())) {
            Intent ret = new Intent();
            ret.putExtra("error", "Unsupported action type");
            setResult(RESULT_ERROR, ret);
            return;
        }
        if (getIntent().getExtras() == null || PICKER_TYPE_PHONE.equals(getIntent().getExtras().getString(PICKER_TYPE))) {
            setContentView(R.layout.contact_list);
            mToolbar = (Toolbar) findViewById(R.id.toolbar);

            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Contacts");
        } else {
            Intent ret = new Intent();
            ret.putExtra("error", "Unsupported picker type");
            setResult(RESULT_ERROR, ret);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contact_picker_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.contacts_done) {
            returnResults();
            return true;
        } else if (item.getItemId() == R.id.contacts_cancel) {
            cancel();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void returnResults() {
        ContactListFragment fragment = (ContactListFragment) getSupportFragmentManager().getFragments().get(0);
        ArrayList<ContactResult> resultList = new ArrayList<ContactResult>( fragment.getResults().values() );
        Intent retIntent = new Intent();
        retIntent.putExtra(CONTACT_PICKER_RESULT, resultList);
        if(resultList.size() == 1){
            setResult(RESULT_OK, retIntent);
            finish();
        }
        else{
            Toast.makeText(getApplicationContext(),"Choose only one contact!",Toast.LENGTH_LONG).show();
        }

    }

    private void cancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onBackPressed() {
        cancel();
        super.onBackPressed();
    }
}