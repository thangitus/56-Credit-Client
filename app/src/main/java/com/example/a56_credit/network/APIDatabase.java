package com.example.a56_credit.network;

import com.example.a56_credit.model.City;
import com.example.a56_credit.model.ListCity;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface APIDatabase {
   @GET("/api/city")
   Call<ListCity> getListCity();

   @GET
   Call<List<City>> getListDistrict(@Url String url);
}
