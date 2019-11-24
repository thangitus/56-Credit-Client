package com.example.a56_credit.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions;

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

   ImageView imgFrame;
   ImageButton buttonTakePic;
   ImageView buttonClose;
   View viewRight;
   TextView textViewTittle;
   CameraView cameraView;
   Fotoapparat fotoapparat;
   Intent intent;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_camera_back);
      intent = getIntent();
      mapping();
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
   protected void onStart() {
      super.onStart();
//      fotoapparat.start();
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
              .frameProcessor(new FrameProcessor() {
                 @Override
                 public void process(@NotNull Frame frame) {
                    faceOptions(frame);
                 }
              })
              .previewScaleType(ScaleType.CenterCrop)
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
      }
      else{
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
              .setRotation(FirebaseVisionImageMetadata.ROTATION_90)
              .setHeight(frame.getSize().height)
              .setWidth(frame.getSize().width)
              .build();

      FirebaseVisionImage image = FirebaseVisionImage.fromByteArray(bytes, metadata);
      return image;
   }

   private void faceOptions(Frame frame) {
      Log.wtf(TAG, frame.toString());
      FirebaseVisionFaceDetectorOptions realTimeOpts =
              new FirebaseVisionFaceDetectorOptions.Builder()
                      .setPerformanceMode(FirebaseVisionObjectDetectorOptions.STREAM_MODE)
                      .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                      .build();
      FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
              .getVisionFaceDetector(realTimeOpts);
      detector.detectInImage(getVisionImageFromFrame(frame))
              .addOnSuccessListener(
                      new OnSuccessListener<List<FirebaseVisionFace>>() {
                         @Override
                         public void onSuccess(List<FirebaseVisionFace> faces) {
                            // Task completed successfully
                            // [START_EXCLUDE]
                            // [START get_face_info]
                            for (FirebaseVisionFace face : faces) {
                               Rect bounds = face.getBoundingBox();
                               float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
                               float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees
                               Log.wtf(TAG, "Z: " + rotZ + "Y: " + rotY);
                               // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                               // nose available):
                               FirebaseVisionFaceLandmark leftEar = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR);
                               if (leftEar != null) {
                                  FirebaseVisionPoint leftEarPos = leftEar.getPosition();
                               }

                               // If classification was enabled:
                               if (face.getSmilingProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                  float smileProb = face.getSmilingProbability();
                               }
                               if (face.getRightEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                  float rightEyeOpenProb = face.getRightEyeOpenProbability();
                               }

                               // If face tracking was enabled:
                               if (face.getTrackingId() != FirebaseVisionFace.INVALID_ID) {
                                  int id = face.getTrackingId();
                               }
                            }
                            // [END get_face_info]
                            // [END_EXCLUDE]
                         }
                      })
              .addOnFailureListener(
                      new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {
                            Log.wtf("FrontCamera","onFailure");
                            // Task failed with an exception
                            // ...
                         }
                      });

   }
}