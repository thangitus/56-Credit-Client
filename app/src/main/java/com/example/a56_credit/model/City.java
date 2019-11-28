package com.example.a56_credit.model;

import com.google.gson.annotations.SerializedName;

public class City {
   // Do not use id because id equals index + 1
   @SerializedName("Title")
   String tittle;

   @SerializedName("ID")
   int id;

   public String getTittle() {
      return tittle;
   }

   public void setTittle(String tittle) {
      this.tittle = tittle;
   }

   public int getId() {
      return id;
   }

   public City(String tittle, int id) {
      this.tittle = tittle;
      this.id = id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public City(City city) {
      this.tittle = city.getTittle();
      this.id = city.getId();
   }
}
