package com.example.randomlocks.gamesnote.HelperClass.CustomView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by randomlocks on 7/9/2016.
 */
public class PicassoCoordinatorLayout extends CoordinatorLayout implements Target {
    public PicassoCoordinatorLayout(Context context) {
        super(context);
    }

    public PicassoCoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PicassoCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        setBackground(new BitmapDrawable(getResources(), bitmap));

    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}
