package com.example.a56_credit.model;

import com.google.gson.annotations.SerializedName;

public class City {
   // Do not use id because id equals index + 1
   @SerializedName("Title")
   String tittle;

   public String getTittle() {
      return tittle;
   }

   public void setTittle(String tittle) {
      this.tittle = tittle;
   }
}
