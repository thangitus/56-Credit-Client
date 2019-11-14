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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.Display;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a56_credit.R;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class CameraBackActivity extends AppCompatActivity {
   ImageView imgFrame;
   ImageButton buttonTakePic;
   ImageView buttonClose;
   View viewRight, viewLeft, viewTop, viewBottom;
   TextView textViewTittle;
   TextureView textureView;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_camera_back);
      final Intent intent = getIntent();
      mapping();
      setSizeFrame(imgFrame);
      CameraX.unbindAll();
      Preview preview = createPreview();
      ImageCapture imageCapture = createImageCapture();
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

      buttonTakePic.setOnClickListener(new View.OnClickListener() {
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

      buttonClose.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            intent.putExtra("hasPhoto", false);
            setResult(Activity.RESULT_OK, intent);
            finish();
         }
      });
   }

   private ImageCapture createImageCapture() {
      ImageCaptureConfig imageCaptureConfig =
              new ImageCaptureConfig.Builder()
                      .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
                      .setLensFacing(CameraX.LensFacing.BACK)
                      .build();

      ImageCapture imageCapture = new ImageCapture(imageCaptureConfig);
      return imageCapture;
   }

   private Preview createPreview() {
      PreviewConfig previewConfig = new PreviewConfig.Builder()
              .setLensFacing(CameraX.LensFacing.BACK)
              .build();
      Preview preview = new Preview(previewConfig);
      return preview;
   }

   private byte[] cropIMG(byte[] capturedImage) {
      Bitmap bitmap = BitmapFactory.decodeByteArray(capturedImage, 0, capturedImage.length);
      float widthScreen, heightScreen, heightTop, heightBottom, widthLeft, widthRight;
      widthScreen = getWidthScreen();
      heightScreen = getHeightScreen();
      heightTop = viewTop.getHeight();
      heightBottom = viewBottom.getHeight();
      widthLeft = viewLeft.getWidth();
      widthRight = viewRight.getHeight();
      Bitmap res = Bitmap.createBitmap(bitmap, 0, 0, (int) (widthScreen - widthRight), (int) (heightScreen - heightBottom));
      res = Bitmap.createBitmap(res, (int) imgFrame.getX(), (int) imgFrame.getY(), res.getWidth() - (int) widthLeft, res.getHeight() - (int) heightTop);
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      res.compress(Bitmap.CompressFormat.PNG, 100, stream);
      byte[] byteArray = stream.toByteArray();
      return byteArray;
   }


   private void mapping() {
      imgFrame = findViewById(R.id.frame);
      buttonTakePic = findViewById(R.id.buttonTakePic);
      viewRight = findViewById(R.id.viewRight);
      textViewTittle = findViewById(R.id.textViewTittle);
      buttonClose = findViewById(R.id.buttonClose);
      viewLeft = findViewById(R.id.viewLeft);
      viewTop = findViewById(R.id.viewTop);
      viewBottom = findViewById(R.id.viewBottom);
      textureView = findViewById(R.id.view_finder);
   }

   private float getWidthScreen() {
      Display display = getWindowManager().getDefaultDisplay();
      DisplayMetrics outMetrics = new DisplayMetrics();
      display.getMetrics(outMetrics);
      float width = outMetrics.widthPixels;
      return width;
   }

   private float getHeightScreen() {
      Display display = getWindowManager().getDefaultDisplay();
      DisplayMetrics outMetrics = new DisplayMetrics();
      display.getMetrics(outMetrics);
      float height = outMetrics.widthPixels;
      return height;
   }

   private void setSizeFrame(ImageView imageView) {
      float width = (float) (getWidthScreen() * 0.8);
      imageView.getLayoutParams().width = (int) width;
      imageView.getLayoutParams().height = (int) (width * 1.6);
      imageView.requestLayout();

   }
}