package com.darkhex.hexalibre;

import android.content.Intent;

import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
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


public class MainActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "MainActivity";
    private boolean doubleBackToExitPressedOnce = false;
    private Toast exitToast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        com.darkhex.hexalibre.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

}