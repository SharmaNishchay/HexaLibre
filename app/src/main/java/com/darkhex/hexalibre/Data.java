package com.darkhex.hexalibre;

import android.content.Context;
import android.widget.Toast;

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

    private final Context context;

    // Constructor to pass the context
    public Data(Context context) {
        this.context = context;
    }


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
                    Toast.makeText(context, "Failed to get response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<MyResponse>> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    public void getColleges(CollegeFetchCallback callback){
        ApiService_colleges apiService = retrofit("https://application-1odo.onrender.com/").create(ApiService_colleges.class);
        apiService.getData().enqueue(new Callback<List<MyResponse>>() {
            @Override
            public void onResponse(Call<List<MyResponse>> call, Response<List<MyResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MyResponse> data = response.body();
                    List<String> col = new ArrayList<>();
                    for (MyResponse item : data) {
                        col.add(item.get_name());
                    }
                    callback.onCollegeFetched(col);
                } else {
                    Toast.makeText(context, "Failed to get response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<MyResponse>> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
