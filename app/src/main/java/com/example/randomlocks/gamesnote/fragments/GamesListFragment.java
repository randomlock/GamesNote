package com.example.randomlocks.gamesnote.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.fragments.ViewPagerFragment.GamesListPagerFragment;
import com.example.randomlocks.gamesnote.helperClass.GiantBomb;
import com.example.randomlocks.gamesnote.helperClass.SharedPreference;

import java.util.ArrayList;

//TODO improve speed of viewpager

/**
 * A simple {@link Fragment} subclass.
 */
public class GamesListFragment extends Fragment {

    private static final String POSITION = "position";
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    FragmentActivity fragmentActivity;
    GameListPagerAdapter adapter;

    public GamesListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentActivity = getActivity();


    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //   outState.putInt(POSITION,tabLayout.getSelectedTabPosition());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_games_list, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        toolbar = (Toolbar) fragmentActivity.findViewById(R.id.my_toolbar);
        tabLayout = (TabLayout) fragmentActivity.findViewById(R.id.my_tablayout);
        viewPager = (ViewPager) fragmentActivity.findViewById(R.id.my_pager);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        DrawerLayout drawer = (DrawerLayout) fragmentActivity.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        adapter = new GameListPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnAdapterChangeListener(new ViewPager.OnAdapterChangeListener() {
            @Override
            public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter oldAdapter, @Nullable PagerAdapter newAdapter) {


            }
        });


        //  if (savedInstanceState!=null) {
        //     viewPager.setCurrentItem(savedInstanceState.getInt(POSITION));
        // }


    }


    public void updateViewPager(){
        if (viewPager!=null && viewPager.getAdapter()!=null) {
            viewPager.getAdapter().notifyDataSetChanged();

        }
    }

    public void updateViewpagerFragment(boolean isSimple) {
        int pos = viewPager.getCurrentItem();
        // if first or last fragment then update only the adjacent right fragment and
        // left fragment respectively
        if (pos == 0) {
            ((GamesListPagerFragment) adapter.getItem(pos + 1)).updateAdapter(isSimple);
        } else if (pos == viewPager.getAdapter().getCount() - 1) {
            ((GamesListPagerFragment) adapter.getItem(pos - 1)).updateAdapter(isSimple);
        } else {
            //update left and right fragment
            ((GamesListPagerFragment) adapter.getItem(pos - 1)).updateAdapter(isSimple);
            ((GamesListPagerFragment) adapter.getItem(pos + 1)).updateAdapter(isSimple);
        }
    }


    //viewpager adapter

    private class GameListPagerAdapter extends FragmentStatePagerAdapter {

        private String pageTitle[] = {"All","Replaying", "Planning", "dropped", "playing", "completed"};
        private ArrayList<Fragment> fragments;

        GameListPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<>(pageTitle.length);
            boolean isSimple = SharedPreference.getFromSharedPreferences(GiantBomb.REDUCE_LIST_VIEW,false,getContext());
            fragments.add(GamesListPagerFragment.newInstance(GiantBomb.ALL_GAMES,isSimple));
            fragments.add(GamesListPagerFragment.newInstance(GiantBomb.REPLAYING,isSimple));
            fragments.add(GamesListPagerFragment.newInstance(GiantBomb.PLANNING,isSimple));
            fragments.add(GamesListPagerFragment.newInstance(GiantBomb.DROPPED,isSimple));
            fragments.add(GamesListPagerFragment.newInstance(GiantBomb.PLAYING,isSimple));
            fragments.add(GamesListPagerFragment.newInstance(GiantBomb.COMPLETED,isSimple));

        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return pageTitle.length;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return pageTitle[position];
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

    /*@Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }*/
    }


}








