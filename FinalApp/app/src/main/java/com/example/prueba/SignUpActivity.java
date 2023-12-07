package com.example.prueba;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    private AppDatabase db;
    private EditText nameText, mailText,  passwordText, phoneText;
    private RadioGroup inputPreference;
    private  String preferences, language;
    private Spinner languageSpinner;
    private SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE);


        // store buttons values at variables
        Button back = findViewById(R.id.back);
        Button insert = findViewById(R.id.signUp);

        // If press return button go back
        back.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), MainActivity.class);
            startActivity(intent); });

        // If press sign up button
        insert.setOnClickListener(view -> {

            // get all the necessary data for creating a new user and storing it
            db = AppDatabase.getInstance(getApplicationContext());
            nameText = findViewById(R.id.signUpName);
            mailText = findViewById(R.id.signUpEmail);
            passwordText = findViewById(R.id.signUpPassword);
            languageSpinner = findViewById(R.id.signUpLanguage);
            phoneText = findViewById(R.id.signUpNumber);
            inputPreference = findViewById(R.id.preferences);
            int selectedPreferenceID = inputPreference.getCheckedRadioButtonId();
            if(selectedPreferenceID == R.id.culturalbutton){
                preferences = "Cultural";
            } else if(selectedPreferenceID == R.id.socialbutton){
                preferences = "Social";
            } else if(selectedPreferenceID == R.id.nightlifebutton){
                preferences = "Night_life";
            } else if(selectedPreferenceID == R.id.sportsbutton){
                preferences = "Sports";
            }else if(selectedPreferenceID == R.id.restaurantbutton){
                preferences = "Restaurants";
            }

            // Get the language from the spinner
            languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                    language = parent.getItemAtPosition(i).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    Toast.makeText(getApplicationContext(), "You have to select a language",
                            Toast.LENGTH_LONG).show();
                }
            });

            // change all the EditText types to Strings
            String name = nameText.getText().toString();
            String mail = mailText.getText().toString();
            String password = passwordText.getText().toString();
            String phone = phoneText.getText().toString();

            // check that all the fields are filled and correctly
            if (name.length()!=0 && mail.length()!=0 && password.length()!=0 && phone.length()!=0
                    && preferences.length()!=0) {
                System.out.println("----> All fields filled");
                System.out.println("----> name " + name);
                System.out.println("----> mail " + mail);
                System.out.println("----> password " + password);
                System.out.println("----> phone" + phone);

                // Check if name and phone are unique, and if mail and phone are in proper format
                User user_name = db.userDao().selectByName(name);
                User user_phone = db.userDao().selectByPhone(phone);

                if (user_name != null){
                    Toast.makeText(getApplicationContext(), "User " + name +
                                    " all ready registered",
                            Toast.LENGTH_LONG).show();
                } else if (phone.length()!=9) {
                    Toast.makeText(getApplicationContext(), "Please, introduce a valid phone",
                            Toast.LENGTH_LONG).show();
                } else if (!mail.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                    Toast.makeText(getApplicationContext(), "Please, enter a valid mail",
                            Toast.LENGTH_LONG).show();
                } else if (user_phone != null){
                    Toast.makeText(getApplicationContext(), "User with phone " + phone +
                                    " all ready registered",
                            Toast.LENGTH_LONG).show();
                } else {
                    // ALL VARIABLES ARE FILLED CORRECTLY

                    // look for a user with that email at the db.
                    // In case it is null, we create the user and insert it.
                    User user = db.userDao().selectByEmail(mail);
                    if (user == null){
                        User new_user = new User (name, mail, password, language, preferences, phone);
                        long id = db.userDao().insert(new_user);
                        Toast.makeText(getApplicationContext(), "Inserted user " + name,
                                Toast.LENGTH_LONG).show();

                        // Turn the log in to True for detecting it in the other activities
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.apply();

                        // Start Events activity
                        Intent eventsIntent = new Intent(view.getContext(), EventsActivity.class);
                        eventsIntent.putExtra("user_name", new_user.user_name);
                        startActivity(eventsIntent);

                    }
                    else{
                        System.out.println("----> mail: " + user.email);
                        Toast.makeText(getApplicationContext(), "User " + name + "already exists",
                                Toast.LENGTH_LONG).show();
                    }
                }

            } else {
                System.out.println("----> Please fill all fields");
                System.out.println("----> name " + name);
                System.out.println("----> mail " + mail);
                System.out.println("----> password " + password);
                System.out.println("----> phone " + phone);
                Toast.makeText(getApplicationContext(), "Please, fill all fields",
                        Toast.LENGTH_LONG).show();
            }

        });


    }

}
