package edu.csueb.codepath.fitness_tracker;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {
    public static final String TAG = "Signup Activity";

    private EditText firstName;
    private EditText lastName;
    private EditText emailAddress;
    private EditText weight;
    private EditText height;
    private EditText age;
    private EditText username;
    private EditText password;
    private Button submit;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_signup);

        back = findViewById(R.id.back);
        firstName = findViewById(R.id.etfirstname);
        lastName = findViewById(R.id.etlastname);
        emailAddress = findViewById(R.id.etEmailAddress);
        weight = findViewById(R.id.etWeight);
        height = findViewById(R.id.etHeight);
        age = findViewById(R.id.etAge);
        username = findViewById(R.id.etUsername);
        password = findViewById(R.id.etPassword);
        submit = findViewById(R.id.btnSubmit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validate input fields
                if (validateInputs()) {
                    // Proceed with signup if all inputs are valid
                    ParseUser user = new ParseUser();
                    user.setPassword(password.getText().toString());
                    user.setUsername(username.getText().toString());
                    user.setEmail(emailAddress.getText().toString());
                    user.put("firstname", firstName.getText().toString());
                    user.put("lastname", lastName.getText().toString());
                    user.put("height", Integer.parseInt(height.getText().toString()));
                    user.put("weight", Integer.parseInt(weight.getText().toString()));
                    user.put("age", Integer.parseInt(age.getText().toString()));

                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "User create unsuccessful", e);
                                Toast.makeText(SignupActivity.this, "Sign up unsuccessful", Toast.LENGTH_LONG).show();
                            } else {
                                Log.i(TAG, "Success on Sign up!");
                                Toast.makeText(SignupActivity.this, "Sign up successful", Toast.LENGTH_LONG).show();
                                goMainActivity();
                            }
                        }
                    });
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SignupActivity.this, "Going back", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(i);
                Animatoo.animateSlideRight(SignupActivity.this);
                finish();
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Reset borders
        resetBorders();

        // Check if any field is empty
        if (TextUtils.isEmpty(firstName.getText())) {
            firstName.setError("This field cannot be empty");
            isValid = false;
        }
        if (TextUtils.isEmpty(lastName.getText())) {
            lastName.setError("This field cannot be empty");
            isValid = false;
        }
        if (TextUtils.isEmpty(emailAddress.getText())) {
            emailAddress.setError("This field cannot be empty");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress.getText()).matches()) {
            emailAddress.setError("Invalid email format");
            isValid = false;
        }
        if (TextUtils.isEmpty(weight.getText())) {
            weight.setError("This field cannot be empty");
            isValid = false;
        }else {
            try {
                int weightValue = Integer.parseInt(weight.getText().toString());
                if (weightValue < 0) {
                    weight.setError("Weight cannot be negative");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                weight.setError("Invalid weight format");
                isValid = false;
            }
        }
        if (TextUtils.isEmpty(height.getText())) {
            height.setError("This field cannot be empty");
            isValid = false;
        } else {
            try {
                int heightValue = Integer.parseInt(height.getText().toString());
                if (heightValue < 0) {
                    height.setError("Height cannot be negative");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                height.setError("Invalid height format");
                isValid = false;
            }
        }
        if (TextUtils.isEmpty(age.getText())) {
            age.setError("This field cannot be empty");
            isValid = false;
        } else {
            try {
                int ageValue = Integer.parseInt(age.getText().toString());
                if (ageValue < 0) {
                    age.setError("Age cannot be negative");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                age.setError( "Invalid age format");
                isValid = false;
            }
        }
        if (TextUtils.isEmpty(username.getText())) {
            username.setError("This field cannot be empty");
            isValid = false;
        }
        if (TextUtils.isEmpty(password.getText())) {
            password.setError("This field cannot be empty");
            isValid = false;
        }

        return isValid;
    }

    private void resetBorders() {
        // Clear errors from fields
        firstName.setError(null);
        lastName.setError(null);
        emailAddress.setError(null);
        weight.setError(null);
        height.setError(null);
        age.setError(null);
        username.setError(null);
        password.setError(null);
    }

    private void goMainActivity() {
        Intent i = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}
