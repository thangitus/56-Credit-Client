package com.example.a56_credit.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.a56_credit.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.error.CameraErrorListener;
import io.fotoapparat.exception.camera.CameraException;
import io.fotoapparat.facedetector.Rectangle;
import io.fotoapparat.facedetector.processor.FaceDetectorProcessor;
import io.fotoapparat.facedetector.view.RectanglesView;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.result.WhenDoneListener;
import io.fotoapparat.view.CameraView;
import kotlin.Unit;

import static io.fotoapparat.log.LoggersKt.fileLogger;
import static io.fotoapparat.log.LoggersKt.logcat;
import static io.fotoapparat.log.LoggersKt.loggers;
import static io.fotoapparat.selector.AspectRatioSelectorsKt.standardRatio;
import static io.fotoapparat.selector.LensPositionSelectorsKt.front;
import static io.fotoapparat.selector.ResolutionSelectorsKt.highestResolution;

public class CameraFrontActivity extends AppCompatActivity {
   private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
   ImageButton buttonTakePhoto;
   ImageView buttonClose;
   CameraView cameraView;
   Fotoapparat fotoapparat;
   RectanglesView rectanglesView;
   FaceDetectorProcessor processor;
   Intent intent;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_camera_front);

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
            final PhotoResult photoResult = fotoapparat.takePicture();
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
            final File file = new File(getExternalFilesDir("Photos"), dateFormat.format(date) + ".JPG");
            photoResult.saveToFile(file).whenDone(new WhenDoneListener<Unit>() {
               @Override
               public void whenDone(@Nullable Unit unit) {
                  intent.putExtra("hasPhoto", true);
                  intent.putExtra("photo", file.getAbsolutePath());
                  setResult(Activity.RESULT_OK, intent);
                  finish();
               }
            });
         }
      });
      processor = FaceDetectorProcessor.with(this)
              .listener(new FaceDetectorProcessor.OnFacesDetectedListener() {
                 @Override
                 public void onFacesDetected(List<Rectangle> faces) {
                    rectanglesView.setRectangles(faces);
                 }
              }).build();
      fotoapparat = createFotoapparat();
      checkPermission();
   }


   private void mapping() {
      buttonTakePhoto = findViewById(R.id.buttonTakePicFront);
      buttonClose = findViewById(R.id.buttonCloseFront);
      cameraView = findViewById(R.id.camera_front);
      rectanglesView = findViewById(R.id.rectanglesView);
   }

   private Fotoapparat createFotoapparat() {
      return Fotoapparat
              .with(this)
              .into(cameraView)
//              .frameProcessor(processor)
              .previewScaleType(ScaleType.CenterCrop)
              .lensPosition(front())
              .logger(loggers(
                      logcat(),
                      fileLogger(this)
              ))
              .cameraErrorCallback(new CameraErrorListener() {
                 @Override
                 public void onError(@NotNull CameraException e) {
                    Toast.makeText(CameraFrontActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                 }
              })
              .photoResolution(standardRatio(
                      highestResolution()
              ))
              .build();
   }


   @Override
   protected void onPause() {
      super.onPause();
      fotoapparat.stop();
   }

   private void checkPermission() {
      if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
         ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
      } else {
         fotoapparat.start();
      }
   }

   @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
         fotoapparat.start();
      } else {
         intent.putExtra("hasPhoto", false);
         setResult(Activity.RESULT_OK, intent);
         finish();
      }
   }
}