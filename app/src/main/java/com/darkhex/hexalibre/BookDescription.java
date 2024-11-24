package com.darkhex.hexalibre;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BookDescription extends AppCompatActivity {
    private TextView Title;
    private Button hold;
    private Button issue;
    private Button cancel;
    private ImageView cover;
    private LottieAnimationView progressbar;
    private String User_Id;
    private String C_Id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_book_details);

        // Initialize views
        Title = findViewById(R.id.bookTitle);
        issue =findViewById(R.id.issueButton);
        hold = findViewById(R.id.holdButton);
        cancel = findViewById(R.id.Button_cancel);
        cover = findViewById(R.id.bookImage);
        progressbar=findViewById(R.id.image_loading_animation2);

        // Get ISBN from Intent
        Intent i = getIntent();
        String isbn = i.getStringExtra("ISBN");
        User_Id=i.getStringExtra("User-Id");
        C_Id=i.getStringExtra("C-Id");

        // Fetch book details
        Data d = new Data();
        d.showPreview(isbn, new oneBookCallback() {
            @Override
            public void onBooksFetched(String title, String url) {
                // Update title
                runOnUiThread(() -> {
                    Title.setText(title);
                    Title.setVisibility(View.GONE);
                    cover.setVisibility(View.GONE);
                    issue.setOnClickListener(v->{
                        showConfirmationDialog(title,isbn);
                    });
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
                                        progressbar.setVisibility(View.GONE);
                                        Title.setVisibility(View.VISIBLE);
                                        cover.setVisibility(View.VISIBLE);
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
            intent.putExtra("uid", User_Id);
            intent.putExtra("c_id", C_Id);
            startActivity(intent);
        });
        hold.setOnClickListener(v->{
            IssueService issue=new IssueService();
//            issue.getHistoryValues("1@230102059","1");
        });

    }

    private void showConfirmationDialog(String bookName,String isbn) {
        new AlertDialog.Builder(this)
                .setTitle("Book Found")
                .setMessage("Do you want to select the book: " + bookName + "?")
                .setPositiveButton("Yes", (dialog, which) -> {

                    // Get current date and date after 14 days
                    LocalDate currentDate = LocalDate.now();
                    LocalDate dueDate = currentDate.plusDays(14);

                    // Format dates
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    String formattedCurrentDate = currentDate.format(formatter);
                    String formattedDueDate = dueDate.format(formatter);
                    IssueService issue=new IssueService();
                    History history=new History(isbn,formattedCurrentDate,formattedDueDate,"Issued");
                    issue.Issue(C_Id,User_Id,history);
                    Toast.makeText(this, "Book issued: " + bookName, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    // Helper method to validate URL
    private boolean isValidUrl(String url) {
        return url != null && url.startsWith("http");
    }
}
