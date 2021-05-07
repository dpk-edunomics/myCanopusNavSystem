package com.canopus.Activities;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.canopus.MapDataParser.DataParser;
import com.canopus.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {
    private MarkerOptions mUserLocation, mUserDestination;
    private GoogleMap mMap;
    private MaterialButton mStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        findViewById();
    }

    private void init() {
        mUserLocation = new MarkerOptions().position(new LatLng(22.720428309723747, 75.85525466368902))
                .title("Clothing Store")
                .snippet("Your Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
        mUserDestination = new MarkerOptions().position(new LatLng(22.724160268090415, 75.8794095230817))
                .title("Holkar Stadium")
                .snippet("Your Destination")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.flag));

        String mUrl = getDirectionsUrl(mUserLocation.getPosition(), mUserDestination.getPosition());

        getDownloadTask mDownloadTask = new getDownloadTask();
        mDownloadTask.execute(mUrl);
    }

    private void findViewById() {
        mStart = (MaterialButton) findViewById(R.id.btn_ViewMap);
        mStart.setOnClickListener(this::onClick);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void onClickView() {

        mMap.animateCamera(CameraUpdateFactory.zoomIn());

        mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(mUserLocation.getPosition())
                .zoom(18)
                .bearing(150)
                .tilt(80)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_ViewMap) {
            onClickView();
            mStart.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(mUserLocation);
        mMap.addMarker(mUserDestination);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mUserLocation.getPosition(), 12));
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        String mStrOrigin = "origin=" + origin.latitude + "," + origin.longitude;
        String mStrDestination = "destination=" + dest.latitude + "," + dest.longitude;
        String mMode = "mode=driving";
        String mParameters = mStrOrigin + "&" + mStrDestination + "&" + mMode;
        String mOutput = "json";
        String mUrl = "https://maps.googleapis.com/maps/api/directions/" + mOutput + "?" + mParameters + "&key=" + "AIzaSyAp1ZKZevC_88ONPb4ggUG9RnKyEtP7TQI";

        return mUrl;
    }

    private String getDownloadUrl(String strUrl) throws IOException {
        String mData = "";
        InputStream mStream = null;
        HttpURLConnection mUrlConnection = null;
        try {
            URL mUrl = new URL(strUrl);

            mUrlConnection = (HttpURLConnection) mUrl.openConnection();

            mUrlConnection.connect();

            mStream = mUrlConnection.getInputStream();

            BufferedReader mBufferReader = new BufferedReader(new InputStreamReader(mStream));

            StringBuffer mStringReader = new StringBuffer();

            String mLine = "";
            while ((mLine = mBufferReader.readLine()) != null) {
                mStringReader.append(mLine);
            }

            mData = mStringReader.toString();

            mBufferReader.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            mStream.close();
            mUrlConnection.disconnect();
        }
        return mData;
    }

    private class getDownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            String mData = "";
            try {
                mData = getDownloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return mData;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            getParserTask mParserTask = new getParserTask();
            mParserTask.execute(result);
        }
    }

    private class getParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject mJsonObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                mJsonObject = new JSONObject(jsonData[0]);

                DataParser mParser = new DataParser();

                routes = mParser.parse(mJsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList mArrayList = new ArrayList();
            PolylineOptions mPolylineOptions = new PolylineOptions();

            for (int i = 0; i < result.size(); i++) {

                List<HashMap<String, String>> mHashMaps = result.get(i);

                for (int j = 0; j < mHashMaps.size(); j++) {
                    HashMap<String, String> point = mHashMaps.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    mArrayList.add(position);
                }
                mPolylineOptions.addAll(mArrayList);
                mPolylineOptions.width(12);
                mPolylineOptions.color(Color.BLACK);
                mPolylineOptions.geodesic(true);
            }

            if (mArrayList.size() != 0)
                mMap.addPolyline(mPolylineOptions);
        }
    }
}