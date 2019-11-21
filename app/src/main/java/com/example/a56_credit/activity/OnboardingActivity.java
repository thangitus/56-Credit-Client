package com.example.a56_credit.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.asksira.loopingviewpager.LoopingViewPager;
import com.example.a56_credit.adapter.OnboardingAdapter;
import com.example.a56_credit.R;
import com.rd.PageIndicatorView;

import java.util.ArrayList;

public class OnboardingActivity extends AppCompatActivity {
   LoopingViewPager viewPager;
   OnboardingAdapter adapter;
   PageIndicatorView indicatorView;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_onboarding);
      viewPager = findViewById(R.id.viewpager);
      indicatorView = findViewById(R.id.indicator);
      adapter = new OnboardingAdapter(this, createList(), true);
      viewPager.setAdapter(adapter);
      //Custom bind indicator
      indicatorView.setCount(viewPager.getIndicatorCount());
      viewPager.setIndicatorPageChangeListener(new LoopingViewPager.IndicatorPageChangeListener() {
         @Override
         public void onIndicatorProgress(int selectingPosition, float progress) {
            indicatorView.setProgress(selectingPosition, progress);
            LottieAnimationView lottieAnimationView;
            if (selectingPosition == 0)
               lottieAnimationView = findViewById(R.id.step1);
            else if (selectingPosition == 1)
               lottieAnimationView = findViewById(R.id.step2);
            else if (selectingPosition == 2)
               lottieAnimationView = findViewById(R.id.step3);
            else lottieAnimationView = findViewById(R.id.step4);
            lottieAnimationView.playAnimation();
         }

         @Override
         public void onIndicatorPageChange(int newIndicatorPosition) {

         }
      });
   }

   private ArrayList<Integer> createList() {
      ArrayList<Integer> items = new ArrayList<>();
      items.add(0);
      items.add(1);
      items.add(2);
      items.add(3);
      return items;
   }
}
