package com.example.randomlocks.gamesnote.HelperClass.CustomView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.widget.Toast;

import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import es.dmoral.toasty.Toasty;

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
        Toasty.error(getContext(),"Cannot load background image", Toast.LENGTH_SHORT,true).show();
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}
