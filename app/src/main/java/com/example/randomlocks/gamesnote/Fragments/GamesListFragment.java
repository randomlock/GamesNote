package com.example.randomlocks.gamesnote.Fragments;


import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.randomlocks.gamesnote.Adapter.GameListPagerAdapter;
import com.example.randomlocks.gamesnote.R;

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
        mCollapsingToolbarLayout.setTitle(getResources().getString(R.string.GameListFragment));

        adapter = new GameListPagerAdapter(getChildFragmentManager(), getContext());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(5);
        tabLayout.setupWithViewPager(viewPager);


        //  if (savedInstanceState!=null) {
        //     viewPager.setCurrentItem(savedInstanceState.getInt(POSITION));
        // }
        //tabLayout.setupWithViewPager(viewPager);


    }


}








