package com.example.lavir.maps;

import android.os.AsyncTask;
import android.provider.Telephony;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private JSONArray places;
    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        FetchDataTask task = new FetchDataTask();
        task.execute("http://student.labranet.jamk.fi/~K8455/js/map.json");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




    }



    class FetchDataTask extends AsyncTask<String, Void, JSONObject>{
        @Override
        protected JSONObject doInBackground(String... urls) {
            HttpURLConnection urlConnection = null;
            JSONObject json = null;
            try {
                URL url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while((line=bufferedReader.readLine()) != null){
                    stringBuilder.append(line).append("\n");
                }

                bufferedReader.close();
                json = new JSONObject(stringBuilder.toString());
            }catch (IOException e){
                e.printStackTrace();
            }catch (JSONException e){
                e.printStackTrace();
            }finally {
                if(urlConnection != null) urlConnection.disconnect();
            }
            return json;
        }


        protected void onPostExecute(JSONObject json){
            StringBuffer text = new StringBuffer("");
            try{
               places = json.getJSONArray("places");
                for(int i =0; i<places.length();i++){
                    JSONObject plc = places.getJSONObject(i);
                    mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(plc.getDouble("latitude") , plc.getDouble("longitude")))
                        .title(plc.getString("name"))
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_money))
                    );


                }
            }catch (JSONException e) {
                Log.e("JSON", "ERROR getting data.");
            }
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // store map object to member variable
        mMap = googleMap;
        // set map type
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // add one marker
        /* Get Data from LatLng Listarray not working
        for(int i = 0; i < locations.size(); i++){
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(locations.get(i).latitude, locations.get(i).longitude)));
        }
        mMap.addMarker(new MarkerOptions()
        .position(new LatLng(62.3,24.5))
            );
*/
        LatLng ICT = new LatLng(62.24324,25.7597);
        final Marker ict = mMap.addMarker(new MarkerOptions()
                .position(ICT)
                .title("JAMK/ICT")
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_blur_circular_black_24dp))
        );
        // point to jamk/ict and zoom a little
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ICT, 14));
        // marker listener
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                if (marker.equals(ict)) {
                    Toast.makeText(getApplicationContext(), "Marker = " + marker.getTitle(), Toast.LENGTH_SHORT).show();
                    return true;
                }else {Toast.makeText(getApplicationContext(), marker.getTitle(), Toast.LENGTH_SHORT).show();}
                return false;
            }
        });



    }

}

