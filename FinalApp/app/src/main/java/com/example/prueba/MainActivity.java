package com.example.prueba;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferencesLogIn, sharedPreferencesEvent;

    private static final String CHANNEL_ID = "com.example.prueba.notify_001";
    private static final String CHANNEL_NAME = "My notification channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // create the shared preferences objects
        sharedPreferencesLogIn = getSharedPreferences("login_prefs", Context.MODE_PRIVATE);

        // Turn the shared preferences object to false by default
        sharedPreferencesEvent = getSharedPreferences("event_prefs", Context.MODE_PRIVATE);
        sharedPreferencesEvent.edit().putBoolean("fromFavs", false).commit();

        // Turn the log in to False for detecting it in the other activities
        SharedPreferences.Editor editorLogIn = sharedPreferencesLogIn.edit();
        editorLogIn.putBoolean("isLoggedIn", false);
        editorLogIn.apply();

        View button_logIn = findViewById(R.id.logIn);
        button_logIn.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), LogInActivity.class);
            startActivity(intent);
        });

        View button_signUp = findViewById(R.id.signUp);
        button_signUp.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), SignUpActivity.class);
            startActivity(intent);
        });

        View button_invited = findViewById(R.id.invited);
        button_invited.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), EventsActivity.class);
            startActivity(intent);
        });

    }
}