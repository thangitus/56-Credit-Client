package com.example.a56_credit.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class OpeningActivity extends AppCompatActivity {
   private static final String TAG = "OpeningActivity";
//   SharedPreferences mPrefs;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
//      mPrefs = getSharedPreferences("isFirst", MODE_PRIVATE);
      Boolean isFirst;
//      isFirst = mPrefs.getBoolean("first", true);
//      if (isFirst) {
//         intent = new Intent(this, OnboardingActivity.class);
//         SharedPreferences.Editor editor = mPrefs.edit();
//         editor.putBoolean("first", false);
//         editor.apply();
//      } else
      Intent intent;
         intent = new Intent(this, OnboardingActivity.class);


      new Thread(new Runnable() {
         @Override
         public void run() {
            try {
               Thread.sleep(3200);
               startActivity(intent);
               finish();
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }
      }).start();
   }
}
