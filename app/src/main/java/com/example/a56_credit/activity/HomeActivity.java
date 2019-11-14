package com.example.a56_credit.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a56_credit.R;
import com.example.a56_credit.model.PersonalInformation;

import java.io.ByteArrayOutputStream;

public class HomeActivity extends AppCompatActivity {
   private static final int REQUEST_CODE_INFO = 1, REQUEST_CODE_CAMERA_BACK = 2, REQUEST_CODE_CAMERA_FRONT = 3;
   ConstraintLayout constraintLayoutCMND, constraintLayoutAddInfo, constraintLayoutInfo, constraintLayoutSelfie;
   TextView tvFullName, tvIdNumber, tvBirthday, tvBuildingNumber, tvWards, tvProvince, tvDistrict;
   TextView tvButtonEdit, tvReIdenty, tvReSelfie;
   PersonalInformation personalInformation;
   Intent intentToAddInfo, intentCameraBack, intentCameraFront;
   ImageView imgCMND, imgSelfie;
   Bitmap bitmapCMND, bitmapSelfie;
   SharedPreferences mPrefs;

   //   @SuppressLint("WrongThread")
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_home);
      mapping();
      Log.wtf("abc", "onCreate");
      mPrefs = getSharedPreferences("data", MODE_PRIVATE);

      if (personalInformation == null) {
         constraintLayoutAddInfo.setVisibility(View.GONE);
         tvButtonEdit.setVisibility(View.GONE);
      }
      setupIMG();
      personalInformation = new PersonalInformation();
      intentToAddInfo = new Intent(this, AddPersonalInfoActivity.class);
      constraintLayoutInfo.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            if (personalInformation.getIdNumber() == null) {
               intentToAddInfo.putExtra("isEdit", false);
               startActivityForResult(intentToAddInfo, REQUEST_CODE_INFO);
            }
         }
      });
      tvButtonEdit.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            editInfo();
         }
      });
      constraintLayoutCMND.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            if (bitmapCMND == null)
               openCameraBack();
         }
      });
      tvReIdenty.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            openCameraBack();
         }
      });
      constraintLayoutSelfie.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            if (bitmapSelfie == null)
               openCameraFront();
         }
      });
      tvReSelfie.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            openCameraFront();
         }
      });
   }

   private void setupIMG() {
      imgCMND.setVisibility(View.GONE);
      tvReIdenty.setVisibility(View.GONE);
      imgSelfie.setVisibility(View.GONE);
      tvReSelfie.setVisibility(View.GONE);

   }

   private void setIMG(ImageView img, Bitmap bitmap) {
      img.setImageBitmap(bitmap);
      img.setVisibility(View.VISIBLE);
   }


   private void mapping() {
      constraintLayoutCMND = findViewById(R.id.layoutCMND);
      constraintLayoutInfo = findViewById(R.id.layoutInfo);
      constraintLayoutAddInfo = findViewById(R.id.layoutAddInfo);
      constraintLayoutSelfie = findViewById(R.id.layoutSelfie);
      tvFullName = findViewById(R.id.textViewFullName);
      tvBirthday = findViewById(R.id.textViewBirthday);
      tvWards = findViewById(R.id.textViewWards);
      tvDistrict = findViewById(R.id.textViewDistrict);
      tvIdNumber = findViewById(R.id.textViewIdNumber);
      tvBuildingNumber = findViewById(R.id.textViewBuildingNumber);
      tvProvince = findViewById(R.id.textViewProvince);
      tvButtonEdit = findViewById(R.id.tvButtonEdit);
      tvReSelfie = findViewById(R.id.reTakePictureSelfie);
      tvReIdenty = findViewById(R.id.reTakePicture);
      imgCMND = findViewById(R.id.imgCMND);
      imgSelfie = findViewById(R.id.imgSelfie);
   }

   private void editInfo() {
      intentToAddInfo.putExtra("isEdit", true);
      intentToAddInfo.putExtra("info", personalInformation);
      startActivityForResult(intentToAddInfo, REQUEST_CODE_INFO);
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      if ((requestCode == REQUEST_CODE_INFO) && (resultCode == Activity.RESULT_OK)) {
         personalInformation = data.getParcelableExtra("info");
         setInfo();
      }

      if ((requestCode == REQUEST_CODE_CAMERA_BACK) && (resultCode == Activity.RESULT_OK)) {
         Boolean hasPhoto = data.getBooleanExtra("hasPhoto", false);
         if (hasPhoto) {
            String path = data.getStringExtra("photo");
            bitmapCMND = BitmapFactory.decodeFile(path);
            new Thread(new Runnable() {
               @Override
               public void run() {
                  saveBitmap(bitmapCMND, "identity");
               }
            }).start();
            setIMG(imgCMND, bitmapCMND);
            tvReIdenty.setVisibility(View.VISIBLE);
         }
      }

      if ((requestCode == REQUEST_CODE_CAMERA_FRONT) && (resultCode == Activity.RESULT_OK)) {
         Boolean hasPhoto = data.getBooleanExtra("hasPhoto", false);
         if (hasPhoto) {
            String path = data.getStringExtra("photo");
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            Matrix matrix = new Matrix();
            matrix.postRotate(-90);
            bitmapSelfie = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            new Thread(new Runnable() {
               @Override
               public void run() {
                  saveBitmap(bitmapSelfie, "selfie");
               }
            }).start();
            setIMG(imgSelfie, bitmapSelfie);
            tvReSelfie.setVisibility(View.VISIBLE);
         }
      }
   }

   private void setInfo() {
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

   private void openCameraBack() {
      intentCameraBack = new Intent(this, CameraBackActivity.class);
      startActivityForResult(intentCameraBack, REQUEST_CODE_CAMERA_BACK);
   }

   private void openCameraFront() {
      intentCameraFront = new Intent(this, CameraFrontActivity.class);
      startActivityForResult(intentCameraFront, REQUEST_CODE_CAMERA_FRONT);
   }

   private void saveBitmap(Bitmap bitmap, String key) {
      SharedPreferences.Editor editor = mPrefs.edit();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
      byte[] bytes = baos.toByteArray();
      String encoded = Base64.encodeToString(bytes, Base64.DEFAULT);
      if (key.equals("identity"))
         personalInformation.setIdentity(encoded);
      else
         personalInformation.setSelfie(encoded);
      editor.putString(key, encoded);
      editor.apply();
   }

   private Bitmap loadBitmap(String key) {
      String encoded = mPrefs.getString(key, null);
      if (encoded == null)
         return null;
      byte[] imageAsBytes = Base64.decode(encoded.getBytes(), Base64.DEFAULT);
      Bitmap bm = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
      return bm;
   }
}
