package com.example.a56_credit.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a56_credit.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.error.CameraErrorListener;
import io.fotoapparat.exception.camera.CameraException;
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
   ImageButton buttonTakePhoto;
   ImageView buttonClose;
   CameraView cameraView;
   Fotoapparat fotoapparat;

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
      fotoapparat = createFotoapparat();
   }


   private void mapping() {
      buttonTakePhoto = findViewById(R.id.buttonTakePicFront);
      buttonClose = findViewById(R.id.buttonCloseFront);
      cameraView = findViewById(R.id.camera_front);
   }

   private Fotoapparat createFotoapparat() {
      return Fotoapparat
              .with(this)
              .into(cameraView)
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
   protected void onStart() {
      super.onStart();
      fotoapparat.start();
   }

   @Override
   protected void onPause() {
      super.onPause();
      fotoapparat.stop();
   }
}