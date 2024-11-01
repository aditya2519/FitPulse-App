package edu.csueb.codepath.fitness_tracker;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.parse.ParseUser;
import com.parse.ParseException;
import com.parse.SaveCallback;

public class ProfileEdit extends AppCompatActivity {
    UserModel profile;
    TextView etProfileImage;
    EditText etUsername;
    EditText etEmail;
    EditText etPassword1;
    EditText etPassword2;
    EditText etHeight;
    EditText etWeight;
    Button btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_edit); // Use setContentView for Activity

        // Initialize your views here
        etProfileImage = findViewById(R.id.etProfileImage);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword1 = findViewById(R.id.etPassword1);
        etPassword2 = findViewById(R.id.etPassword2);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        btnUpdate = findViewById(R.id.btnUpdate);

        // Load data from ParseUser
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            etProfileImage.setText(currentUser.getString("profileImage"));
            etUsername.setText(currentUser.getUsername());
            etEmail.setText(currentUser.getEmail());
            etHeight.setText(currentUser.getString("height"));
            etWeight.setText(currentUser.getString("weight"));
        }

        // Set up the button click event
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileData(currentUser);
            }
        });
    }

    private void saveProfileData(ParseUser currentUser) {
        if (currentUser != null) {
            // Retrieve the entered data
            String profileImage = etProfileImage.getText().toString();
            String username = etUsername.getText().toString();
            String email = etEmail.getText().toString();
            String heightStr = etHeight.getText().toString();
            String weightStr = etWeight.getText().toString();

            // Set the new values in the ParseUser object
            if (!username.isEmpty()) {
                currentUser.setUsername(username);
            }
            if (!email.isEmpty()) {
                currentUser.setEmail(email);
            }
            if (!profileImage.isEmpty()) {
                currentUser.put("profileImage", profileImage);
            }
            if (!heightStr.isEmpty()) {
                try {
                    double height = Double.parseDouble(heightStr);
                    currentUser.put("height", height);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid height format", Toast.LENGTH_SHORT).show();
                }
            }
            if (!weightStr.isEmpty()) {
                try {
                    double weight = Double.parseDouble(weightStr);
                    currentUser.put("weight", weight);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid weight format", Toast.LENGTH_SHORT).show();
                }
            }

            // Save the ParseUser object
            currentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        // Save was successful
                        Toast.makeText(ProfileEdit.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        // Save failed, show the error message
                        Toast.makeText(ProfileEdit.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
