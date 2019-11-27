package com.example.a56_credit.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.a56_credit.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

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
import static io.fotoapparat.selector.LensPositionSelectorsKt.front;
import static io.fotoapparat.selector.ResolutionSelectorsKt.highestResolution;

public class CameraFrontActivity extends AppCompatActivity {
   private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
   private static final String TAG = "CameraFrontActivity---";

   TextView textViewNotify;
   ImageView buttonClose;
   CameraView cameraView;
   Fotoapparat fotoapparat;
   Intent intent;
   Boolean isRotatedRight, isRotatedLeft, isFaceProcessRunning;
   FirebaseVisionFaceDetector detector;
   long currentTime, prevTime;
   float minSmileProb, maxSmileProb;
   Dialog dialogProgress;
   LottieAnimationView lottieAnimationView;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_camera_front);
      intent = getIntent();
      mapping();
      init();
      prevTime = System.currentTimeMillis();
      buttonClose.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            intent.putExtra("hasPhoto", false);
            setResult(Activity.RESULT_OK, intent);
            fotoapparat.stop();
            finish();
         }
      });
      fotoapparat = createFotoapparat();
      checkPermission();
      showDialog();
   }


   private void mapping() {
      buttonClose = findViewById(R.id.buttonCloseFront);
      cameraView = findViewById(R.id.camera_front);
      textViewNotify = findViewById(R.id.textViewNotify);
   }

   private Fotoapparat createFotoapparat() {
      return Fotoapparat
              .with(this)
              .into(cameraView)
              .frameProcessor(new FrameProcessor() {
                 @Override
                 public void process(@NotNull Frame frame) {
                    currentTime = System.currentTimeMillis();
                    if (currentTime - 500 > prevTime && isFaceProcessRunning) {
                       faceOptions(frame);
                       prevTime = currentTime;
                    }
                 }
              })
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
      FirebaseVisionFaceDetectorOptions realTimeOpts =
              new FirebaseVisionFaceDetectorOptions.Builder()
                      .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
                      .setLandmarkMode(FirebaseVisionFaceDetectorOptions.NO_LANDMARKS)
                      .setContourMode(FirebaseVisionFaceDetectorOptions.NO_CONTOURS)
                      .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                      .build();
      detector = FirebaseVision.getInstance()
              .getVisionFaceDetector(realTimeOpts);
      isRotatedLeft = false;
      isRotatedRight = false;
      minSmileProb = 1;
      maxSmileProb = 0;
   }

   private void faceOptions(Frame frame) {
      if (!isFaceProcessRunning) return;
      detector.detectInImage(getVisionImageFromFrame(frame))
              .addOnSuccessListener(
                      new OnSuccessListener<List<FirebaseVisionFace>>() {
                         @Override
                         public void onSuccess(List<FirebaseVisionFace> faces) {
                            if (faces.size() > 1) {
                               isRotatedLeft = false;
                               isRotatedRight = false;
                               textViewNotify.setText("Có nhiều hơn một người trong khung hình");
                               return;
                            } else if (faces.size() < 1) {
                               isRotatedLeft = false;
                               isRotatedRight = false;
                               textViewNotify.setText("Không tìm thấy khuôn mặt trong khung hình");
                               return;
                            }

                            FirebaseVisionFace face = faces.get(0);
                            float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
                            if (rotY < -35)
                               isRotatedRight = true;
                            else if (rotY > 30) isRotatedLeft = true;

                            if (!isRotatedRight) {
                               textViewNotify.setText("Xoay đầu qua phải");
                               return;
                            }
                            if (!isRotatedLeft) {
                               textViewNotify.setText("Xoay đầu qua trái");
                               return;
                            }
                            if (face.getSmilingProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                               float smileProb = face.getSmilingProbability();
                               if (minSmileProb == 1 || maxSmileProb == 0) {
                                  minSmileProb = smileProb;
                                  maxSmileProb = smileProb;
                                  if (smileProb < 0.4) textViewNotify.setText("Hãy cười!");
                                  else textViewNotify.setText("Hãy ngưng cười!");
                                  return;
                               }
                               maxSmileProb = smileProb > maxSmileProb ? smileProb : maxSmileProb;
                               minSmileProb = smileProb < minSmileProb ? smileProb : minSmileProb;
                               if (maxSmileProb - minSmileProb > 0.7 && isFaceProcessRunning) {
                                  isFaceProcessRunning = false;
                                  startCountdown(3);
                               }
                            }
                         }
                      })
              .addOnFailureListener(
                      new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {
                            // Task failed with an exception
                            // ...
                         }
                      });
   }

   private void startCountdown(int second) {
      new CountDownTimer(second * 1000, 1000) {
         int time = second;

         public void onTick(long millisUntilFinished) {
            textViewNotify.setText("Bắt đầu chụp trong " + time + " giây nữa!");
            time--;
         }

         public void onFinish() {
            textViewNotify.setText("Bắt đầu chụp trong " + time + " giây nữa!");
            takePhoto();
         }
      }.start();
   }

   private void takePhoto() {
      showDialogProgress();
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
            fotoapparat.stop();
            dialogProgress.dismiss();
            finish();
         }
      });
   }

   private void showDialog() {
      isFaceProcessRunning = false;
      LayoutInflater inflater = this.getLayoutInflater();
      View view = inflater.inflate(R.layout.alert_dialog, null);
      AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
      Button button = view.findViewById(R.id.buttonUnderstood);
      TextView textView = view.findViewById(R.id.textViewAlert);
      textView.setText("Quay đầu qua phải, sau đó quay đầu qua trái và cười để chụp ảnh!");
      dialogBuilder.setView(view);
      AlertDialog alertDialog = dialogBuilder.create();
      alertDialog.setCancelable(false);
      alertDialog.show();
      button.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            isFaceProcessRunning = true;
            alertDialog.dismiss();
         }
      });
   }

   private void showDialogProgress() {
      dialogProgress = new Dialog(this);
      LayoutInflater inflater = this.getLayoutInflater();
      View view = inflater.inflate(R.layout.animation_dialog, null);
      dialogProgress.setContentView(view);
      dialogProgress.setCancelable(false);
      dialogProgress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
      lottieAnimationView = view.findViewById(R.id.animationView);
      lottieAnimationView.playAnimation();
      dialogProgress.show();
   }
}