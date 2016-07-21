package com.example.randomlocks.gamesnote.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.randomlocks.gamesnote.Adapter.GameVideoPagerAdapter;
import com.example.randomlocks.gamesnote.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GamesVideoFragment extends Fragment {


    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    public DrawerLayout mDrawer;




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
        mDrawer = (DrawerLayout) getView().findViewById(R.id.drawer);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) mDrawer.findViewById(R.id.collapsing_toolbar_layout);
        toolbar = (Toolbar) mCollapsingToolbarLayout.findViewById(R.id.my_toolbar);
        tabLayout = (TabLayout) mDrawer.findViewById(R.id.my_tablayout);
        viewPager = (ViewPager) mDrawer.findViewById(R.id.my_pager);

        /****DRAWER LAYOUT ***/
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                } else {
                    mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        /************* SETTING TOOLBAR *****************/
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.GameVideoFragment));
        mCollapsingToolbarLayout.setTitleEnabled(false);


        /************* SETTING Viewpager *****************/
        viewPager.setAdapter(new GameVideoPagerAdapter(getChildFragmentManager(), getContext()));
        tabLayout.setupWithViewPager(viewPager);

    }

}
