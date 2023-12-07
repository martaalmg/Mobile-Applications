package com.example.prueba;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LogInActivity extends AppCompatActivity {
    private AppDatabase db;
    private EditText mailText, passwordText;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // create the shared preferences object to keep track of the log in
        sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE);

        // get values inserted by the user
        db = AppDatabase.getInstance(getApplicationContext());
        mailText = findViewById(R.id.logInMail);
        passwordText = findViewById(R.id.logInPassword);

        // store buttons values at variables
        Button back = findViewById(R.id.back);
        Button logIn = findViewById(R.id.logIn);

        // If we click on return button it returns
        back.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), MainActivity.class);
            startActivity(intent);
        });

        // If we click on log In button we check the credentials in order of logging in
        logIn.setOnClickListener(view -> {
            String mail = mailText.getText().toString();
            String password = passwordText.getText().toString();

            System.out.println("----> mail " + mail);
            User user = db.userDao().selectByEmail(mail); // look for the user with that mail at the db
            System.out.println("----> user " + user);
            if (user == null) {
                Toast.makeText(getApplicationContext(), "user not found",
                        Toast.LENGTH_LONG).show();
            } else {
                System.out.println("----> user.password " + user.password);
                System.out.println("----> password " + password);
                if (user.password.equals(password)) {
                    Toast.makeText(getApplicationContext(), "successful log in",
                            Toast.LENGTH_LONG).show();

                    // Turn the log in to True for detecting it in the other activities
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();

                    // Start Events activity
                    Intent eventsIntent = new Intent(view.getContext(), EventsActivity.class);
                    eventsIntent.putExtra("user_name", user.user_name);
                    startActivity(eventsIntent);

                } else {
                    Toast.makeText(getApplicationContext(), "wrong password" + mail,
                            Toast.LENGTH_LONG).show();
                }


            }

        });
    }
}
