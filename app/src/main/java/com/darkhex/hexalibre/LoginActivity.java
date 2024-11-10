package com.darkhex.hexalibre;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends AppCompatActivity {

    private  Spinner collegeSpinner;
    private ProgressBar progressBar;
    private GoogleSignInClient mGoogleSignInClient;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_login);

        //Already Logged in.
        sharedPreferences = getSharedPreferences("CollegePrefs", MODE_PRIVATE);
        String savedCollege = sharedPreferences.getString("selected_college", null);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null && savedCollege != null) {
            check_user(account.getDisplayName(), account.getEmail(), "1");
            return;
        }

        //Initializing Spinner for college
        collegeSpinner=findViewById(R.id.spinner_colleges);
        progressBar  = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        Get_paths p=new Get_paths();
        p.getPath(new CollegeFetchCallback() {
            @Override
            public void onCollegeFetched(List<String> cols) {
                List<String> collegeList=new ArrayList<>();
                collegeList.add("Select College");
                collegeList.addAll(cols);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_spinner_item, collegeList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                collegeSpinner.setAdapter(adapter);
                setSpinnerAction();
                progressBar.setVisibility(View.GONE);
                collegeSpinner.setVisibility(View.VISIBLE);
            }
        });

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        findViewById(R.id.sign_in_button).setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            signInLauncher.launch(signInIntent);
        });
    }
    //End of on create

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    handleSignInResult(task);
                }
            });
    private void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            store("profile_pic", account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : account.getDisplayName()!=null ? account.getDisplayName() :"H L");
            store("Name", account.getDisplayName());
            store("E-mail", account.getEmail());
            check_user(account.getDisplayName(), account.getEmail(), "1");
        } catch (ApiException e) {
            Log.d("Login Activity", "signInResult:failed code=" + e.getStatusCode());
        }
    }
    //Storing to cache
    public void store(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }


    //Check for existence of user
    void check_user(String name, String email, String college) {
        User_search userSearch = new User_search();
        userSearch.searchUserByEmail(email, "email", new UserSearchCallback() {
            @Override
            public void onUserFound(String uid) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onUserNotFound() {
                Intent intent = new Intent(LoginActivity.this, SignInActivity.class);
                intent.putExtra("Name", name);
                intent.putExtra("Email", email);
                intent.putExtra("c_id", college);
                startActivity(intent);
                finish();
            }
        });
    }
    public void setSpinnerAction(){
        collegeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) { // Ensure something other than "Select College" is chosen
                    String selectedCollege = parent.getItemAtPosition(position).toString();

                    // Store selected college
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("selected_college", selectedCollege);
                    editor.apply();

                    // Show the Google Sign-In button
                    findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.sign_in_button).setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case where nothing is selected
                findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            }
        });
    }
}
