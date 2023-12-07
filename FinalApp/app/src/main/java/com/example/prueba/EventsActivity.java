package com.example.prueba;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class EventsActivity extends AppCompatActivity {
    private AppDatabase db;
    private Spinner preferenceSpinner;
    private String preference;
    private List<Event> events;
    private SharedPreferences sharedPreferencesLogIn, sharedPreferencesEvent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        db = AppDatabase.getInstance(getApplicationContext()); // load the database
        sharedPreferencesLogIn = getSharedPreferences("login_prefs", Context.MODE_PRIVATE);

        // Turn the shared preferences object to false by default
        sharedPreferencesEvent = getSharedPreferences("event_prefs", Context.MODE_PRIVATE);
        sharedPreferencesEvent.edit().putBoolean("fromFavs", false).commit();
        boolean fromFavs = sharedPreferencesEvent.getBoolean("fromFavs", false);
        System.out.println("-------> From favs" +  fromFavs);

        Resources res = getResources();

        View button_map = findViewById(R.id.map);
        button_map.setOnClickListener(view -> {
            Intent mapIntent = new Intent(view.getContext(), MapActivity.class);
            startActivity(mapIntent);
        });

        // make differences when the user is logged in
        boolean isLoggedIn = sharedPreferencesLogIn.getBoolean("isLoggedIn", false);
        if (isLoggedIn){
            System.out.println("----> User log in");

            // Get the user name from the log in
            Intent intent = getIntent();
            String user_name = intent.getStringExtra("user_name");
            System.out.println("----> User name: " + user_name);
            User user = db.userDao().selectByName(user_name);

            TextView title = findViewById(R.id.user);
            title.setText(res.getString(R.string.welcome_events)+" "+user.user_name);

            // Button to access profile view
            View button_profile = findViewById(R.id.profile);
            button_profile.setOnClickListener(view -> {
                Intent profIntent = new Intent(view.getContext(), ProfileActivity.class);
                profIntent.putExtra("user_name",user.user_name);
                startActivity(profIntent);
            });
        }else {
            // Hide the profile button if it is not logged in
            findViewById(R.id.profile).setVisibility(View.GONE);
        }

        LinearLayout linearLayout = findViewById(R.id.linear_events);

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

                // Iterate through all events for showing them
                linearLayout.removeAllViews();
                for (Event event : events){
                    User user = null;

                    // In case there is a user logged in, pass it to the event activity
                    if (isLoggedIn){
                        // Get the user name from the log in
                        Intent intent = getIntent();
                        String user_name = intent.getStringExtra("user_name");
                        System.out.println("----> User name: " + user_name);
                        user = db.userDao().selectByName(user_name);
                    }

                    System.out.println("----> Event name " + event.title);
                    addImageFromResourcesToLayout(event, linearLayout, user);
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(getApplicationContext(), "You have to select a preference",
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    private void addImageFromResourcesToLayout(Event event, LinearLayout linearLayout, User user) {

        // adding the images to the scroll down menu
        int imageResourceId = getResources().getIdentifier(event.image, "drawable", getPackageName());
        ImageButton imageButton = new ImageButton(this);
        imageButton.setImageResource(imageResourceId);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get if the user is logged in or not
                boolean isLoggedIn = sharedPreferencesLogIn.getBoolean("isLoggedIn", false);

                // create an intent to start a new activity with the event details
                Intent intent = new Intent(view.getContext(), EventActivity.class);

                // pass the event title to the new activity
                intent.putExtra("eventTitle", event.title);
                // if the user us logged in, share the user name to the new activity
                if (isLoggedIn){
                    intent.putExtra("user_name", user.user_name);
                }
                startActivity(intent);
            }
        });
        linearLayout.addView(imageButton);

        TextView textView = new TextView(this);
        textView.setText(event.title);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textView.setTextColor(Color.BLACK);
        linearLayout.addView(textView);
    }
}
