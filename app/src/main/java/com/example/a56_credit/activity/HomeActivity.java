package com.example.a56_credit.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import com.airbnb.lottie.LottieAnimationView;
import com.example.a56_credit.R;
import com.example.a56_credit.databinding.ActivityHomeBinding;
import com.example.a56_credit.model.PersonalInformation;
import com.example.a56_credit.model.ServerResponse;
import com.example.a56_credit.model.StatusResponse;
import com.example.a56_credit.network.APIServer;
import com.example.a56_credit.network.ServerNetwork;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
   private static final int REQUEST_CODE_INFO = 1, REQUEST_CODE_CAMERA_BACK = 2, REQUEST_CODE_CAMERA_FRONT = 3;
   private static final int RATIO_SCALE = 1;
   private static final String TAG = "HomeActivity";
   public static String ID;
   protected PowerManager.WakeLock mWakeLock;
   List<String> preAddressList;
   ConstraintLayout constraintLayoutCMND, constraintLayoutInfo, constraintLayoutSelfie;
   TextView tvButtonEdit, tvReIdentity, tvReSelfie;
   Button buttonSend;
   PersonalInformation personalInformation;
   Intent intentToAddInfo, intentCameraBack, intentCameraFront;
   ImageView imgCMND, imgSelfie;
   Bitmap bitmapCMND, bitmapSelfie;
   int step;
   Dialog dialogUpload, dialogResult;
   LottieAnimationView lottieAnimationView;
   StatusResponse status;
   Boolean isChecked = false;
   String identity, selfie;
   ActivityHomeBinding binding;
   @SuppressLint("HandlerLeak")
   private Handler handler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
         super.handleMessage(msg);
         String type = (String) msg.obj;
         if (type.equals("decoded")) {
            step++;
            Log.wtf(TAG, String.valueOf(step));
            if (step == 3)
               enableButtonSend();
            tvReIdentity.setAlpha(1);
            tvReIdentity.setEnabled(true);
            tvReSelfie.setAlpha(1);
            tvReSelfie.setEnabled(true);
            Log.wtf(TAG, "End decode");

         }
      }
   };

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
      setInfo();
      mapping();
      initPrefix();
      disableButtonSend();
      setupIMG();
      personalInformation = new PersonalInformation();
      intentToAddInfo = new Intent(this, AddPersonalInfoActivity.class);
      constraintLayoutInfo.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            if (personalInformation.getIdNumber().equals("")) {
               intentToAddInfo.putExtra("isEdit", false);
               intentToAddInfo.putExtra("info", personalInformation);
               startActivityForResult(intentToAddInfo, REQUEST_CODE_INFO);
            }
         }
      });
      tvButtonEdit.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            step--;
            disableButtonSend();
            intentToAddInfo.putExtra("isEdit", true);
            intentToAddInfo.putExtra("info", personalInformation);
            personalInformation.setIdentity("");
            personalInformation.setSelfie("");
            startActivityForResult(intentToAddInfo, REQUEST_CODE_INFO);
         }
      });
      constraintLayoutCMND.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            if (bitmapCMND == null)
               openCameraBack();
         }
      });
      tvReIdentity.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            step--;
            disableButtonSend();
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
            disableButtonSend();
            openCameraFront();
         }
      });
      buttonSend.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            showDialogAnimation(R.raw.loading);
            PersonalInformation personalInformationTemp = new PersonalInformation(personalInformation);
            preProcessData(personalInformationTemp);
            sendData(personalInformationTemp);
         }
      });
   }

   private void initPrefix() {
      preAddressList = new ArrayList<>();
      preAddressList.add("Huyện ");
      preAddressList.add("Thị Xã ");
      preAddressList.add("Tỉnh ");
      preAddressList.add("Thành Phố ");
   }

   private void setupIMG() {
      imgCMND.setVisibility(View.GONE);
      tvReIdentity.setVisibility(View.GONE);
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
      constraintLayoutSelfie = findViewById(R.id.layoutSelfie);
      tvButtonEdit = findViewById(R.id.buttonEdit);
      tvReSelfie = findViewById(R.id.reTakePictureSelfie);
      tvReIdentity = findViewById(R.id.reTakePicture);
      imgCMND = findViewById(R.id.imgCMND);
      imgSelfie = findViewById(R.id.imgSelfie);
      buttonSend = findViewById(R.id.buttonSend);
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
            startThreadDecodeBitmap(bitmapCMND, "identity");
            setIMG(imgCMND, bitmapCMND);
            tvReIdentity.setVisibility(View.VISIBLE);

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
            startThreadDecodeBitmap(bitmapSelfie, "selfie");
            setIMG(imgSelfie, bitmapSelfie);
            tvReSelfie.setVisibility(View.VISIBLE);
         }
      }
   }

   private void setInfo() {
      binding.setPersonalInformation(personalInformation);
   }

   private void openCameraBack() {
      intentCameraBack = new Intent(this, CameraBackActivity.class);
      startActivityForResult(intentCameraBack, REQUEST_CODE_CAMERA_BACK);
   }

   private void openCameraFront() {
      intentCameraFront = new Intent(this, CameraFrontActivity.class);
      startActivityForResult(intentCameraFront, REQUEST_CODE_CAMERA_FRONT);
   }

   private void decodeBitmap(Bitmap bitmap, String key) {
      bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * RATIO_SCALE), (int) (bitmap.getHeight() * RATIO_SCALE), true);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
      byte[] bytes = baos.toByteArray();
      String encoded = Base64.encodeToString(bytes, Base64.NO_WRAP);
      if (key.equals("identity"))
         identity = encoded;
      else
         selfie = encoded;
   }

   private void preProcessData(PersonalInformation personalInformation) {
      personalInformation.setIdentity(identity);
      personalInformation.setSelfie(selfie);
      for (String string : preAddressList) {
         personalInformation.setHomeTown(personalInformation.getHomeTown().replace(string, ""));
         personalInformation.setProvince(personalInformation.getProvince().replace(string, ""));
         personalInformation.setDistrict(personalInformation.getDistrict().replace(string, ""));
      }
   }

   private void sendData(PersonalInformation personalInformation) {
      APIServer apiServer = ServerNetwork.getInstance().getRetrofit().create(APIServer.class);
      Call<ServerResponse> call = apiServer.sendData(personalInformation);
      call.enqueue(new Callback<ServerResponse>() {
         @Override
         public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
            dialogUpload.dismiss();
            Log.wtf(TAG, String.valueOf(response.code()));
            if (response.code() == 200) {
               ID = response.body().getUserId();
               Log.wtf(TAG, ID);
               startThreadCheckStatus();
            } else
               showDialogResult("-1");
         }

         @Override
         public void onFailure(Call<ServerResponse> call, Throwable t) {
            showDialogResult("-1");
            dialogUpload.dismiss();
         }
      });

   }

   private void startThreadDecodeBitmap(Bitmap bitmap, String key) {
      if (key.equals("identity")) {
         tvReIdentity.setAlpha(0.5f);
         tvReIdentity.setEnabled(false);
      } else {
         tvReSelfie.setAlpha(0.5f);
         tvReSelfie.setEnabled(false);
      }
      new Thread(new Runnable() {
         @Override
         public void run() {
            Log.wtf(TAG, "Start decode");
            decodeBitmap(bitmap, key);
            Message message = handler.obtainMessage(1, "decoded");
            handler.sendMessage(message);
         }
      }).start();

   }

   private void startThreadCheckStatus() {
      disableButtonSend();
      isChecked = false;
      APIServer apiServer = ServerNetwork.getInstance().getRetrofit().create(APIServer.class);
      String url = "/status?id=" + ID;
      new Thread(new Runnable() {
         @Override
         public void run() {
            while (!isChecked) {
               Call<StatusResponse> call = apiServer.getStatus(url);
               call.enqueue(new Callback<StatusResponse>() {
                  @Override
                  public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                     status = response.body();
                     if (status.getStatus().equals("1") || status.getStatus().equals("-1")) {
                        isChecked = true;
                        showDialogResult(status.getStatus());
                        enableButtonSend();
                     }
                  }

                  @Override
                  public void onFailure(Call<StatusResponse> call, Throwable t) {
                     enableButtonSend();
                  }
               });
               try {
                  Thread.sleep(5000);
               } catch (InterruptedException e) {
                  e.printStackTrace();
               }
            }
         }
      }).start();
   }

   private void showDialogResult(String result) {
      dialogResult = new Dialog(this);
      LayoutInflater inflater = this.getLayoutInflater();
      View view = inflater.inflate(R.layout.result_dialog, null);
      ImageView imageViewResult = view.findViewById(R.id.iconResult);
      TextView textViewResult = view.findViewById(R.id.textViewMsg);
      if (result.equals("1")) {
         imageViewResult.setImageResource(R.drawable.ic_happy);
         textViewResult.setText(R.string.msg_success);
      } else {
         imageViewResult.setImageResource(R.drawable.ic_sad);
         textViewResult.setText(R.string.msg_fail);
      }
      dialogResult.setContentView(view);
      dialogResult.setCancelable(false);
      Button buttonBack = view.findViewById(R.id.buttonBackDialog);
      buttonBack.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            dialogResult.dismiss();
         }
      });
      dialogResult.show();
   }

   private void showDialogAnimation(int res) {
      dialogUpload = new Dialog(this);
      LayoutInflater inflater = this.getLayoutInflater();
      View view = inflater.inflate(R.layout.animation_dialog, null);
      dialogUpload.setContentView(view);
      dialogUpload.setCancelable(false);
      dialogUpload.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
      lottieAnimationView = view.findViewById(R.id.animationView);
      lottieAnimationView.setAnimation(res);
      lottieAnimationView.playAnimation();
      dialogUpload.show();
   }

   @SuppressLint("InvalidWakeLockTag")
   @Override
   protected void onResume() {
      super.onResume();
      final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
      this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
      this.mWakeLock.acquire(3 * 60 * 1000L /*3 minutes*/);
   }

   @Override
   public void onDestroy() {
      this.mWakeLock.release();
      super.onDestroy();
   }
}
