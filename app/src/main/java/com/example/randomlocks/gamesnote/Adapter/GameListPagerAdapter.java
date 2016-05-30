package com.example.randomlocks.gamesnote.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.randomlocks.gamesnote.Fragments.GamesListPagerFragment;

/**
 * Created by randomlocks on 3/17/2016.
 */
public class GameListPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 4;
    private Context context;
    String pageTitle[] = {"Playing","Planning","On hold","Dropped"};

    public GameListPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return GamesListPagerFragment.newInstance(position+1);
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


