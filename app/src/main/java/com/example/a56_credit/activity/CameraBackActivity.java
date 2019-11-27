package com.example.a56_credit.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.a56_credit.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.automl.FirebaseAutoMLLocalModel;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceAutoMLImageLabelerOptions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.error.CameraErrorListener;
import io.fotoapparat.exception.camera.CameraException;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.preview.Frame;
import io.fotoapparat.preview.FrameProcessor;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.result.WhenDoneListener;
import io.fotoapparat.view.CameraView;
import kotlin.Unit;

import static io.fotoapparat.log.LoggersKt.fileLogger;
import static io.fotoapparat.log.LoggersKt.logcat;
import static io.fotoapparat.log.LoggersKt.loggers;
import static io.fotoapparat.selector.AspectRatioSelectorsKt.standardRatio;
import static io.fotoapparat.selector.LensPositionSelectorsKt.back;
import static io.fotoapparat.selector.ResolutionSelectorsKt.highestResolution;

public class CameraBackActivity extends AppCompatActivity {
   private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
   private static final String TAG = "CameraBackActivity";
   private static final float MAX_PROP_IDENTITY = 0.7f;
   ImageView imgFrame;
   ImageButton buttonTakePic;
   ImageView buttonClose;
   View viewRight;
   TextView textViewTittle;
   CameraView cameraView;
   Fotoapparat fotoapparat;
   Intent intent;
   long currentTime, prevTime;
   FirebaseVisionImageLabeler labeler;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_camera_back);
      intent = getIntent();
      mapping();
      init();
      setSizeFrame(imgFrame);
      buttonTakePic.setOnClickListener(new View.OnClickListener() {
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
      buttonClose.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            intent.putExtra("hasPhoto", false);
            setResult(Activity.RESULT_OK, intent);
            finish();
         }
      });
      fotoapparat = createFotoapparat();
      checkPermission();
   }

   private void mapping() {
      imgFrame = findViewById(R.id.frame);
      buttonTakePic = findViewById(R.id.buttonTakePic);
      viewRight = findViewById(R.id.viewRight);
      textViewTittle = findViewById(R.id.textViewTittle);
      buttonClose = findViewById(R.id.buttonClose);
      cameraView = findViewById(R.id.camera_view);
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

   @Override
   protected void onPause() {
      super.onPause();
      fotoapparat.stop();
   }

   private Fotoapparat createFotoapparat() {
      return Fotoapparat
              .with(this)
              .into(cameraView)
              .previewScaleType(ScaleType.CenterCrop)
              .frameProcessor(new FrameProcessor() {
                 @Override
                 public void process(@NotNull Frame frame) {
                    currentTime = System.currentTimeMillis();
                    if (currentTime - 500 > prevTime) {
                       mlLable(frame);
                       prevTime = currentTime;
                    }
                 }
              })
              .lensPosition(back())
              .logger(loggers(
                      logcat(),
                      fileLogger(this)
              ))
              .cameraErrorCallback(new CameraErrorListener() {
                 @Override
                 public void onError(@NotNull CameraException e) {
                 }
              })
              .photoResolution(standardRatio(
                      highestResolution()
              ))
              .build();
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

   private FirebaseVisionImage getVisionImageFromFrame(Frame frame) {
      byte[] bytes = frame.getImage();
      FirebaseVisionImageMetadata metadata = new FirebaseVisionImageMetadata.Builder()
              .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
              .setRotation(FirebaseVisionImageMetadata.ROTATION_270)
              .setHeight(frame.getSize().height)
              .setWidth(frame.getSize().width)
              .build();

      FirebaseVisionImage image = FirebaseVisionImage.fromByteArray(bytes, metadata);
      return image;
   }

   private void init() {
      FirebaseAutoMLLocalModel localModel = new FirebaseAutoMLLocalModel.Builder()
              .setAssetFilePath("model/manifest.json")
              .build();
      try {
         FirebaseVisionOnDeviceAutoMLImageLabelerOptions options =
                 new FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder(localModel)
                         .setConfidenceThreshold(0.0f)  // Evaluate your model in the Firebase console
                         .build();
         labeler = FirebaseVision.getInstance().getOnDeviceAutoMLImageLabeler(options);
      } catch (FirebaseMLException e) {
         e.printStackTrace();
      }

   }

   private void mlLable(Frame frame) {
      labeler.processImage(getVisionImageFromFrame(frame))
              .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                 @Override
                 public void onSuccess(List<FirebaseVisionImageLabel> firebaseVisionImageLabels) {
                    FirebaseVisionImageLabel label;
                    label = firebaseVisionImageLabels.get(0);
                    String text = label.getText();
                    float prob = label.getConfidence();
                    if (text.equals("identity") && prob > MAX_PROP_IDENTITY) {
                       textViewTittle.setText("Căn chỉnh CMND vào khung và chụp ảnh");
                       enableButtonTakePic();
                    } else{
                       textViewTittle.setText("Không tìm thấy chứng minh nhân dân");
                       disableButtonTakePic();
                    }
                 }
              });
   }

   private void disableButtonTakePic() {
      buttonTakePic.setAlpha(0.4f);
      buttonTakePic.setEnabled(false);

   }

   private void enableButtonTakePic() {
      buttonTakePic.setAlpha(1f);
      buttonTakePic.setEnabled(true);
   }

}