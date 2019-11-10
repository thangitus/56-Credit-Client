package com.example.a56_credit.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PersonalInformation implements Parcelable {
   String fullName;
   String idNumber;
   String birthday;
   String gender;
   String homeTown;
   String buildingNumber;
   String wards;
   String province;
   String district;

   public PersonalInformation(String fullName, String idNumber, String birthday, String homeTown, String buildingNumber, String wards, String province, String district) {
      this.fullName = fullName;
      this.idNumber = idNumber;
      this.birthday = birthday;
      this.homeTown = homeTown;
      this.buildingNumber = buildingNumber;
      this.wards = wards;
      this.province = province;
      this.district = district;
   }

   public PersonalInformation() {
      this.fullName = null;
      this.idNumber = null;
      this.birthday = null;
      this.gender = null;
      this.homeTown = null;
      this.buildingNumber = null;
      this.wards = null;
      this.province = null;
      this.district = null;
   }

   protected PersonalInformation(Parcel in) {
      fullName = in.readString();
      idNumber = in.readString();
      birthday = in.readString();
      gender = in.readString();
      homeTown = in.readString();
      buildingNumber = in.readString();
      wards = in.readString();
      province = in.readString();
      district = in.readString();
   }

   public static final Creator<PersonalInformation> CREATOR = new Creator<PersonalInformation>() {
      @Override
      public PersonalInformation createFromParcel(Parcel in) {
         return new PersonalInformation(in);
      }

      @Override
      public PersonalInformation[] newArray(int size) {
         return new PersonalInformation[size];
      }
   };

   public String getFullName() {
      return fullName;
   }

   public void setFullName(String fullName) {
      this.fullName = fullName;
   }

   public String getIdNumber() {
      return idNumber;
   }

   public void setIdNumber(String idNumber) {
      this.idNumber = idNumber;
   }

   public String getBirthday() {
      return birthday;
   }

   public void setBirthday(String birthday) {
      this.birthday = birthday;
   }

   public String getGender() {
      return gender;
   }

   public void setGender(String gender) {
      this.gender = gender;
   }

   public String getHomeTown() {
      return homeTown;
   }

   public void setHomeTown(String homeTown) {
      this.homeTown = homeTown;
   }

   public String getBuildingNumber() {
      return buildingNumber;
   }

   public void setBuildingNumber(String buildingNumber) {
      this.buildingNumber = buildingNumber;
   }

   public String getWards() {
      return wards;
   }

   public void setWards(String wards) {
      this.wards = wards;
   }

   public String getProvince() {
      return province;
   }

   public void setProvince(String province) {
      this.province = province;
   }

   public String getDistrict() {
      return district;
   }

   public void setDistrict(String district) {
      this.district = district;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel parcel, int i) {
      parcel.writeString(fullName);
      parcel.writeString(idNumber);
      parcel.writeString(birthday);
      parcel.writeString(gender);
      parcel.writeString(homeTown);
      parcel.writeString(buildingNumber);
      parcel.writeString(wards);
      parcel.writeString(province);
      parcel.writeString(district);
   }
}
