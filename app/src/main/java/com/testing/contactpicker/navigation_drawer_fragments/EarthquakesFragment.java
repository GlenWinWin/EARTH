package com.testing.contactpicker.navigation_drawer_fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.testing.contactpicker.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class EarthquakesFragment extends Fragment {
    ProgressDialog progressDialog;
    ArrayList<HashMap<String, String>> earthQuakes;
    public static final String everyday_url = "http://10.239.33.36:8080/earth/fetch_everyday_earthquake.php";
    ListView list;
    String result = "";
    private static final String TAG_EARTHQUAKE = "earthquake";
    private static final String TAG_LATITUDE = "latitude";
    private static final String TAG_LONGI = "longitude";
    private static final String TAG_PLACE = "place";
    private static final String TAG_MAGNITUDE = "magnitude";
    private static final String TAG_TIME = "time";
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
        list = (ListView)rootView.findViewById(R.id.listView);
        earthQuakes = new ArrayList<HashMap<String, String>>();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String placeEarthQuake = ((TextView) view.findViewById(R.id.place)).getText().toString();
                String magnitudeEarthQuake = ((TextView) view.findViewById(R.id.magnitude)).getText().toString();
                double longiEarthQuake = Double.parseDouble((((TextView) view.findViewById(R.id.longitude)).getText().toString()));
                double latiEarthQuake = Double.parseDouble((((TextView) view.findViewById(R.id.latitude)).getText().toString()));
                String timeEarthQuake = ((TextView) view.findViewById(R.id.timeko)).getText().toString();
                Intent intent = new Intent(getActivity(),EarthQuakeIndividual.class);
                intent.putExtra("place",placeEarthQuake);
                intent.putExtra("magnitude",magnitudeEarthQuake);
                intent.putExtra("time",timeEarthQuake);
                intent.putExtra("lat",latiEarthQuake);
                intent.putExtra("long",longiEarthQuake);
                startActivity(intent);
            }
        });
        getData();
        return rootView;
    }
    public void getData() {
        class GetDataJSON extends AsyncTask<String, Void, String> {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Retrieving. Every Day EarthQuakes...");
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... params) {

                BufferedReader bufferedReader;

                try {
                    URL url = new URL(everyday_url);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    result = sb.toString();
                } catch (Exception e) {
                    Log.e("log_tag", "Error converting result" + e.toString());
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                progressDialog.dismiss();
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }
    protected void showList() {
        try {
            JSONArray jArray = new JSONArray(result);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject c = jArray.getJSONObject(i);
                String earthQuake_list = c.getString("place") + "\n" + "Magnitude : " + c.getString("magnitude");
                String lati = c.getString("latitude");
                String longi = c.getString("longitude");
                String magnitude = c.getString("magnitude");
                String place = c.getString("place");
                String time = c.getString("time");
                HashMap<String, String> earthquakes = new HashMap<String, String>();
                earthquakes.put(TAG_EARTHQUAKE,earthQuake_list);
                earthquakes.put(TAG_LATITUDE,lati);
                earthquakes.put(TAG_LONGI,longi);
                earthquakes.put(TAG_MAGNITUDE,magnitude);
                earthquakes.put(TAG_PLACE,place);
                earthquakes.put(TAG_TIME,time);
                earthQuakes.add(earthquakes);
            }
            ListAdapter adapter = new SimpleAdapter(getActivity(), earthQuakes, R.layout.list_item,
                    new String[]{TAG_EARTHQUAKE,TAG_LATITUDE,TAG_LONGI,TAG_MAGNITUDE,TAG_PLACE,TAG_TIME},
                    new int[]{R.id.earthquake,R.id.latitude,R.id.longitude,R.id.magnitude,R.id.place,R.id.timeko});
            list.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
