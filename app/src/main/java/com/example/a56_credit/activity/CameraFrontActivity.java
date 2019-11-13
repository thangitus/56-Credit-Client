package com.example.a56_credit.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.camerakit.CameraKitView;
import com.example.a56_credit.R;

public class CameraFrontActivity extends AppCompatActivity {
   private CameraKitView cameraKitView;
   ImageButton buttonTakePhoto;
   ImageView buttonClose;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_camera_front);
      final Intent intent;
      intent = getIntent();
      mapping();
      buttonClose.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            intent.putExtra("hasPhoto", false);
            setResult(Activity.RESULT_OK, intent);
            finish();
         }
      });
      buttonTakePhoto.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            cameraKitView.captureImage(new CameraKitView.ImageCallback() {
               @Override
               public void onImage(CameraKitView cameraKitView, final byte[] capturedImage) {
                  sendToHomeActivity(intent, capturedImage);
               }
            });
         }
      });
   }

   private void sendToHomeActivity(Intent intent, byte[] bytes) {
      intent.putExtra("hasPhoto", true);
      intent.putExtra("photo", bytes);
      setResult(Activity.RESULT_OK, intent);
      finish();
   }

   @Override
   protected void onStart() {
      super.onStart();
      cameraKitView.onStart();
   }

   @Override
   protected void onResume() {
      super.onResume();
      cameraKitView.onResume();
   }

   @Override
   protected void onPause() {
      cameraKitView.onPause();
      super.onPause();
   }

   @Override
   protected void onStop() {
      cameraKitView.onStop();
      super.onStop();
   }

   @Override
   public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
   }


   private void mapping() {
      buttonTakePhoto = findViewById(R.id.buttonTakePicFront);
      buttonClose = findViewById(R.id.buttonCloseFront);
      cameraKitView = findViewById(R.id.cameraFront);
   }

}
