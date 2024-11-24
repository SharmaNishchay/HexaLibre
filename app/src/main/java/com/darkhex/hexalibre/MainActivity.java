package com.darkhex.hexalibre;

import android.content.Intent;

import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;


import com.darkhex.hexalibre.databinding.ActivityMainBinding;
import com.darkhex.hexalibre.ui.home.HomeFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "MainActivity";
    private boolean doubleBackToExitPressedOnce = false;
    private Toast exitToast;
    public String UserId;
    private String C_Id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        com.darkhex.hexalibre.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent i=getIntent();
        UserId=i.getStringExtra("uid");
        C_Id=i.getStringExtra("c_id");
        NavigationView navigationView = binding.navView;
        View nav_header = navigationView.getHeaderView(0);
        // Profile setup
        TextView disp_name = nav_header.findViewById(R.id.display_name);
        TextView E_mail = nav_header.findViewById(R.id.E_mail);
        String url = getSharedPreferences("CollegePrefs", MODE_PRIVATE).getString("profile_pic", null);
        disp_name.setText(getSharedPreferences("CollegePrefs", MODE_PRIVATE).getString("Name", null));
        E_mail.setText(getSharedPreferences("CollegePrefs", MODE_PRIVATE).getString("E-mail", null));
        String qr = getSharedPreferences("CollegePrefs", MODE_PRIVATE).getString("Name", null);
        new ProfileActivity(nav_header,binding.appBarMain.btnMenu, url, qr);

        // Sign-out button logic (part 1)
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);

        //================== Navigation ==============================
        setSupportActionBar(binding.appBarMain.toolbar);
        getSupportActionBar().setTitle("");  // Remove title text from toolbar
        EditText search=binding.appBarMain.searchBar1;
        search.setOnFocusChangeListener((v, hasFocus) -> {
            Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
            if (navHostFragment != null) {
                Fragment currentFragment = navHostFragment.getChildFragmentManager().getPrimaryNavigationFragment();
                if (currentFragment instanceof HomeFragment) {
                    // Pass the search query to HomeFragment
                    if (hasFocus) {
                        ((HomeFragment) currentFragment).toggleCategory(true);
                    } else {
                        ((HomeFragment) currentFragment).toggleCategory(false);
                    }

                }
            }

        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
                if (navHostFragment != null) {
                    Fragment currentFragment = navHostFragment.getChildFragmentManager().getPrimaryNavigationFragment();
                    if (currentFragment instanceof HomeFragment) {
                        // Pass the search query to HomeFragment
                        ((HomeFragment) currentFragment).filterData(s.toString());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        DrawerLayout drawer = binding.drawerLayout;
        ImageButton menuButton = findViewById(R.id.btn_menu);
        menuButton.setOnClickListener(v -> {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                EditText search=binding.appBarMain.searchBar1;
                if (id == R.id.nav_sign_out) {
                    sign_out();
                } else if (id==R.id.nav_home) {
                    search.setVisibility(View.VISIBLE);
                    NavigationUI.onNavDestinationSelected(item, navController);
                } else {
                    // Let the Navigation Component handle other navigation items
                    search.setVisibility(View.GONE);
                    NavigationUI.onNavDestinationSelected(item, navController);
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    public void BookDescriptionService(View activity,Book book){
        Intent intent=new Intent(activity.getContext(),BookDescription.class);
        intent.putExtra("ISBN",book.isbn);
        intent.putExtra("User-Id",UserId);
        intent.putExtra("C-Id",C_Id);
        activity.getContext().startActivity(intent);
    }

    public void sign_out(){
        mGoogleSignInClient.signOut().addOnCompleteListener(MainActivity.this, task -> {
            MainActivity.this.startActivity(new Intent(MainActivity.this, LoginActivity.class));
            MainActivity.this.finish();
        });
    }
    @Override
    public void onBackPressed() {
        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        if (navHostFragment != null) {
            Fragment currentFragment = navHostFragment.getChildFragmentManager().getPrimaryNavigationFragment();
            if (currentFragment instanceof HomeFragment) {
                ((HomeFragment) currentFragment).onBackPressed();
            }
            else {
                super.onBackPressed();
            }
        }
    }

    public void handleDoubleBackPress() {
        if (doubleBackToExitPressedOnce) {
            if (exitToast != null) {
                exitToast.cancel(); // Cancel the previous toast
            }
            super.onBackPressed(); // Exit the app
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        exitToast = Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT);
        exitToast.show();

        // Reset the flag after 2 seconds
        new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View currentFocus = getCurrentFocus();
            if (currentFocus instanceof EditText) {
                int[] location = new int[2];
                currentFocus.getLocationOnScreen(location);

                float x = event.getRawX() + currentFocus.getLeft() - location[0];
                float y = event.getRawY() + currentFocus.getTop() - location[1];

                if (x < currentFocus.getLeft() || x >= currentFocus.getRight() ||
                        y < currentFocus.getTop() || y >= currentFocus.getBottom()) {
                    currentFocus.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
    public void set_history(HistoryCallback callback){
        IssueService s=new IssueService();
        s.getHistoryValues(UserId, C_Id, new HistoryCallback() {
            @Override
            public void onHistoryFetched(List<History> histories) {
                callback.onHistoryFetched(histories);
            }

            @Override
            public void onHistory_notFetched() {
                callback.onHistory_notFetched();
            }
        });
    }
}