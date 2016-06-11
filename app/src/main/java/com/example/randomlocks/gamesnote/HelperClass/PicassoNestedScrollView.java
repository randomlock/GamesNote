package com.example.randomlocks.gamesnote.HelperClass;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by randomlocks on 6/10/2016.
 */
public class PicassoNestedScrollView extends NestedScrollView implements Target {
    public PicassoNestedScrollView(Context context) {
        super(context);
    }

    public PicassoNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PicassoNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        setBackground(new BitmapDrawable(getResources(), bitmap));

    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        Toaster.make(getContext(), "Cannot load background image");
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}
