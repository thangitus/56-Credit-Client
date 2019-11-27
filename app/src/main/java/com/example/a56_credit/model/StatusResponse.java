package com.example.a56_credit.model;

import com.google.gson.annotations.SerializedName;

public class StatusResponse {
   @SerializedName("Status")
   String status;

   public String getStatus() {
      return status;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public StatusResponse(StatusResponse statusResponse) {
      this.status = statusResponse.getStatus();
   }
}
