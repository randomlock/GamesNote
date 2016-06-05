package com.example.randomlocks.gamesnote.Adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by randomlocks on 4/24/2016.
 */
public class WikiPagerAdapter extends PagerAdapter {



Context context;
    int count;
private ImageView imageView;
    ArrayList<String> images;

    public WikiPagerAdapter(Context context,int count,ArrayList<String> images){
        this.context = context;
        this.count = count;
        this.images = new ArrayList<>(images);
    }




    @Override
    public int getCount() {
        return count;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==(FrameLayout)object;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((FrameLayout) object);
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        String imageUrl = images.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view =  inflater.inflate(R.layout.viewpager_wiki_image, container, false);
        imageView = (ImageView) view.findViewById(R.id.image);
        Picasso.with(context).load(imageUrl).fit().into(imageView);

        container.addView(view);


        return view;
    }


}
