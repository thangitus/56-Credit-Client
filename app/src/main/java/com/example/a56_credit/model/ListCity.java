package com.example.a56_credit.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListCity {
   @SerializedName("LtsItem")
   List<City> cityList;

   public List<City> getCityList() {
      return cityList;
   }

   public void setCityList(List<City> cityList) {
      this.cityList = cityList;
   }

   public ListCity(ListCity listCity) {
      this.cityList = listCity.getCityList();
   }
}
