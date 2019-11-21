package com.example.a56_credit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.asksira.loopingviewpager.LoopingPagerAdapter;
import com.example.a56_credit.R;

import java.util.List;

public class OnboardingAdapter extends LoopingPagerAdapter<Integer> {

   public OnboardingAdapter(Context context, List<Integer> itemList, boolean isInfinite) {
      super(context, itemList, isInfinite);
   }

   @Override
   protected int getItemViewType(int listPosition) {
      return listPosition;
   }

   @Override
   protected View inflateView(int viewType, ViewGroup container, int listPosition) {
      if (viewType == 0)
         return LayoutInflater.from(context).inflate(R.layout.onboarding1, container, false);
      if (viewType == 1)
         return LayoutInflater.from(context).inflate(R.layout.onboarding2, container, false);
      if (viewType == 2)
         return LayoutInflater.from(context).inflate(R.layout.onboarding3, container, false);
      return LayoutInflater.from(context).inflate(R.layout.onboarding4, container, false);
   }

   @Override
   protected void bindView(View convertView, int listPosition, int viewType) {
   }

}
