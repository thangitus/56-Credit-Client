package com.example.a56_credit.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.camerakit.CameraKitView;
import com.example.a56_credit.R;

public class CameraBackActivity extends AppCompatActivity {
   private CameraKitView cameraKitView;
   ImageView imgFrame;
   ImageButton buttonTakePic;
   ImageView buttonClose;
   View viewRight;
   TextView textViewTittle;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_camera_back);
      final Intent intent = getIntent();
      mapping();
      setSizeFrame(imgFrame);
      buttonTakePic.setOnClickListener(new View.OnClickListener() {
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
      buttonClose.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            intent.putExtra("hasPhoto", false);
            setResult(Activity.RESULT_OK, intent);
            finish();
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
      imgFrame = findViewById(R.id.frame);
      buttonTakePic = findViewById(R.id.buttonTakePic);
      viewRight = findViewById(R.id.viewRight);
      textViewTittle = findViewById(R.id.textViewTittle);
      buttonClose = findViewById(R.id.buttonClose);
      cameraKitView = findViewById(R.id.camera);

   }

   private float getWidthScreen() {
      Display display = getWindowManager().getDefaultDisplay();
      DisplayMetrics outMetrics = new DisplayMetrics();
      display.getMetrics(outMetrics);

      float dpWidth = outMetrics.widthPixels;
      return dpWidth;
   }

   private void setSizeFrame(ImageView imageView) {
      float width = (float) (getWidthScreen() * 0.8);
      imageView.getLayoutParams().width = (int) width;
      imageView.getLayoutParams().height = (int) (width * 1.6);
      imageView.requestLayout();

   }
}