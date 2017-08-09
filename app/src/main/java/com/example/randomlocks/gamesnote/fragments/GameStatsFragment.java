package com.example.randomlocks.gamesnote.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.fragments.ViewPagerFragment.GameDetailStatPagerFragment;
import com.example.randomlocks.gamesnote.fragments.ViewPagerFragment.GameStatPagerFragment;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameStatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameStatsFragment extends Fragment {

    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    GameStatsPagerAdapter adapter;



    public GameStatsFragment() {
        // Required empty public constructor
    }

    public static GameStatsFragment newInstance() {
        GameStatsFragment fragment = new GameStatsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_stats, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        toolbar = (Toolbar) getActivity().findViewById(R.id.my_toolbar);
        viewPager = (ViewPager) getActivity().findViewById(R.id.my_pager);
        tabLayout = (TabLayout) getActivity().findViewById(R.id.my_tablayout);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        adapter = new GameStatsFragment.GameStatsPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);

    }

    private class GameStatsPagerAdapter extends FragmentPagerAdapter {

        private final int PAGE_COUNT = 2;
        private String pageTitle[] = {"basic","detail"};
        private ArrayList<Fragment> fragments;

        private GameStatsPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<>();
            fragments.add(new GameStatPagerFragment());
            fragments.add(new GameDetailStatPagerFragment());
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
