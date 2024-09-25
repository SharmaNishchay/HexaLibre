package com.darkhex.hexalibre;
import com.darkhex.hexalibre.R;

import android.annotation.SuppressLint;
import android.content.Intent;

import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import com.darkhex.hexalibre.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "MainActivity";
    private AppBarConfiguration appBarConfiguration;
    private ActionBarDrawerToggle toggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.darkhex.hexalibre.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavigationView navigationView = binding.navView;
        View nav_header=navigationView.getHeaderView(0);
        //profile Setup
        TextView disp_name = nav_header.findViewById(R.id.display_name);
        TextView E_mail = nav_header.findViewById(R.id.E_mail);
        String url = getSharedPreferences("CollegePrefs", MODE_PRIVATE).getString("profile_pic", null);
        disp_name.setText(getSharedPreferences("CollegePrefs", MODE_PRIVATE).getString("Name", null));
        E_mail.setText(getSharedPreferences("CollegePrefs", MODE_PRIVATE).getString("E-mail", null));
        String qr = getSharedPreferences("CollegePrefs", MODE_PRIVATE).getString("Name", null);
        new ProfileActivity(nav_header, url, qr);
        // Sign-out button logic(part1)
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);


        //==================Navigation==============================
        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        toggle = new ActionBarDrawerToggle(this, drawer, binding.appBarMain.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_sign_out) {
                    sign_out();
                } else {
                    // Let the Navigation Component handle other navigation items
                    NavigationUI.onNavDestinationSelected(item, navController);
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState(); // Sync the toggle after onCreate
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle ActionBar item clicks
        if (toggle.onOptionsItemSelected(item)) {
            return true; // Toggle drawer if hamburger icon is clicked
        }
        return super.onOptionsItemSelected(item);
    }


    public void sign_out(){
        mGoogleSignInClient.signOut().addOnCompleteListener(MainActivity.this, task -> {
            MainActivity.this.startActivity(new Intent(MainActivity.this, LoginActivity.class));
            MainActivity.this.finish();
        });
    }


}