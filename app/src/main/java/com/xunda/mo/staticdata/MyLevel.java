package com.xunda.mo.staticdata;

import android.content.Context;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xunda.mo.R;
import com.xunda.mo.main.info.MyInfo;

public class MyLevel {
    public static void setGrade(LinearLayout myLin, int Grade, Context mContext) {
        MyInfo myInfo = new MyInfo(mContext);
        int grade = myInfo.getUserInfo().getGrade();
        if (Grade != 0) {
            grade = Grade;
        }
//        int remainderCrown = grade % 256;
//        int merchantCrown = grade / 256;
//        if (remainderCrown == 0) {
//            imgGarde(myLin, merchantCrown, 1, mContext);
//            return;
//        }
        int remainderCrown = grade % 64;
        int merchantCrown = grade / 64;
        imgGarde(myLin, merchantCrown, 1, mContext);
        int remainderSun = remainderCrown % 16;
        int merchantSun = remainderCrown / 16;
        imgGarde(myLin, merchantSun, 2, mContext);
        int remainderMoon = remainderSun % 4;
        int merchantMoon = remainderSun / 4;
        imgGarde(myLin, merchantMoon, 3, mContext);
        int remainderStars = 0;
        int merchantStars = remainderMoon;
        imgGarde(myLin, merchantStars, 4, mContext);
    }

    public static void imgGarde(LinearLayout myFlow, int size, int type, Context mContext) {
        for (int i = 0; i < size; i++) {
            ImageView imageView = new ImageView(mContext);
            int widthDip = 1;
            if (type == 1) {
                widthDip = 18;
                imageView.setImageResource(R.mipmap.crown_icon);
            } else if (type == 2) {
                widthDip = 16;
                imageView.setImageResource(R.mipmap.sun_icon);
            } else if (type == 3) {
                widthDip = 10;
                imageView.setImageResource(R.mipmap.moon_icon);
            } else if (type == 4) {
                widthDip = 7;
                imageView.setImageResource(R.mipmap.stars_icon);
            }
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthDip, mContext.getResources().getDisplayMetrics());
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, mContext.getResources().getDisplayMetrics());
            LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(width, height);
            int margins = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, mContext.getResources().getDisplayMetrics());
            itemParams.setMargins(0,0,margins,0);
            imageView.setLayoutParams(itemParams);
            imageView.setTag(i);
            myFlow.addView(imageView);
            imageView.setOnClickListener(v -> {
            });
        }
    }

}
