package com.example.prueba;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.EventLogTags;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;



public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener{
    private static final int REQUEST_PERMISSION_ACCESS_FINE_LOCATION = 1;
    private AppDatabase db;
    private GoogleMap mMap;
    private Spinner preferenceSpinner;
    private String preference;
    private List<Event> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSION_ACCESS_FINE_LOCATION);
        } else {
            LocationManager locationManager =
                    (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager
                    .GPS_PROVIDER, 0, 1, this);
        }
    }

    @Override // Creates the map and add the markers in the events
    public void onMapReady(GoogleMap googleMap) {

        db = AppDatabase.getInstance(getApplicationContext()); // load the database

        // By default show all preferences
        events = db.eventDao().selectAll();
        // Iterate through all events for showing a marker at each event
        for (Event event : events){
            System.out.println("----> Event name " + event.title);
            LatLng event_marker = new LatLng(event.longitude, event.latitude);
            googleMap.addMarker(new MarkerOptions()
                    .position(event_marker)
                    .title(event.title));
        }

        // Get the preference from the spinner
        preferenceSpinner = findViewById(R.id.preferencesFilter);
        preferenceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                preference = parent.getItemAtPosition(i).toString();
                // Look for the events given the preference
                System.out.println("----> preference: " + preference);
                if (preference.equals("All")){
                    events = db.eventDao().selectAll();
                } else {
                    events = db.eventDao().selectByPreference(preference);
                }

                // Remove all existing markers from the map
                googleMap.clear();

                // Iterate through all events for showing a marker at each event
                for (Event event : events){
                    System.out.println("----> Event name " + event.title);
                    LatLng event_marker = new LatLng(event.longitude, event.latitude);
                    googleMap.addMarker(new MarkerOptions()
                            .position(event_marker)
                            .title(event.title));
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(getApplicationContext(), "You have to select a preference",
                        Toast.LENGTH_LONG).show();

            }
        });


        mMap = googleMap; // for using the map out of this method


    }

    @Override // Makes zoom to the location of the user
    public void onLocationChanged(Location location) {
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 10));

    }

    @Override // checks permission to access the location of the device
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSION_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    finish();
                    startActivity(getIntent());
                }
                break;
            }
        }
    }
}


