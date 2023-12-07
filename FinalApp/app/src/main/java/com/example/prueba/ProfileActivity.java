package com.example.prueba;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private AppDatabase db;
    private SharedPreferences sharedPreferencesLogIn, sharedPreferencesEvent;
    private List<Favourite> favourites;
    private CalendarView calendarView;
    private ListView listViewEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db = AppDatabase.getInstance(getApplicationContext()); // load the database
        sharedPreferencesLogIn = getSharedPreferences("login_prefs", Context.MODE_PRIVATE);

        // Turn the shared preferences object to true
        sharedPreferencesEvent = getSharedPreferences("event_prefs", Context.MODE_PRIVATE);
        sharedPreferencesEvent.edit().putBoolean("fromFavs", true).commit();
        boolean fromFavs = sharedPreferencesEvent.getBoolean("fromFavs", false);
        System.out.println("-------> From favs" +  fromFavs);

        Resources res = getResources();

        // Get the user name from the log in
        Intent intent = getIntent();
        String user_name = intent.getStringExtra("user_name");
        System.out.println("----> User name: " + user_name);
        User user = db.userDao().selectByName(user_name);

        TextView title = findViewById(R.id.user);
        title.setText(res.getString(R.string.hello_profile) + " " + user.user_name);


        // Show the favourites events
        favourites = db.favouriteDao().selectByUserId(user.ID);
        LinearLayout linearLayout = findViewById(R.id.linear_events);
        linearLayout.removeAllViews();
        TextView fav_text = findViewById(R.id.fav_text);


        // Button for logging out
        View button_logOut = findViewById(R.id.logOut);
        button_logOut.setOnClickListener(view -> {
            // Turn the log in shared preference object to false
            SharedPreferences.Editor editorLogIn = sharedPreferencesLogIn.edit();
            editorLogIn.putBoolean("isLoggedIn", false);
            editorLogIn.apply();

            // Redirect to the main page
            Intent mainIntent = new Intent(view.getContext(), MainActivity.class);
            startActivity(mainIntent);
        });

        // Check if the list is empty
        if (favourites.isEmpty()){
            // erase the scroll view
            ScrollView scrollView = findViewById(R.id.scrollView);
            scrollView.setVisibility(View.GONE);
            fav_text.setText(res.getString(R.string.no_favs));
            // ERASE THE CALENDAR TOO

        }else {
            // hide the button for directing to events
            findViewById(R.id.eventsActivity).setVisibility(View.GONE);

            // Iterate through all events for showing them
            for (Favourite favourite : favourites) {
                addImageFromResourcesToLayout(favourite.event_id, linearLayout, user);
                fav_text.setText(res.getString(R.string.yes_favs));
            }
        }

        // Button for redirecting to events view
        View button_events = findViewById(R.id.eventsActivity);
        button_events.setOnClickListener(view -> {
            // Start Events activity
            Intent eventsIntent = new Intent(view.getContext(), EventsActivity.class);
            eventsIntent.putExtra("user_name", user.user_name);
            startActivity(eventsIntent);
        });



    }
    private void addImageFromResourcesToLayout(long event_id, LinearLayout linearLayout, User user) {
        // Obtain the event from favourite object
        Event event = db.eventDao().selectByID((int) event_id);

        // adding the images to the scroll down menu
        int imageResourceId = getResources().getIdentifier(event.image, "drawable", getPackageName());
        ImageButton imageButton = new ImageButton(this);
        imageButton.setImageResource(imageResourceId);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create an intent to start a new activity with the event details
                Intent intent = new Intent(view.getContext(), EventActivity.class);

                // pass the event title to the new activity
                intent.putExtra("eventTitle", event.title);
                intent.putExtra("user_name", user.user_name);
                System.out.println("-----> Event title: " + event.title);
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
