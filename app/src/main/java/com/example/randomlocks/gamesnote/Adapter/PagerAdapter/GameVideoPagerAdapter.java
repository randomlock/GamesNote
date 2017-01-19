package com.example.randomlocks.gamesnote.Adapter.PagerAdapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.randomlocks.gamesnote.Fragments.ViewPagerFragment.GameVideoOtherPagerFragment;
import com.example.randomlocks.gamesnote.Fragments.ViewPagerFragment.GameVideoPagerFragment;

import java.util.ArrayList;

/**
 * Created by randomlocks on 7/19/2016.
 */
public class GameVideoPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 3;
    private Context context;
    String pageTitle[] = {"Videos", "Favorite", "Watch later"};
    ArrayList<Fragment> fragments;

    public GameVideoPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        fragments = new ArrayList<>();
        fragments.add(GameVideoPagerFragment.newInstance(0));
        fragments.add(GameVideoOtherPagerFragment.newInstance(1));
        fragments.add(GameVideoOtherPagerFragment.newInstance(2));
    }


    @Override
    public Fragment getItem(int position) {

        return fragments.get(position);
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
