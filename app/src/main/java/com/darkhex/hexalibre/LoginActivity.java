package com.darkhex.hexalibre;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

        private GoogleSignInClient mGoogleSignInClient;
        private static final String TAG = "LoginActivity";
        private SharedPreferences sharedPreferences;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = getSharedPreferences("CollegePrefs", MODE_PRIVATE);
        String savedCollege = sharedPreferences.getString("selected_college", null);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null && savedCollege != null) {
            // User is already signed in, redirect to MainActivity
            Log.d(TAG, "User already signed in: " + account.getEmail());
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;  // Prevent further execution of the onCreate method
        }

        // Spinner setup
        Spinner collegeSpinner = findViewById(R.id.spinner_colleges);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        Data data = new Data(this);
        data.getColleges(cols -> {
            List<String> collegeList = new ArrayList<>();
            collegeList.add("Select College");
            collegeList.addAll(cols);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_spinner_item, collegeList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // Set the adapter to the Spinner
            collegeSpinner.setAdapter(adapter);
            progressBar.setVisibility(View.GONE);
            collegeSpinner.setVisibility(View.VISIBLE);
        });

        com.google.android.gms.common.SignInButton toLoginPage = findViewById(R.id.sign_in_button);
        collegeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (id != 0) {
                    // Get the selected item
                    String selectedCollege = parent.getItemAtPosition(position).toString();

                    // Store the selected college in SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("selected_college", selectedCollege);
                    editor.apply();

                    // Show the Google Sign-In button
                    toLoginPage.setVisibility(View.VISIBLE);
                    toLoginPage.setEnabled(true);
                } else {
                    toLoginPage.setVisibility(View.GONE);
                    toLoginPage.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                toLoginPage.setVisibility(View.GONE);
                toLoginPage.setEnabled(false);
            }
        });

        // Configure sign-in to request the user's ID, email address, and basic profile
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        // Build a GoogleSignInClient with the options specified by gso
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set up the Google Sign-In button
        findViewById(R.id.sign_in_button).setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            signInLauncher.launch(signInIntent);
        });
    }

    // Register the ActivityResultLauncher to handle the result of the sign-in intent
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    // Handle the sign-in result here
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    handleSignInResult(task);
                }
            });

    private void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Local Storage

            store("profile_pic", account.getPhotoUrl() != null ? account.getPhotoUrl().toString() :
                    account.getDisplayName() != null ? account.getDisplayName() :"H L");
            store("Name",String.valueOf(account.getDisplayName()));
            store("E-mail",String.valueOf(account.getEmail()));


            Log.d(TAG, "signInResult:success, user email: " + account.getEmail());
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }
    public  void store(String key, String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.apply();
    }



}
