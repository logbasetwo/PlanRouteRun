package com.example.matthew.planrouterun;

import android.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Thread.*;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap; //googleMap
    private final static int MY_PERMISSION_FINE_LOCATION = 101;
    ArrayList<LatLng> MarkerPoints;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // MANUAL MODE:
        ////////    addLines();  //////// Infinite loop need to fix, crashes app

    }

    // Creates Menu on the app
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // Called when an options menu item is selected
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_hybrid) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            return true;
        } else if (id == R.id.action_satellite) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            return true;
        } else if (id == R.id.action_terrain) {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            return true;
        } else if (id == R.id.action_normal) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            return true;
        }
          else if (id == R.id.clear_pins) {
            mMap.clear();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        mMap = googleMap;

        //LatLng sydney = new LatLng(-34, 151);
        LatLng MattPos = new LatLng(40.7934, -77.86);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            mMap.setMyLocationEnabled(true);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
            }
        }
        mMap.addMarker(new MarkerOptions().position(MattPos).title("Marker in State College").draggable(true));
        //.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(MattPos));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); // CHANGE MAP TYPE!!!
        //mMap.setTrafficEnabled(true);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                // TODO Auto-generated method stub
                //MattPos.add(point);
                //mMap.clear();
                mMap.addMarker(new MarkerOptions().position(point).draggable(true));
            }
        });

        ////// MANUAL MODE TEMP: //////
        mMap.addPolyline(new PolylineOptions().add(
                new LatLng(40.7659370,-77.8731410),
                new LatLng(40.7705440,-77.8665170)
                ).width(10).color(Color.BLUE)
        );


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }
                } else{
                    Toast.makeText(getApplicationContext(), "This app requires location permissions to be granted", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }
    // Trying to add support for manual routes:
    private void addLines(){
        /*
        LatLng cur = new LatLng(0, 0);
        LinkedList<LatLng> pins = new LinkedList<LatLng>();
        int i = 0;
        LatLng start = new LatLng(0, 0);
        start = mCurrLocationMarker.getPosition();

        while (true) {
            if (mCurrLocationMarker.getPosition() == cur) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            } else{
                if(cur.equals(start)) { // Should only happen once, more efficient than seperate else if
                    pins.add(i, start);
                    i++;
                }
                cur = mCurrLocationMarker.getPosition();
                pins.add(i, cur);
                i++;
            }
            mMap
                    .addPolyline((new PolylineOptions())
                            .add(pins.get(i-1), pins.get(i)).width(5).color(Color.BLUE)
                            .geodesic(true));
            // move camera to zoom on map
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrLocationMarker.getPosition(), 13));
            //Log.d("LOGTAG", "This is a fucking test"); //String.valueOf(mCurrLocationMarker.getPosition())
            TextView latlongtest;
            String output = "" + mCurrLocationMarker.getPosition();
            latlongtest = (TextView)findViewById(R.id.latlongtest);
            latlongtest.setText(output);
            //////////
        mMap.addPolyline(new PolylineOptions().add(
                                new LatLng(40.7659370,-77.8731410),
                                new LatLng(40.7705440,-77.8665170)
                        ).width(10).color(Color.BLUE)
        );
        */
        // move camera to zoom on map
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrLocationMarker.getPosition(), 13));



    }
}
