package com.testing.contactpicker.navigation_drawer_fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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
    private static final String TAG_EARTHQUAKE_ID = "earthquake_id";
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

        getData();
        return rootView;
    }
    public void getData() {
        class GetDataJSON extends AsyncTask<String, Void, String> {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Refreshing. Please wait...");
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
                String earthQuake_id = c.getString("id");
                String earthQuake_list = c.getString("place") + "\n" + "Magnitude : " + c.getString("magnitude");
                HashMap<String, String> earthquakes = new HashMap<String, String>();
                earthquakes.put(TAG_EARTHQUAKE_ID,earthQuake_id);
                earthquakes.put(TAG_EARTHQUAKE,earthQuake_list);
                earthQuakes.add(earthquakes);
            }
            ListAdapter adapter = new SimpleAdapter(getActivity(), earthQuakes, R.layout.list_item,
                    new String[]{TAG_EARTHQUAKE_ID,TAG_EARTHQUAKE},
                    new int[]{R.id.earthQuakeId,R.id.earthquake});
            list.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
