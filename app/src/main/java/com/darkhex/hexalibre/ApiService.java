package com.darkhex.hexalibre;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("https://backend-ax01.onrender.com/items")  // Your Node.js endpoint
    Call<List<MyResponse>> getData();
}
