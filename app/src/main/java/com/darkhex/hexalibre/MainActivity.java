package com.darkhex.hexalibre;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.darkhex.hexalibre.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.darkhex.hexalibre.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);
        View nav_header=binding.navView.getHeaderView(0);

        //Header
        ImageView profile= nav_header.findViewById(R.id.imageView);
        TextView disp_name=nav_header.findViewById(R.id.display_name);
        TextView E_mail=nav_header.findViewById(R.id.E_mail);
        String url = getSharedPreferences("CollegePrefs", MODE_PRIVATE).getString("profile_pic", null);
        disp_name.setText(getSharedPreferences("CollegePrefs", MODE_PRIVATE).getString("Name", null));
        E_mail.setText(getSharedPreferences("CollegePrefs", MODE_PRIVATE).getString("E-mail", null));
        ImageButton qr_icon=nav_header.findViewById(R.id.qr_icon);

        Log.d(TAG, "Test url: "+url);
        QRCodeGenerator qrs=new QRCodeGenerator();
        float density = MainActivity.this.getResources().getDisplayMetrics().density;
        Bitmap qr=qrs.generateQRCode(getSharedPreferences("CollegePrefs", MODE_PRIVATE).getString("Name", null),200,200);
        Glide.with(this)
                .load(qr)
                .circleCrop()
                .into(qr_icon);


        if(!isValidUrl(url)&&url!=null){
            Bitmap uri=createInitialsImage(url);
            Glide.with(this)
                    .load(uri)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .circleCrop()
                    .into(profile);
        }
        else {
            Glide.with(this)
                    .load(url)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .circleCrop()
                    .into(profile);
        }
        setSupportActionBar(binding.appBarMain.toolbar);
        ImageButton btn=binding.appBarMain.iconButton;

        btn.setOnClickListener(v -> {
            mGoogleSignInClient.signOut().addOnCompleteListener(MainActivity.this, task -> {
                MainActivity.this.startActivity(new Intent(MainActivity.this, LoginActivity.class));
                MainActivity.this.finish();
            });
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    public boolean isValidUrl(String urlString) {
        if (urlString == null || urlString.isEmpty()) {
            return false;
        }

        try {
            URL url = new URL(urlString);
            // Optionally check if the URL has a valid protocol (http or https)
            return url.getProtocol().equals("http") || url.getProtocol().equals("https");
        } catch (MalformedURLException e) {
            return false; // Return false if the URL is malformed
        }
    }
    private Bitmap createInitialsImage(String displayName) {

        String[] parts = displayName.split(" ");
        StringBuilder initials1 = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                initials1.append(part.charAt(0));
            }
        }
        String initials;
        if(initials1.length()>=2) {
            initials = initials1.substring(0, 2).toUpperCase();
        }
        else{
            initials = initials1.substring(0, 1).toUpperCase();
        }
        int width = 100; // Image width
        int height = 100; // Image height
        float width1= (float) width /2;
        float height1= (float) height /2;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        // Background color
        paint.setColor(generateRandomDarkColor()); // Change as needed
        canvas.drawCircle(width1, height1, width1, paint);

        // Text color
        paint.setColor(Color.WHITE);
        paint.setTextSize(40);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(initials, width1, height1 + 15, paint);

        return bitmap;
    }
    public int generateRandomDarkColor() {
        Random random = new Random();

        // Generate dark colors by limiting the maximum RGB values
        int red = random.nextInt(128);   // 0-127 for dark red
        int green = random.nextInt(128); // 0-127 for dark green
        int blue = random.nextInt(128);  // 0-127 for dark blue

        return Color.rgb(red, green, blue);
    }
}