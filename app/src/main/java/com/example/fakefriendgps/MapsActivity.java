package com.example.fakefriendgps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.*;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private double longitude, latitude;
    private FloatingActionButton floatingActionButton1, floatingActionButton2;
    private ArrayList<Person> group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFrag);
        mapFragment.getMapAsync(this);
        floatingActionButton1 = findViewById(R.id.fab1);
        floatingActionButton2 = findViewById(R.id.fab2);
        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getCurrentLocation();
                moveCameraToCurrentLocation();
            }
        });
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), JoinGroupActivity.class);
                startActivityForResult(intent, 1);
            }
        });

//        writeToFile();/
        new CoordinateSaver(getApplicationContext(), latitude, longitude).start();

    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mMap.clear();
            Bundle bundle = data.getBundleExtra("Bundle");
            group = bundle.getParcelableArrayList("Group");
            for (int i = 0; i < group.size(); i++) {
                Log.d(TAG, "onActivityResult: " + group.get(i).getName() + ", " + group.get(i).getLatitude() + ", " + group.get(i).getLongitude());
                LatLng latLng = new LatLng(group.get(i).getLatitude(), group.get(i).getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLng)).setTitle(group.get(i).getName());
            }
        }
    }

    public void moveCameraToCurrentLocation() {
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
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
        getCurrentLocation();
        moveCameraToCurrentLocation();
    }

    private void getCurrentLocation() {
//        Log.d(TAG, "onLocationChanged: Longitude: " + longitude + ", Latitude: " + latitude);
//        LatLng latLng = new LatLng(latitude, longitude);
//        mMap.clear();
//        mMap.addMarker(new MarkerOptions().position(latLng)).setTitle("You're here!");
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mMap.clear();
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))).setTitle("You're here!");
                if(group != null){
                    for(int i = 0; i < group.size(); i++){
                        Log.d(TAG, "onLocationChanged: " + group.get(i).getName());
                        LatLng latLng1 = new LatLng(group.get(i).getLatitude(), group.get(i).getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng1)).setTitle(group.get(i).getName());
                    }
                }
                Log.d(TAG, "onLocationChanged: YAY");
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
        };
        
        if (group != null) {
            for (int i = 0; i < group.size(); i++) {
                Log.d(TAG, "onActivityResult: " + group.get(i).getName() + ", " + group.get(i).getLatitude() + ", " + group.get(i).getLongitude());
                LatLng latLng1 = new LatLng(group.get(i).getLatitude(), group.get(i).getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLng1)).setTitle(group.get(i).getName());
            }
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
        
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            mMap.addMarker(new MarkerOptions().position(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude())));
        }
}
}
