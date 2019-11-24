package com.example.a56_credit.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a56_credit.R;

public class OpeningActivity extends AppCompatActivity {
//   SharedPreferences mPrefs;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_opening);
//      mPrefs = getSharedPreferences("isFirst", MODE_PRIVATE);
      Boolean isFirst;
      Intent intent;
//      isFirst = mPrefs.getBoolean("first", true);
//      if (isFirst) {
//         intent = new Intent(this, OnboardingActivity.class);
//         SharedPreferences.Editor editor = mPrefs.edit();
//         editor.putBoolean("first", false);
//         editor.apply();
//      } else
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
