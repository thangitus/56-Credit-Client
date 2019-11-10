package com.example.a56_credit.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.a56_credit.R;
import com.example.a56_credit.model.PersonalInformation;

public class HomeActivity extends AppCompatActivity {
   private static final int REQUEST_CODE = 1;
   ConstraintLayout constraintLayoutCMND, constraintLayoutAddInfo, constraintLayoutInfo;
   TextView tvFullName, tvIdNumber, tvBirthday, tvBuildingNumber, tvWards, tvProvince, tvDistrict;
   TextView tvButtonEdit;
   PersonalInformation personalInformation;
   Intent intentToAddInfo;

   @SuppressLint("ClickableViewAccessibility")
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_home);
      mapping();
      if (personalInformation == null) {
         constraintLayoutAddInfo.setVisibility(View.GONE);
         tvButtonEdit.setVisibility(View.GONE);
      }
      intentToAddInfo = new Intent(this, AddPersonalInfoActivity.class);
      constraintLayoutInfo.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            if (personalInformation == null){
               intentToAddInfo.putExtra("isEdit",false);
               startActivityForResult(intentToAddInfo, REQUEST_CODE);
            }
         }
      });
      tvButtonEdit.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            intentToAddInfo.putExtra("isEdit",true);
            intentToAddInfo.putExtra("info",personalInformation);
            startActivityForResult(intentToAddInfo,REQUEST_CODE);
         }
      });
   }

   private void mapping() {
      constraintLayoutCMND = findViewById(R.id.layoutCMND);
      constraintLayoutInfo = findViewById(R.id.layoutInfo);
      constraintLayoutAddInfo = findViewById(R.id.layoutAddInfo);
      tvFullName = findViewById(R.id.textViewFullName);
      tvBirthday = findViewById(R.id.textViewBirthday);
      tvWards = findViewById(R.id.textViewWards);
      tvDistrict = findViewById(R.id.textViewDistrict);
      tvIdNumber = findViewById(R.id.textViewIdNumber);
      tvBuildingNumber = findViewById(R.id.textViewBuildingNumber);
      tvProvince = findViewById(R.id.textViewProvince);
      tvButtonEdit = findViewById(R.id.tvButtonEdit);
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      if ((requestCode == REQUEST_CODE) && (resultCode == Activity.RESULT_OK)) {
         personalInformation = data.getParcelableExtra("info");
         tvFullName.setText(personalInformation.getFullName());
         tvIdNumber.setText(personalInformation.getIdNumber());
         tvProvince.setText(personalInformation.getProvince());
         tvBuildingNumber.setText(personalInformation.getBuildingNumber());
         tvBirthday.setText(personalInformation.getBirthday());
         tvDistrict.setText(personalInformation.getDistrict());
         tvWards.setText(personalInformation.getWards());
         constraintLayoutAddInfo.setVisibility(View.VISIBLE);
         tvButtonEdit.setVisibility(View.VISIBLE);
      }
   }
}
