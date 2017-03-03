package com.example.randomlocks.gamesnote.HelperClass.CustomView;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.SharedPreference;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import es.dmoral.toasty.Toasty;

/**
 * Created by randomlocks on 6/13/2016.
 */
public class PicassoFrameLayout extends FrameLayout implements Target {


    String url;

    public PicassoFrameLayout(Context context) {
        super(context);
    }

    public PicassoFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PicassoFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PicassoFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        setBackground(new BitmapDrawable(getResources(), bitmap));
        SharedPreference.saveToSharedPreference(GiantBomb.NAV_HEADER_URL, url, getContext());
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        Toasty.error(getContext(),"Cannot load background image", Toast.LENGTH_SHORT,true).show();


    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }

    public void setUrl(String url) {
        this.url = url;
    }
}
