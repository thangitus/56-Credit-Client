package com.example.a56_credit.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class PersonalInformation implements Parcelable {

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
   @SerializedName("Fullname")
   String fullName;
   @SerializedName("IdentityNumber")
   String idNumber;
   @SerializedName("Birthday")
   String birthday;
   String gender;
   @SerializedName("Hometown")
   String homeTown;
   @SerializedName("BuildingNumber")
   String buildingNumber;
   @SerializedName("Wards")
   String wards;
   @SerializedName("Province")
   String province;
   @SerializedName("District")
   String district;
   @SerializedName("IDCardImage")
   String identity;
   @SerializedName("SelfieImage")
   String selfie;
   @SerializedName("PhoneNumber")
   String phoneNumber;

   public PersonalInformation(String fullName, String idNumber, String birthday, String gender, String homeTown, String buildingNumber, String wards, String province, String district, String identity, String selfie, String phoneNumber) {
      this.fullName = fullName;
      this.idNumber = idNumber;
      this.birthday = birthday;
      this.gender = gender;
      this.homeTown = homeTown;
      this.buildingNumber = buildingNumber;
      this.wards = wards;
      this.province = province;
      this.district = district;
      this.identity = identity;
      this.selfie = selfie;
      this.phoneNumber = phoneNumber;
   }

   public PersonalInformation() {
      this.fullName = "";
      this.idNumber = "";
      this.birthday = "";
      this.gender = "";
      this.homeTown = "";
      this.buildingNumber = "";
      this.wards = "";
      this.province = "";
      this.district = "";
      this.identity = "";
      this.selfie = "";
      this.phoneNumber = "";
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
      identity = in.readString();
      selfie = in.readString();
      phoneNumber = in.readString();
   }

   public PersonalInformation(PersonalInformation personalInformation) {
      this.fullName = personalInformation.getFullName();
      this.idNumber = personalInformation.getIdNumber();
      this.birthday = personalInformation.getBirthday();
      this.gender = personalInformation.getGender();
      this.homeTown = personalInformation.getHomeTown();
      this.buildingNumber = personalInformation.getBuildingNumber();
      this.wards = personalInformation.getWards();
      this.province = personalInformation.getProvince();
      this.district = personalInformation.getDistrict();
      this.identity = personalInformation.getIdentity();
      this.selfie = personalInformation.getSelfie();
      this.phoneNumber = personalInformation.getPhoneNumber();
   }

   @Override
   public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(fullName);
      dest.writeString(idNumber);
      dest.writeString(birthday);
      dest.writeString(gender);
      dest.writeString(homeTown);
      dest.writeString(buildingNumber);
      dest.writeString(wards);
      dest.writeString(province);
      dest.writeString(district);
      dest.writeString(identity);
      dest.writeString(selfie);
      dest.writeString(phoneNumber);
   }

   @Override
   public int describeContents() {
      return 0;
   }

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

   public String getIdentity() {
      return identity;
   }

   public void setIdentity(String identity) {
      this.identity = identity;
   }

   public String getSelfie() {
      return selfie;
   }

   public void setSelfie(String selfie) {
      this.selfie = selfie;
   }

   public String getPhoneNumber() {
      return phoneNumber;
   }

   public void setPhoneNumber(String phoneNumber) {
      this.phoneNumber = phoneNumber;
   }
}
