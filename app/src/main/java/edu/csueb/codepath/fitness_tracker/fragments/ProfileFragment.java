package edu.csueb.codepath.fitness_tracker.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import edu.csueb.codepath.fitness_tracker.LoginActivity;
import edu.csueb.codepath.fitness_tracker.ProfileEdit;
import edu.csueb.codepath.fitness_tracker.R;

public class ProfileFragment extends Fragment {
    private TextView tvName;
    private TextView tvUsername;
    private TextView tvUserHeight;
    private TextView tvUserWeight;
    private ImageView ivProfileImage;
    private ImageButton btnLogout;
    private ImageButton btnEdit;

    private static final String TAG = "ProfileFragment";

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvName = view.findViewById(R.id.tvUsernameProf);
        tvUsername = view.findViewById(R.id.tvName);
        tvUserHeight = view.findViewById(R.id.tvUserHeight);
        tvUserWeight = view.findViewById(R.id.tvUserWeight);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnEdit = view.findViewById(R.id.btnEdit);

        // Fetch and display user data
        getCurrentUser();

        // Logout button
        btnLogout.setOnClickListener(v -> {
            ParseUser.logOut();
            Intent i = new Intent(getContext(), LoginActivity.class);
            startActivity(i);
        });

        // Edit Profile button
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ProfileEdit.class);
                startActivity(i);
            }
        });
    }

    private void getCurrentUser() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // Display user details
            tvName.setText("@" + currentUser.getUsername());
            tvUsername.setText(currentUser.getString("firstname") + " " + currentUser.getString("lastname"));
            tvUserHeight.setText(String.valueOf(currentUser.getInt("height")) + " cm");
            tvUserWeight.setText(String.valueOf(currentUser.getInt("weight")) + " kg");

            // Load profile image
            ParseFile profileImageFile = currentUser.getParseFile("profile_image");
            if (profileImageFile != null) {
                profileImageFile.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, ParseException e) {
                        if (e == null) {
                            // Use Glide to load the image
                            Glide.with(getContext())
                                    .load(profileImageFile.getUrl())
                                    .centerCrop()
                                    .circleCrop()
                                    .into(ivProfileImage);
                        } else {
                            Log.e(TAG, "Error retrieving profile image: " + e.getMessage());
                        }
                    }
                });
            }
        } else {
            Log.e(TAG, "No current user found.");
        }
    }
}
