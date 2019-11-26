package com.example.a56_credit.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.example.a56_credit.R;
import com.example.a56_credit.model.PersonalInformation;
import com.example.a56_credit.model.ServerResponse;
import com.example.a56_credit.network.APIServer;
import com.example.a56_credit.network.ServerNetwork;

import java.io.ByteArrayOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
   private static final int REQUEST_CODE_INFO = 1, REQUEST_CODE_CAMERA_BACK = 2, REQUEST_CODE_CAMERA_FRONT = 3;
   private static final String TAG = "HomeActivity";
   ConstraintLayout constraintLayoutCMND, constraintLayoutAddInfo, constraintLayoutInfo, constraintLayoutSelfie;
   TextView tvFullName, tvIdNumber, tvBirthday, tvBuildingNumber, tvWards, tvProvince, tvDistrict;
   TextView tvButtonEdit, tvReIdenty, tvReSelfie;
   Button buttonSend;
   PersonalInformation personalInformation;
   Intent intentToAddInfo, intentCameraBack, intentCameraFront;
   ImageView imgCMND, imgSelfie;
   Bitmap bitmapCMND, bitmapSelfie;
   int step = 0;
   Dialog dialogUpload;
   LottieAnimationView lottieAnimationView;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_home);
      mapping();
      disableButtonSend();
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
            if (personalInformation.getIdNumber().equals("")) {
               intentToAddInfo.putExtra("isEdit", false);
               startActivityForResult(intentToAddInfo, REQUEST_CODE_INFO);
            }
         }
      });
      tvButtonEdit.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            step--;
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
            step--;
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
            step--;
            openCameraFront();
         }
      });
      buttonSend.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            showDialogProgress();
            sendData(personalInformation);
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

   private void disableButtonSend() {
      buttonSend.getBackground().setAlpha(100);
      buttonSend.setEnabled(false);

   }

   private void enableButtonSend() {
      buttonSend.getBackground().setAlpha(255);
      buttonSend.setEnabled(true);
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
      buttonSend = findViewById(R.id.buttonSend);
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
         if (data.getBooleanExtra("hasData", false)) {
            personalInformation = data.getParcelableExtra("info");
            step++;
            if (step == 3)
               enableButtonSend();
            setInfo();
         }
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
            step++;
            if (step == 3)
               enableButtonSend();
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
            matrix.preScale(1, -1);
            bitmapSelfie = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            new Thread(new Runnable() {
               @Override
               public void run() {
                  saveBitmap(bitmapSelfie, "selfie");
               }
            }).start();
            step++;
            if (step == 3)
               enableButtonSend();
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
      Log.wtf("HomeActivity", "Save Bitmap Start " + key);
      bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 0.1), (int) (bitmap.getHeight() * 0.1), true);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
      byte[] bytes = baos.toByteArray();
      String encoded = Base64.encodeToString(bytes, Base64.NO_WRAP);
      if (key.equals("identity"))
         personalInformation.setIdentity(encoded);
      else
         personalInformation.setSelfie(encoded);
      Log.wtf("HomeActivity", "Save Bitmap Done");
   }

   private void sendData(PersonalInformation personalInformation) {
//      personalInformation.setFullName("NGUYỄN VĂN THẮNG");
//      personalInformation.setIdNumber("245401302");
//      personalInformation.setBirthday("20-05-1999");
//      personalInformation.setHomeTown("Nam Định");
//      personalInformation.setProvince("TP Hồ Chí Minh");
//      personalInformation.setDistrict("Quận 8");
//      personalInformation.setPhoneNumber("0352846131");
      APIServer apiServer = ServerNetwork.getInstance().getRetrofit().create(APIServer.class);
      Call<ServerResponse> call = apiServer.sendData(personalInformation);
      call.enqueue(new Callback<ServerResponse>() {
         @Override
         public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
            lottieAnimationView.cancelAnimation();
            dialogUpload.dismiss();
            Toast.makeText(HomeActivity.this, "" + response.body().getUserId(), Toast.LENGTH_SHORT).show();
         }

         @Override
         public void onFailure(Call<ServerResponse> call, Throwable t) {
            dialogUpload.dismiss();
            Log.wtf("HomeActivity", "onFailure");
         }
      });

   }

   private void showDialogProgress() {
      dialogUpload = new Dialog(this);
      LayoutInflater inflater = this.getLayoutInflater();
      View view = inflater.inflate(R.layout.upload_dialog, null);
      dialogUpload.setContentView(view);
      dialogUpload.setCancelable(false);
      dialogUpload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
      lottieAnimationView = view.findViewById(R.id.progressLoading);
      lottieAnimationView.playAnimation();
      dialogUpload.show();
   }

}
