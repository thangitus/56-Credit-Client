package com.example.a56_credit.network;

import com.example.a56_credit.manager.Constants;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServerNetwork {
   private static volatile ServerNetwork mInstance = null;
   private Retrofit retrofit;

   private ServerNetwork() {
      OkHttpClient client = new OkHttpClient.Builder().build();
      retrofit = new Retrofit.Builder()
              .baseUrl(Constants.APIServerURL)
              .client(client)
              .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
              .build();
   }

   public Retrofit getRetrofit() {
      return retrofit;
   }

   public static ServerNetwork getInstance() {
      if (mInstance == null)
         mInstance = new ServerNetwork();
      return mInstance;
   }
}