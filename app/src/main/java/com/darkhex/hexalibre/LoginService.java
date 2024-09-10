package com.darkhex.hexalibre;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

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

import java.util.Arrays;
import java.util.List;

public class LoginService extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "LoginService";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //College Names
        Spinner collegeSpinner = findViewById(R.id.spinner_colleges);
        // List of colleges (can be dynamically generated)
        List<String> collegeList = Arrays.asList(
                "Select College",
                "College of Engineering, Pune",
                "Indian Institute of Technology, Bombay",
                "Delhi Technological University",
                "Birla Institute of Technology, Pilani",
                "Vellore Institute of Technology"
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, collegeList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Set the adapter to the Spinner
        collegeSpinner.setAdapter(adapter);
        Button signOutButton = findViewById(R.id.sign_out_button);
        Button toLoginPage = findViewById(R.id.button_login_redirect);
        collegeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (id != 0) {
                    // Get the selected item
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
                .build();

        // Build a GoogleSignInClient with the options specified by gso
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set up the Google Sign-In button
        toLoginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        // Set up the Sign Out button

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
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

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        signInLauncher.launch(signInIntent);  // Use the launcher instead of startActivityForResult
    }

    private void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask) {
        try {
            Button signOutButton = findViewById(R.id.sign_out_button);
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI
            Log.d(TAG, "signInResult:success, user email: " + account.getEmail());
            Toast.makeText(LoginService.this, "Signed in as: " + account.getEmail(), Toast.LENGTH_LONG).show();
            signOutButton.setVisibility(View.VISIBLE);
            signOutButton.setEnabled(true);
            Intent home = new Intent(LoginService.this, MainActivity.class);
            startActivity(home);
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(LoginService.this, "Sign in failed", Toast.LENGTH_SHORT).show();
        }
    }

    // Sign the user out of their Google account
    private void signOut() {
        Button signOutButton = findViewById(R.id.sign_out_button);
        signOutButton.setVisibility(View.GONE);
        signOutButton.setEnabled(false);
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            // On sign-out completion
            Toast.makeText(LoginService.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
        });
    }
}
