package com.example.prueba;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.os.Build;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.TravelMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class EventActivity extends AppCompatActivity {

    private AppDatabase db;
    private GoogleMap mMap;
    private SharedPreferences sharedPreferencesLogIn, sharedPreferencesEvent;
    private List<Favourite> user_favourites;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private  LatLng currentLoc;

    private static final String CHANNEL_ID = "com.example.prueba.notify_001";
    private static final String CHANNEL_NAME = "My notification channel";
    private NotificationManager notificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Resources res = getResources();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        sharedPreferencesLogIn = getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferencesLogIn.getBoolean("isLoggedIn", false);

        // Get shared preference for the events precedence
        sharedPreferencesEvent = getSharedPreferences("event_prefs", Context.MODE_PRIVATE);
        boolean fromFavs = sharedPreferencesEvent.getBoolean("fromFavs", false);

        // To convert the string (date) into a date object
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

        // Get the event title from the events intent
        Intent intent = getIntent();
        String eventTitle = intent.getStringExtra("eventTitle");
        String user_name = intent.getStringExtra("user_name"); // Get the user name from the events
        System.out.println("-----> title: " + eventTitle);

        // Load the database and perform a query to obtain the event with that title
        db = AppDatabase.getInstance(getApplicationContext());
        Event event = db.eventDao().selectByTitle(eventTitle);
        System.out.println("-----> EVENT: " + event);
        long event_id = (int) event.ID;

        System.out.println("----> User name: " + user_name);
        User user = db.userDao().selectByName(user_name);

        // Set parameters at the layout
        TextView title = findViewById(R.id.eventTitle);
        title.setText(event.title);

        // Description
        TextView description = findViewById(R.id.eventDescription);
        System.out.println("-----> Event Description: " + event.description);
        description.setText(event.description);

        // Show Date
        TextView date = findViewById(R.id.eventDate);
        System.out.println("-----> Event date: " + event.date);
        int int_date = (int) event.date;
        String string_date = String.format("%08d", int_date);
        Date date_date = null;
        try {
            date_date = format.parse(string_date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        date.setText("Date of the event: " + date_date);

        // add a map for showing the event
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                mMap = googleMap;

                // Obtain location permissions
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(EventActivity.this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
                    return;
                }

                LatLng eventLoc = new LatLng(event.longitude, event.latitude);
                mMap.addMarker(new MarkerOptions().position(eventLoc));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLoc, 10));

                // Add Marker for the user location
                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
                                    mMap.addMarker(new MarkerOptions()
                                            .position(currentLoc)
                                            .title("You are here!"));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 10));
                                }
                            }
                        });

                // Give directions
                View button_directions= findViewById(R.id.directions);
                button_directions.setOnClickListener(view -> {
                    try{
                        // parse to the correct variable type
                        com.google.maps.model.LatLng myLatLng = new com.google.maps.model.LatLng(currentLoc.latitude, currentLoc.longitude);
                        com.google.maps.model.LatLng myEventLoc = new com.google.maps.model.LatLng(eventLoc.latitude, eventLoc.longitude);

                        System.out.println("------> myLatLng " + myLatLng);
                        System.out.println("------> myEventLoc " + myEventLoc);

                        // Directions
                        GeoApiContext context = new GeoApiContext.Builder()
                                .apiKey(BuildConfig.MAPS_API_KEY)
                                .build();
                        DirectionsResult result = DirectionsApi.newRequest(context)
                                .origin(myLatLng)
                                .destination(myEventLoc)
                                .mode(TravelMode.DRIVING)
                                .await();
                        PolylineOptions polylineOptions =
                                new PolylineOptions().width(8).color(Color.BLUE);

                        System.out.println("----------------> routes " + result.routes);
                        for (DirectionsRoute route : result.routes) {
                            EncodedPolyline encodedPolyline = route.overviewPolyline;
                            for (com.google.maps.model.LatLng latLng : encodedPolyline.decodePath()) {
                                polylineOptions.add(new LatLng(latLng.lat, latLng.lng));
                            }
                        }
                        mMap.addPolyline(polylineOptions);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                });
            }

        });

        // hide favourite buttons and back to event if user is not logged in
        if (!isLoggedIn) {
            findViewById(R.id.favs).setVisibility(View.GONE);
            findViewById(R.id.back).setVisibility(View.GONE);
        }

        // Back to events button
        View button_events = findViewById(R.id.back);
        button_events.setOnClickListener(view -> {
            Intent eventsIntent = new Intent(view.getContext(), EventsActivity.class);
            eventsIntent.putExtra("user_name", user.user_name);
            startActivity(eventsIntent);
        });

        // Add event to favourites or delete them in case is already at favs
        Button favs = findViewById(R.id.favs);
        if (fromFavs) {
            favs.setText(res.getString(R.string.removeFav));
            // remove events from favs
            favs.setOnClickListener(view -> {
                System.out.println("------> Event ID: " + event.ID);
                System.out.println(("-----> User ID: " + user.ID));

                // Get the event and delete it
                Favourite favEvent = db.favouriteDao().selectByUserAndEventId(user.ID, event.ID);
                if (favEvent == null){
                    Toast.makeText(getApplicationContext(), "Event " + event.title + " already removed",
                            Toast.LENGTH_LONG).show();
                } else {
                    db.favouriteDao().delete(favEvent);
                    Toast.makeText(getApplicationContext(), "Event " + event.title + " removed from favourites!",
                            Toast.LENGTH_LONG).show();
                    // Create Notification
                    createNotification(event.title, "Event " + event.title + " has been deleted", event.date);
                }

            });


        }else {
            favs.setText(res.getString(R.string.fav));

            // add events to fav
            favs.setOnClickListener(view -> {
                System.out.println("-----> Event to add: " + event.ID);
                System.out.println("-----> User to add: " + user.ID);

                // get user favourites events
                user_favourites = db.favouriteDao().selectByUserId(user.ID);
                System.out.println("-----> Favourite lists: " + user_favourites);
                Boolean is_fav = false;
                // Check if the event is in the fav list of that user
                for (Favourite favourite : user_favourites) {
                    long event_id_fav = favourite.event_id;
                    if (event_id_fav == event.ID) {
                        is_fav = true;
                    }
                }
                if (is_fav) {
                    Toast.makeText(getApplicationContext(), "Event is already added",
                            Toast.LENGTH_LONG).show();
                } else {

                    Favourite new_fav = new Favourite(event.ID, user.ID, event.preference_event);
                    long id = db.favouriteDao().insert(new_fav);
                    Toast.makeText(getApplicationContext(), "Event " + event.title + " added to your favourites!",
                            Toast.LENGTH_LONG).show();
                    // Create Notification
                    createNotification(event.title, "Event " + event.title + " added to favourites!", event.date);

                }
            });

        }

    }
    private void createNotification(String title, String message, long eventDate) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, EventActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(CHANNEL_ID);
        }

        // set the notification to appear at the event date
        builder.setWhen(eventDate);
        notificationManager.notify(0, builder.build());
    }

}
