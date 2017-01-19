package com.example.randomlocks.gamesnote.Adapter;

import android.content.Context;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.randomlocks.gamesnote.Activity.ImageViewPagerActivity;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.HelperClass.TouchImageView;
import com.example.randomlocks.gamesnote.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by randomlocks on 4/24/2016.
 */
public class ImageViewerPagerAdapter extends PagerAdapter {


    Context context;
    private int count;
    private ArrayList<String> images;
    private boolean shouldFit = false;
    PhotoViewAttacher mAttacher;
    PhotoView imageView;


    public ImageViewerPagerAdapter(Context context, int count, ArrayList<String> images, boolean shouldFit) {
        this.context = context;
        this.count = count;
        this.images = new ArrayList<>(images);
        this.shouldFit = shouldFit;

    }


    @Override
    public int getCount() {
        return count;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        String imageUrl = images.get(position);
           imageView = new PhotoView(context);

        if (shouldFit) {
            Picasso.with(context).load(imageUrl).into(imageView,callback);
        } else {
            Picasso.with(context).load(imageUrl).into(imageView,callback);
        }

        container.addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


        return imageView;
    }

    private Callback callback = new Callback() {
        @Override
        public void onSuccess() {
            if(mAttacher!=null){
                mAttacher.update();
            }else{
                mAttacher = new PhotoViewAttacher(imageView);
            }
        }

        @Override
        public void onError() {

        }
    };





}
