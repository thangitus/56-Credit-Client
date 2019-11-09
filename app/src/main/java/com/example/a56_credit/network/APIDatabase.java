package com.example.a56_credit.network;

import com.example.a56_credit.model.ListCity;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface APIDatabase {
   @GET("/api/city")
   Call<ListCity> getListCity();

   @GET
   Call<ListCity> getListDistrict(@Url String url);
}
