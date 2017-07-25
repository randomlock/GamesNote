package com.example.randomlocks.gamesnote.Fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.randomlocks.gamesnote.Fragments.ViewPagerFragment.NewsDetailPagerFragment;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.PagerZoomOutSlideAnimation;
import com.example.randomlocks.gamesnote.Modal.NewsModal.NewsModal;
import com.example.randomlocks.gamesnote.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by randomlock on 2/23/2017.
 */

public class NewsDetailFragment extends Fragment {

    ViewPager viewPager;
    List<NewsModal> modalList;
    int position;
    Toolbar toolbar;


    public NewsDetailFragment() {

    }

    public static NewsDetailFragment newInstance(List<NewsModal> modalList, int position) {

        Bundle args = new Bundle();
        args.putParcelableArrayList(GiantBomb.MODAL, (ArrayList<? extends Parcelable>) modalList);
        args.putInt(GiantBomb.POSITION, position);
        NewsDetailFragment fragment = new NewsDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(GiantBomb.POSITION);
        modalList = getArguments().getParcelableArrayList(GiantBomb.MODAL);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news_detail, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
        toolbar = (Toolbar) getActivity().findViewById(R.id.my_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        viewPager.setAdapter(new NewsDetailPagerAdapter(getChildFragmentManager(), modalList));
        viewPager.setPageTransformer(false, new PagerZoomOutSlideAnimation());
        viewPager.post(new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(position, true);
            }
        });
      /*  new Handler().post(new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(position);
            }
        });*/
    }


    private class NewsDetailPagerAdapter extends FragmentStatePagerAdapter {

        List<NewsModal> modalList;
        ArrayList<Fragment> fragments;

        NewsDetailPagerAdapter(FragmentManager fm, List<NewsModal> modalList) {
            super(fm);
            this.modalList = modalList;
            fragments = new ArrayList<>(modalList.size());
            for (int i = 0; i < modalList.size(); i++) {
                fragments.add(NewsDetailPagerFragment.newInstance(modalList.get(i)));

            }
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return modalList.size();
        }


    }


}