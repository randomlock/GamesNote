package com.example.randomlocks.gamesnote.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.randomlocks.gamesnote.Fragments.ViewPagerFragment.GamesListPagerFragment;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.R;

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
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    GameListPagerAdapter adapter;

    public GamesListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentActivity = getActivity();
        setHasOptionsMenu(true);


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
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) fragmentActivity.findViewById(R.id.collapsing_toolbar_layout);


        viewPager = (ViewPager) fragmentActivity.findViewById(R.id.my_pager);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        AppCompatActivity actionBar = (AppCompatActivity) getActivity();
        DrawerLayout drawer = (DrawerLayout) actionBar.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        mCollapsingToolbarLayout.setTitle(getResources().getString(R.string.GameListFragment));

        adapter = new GameListPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(5);
        tabLayout.setupWithViewPager(viewPager);


        //  if (savedInstanceState!=null) {
        //     viewPager.setCurrentItem(savedInstanceState.getInt(POSITION));
        // }
        tabLayout.setupWithViewPager(viewPager);


    }


    //viewpager adapter

    class GameListPagerAdapter extends FragmentStatePagerAdapter {

        private final int PAGE_COUNT = 6;
        private String pageTitle[] = {"All","Replaying", "Planning", "dropped", "playing", "completed"};
        private ArrayList<Fragment> fragments;

        public GameListPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<>();
            fragments.add(GamesListPagerFragment.newInstance(GiantBomb.ALL_GAMES));
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








