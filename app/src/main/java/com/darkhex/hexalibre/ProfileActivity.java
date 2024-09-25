package com.darkhex.hexalibre;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;


public class ProfileActivity extends AppCompatActivity {

    public ProfileActivity(View nav_header,String url,String q){
        //profile setup
        ImageView profile = nav_header.findViewById(R.id.imageView);


        //Qr setup
        ImageButton qr_icon = nav_header.findViewById(R.id.qr_icon);
        QRCodeGenerator qrs = new QRCodeGenerator();
        float density = nav_header.getContext().getResources().getDisplayMetrics().density;
        Bitmap qr = qrs.generateQRCode(q, 200, 200);
        Glide.with(nav_header.getContext()).load(qr).circleCrop().into(qr_icon);

        //profile pic setup
        if (!isValidUrl(url) && url != null) {
            Bitmap uri = createInitialsImage(url);
            Glide.with(nav_header.getContext()).load(uri).placeholder(R.mipmap.ic_launcher_round).circleCrop().into(profile);
        } else {
            Glide.with(nav_header.getContext()).load(url).placeholder(R.mipmap.ic_launcher_round).circleCrop().into(profile);
        }

    }

    //Check Url
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

    //Profile picture generator

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
