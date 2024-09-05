package com.darkhex.hexalibre;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

public class ProfileActivity extends Activity {

    private TextView profileName;
    private TextView profileEmail;
    private TextView profileHonorScore;
    private TextView profileRollNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize TextView elements
        profileName = findViewById(R.id.profile_name);
        profileEmail = findViewById(R.id.profile_email);
        profileHonorScore = findViewById(R.id.profile_honor_score);
        profileRollNumber = findViewById(R.id.profile_roll_number);

        // Example email for demo
        String email = "user@example.com"; // Replace with actual email from OAuth
        new FetchUserProfileTask().execute(email);
    }

    private class FetchUserProfileTask extends AsyncTask<String, Void, UserProfile> {

        @Override
        protected UserProfile doInBackground(String... params) {
            ProfileService profileService = new ProfileService();
            return profileService.fetchUserProfile(params[0]);
        }

        @Override
        protected void onPostExecute(UserProfile userProfile) {
            if (userProfile != null) {
                profileName.setText("Name: " + userProfile.getName());
                profileEmail.setText("Email: " + userProfile.getEmail());
                profileHonorScore.setText("Honor Score: " + userProfile.getHonorScore());
                profileRollNumber.setText("Roll Number: " + userProfile.getRollNumber());
            } else {
                // Handle error case
                profileName.setText("Error fetching profile");
            }
        }
    }
}
