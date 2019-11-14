package com.example.a56_credit.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.a56_credit.R;

import java.io.File;

public class CameraFrontActivity extends AppCompatActivity {
   ImageButton buttonTakePhoto;
   ImageView buttonClose;
   TextureView textureView;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_camera_front);
      final Intent intent;
      intent = getIntent();
      mapping();
      PreviewConfig config = new PreviewConfig.Builder().setLensFacing(CameraX.LensFacing.FRONT).build();
      Preview preview = new Preview(config);
      CameraX.unbindAll();
      ImageCaptureConfig config1 =
              new ImageCaptureConfig.Builder()
                      .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
                      .setLensFacing(CameraX.LensFacing.FRONT)
                      .build();

      ImageCapture imageCapture = new ImageCapture(config1);

      preview.setOnPreviewOutputUpdateListener(
              new Preview.OnPreviewOutputUpdateListener() {
                 @Override
                 public void onUpdated(Preview.PreviewOutput previewOutput) {
                    // The output data-handling is configured in a listener.
                    textureView.setSurfaceTexture(previewOutput.getSurfaceTexture());
                    // Your custom code here.
                 }
              });

// The use case is bound to an Android Lifecycle with the following code.
      CameraX.bindToLifecycle(this, imageCapture, preview);

      buttonClose.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            intent.putExtra("hasPhoto", false);
            setResult(Activity.RESULT_OK, intent);
            finish();
         }
      });
      buttonTakePhoto.setOnClickListener(new View.OnClickListener() {
         @SuppressLint("RestrictedApi")
         @Override
         public void onClick(View view) {
            File file = new File(Environment.getExternalStorageDirectory() + "/" + System.currentTimeMillis() + ".jpg");
            imageCapture.takePicture(file, CameraXExecutors.mainThreadExecutor(), new ImageCapture.OnImageSavedListener() {
               @Override
               public void onImageSaved(@NonNull File file) {
                  intent.putExtra("hasPhoto", true);
                  intent.putExtra("photo", file.getAbsolutePath());
                  setResult(Activity.RESULT_OK, intent);
                  finish();
               }
               @Override
               public void onError(@NonNull ImageCapture.ImageCaptureError imageCaptureError, @NonNull String message, @Nullable Throwable cause) {

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

   private void mapping() {
      buttonTakePhoto = findViewById(R.id.buttonTakePicFront);
      buttonClose = findViewById(R.id.buttonCloseFront);
      textureView = findViewById(R.id.view_finder);
   }

}
