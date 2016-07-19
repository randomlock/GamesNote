package com.example.randomlocks.gamesnote.Fragments.ViewPagerFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.randomlocks.gamesnote.R;

/**
 * Created by randomlocks on 7/19/2016.
 */
public class GameVideoOtherPagerAdapter extends Fragment {


    public GameVideoOtherPagerAdapter() {
    }

    public static GameVideoOtherPagerAdapter newInstance(int position) {

        Bundle args = new Bundle();

        GameVideoOtherPagerAdapter fragment = new GameVideoOtherPagerAdapter();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_games_video_pager, container, false);
    }
}
