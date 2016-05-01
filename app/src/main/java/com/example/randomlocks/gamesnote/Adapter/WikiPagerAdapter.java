package com.example.randomlocks.gamesnote.Adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.randomlocks.gamesnote.R;

/**
 * Created by randomlocks on 4/24/2016.
 */
public class WikiPagerAdapter extends PagerAdapter {



Context context;
private ImageView imageView;

    public WikiPagerAdapter(Context context){
        this.context = context;
    }




    @Override
    public int getCount() {
        return 3;
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

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view =  inflater.inflate(R.layout.viewpager_wiki_image, container, false);
        imageView = (ImageView) view.findViewById(R.id.image);
        container.addView(view);


        return view;
    }
}
