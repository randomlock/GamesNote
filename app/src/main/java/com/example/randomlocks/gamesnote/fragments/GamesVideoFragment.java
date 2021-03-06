package com.example.randomlocks.gamesnote.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.MediaRouteButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.fragments.ViewPagerFragment.GameVideoOtherPagerFragment;
import com.example.randomlocks.gamesnote.fragments.ViewPagerFragment.GameVideoPagerFragment;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.cast.framework.CastStateListener;
import com.google.android.gms.cast.framework.IntroductoryOverlay;

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

    CastContext mCastContext;
    MediaRouteButton mMediaRouteButton;
    int viewpager_number;
    private IntroductoryOverlay mIntroductoryOverlay;
    private CastStateListener mCastStateListener;




    public GamesVideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mCastStateListener = new CastStateListener() {
            @Override
            public void onCastStateChanged(int newState) {
                if (newState != CastState.NO_DEVICES_AVAILABLE) {
                    showIntroductoryOverlay();
                }
            }
        };


        mCastContext = CastContext.getSharedInstance(getContext());

    }

    private void showIntroductoryOverlay() {
        if (mIntroductoryOverlay != null) {
            mIntroductoryOverlay.remove();
        }
        if ((mMediaRouteButton != null) && mMediaRouteButton.getVisibility() == View.VISIBLE) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mIntroductoryOverlay = new IntroductoryOverlay.Builder(
                            getActivity(), mMediaRouteButton)
                            .setTitleText("Introducing Cast")
                            .setSingleTime()
                            .setOnOverlayDismissedListener(
                                    new IntroductoryOverlay.OnOverlayDismissedListener() {
                                        @Override
                                        public void onOverlayDismissed() {
                                            mIntroductoryOverlay = null;
                                        }
                                    })
                            .build();
                    mIntroductoryOverlay.show();
                }
            });
        }
    }


    @Override
    public void onResume() {
        mCastContext.addCastStateListener(mCastStateListener);
        super.onResume();
    }

    @Override
    public void onPause() {
        mCastContext.removeCastStateListener(mCastStateListener);
        super.onPause();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_games_video, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDrawer = (DrawerLayout) getActivity().findViewById(R.id.drawer);
        tabLayout = (TabLayout) mDrawer.findViewById(R.id.my_tablayout);
        viewPager = (ViewPager) mDrawer.findViewById(R.id.my_pager);
        appBarLayout = (AppBarLayout) mDrawer.findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(this);
        mMediaRouteButton = (MediaRouteButton) mDrawer.findViewById(R.id.media_route_button);
        CastButtonFactory.setUpMediaRouteButton(getActivity().getApplicationContext(), mMediaRouteButton);
        floatingSearchView = (FloatingSearchView) mDrawer.findViewById(R.id.floating_search_view);
        floatingSearchView.inflateOverflowMenu(R.menu.game_video_menu);
     /*  CastButtonFactory.setUpMediaRouteButton(getActivity().getApplicationContext(),
                menu,
                R.id.media_route_menu_item);*/

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                viewpager_number = position;
            }

            @Override
            public void onPageSelected(int position) {

                if (position == 0) {
                    mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    floatingSearchView.setSearchFocusable(true);
                    floatingSearchView.inflateOverflowMenu(R.menu.game_video_menu);
                } else {
                    mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    floatingSearchView.inflateOverflowMenu(R.menu.empty_menu);
                    floatingSearchView.setSearchFocusable(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
      /*  ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);*/



        AppCompatActivity actionBar = (AppCompatActivity) getActivity();
        DrawerLayout drawer = (DrawerLayout) actionBar.findViewById(R.id.drawer_layout);
        if (drawer != null) {
            floatingSearchView.attachNavigationDrawerToMenuButton(drawer);
        }


        viewPager.setAdapter(new GameVideoPagerAdapter(getChildFragmentManager(), getContext()));
        viewPager.setOffscreenPageLimit(3);
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

    public void updateViewPager() {
        if (viewPager != null && viewPager.getAdapter() != null) {
            viewPager.getAdapter().notifyDataSetChanged();
        }
    }

    public void updateViewPagerFragment(boolean isReduced) {
        int pos = viewPager.getCurrentItem();
        GameVideoPagerAdapter adapter = (GameVideoPagerAdapter) viewPager.getAdapter();
        // if first or last fragment then update only the adjacent right fragment and
        // left fragment respectively

        ((GameVideoOtherPagerFragment) adapter.getItem(pos + 1)).updateAdapter(isReduced);
        ((GameVideoOtherPagerFragment) adapter.getItem(pos + 2)).updateAdapter(isReduced);

    }


    private static class GameVideoPagerAdapter extends FragmentStatePagerAdapter {

        final int PAGE_COUNT = 3;
        String pageTitle[] = {"Videos", "Liked", "Watch later"};
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

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }



    }


}
