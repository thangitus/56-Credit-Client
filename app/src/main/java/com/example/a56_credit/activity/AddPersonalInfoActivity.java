package com.example.a56_credit.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.example.a56_credit.R;

public class AddPersonalInfoActivity extends AppCompatActivity {
   EditText edtFullName, edtIdNumber, edtBirthday, edtBuildingNumber, edtWards;
   ConstraintLayout layoutFillBirthdayInput;
   Intent intent;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_add_personal_info);
      mapping();
      changeBackground();
      intent = getIntent();

   }

   private void mapping() {
      edtFullName = findViewById(R.id.editTextFillFullName);
      edtIdNumber = findViewById(R.id.editTextFillIdNumber);
      edtBirthday = findViewById(R.id.editTextFillBirthday);
      edtBuildingNumber = findViewById(R.id.editTextFillBuildingNumber);
      edtWards = findViewById(R.id.editTextFillWards);
      layoutFillBirthdayInput=findViewById(R.id.linearFillBirthdayInput);
   }
   private void changeBackground(){
      edtFullName.getBackground().setAlpha(40);
      edtIdNumber.getBackground().setAlpha(40);
      layoutFillBirthdayInput.getBackground().setAlpha(40);
      edtBuildingNumber.getBackground().setAlpha(40);
      edtWards.getBackground().setAlpha(40);
   }
}
