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
import android.widget.Toast;

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

    private Spinner collegeSpinner;
    private ProgressBar progressBar ;
    private GoogleSignInClient mGoogleSignInClient;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_login);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        sharedPreferences = getSharedPreferences("CollegePrefs", MODE_PRIVATE);
        String savedCollege = sharedPreferences.getString("selected_college", null);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null && savedCollege != null) {
            checkUser(account.getDisplayName(), account.getEmail(), savedCollege, "Stored", userFound -> {
                if (!userFound) {
                    setupSpinner();
                    setupGoogleSignIn();
                }
            });
        }else {
            setupSpinner();
            setupGoogleSignIn();
        }
    }

    private void setupSpinner() {
        collegeSpinner = findViewById(R.id.spinner_colleges);
        progressBar = findViewById(R.id.progressBar);
//        progressBar.setVisibility(View.VISIBLE);

        Get_paths p = new Get_paths();
        p.getPath("Colleges","",cols -> {
            List<String> collegeList = new ArrayList<>();
            collegeList.add("Select College");
            collegeList.addAll(cols);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_spinner_item, collegeList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            collegeSpinner.setAdapter(adapter);
            setSpinnerAction();
            progressBar.setVisibility(View.GONE);
            collegeSpinner.setVisibility(View.VISIBLE);
        });
    }

    private void setupGoogleSignIn() {
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
            storeUserData("profile_pic", account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : account.getDisplayName());
            storeUserData("Name", account.getDisplayName());
            storeUserData("E-mail", account.getEmail());
            String savedCollege = sharedPreferences.getString("selected_college", null);
            checkUser(account.getDisplayName(), account.getEmail(), savedCollege, "notStored", userFound -> {
                if (!userFound) {
                    setupSpinner();
                    setupGoogleSignIn();
                }
            });
        } catch (ApiException e) {
            Toast.makeText(this, "Login Failed: "+e, Toast.LENGTH_SHORT).show();
        }
    }

    private void storeUserData(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void checkUser(String name, String email, String college, String act,testUser callback) {
        User_search userSearch = new User_search();
        userSearch.searchUserByEmail("",college, "Name", new UserSearchCallback(){

            @Override
            public void onUserFound(String c_id) {
                userSearch.searchUserByEmail(c_id, email, "email", new UserSearchCallback() {
                    @Override
                    public void onUserFound(String uid) {
                        callback.onResult(true);
                        launchMainActivity(uid,c_id);
                    }

                    @Override
                    public void onUserNotFound() {
                        if (act.equals("notStored")) {
                            launchSignInActivity(name, email, c_id);
                        }
                        callback.onResult(false);
                    }
                });
            }

            @Override
            public void onUserNotFound() {

            }
        });
    }

    private void launchMainActivity(String uid,String c_id) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("uid", uid);
        intent.putExtra("c_id", c_id);
        startActivity(intent);
        finish();
    }

    private void launchSignInActivity(String name, String email, String college) {
        Intent intent = new Intent(LoginActivity.this, SignInActivity.class);
        intent.putExtra("Name", name);
        intent.putExtra("Email", email);
        intent.putExtra("c_id", college);
        startActivity(intent);
        finish();
    }

    private void setSpinnerAction() {
        collegeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCollege = (String) parent.getItemAtPosition(position);

                if (!"Select College".equals(selectedCollege)) {
                    sharedPreferences.edit().putString("selected_college", selectedCollege).apply();
                    findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.sign_in_button).setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            }
        });
    }
}
