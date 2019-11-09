package com.example.a56_credit.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.a56_credit.R;
import com.example.a56_credit.model.PersonalInformation;

public class HomeActivity extends AppCompatActivity {
   ConstraintLayout constraintLayoutCMND, constraintLayoutAddInfo, constraintLayoutInfo;

   PersonalInformation personalInformation;
   Intent intentToAddInfo;

   @SuppressLint("ClickableViewAccessibility")
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_home);
      mapping();
      if (personalInformation == null) constraintLayoutAddInfo.setVisibility(View.GONE);
      intentToAddInfo = new Intent(this, AddPersonalInfoActivity.class);
      constraintLayoutInfo.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            startActivity(intentToAddInfo);
         }
      });
   }

   private void mapping() {
      constraintLayoutCMND = findViewById(R.id.layoutCMND);
      constraintLayoutInfo = findViewById(R.id.layoutInfo);
      constraintLayoutAddInfo = findViewById(R.id.layoutAddInfo);
   }
}
