package com.example.a56_credit.network;

import com.example.a56_credit.manager.Constants;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class NetworkProviderDatabase {
   private static volatile NetworkProviderDatabase mInstance = null;
   private Retrofit retrofit;

   private NetworkProviderDatabase() {
      OkHttpClient client = new OkHttpClient.Builder().build();
      retrofit = new Retrofit.Builder()
              .baseUrl(Constants.APIDatabase)
              .client(client)
              .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
              .build();
   }

   public Retrofit getRetrofit() {
      return retrofit;
   }

   public static NetworkProviderDatabase getInstance(){
      if(mInstance==null)
         mInstance=new NetworkProviderDatabase();
      return mInstance;
   }
}
