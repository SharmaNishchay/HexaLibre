package com.darkhex.hexalibre;

import android.util.Log;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Data {
    public static boolean datafetched=false;
    private Retrofit retrofit(String url) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(180, TimeUnit.SECONDS)
                .readTimeout(180, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
                .build();

        return new Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    // Fetch data from the server asynchronously
    public void getBooks(BookFetchCallback callback) {
        ApiService apiService = retrofit("https://backend-ax01.onrender.com/").create(ApiService.class);

        apiService.getData().enqueue(new Callback<List<MyResponse>>() {
            @Override
            public void onResponse(Call<List<MyResponse>> call, Response<List<MyResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MyResponse> data = response.body();
                    List<Book> books = new ArrayList<>();
                    for (MyResponse item : data) {
                        books.add(new Book(item.getP_name(), "https://example.com/book1.jpg"));
                    }
                    callback.onBooksFetched(books);
                } else {
                    Log.d("Data","Failed to get response");
                }
            }

            @Override
            public void onFailure(Call<List<MyResponse>> call, Throwable t) {
                Log.d("Data", "Error: " + t.getMessage());
            }
        });
    }
    public void getColleges(final CollegeFetchCallback callback) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Colleges");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (datafetched) return;
                List<String> pathsList = new ArrayList<>();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    pathsList.add(childSnapshot.getKey());
                }
                datafetched = true;
                Log.d("Data", "Fetched colleges: " + pathsList);
                // Run callback on the main thread
                callback.onCollegeFetched(pathsList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Data", "Error retrieving data: " + databaseError.getMessage());
            }
        });
    }

}
