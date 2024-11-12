package com.darkhex.hexalibre;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Data {
    Handler mainHandler = new Handler(Looper.getMainLooper());

    public void Allbooks(List<String> isbns, BookFetchCallback callback) {
        List<Book> books = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(isbns.size());  // Track completion of all requests

        for (String isbn : isbns) {
            showPreview(isbn, new oneBookCallback() {
                @Override
                public void onBooksFetched(String title, String url) {
                    books.add(new Book(title, url));
                    latch.countDown();  // Decrement the latch count when a book is fetched
                }

                @Override
                public void onBook_notFetched() {
                    latch.countDown();
                }
            });
        }

        // Run a separate thread to wait for all requests to complete
        new Thread(() -> {
            try {
                latch.await();  // Wait for all requests to finish
                mainHandler.post(() -> {
                    if (!books.isEmpty()) {
                        callback.onBooksFetched(books);  // Only call callback after all requests are completed
                    }
                });
            }  catch (InterruptedException e) {
                Log.e("Data", "Interrupted while waiting for books to fetch", e);
            }
        }).start();
    }

    public void showPreview(String isbn, oneBookCallback callback) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://openlibrary.org/api/books?bibkeys=ISBN:" + isbn + "&format=json&jscmd=data";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("Data", "Failed to fetch data: " + e.getMessage());
                callback.onBook_notFetched();  // Invoke callback even on failure
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();
                    Log.d("Data", "Success to fetch data for ISBN " + isbn);
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONObject bookData = jsonObject.getJSONObject("ISBN:" + isbn);

                        // Get the title
                        String title = bookData.optString("title", "Unknown Title");
                        Log.d("Data", "Title: " + title);

                        // Get the cover image URL if available
                        String imageUrl = "no url";
                        if (bookData.has("cover")) {
                            imageUrl = bookData.getJSONObject("cover").optString("medium", "no url");
                            Log.d("Data", "Cover Image URL: " + imageUrl);
                        }

                        // Pass the data to the callback
                        callback.onBooksFetched(title, imageUrl);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("Data", "Error parsing book details: " + e.getMessage());
                        callback.onBook_notFetched();  // Handle parsing failure
                    }
                } else {
                    callback.onBooksFetched("Unknown Title", "no url");  // Handle non-successful response
                }
            }
        });
    }

}
