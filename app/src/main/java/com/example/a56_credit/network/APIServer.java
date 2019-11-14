package com.example.a56_credit.network;

import com.example.a56_credit.model.PersonalInformation;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface APIServer {
   @POST("/test")
   Call<Boolean> sendData(@Body PersonalInformation personalInformation);
}