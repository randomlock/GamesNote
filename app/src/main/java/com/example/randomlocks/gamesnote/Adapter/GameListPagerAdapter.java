package com.example.randomlocks.gamesnote.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.randomlocks.gamesnote.Fragments.ViewPagerFragment.GamesListPagerFragment;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;

import java.util.ArrayList;

/**
 * Created by randomlocks on 3/17/2016.
 */
public class GameListPagerAdapter extends FragmentStatePagerAdapter {

    private final int PAGE_COUNT = 5;
    private Context context;
    private String pageTitle[] = {"Replaying", "Planning", "dropped", "playing", "completed"};
    private ArrayList<Fragment> fragments;

    public GameListPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        fragments = new ArrayList<>();
        fragments.add(GamesListPagerFragment.newInstance(GiantBomb.REPLAYING));
        fragments.add(GamesListPagerFragment.newInstance(GiantBomb.PLANNING));
        fragments.add(GamesListPagerFragment.newInstance(GiantBomb.DROPPED));
        fragments.add(GamesListPagerFragment.newInstance(GiantBomb.PLAYING));
        fragments.add(GamesListPagerFragment.newInstance(GiantBomb.COMPLETED));
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

  /*  @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }*/

    /*@Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }*/
}


