package com.example.randomlocks.gamesnote.HelperClass;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Transformation;

import com.example.randomlocks.gamesnote.R;

/**
 * Created by randomlocks on 4/27/2016.
 */
public class MyAnimation {


    public static void expand(final View v,View icon,Context context) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        android.view.animation.Animation a = new android.view.animation.Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
        icon.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_arrow_drop_up_black_36dp));

    }

    public static void collapse(final View v,View icon,Context context) {
        final int initialHeight = v.getMeasuredHeight();

        android.view.animation.Animation a = new android.view.animation.Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);

        icon.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_arrow_drop_down_black_36dp));
    }

}
