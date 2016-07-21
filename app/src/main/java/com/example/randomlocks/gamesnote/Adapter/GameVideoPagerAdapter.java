package com.example.randomlocks.gamesnote.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.randomlocks.gamesnote.Fragments.ViewPagerFragment.GameVideoOtherPagerAdapter;
import com.example.randomlocks.gamesnote.Fragments.ViewPagerFragment.GameVideoPagerFragment;

/**
 * Created by randomlocks on 7/19/2016.
 */
public class GameVideoPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 3;
    private Context context;
    String pageTitle[] = {"Videos", "Favorite", "Watch later"};

    public GameVideoPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return GameVideoPagerFragment.newInstance(position);
            case 1:
                return GameVideoOtherPagerAdapter.newInstance(position);
            case 2:
                return GameVideoOtherPagerAdapter.newInstance(position);


            default:
                return GameVideoPagerFragment.newInstance(position);

        }

    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pageTitle[position];
    }
}
