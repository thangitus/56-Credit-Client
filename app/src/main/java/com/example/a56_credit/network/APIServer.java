package com.example.a56_credit.network;

import com.example.a56_credit.model.PersonalInformation;
import com.example.a56_credit.model.ServerResponse;
import com.example.a56_credit.model.StatusResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface APIServer {

   @POST("/register")
   Call<ServerResponse> sendData(@Body PersonalInformation personalInformation);
   @GET
   Call<StatusResponse> getStatus(@Url String url);
}