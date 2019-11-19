package com.example.a56_credit.model;

import com.google.gson.annotations.SerializedName;

public class ServerResponse {
   @SerializedName("UserId")
   String UserId;


   public String getUserId() {
      return UserId;
   }

   public void setUserId(String userId) {
      UserId = userId;
   }

   public ServerResponse(ServerResponse serverResponse) {
      this.UserId = serverResponse.getUserId();
   }
}
