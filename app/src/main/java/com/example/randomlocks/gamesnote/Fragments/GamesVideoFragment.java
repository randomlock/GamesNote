package com.example.randomlocks.gamesnote.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.example.randomlocks.gamesnote.Fragments.ViewPagerFragment.GameVideoOtherPagerFragment;
import com.example.randomlocks.gamesnote.Fragments.ViewPagerFragment.GameVideoPagerFragment;
import com.example.randomlocks.gamesnote.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class GamesVideoFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener {


    public DrawerLayout mDrawer;
    ViewPager viewPager;
    TabLayout tabLayout;
    AppBarLayout appBarLayout;
    FloatingSearchView floatingSearchView;



    public GamesVideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_games_video, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDrawer = (DrawerLayout) getActivity().findViewById(R.id.drawer);
        tabLayout = (TabLayout) mDrawer.findViewById(R.id.my_tablayout);
        viewPager = (ViewPager) mDrawer.findViewById(R.id.my_pager);
        appBarLayout = (AppBarLayout) mDrawer.findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(this);
        floatingSearchView = (FloatingSearchView) mDrawer.findViewById(R.id.floating_search_view);

        /****DRAWER LAYOUT ***/
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    floatingSearchView.inflateOverflowMenu(R.menu.game_video_menu);
                } else {
                    mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    floatingSearchView.inflateOverflowMenu(R.menu.empty_menu);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        /************* SETTING TOOLBAR *****************/
      /*  ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);*/



        AppCompatActivity actionBar = (AppCompatActivity) getActivity();
        DrawerLayout drawer = (DrawerLayout) actionBar.findViewById(R.id.drawer_layout);
        if (drawer != null) {
            floatingSearchView.attachNavigationDrawerToMenuButton(drawer);
        }


        /************* SETTING Viewpager *****************/
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(new GameVideoPagerAdapter(getChildFragmentManager(), getContext()));

        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        floatingSearchView.setTranslationY(verticalOffset);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (Fragment fragment : getChildFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

   private static class GameVideoPagerAdapter extends FragmentPagerAdapter {

        final int PAGE_COUNT = 3;
        String pageTitle[] = {"Videos", "Favorite", "Watch later"};
        ArrayList<Fragment> fragments;
       private Context context;

        GameVideoPagerAdapter(FragmentManager fm, Context context) {
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
}
