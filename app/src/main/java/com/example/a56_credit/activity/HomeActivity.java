package com.example.a56_credit.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
<<<<<<< Updated upstream
import android.os.Bundle;
=======
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
>>>>>>> Stashed changes
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a56_credit.R;
import com.example.a56_credit.model.PersonalInformation;

<<<<<<< Updated upstream
=======
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

>>>>>>> Stashed changes
public class HomeActivity extends AppCompatActivity {
   private static final int REQUEST_CODE = 1;
   ConstraintLayout constraintLayoutCMND, constraintLayoutAddInfo, constraintLayoutInfo;
   TextView tvFullName, tvIdNumber, tvBirthday, tvBuildingNumber, tvWards, tvProvince, tvDistrict;
   TextView tvButtonEdit;
   PersonalInformation personalInformation;
<<<<<<< Updated upstream
   Intent intentToAddInfo;
=======
   Intent intentToAddInfo, intentToCameraBack, intentCameraBack, intentCameraFront;
   ImageView imgCMND, imgSelfie;
   Bitmap bitmapCMND, bitmapSelfie;
   SharedPreferences mPrefs;
   private Uri mImageUri;
   String currentPhotoPath;
>>>>>>> Stashed changes

   @SuppressLint("ClickableViewAccessibility")
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_home);
      mapping();
      if (personalInformation == null) {
         constraintLayoutAddInfo.setVisibility(View.GONE);
         tvButtonEdit.setVisibility(View.GONE);
      }
      intentToAddInfo = new Intent(this, AddPersonalInfoActivity.class);
      constraintLayoutInfo.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            if (personalInformation == null){
               intentToAddInfo.putExtra("isEdit",false);
               startActivityForResult(intentToAddInfo, REQUEST_CODE);
            }
         }
      });
      tvButtonEdit.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
<<<<<<< Updated upstream
            intentToAddInfo.putExtra("isEdit",true);
            intentToAddInfo.putExtra("info",personalInformation);
            startActivityForResult(intentToAddInfo,REQUEST_CODE);
=======
            intentToAddInfo.putExtra("isEdit", true);
            intentToAddInfo.putExtra("info", personalInformation);
            startActivityForResult(intentToAddInfo, REQUEST_CODE_INFO);
         }
      });
      constraintLayoutCMND.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            if (bitmapCMND == null)
            startActivityForResult(intentToCameraBack, REQUEST_CODE_CAMERA_BACK);
//               openCameraBack();
         }
      });
      tvReIdenty.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
//            openCameraBack();
            startActivityForResult(intentToCameraBack, REQUEST_CODE_CAMERA_BACK);
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
            openCameraFront();
>>>>>>> Stashed changes
         }
      });
   }

   private void mapping() {
      constraintLayoutCMND = findViewById(R.id.layoutCMND);
      constraintLayoutInfo = findViewById(R.id.layoutInfo);
      constraintLayoutAddInfo = findViewById(R.id.layoutAddInfo);
      tvFullName = findViewById(R.id.textViewFullName);
      tvBirthday = findViewById(R.id.textViewBirthday);
      tvWards = findViewById(R.id.textViewWards);
      tvDistrict = findViewById(R.id.textViewDistrict);
      tvIdNumber = findViewById(R.id.textViewIdNumber);
      tvBuildingNumber = findViewById(R.id.textViewBuildingNumber);
      tvProvince = findViewById(R.id.textViewProvince);
      tvButtonEdit = findViewById(R.id.tvButtonEdit);
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      if ((requestCode == REQUEST_CODE) && (resultCode == Activity.RESULT_OK)) {
         personalInformation = data.getParcelableExtra("info");
         tvFullName.setText(personalInformation.getFullName());
         tvIdNumber.setText(personalInformation.getIdNumber());
         tvProvince.setText(personalInformation.getProvince());
         tvBuildingNumber.setText(personalInformation.getBuildingNumber());
         tvBirthday.setText(personalInformation.getBirthday());
         tvDistrict.setText(personalInformation.getDistrict());
         tvWards.setText(personalInformation.getWards());
         constraintLayoutAddInfo.setVisibility(View.VISIBLE);
         tvButtonEdit.setVisibility(View.VISIBLE);
      }
<<<<<<< Updated upstream
   }
=======
      if ((requestCode == REQUEST_CODE_CAMERA_BACK) && (resultCode == Activity.RESULT_OK)) {
//         Boolean hasPic = data.getBooleanExtra("hasPic", false);
//         if (hasPic) {
//            byte[] bytes = data.getByteArrayExtra("image");
//            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//            imgCMND.setImageBitmap(bmp);
//         }
         grabImage(imgCMND, "identity");
         tvReIdenty.setVisibility(View.VISIBLE);
      }
      if ((requestCode == REQUEST_CODE_CAMERA_FRONT) && (resultCode == Activity.RESULT_OK)) {
         bitmapSelfie = (Bitmap) data.getExtras().get("data");
         imgSelfie.setImageBitmap(bitmapSelfie);
         imgSelfie.setVisibility(View.VISIBLE);
         tvReSelfie.setVisibility(View.VISIBLE);
         saveBitmap(bitmapSelfie, "selfie");
      }
   }

   private void openCameraBack() {
      intentCameraBack = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      File photoFile;
      try {
         photoFile = createImageFile();
      } catch (Exception e) {
         Log.v("Pic", "Can't create file to take picture!");
         Toast.makeText(this, "Please check SD card! Image shot is impossible!", Toast.LENGTH_SHORT).show();
         return;
      }
      if (photoFile != null) {
         Uri photoURI = FileProvider.getUriForFile(this,
                 "com.example.android.fileprovider",
                 photoFile);
         intentCameraBack.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
         startActivityForResult(intentCameraBack, REQUEST_CODE_CAMERA_BACK);
      }
   }
//      startActivityForResult(intentCameraBack, REQUEST_CODE_CAMERA_BACK);

   private void openCameraFront() {
      intentCameraFront = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      intentCameraFront.putExtra("android.intent.extras.CAMERA_FACING", 1);
      startActivityForResult(intentCameraFront, REQUEST_CODE_CAMERA_FRONT);
   }

   private void saveBitmap(Bitmap bitmap, String key) {
      SharedPreferences.Editor editor = mPrefs.edit();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
      byte[] bytes = baos.toByteArray();
      String encoded = Base64.encodeToString(bytes, Base64.DEFAULT);
      if (key.equals("identity"))
         personalInformation.setIdentity(encoded);
      else
         personalInformation.setSelfie(encoded);
      editor.putString(key, encoded);
      editor.apply();
   }

   private Bitmap loadBitmap(String key) {
      String encoded = mPrefs.getString(key, null);
      if (encoded == null)
         return null;
      byte[] imageAsBytes = Base64.decode(encoded.getBytes(), Base64.DEFAULT);
      Bitmap bm = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
      return bm;
   }

   private File createImageFile() throws IOException {
      // Create an image file name
      String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
      String imageFileName = "JPEG_" + timeStamp + "_";
      File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
      File image = File.createTempFile(
              imageFileName,  /* prefix */
              ".JPG",         /* suffix */
              storageDir      /* directory */
      );

      // Save a file: path for use with ACTION_VIEW intents
      currentPhotoPath = image.getAbsolutePath();
      return image;
   }

   public void grabImage(ImageView imageView, String key) {
      this.getContentResolver().notifyChange(mImageUri, null);
      ContentResolver cr = this.getContentResolver();
      Bitmap bitmap;
      try {
         bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, mImageUri);
         saveBitmap(bitmap, key);
         imageView.setImageBitmap(bitmap);
      } catch (Exception e) {
         Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
         Log.d("Pic", "Failed to load", e);
      }
      imageView.setVisibility(View.VISIBLE);
   }
>>>>>>> Stashed changes
}
