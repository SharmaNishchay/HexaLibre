package com.darkhex.hexalibre;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class BookDescription extends AppCompatActivity {
    private TextView Title;
    private Button hold;
    private Button cancel;
    private ImageView cover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_book_details);

        // Initialize views
        Title = findViewById(R.id.bookTitle);
        hold = findViewById(R.id.holdButton);
        cancel = findViewById(R.id.Button_cancel);
        cover = findViewById(R.id.bookImage);

        // Get ISBN from Intent
        Intent i = getIntent();
        String isbn = i.getStringExtra("ISBN");
        Log.d("Description", "ISBN: " + isbn);

        // Fetch book details
        Data d = new Data();
        d.showPreview(isbn, new oneBookCallback() {
            @Override
            public void onBooksFetched(String title, String url) {
                // Update title
                runOnUiThread(() -> {
                    Title.setText(title);

                    // Load image using Glide
                    if (isValidUrl(url)) {
                        Glide.with(BookDescription.this)
                                .load(url)
                                .placeholder(R.drawable.placeholder_image) // Placeholder image
                                .error(R.drawable.placeholder_image) // Error image
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        if (e != null) e.logRootCauses("GlideError");
                                        Log.d("GlideError in Description", "Image load failed for URL: " + url + " Error: " + e);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        Log.d("Description", "Image loaded successfully for URL: " + url);
                                        return false;
                                    }
                                })
                                .into(cover);
                    } else {
                        Log.d("Description", "Invalid URL: " + url);
                    }
                });
            }

            @Override
            public void onBook_notFetched() {
                runOnUiThread(() -> {
                    Title.setText("Book details not available");
                    Log.d("Description", "Failed to fetch book details for ISBN: " + isbn);
                });
            }
        });

        // Cancel button action
        cancel.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }

    // Helper method to validate URL
    private boolean isValidUrl(String url) {
        return url != null && url.startsWith("http");
    }
}
