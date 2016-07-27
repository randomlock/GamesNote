package com.example.randomlocks.gamesnote.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.randomlocks.gamesnote.Adapter.GameNewsAdapter;
import com.example.randomlocks.gamesnote.HelperClass.AVLoadingIndicatorView;
import com.example.randomlocks.gamesnote.HelperClass.DividerItemDecoration;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.SharedPreference;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.Modal.NewsModal.NewsModal;
import com.example.randomlocks.gamesnote.R;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class GamesNewsFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    private static final String MODAL = "news_modal";
    private static final String SCROLL_POSITION = "recycler_scroll_position";
    private static final String DEFAULT_TITLE = "GiantBomb";
    public static final String KEY = "navigation_news_id"; //FOR SAVING MENU ITEM
    public static final String TITLE = "navigation_news_title"; //FOR MENU TOOLBAR TITLE
    public DrawerLayout mDrawer;
    NavigationView mNavigation;
    RecyclerView recyclerView;
    AVLoadingIndicatorView progressBar;
    Toolbar toolbar;
    CoordinatorLayout coordinatorLayout;
    List<NewsModal> modals;
    DividerItemDecoration itemDecoration;
    GameNewsAdapter gameNewsAdapter;
    LinearLayoutManager layoutManager;
    public String URL = "http://www.eurogamer.net/?format=rss";
    String mTitle;
    int mSelectedId;
    boolean isReduced;


    public GamesNewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        itemDecoration = new DividerItemDecoration(getContext());
        isReduced = SharedPreference.getFromSharedPreferences(GiantBomb.REDUCE_NEWS_VIEW, false, getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_games_news, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDrawer = (DrawerLayout) getView().findViewById(R.id.drawer);
        mNavigation = (NavigationView) mDrawer.findViewById(R.id.navigation);
        coordinatorLayout = (CoordinatorLayout) mDrawer.findViewById(R.id.coordinator);
        recyclerView = (RecyclerView) coordinatorLayout.findViewById(R.id.news_recycler_view);
        toolbar = (Toolbar) coordinatorLayout.findViewById(R.id.my_toolbar);
        progressBar = (AVLoadingIndicatorView) coordinatorLayout.findViewById(R.id.indicator);
        layoutManager = new LinearLayoutManager(getContext());
        /****************** toolbar ************************/

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSelectedId = SharedPreference.getFromSharedPreferences(KEY, R.id.nav_giantbomb, getContext());
        mTitle = SharedPreference.getFromSharedPreferences(TITLE, getResources().getString(R.string.giantbomb), getContext());
        mNavigation.setCheckedItem(mSelectedId);
        selectDrawer(mSelectedId, mTitle);


        if (savedInstanceState != null) {
            modals = savedInstanceState.getParcelableArrayList(MODAL);
            loadRecycler(modals, savedInstanceState.getParcelable(SCROLL_POSITION));
        } else {
            if (modals == null) {


                progressBar.setVisibility(View.VISIBLE);


                try {
                    runOkHttp();
                } catch (IOException e) {
                    Toaster.make(getContext(), "connectivity problem");
                }
            } else {
                loadRecycler(modals, null);
            }
        }

        mNavigation.setNavigationItemSelectedListener(this);


    }


    public void runOkHttp() throws IOException {


        GiantBomb.getHttpClient().newCall(GiantBomb.getRequest(URL)).enqueue(new Callback() {

            Handler mainHandler = new Handler(Looper.getMainLooper());


            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        Snackbar.make(coordinatorLayout, "Connectivity Problem", Snackbar.LENGTH_INDEFINITE)
                                .setAction("RETRY", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            progressBar.setVisibility(View.VISIBLE);
                                            runOkHttp();
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }

                                    }
                                }).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String str = response.body().string();
                NewsModal mod = new NewsModal();
                try {
                    modals = mod.parse(str);

                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            loadRecycler(modals, null);
                        }
                    });


                } catch (XmlPullParserException e) {
                    Log.d("tag", e.toString());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            Toaster.make(getContext(), "Unable to get the news feed");
                        }
                    });

                }
            }
        });


    }

    private void loadRecycler(List<NewsModal> modals, Parcelable parcelable) {

        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
        }
        recyclerView.setLayoutManager(layoutManager);
        if (parcelable != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(parcelable);
        }
        gameNewsAdapter = new GameNewsAdapter(modals, getContext(), isReduced);
        if (isReduced) {
            recyclerView.addItemDecoration(itemDecoration);
        }

        recyclerView.setAdapter(gameNewsAdapter);


    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (isReduced) {
            menu.getItem(0).setTitle(getString(R.string.reduce_view));
        } else {
            menu.getItem(0).setTitle(getString(R.string.compact_view));
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.game_news_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                getActivity().onBackPressed();
                return true;


            case R.id.view:


                if (item.getTitle().equals(getString(R.string.compact_view))) {
                    isReduced = true;
                    item.setTitle(getString(R.string.reduce_view));


                } else {
                    item.setTitle(getString(R.string.compact_view));
                    isReduced = false;
                }


                if (gameNewsAdapter != null) {
                    gameNewsAdapter.setSimple(isReduced);
                }

                if (recyclerView != null) {
                    if (isReduced) {
                        recyclerView.addItemDecoration(itemDecoration);
                    } else {
                        recyclerView.removeItemDecoration(itemDecoration);
                    }
                }


                return true;


            case R.id.drawer_right:

                mDrawer.openDrawer(GravityCompat.END);


        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        item.setChecked(true);
        mSelectedId = item.getItemId();
        mTitle = item.getTitle().toString();
        selectDrawer(mSelectedId, mTitle);

        changeNewsSource();

        return true;
    }

    private void selectDrawer(int mselectedId, String toolbarTitle) {

        switch (mselectedId) {

            case R.id.nav_giantbomb:
                URL = "http://www.giantbomb.com/feeds/news/";
                break;

            case R.id.nav_gamespot:
                URL = "http://www.gamespot.com/feeds/news/";
                break;

            case R.id.nav_eurogamer:
                URL = "http://www.eurogamer.net/?format=rss&type=news";
                break;

            case R.id.nav_g4tv:
                URL = "http://feeds.g4tv.com/g4tv/thefeed?format=xml";
                break;

            case R.id.nav_gameinformer:
                URL = "https://www.gameinformer.com/b/mainfeed.aspx?Tags=news";
                break;

            case R.id.nav_videogamer:
                URL = "http://feeds.videogamer.com/rss/news.xml";
                break;

            case R.id.nav_pcworld:
                URL = "http://www.pcworld.com/index.rss";
                break;

            case R.id.nav_venturebeat:
                URL = "http://venturebeat.com/category/games/feed/";
                break;

            case R.id.toms_harware:
                URL = "http://www.tomshardware.com/feeds/rss2/all.xml";
                break;

            case R.id.ign:
                // URL = "http://feeds.ign.com/ign/games-all?format=xml";
                URL = "http://feeds.ign.com/ign/all?format=xml";
                break;

            case R.id.nav_playstation4:
                URL = "http://cdn.us.playstation.com/pscomauth/groups/public/documents/webasset/rss/playstation/Games_PS4.rss";
                break;

            case R.id.nav_purexbox:
                URL = "http://www.purexbox.com/feeds/news";
                break;

            case R.id.nav_news_gamespot:
                URL = "http://www.gamespot.com/feeds/reviews/";
                break;

            case R.id.nav_news_giantbomb:
                URL = "http://www.giantbomb.com/feeds/reviews/";
                break;

            case R.id.nav_news_ign:
                URL = "http://feeds.ign.com/ign/reviews?format=xml";
                break;

            case R.id.nav_news_gameinformer:
                URL = "https://www.gameinformer.com/b/mainfeed.aspx?Tags=review";
                break;

            case R.id.nav_news_pcworld:
                URL = "http://www.pcworld.com/reviews/index.rss";
                break;

            case R.id.nav_news_metaleater:
                URL = "http://metaleater.com/rss-feeds/game-reviews";
                break;

            case R.id.nav_giantbomb_release:
                URL = "http://www.giantbomb.com/feeds/new_releases/";
                break;


            case R.id.nav_kotaku:
                URL = "http://feeds.gawker.com/kotaku/full";
                break;


        }

        if (mDrawer.isDrawerOpen(GravityCompat.END)) {
            mDrawer.closeDrawers();
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(toolbarTitle);

    }


    void changeNewsSource() {

        progressBar.setVisibility(View.VISIBLE);
        if (modals != null && recyclerView.getAdapter() != null) {
            modals.clear();
            gameNewsAdapter.notifyDataSetChanged();
        }
        try {
            runOkHttp();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Toaster.make(getContext(), "coming here");
        if (modals != null) {
            outState.putParcelableArrayList(MODAL, new ArrayList<>(modals));
            outState.putParcelable(SCROLL_POSITION, recyclerView.getLayoutManager().onSaveInstanceState());
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        Context context = getContext();
        if (context != null) {
            SharedPreference.saveToSharedPreference(GiantBomb.REDUCE_NEWS_VIEW, isReduced, context);
            SharedPreference.saveToSharedPreference(KEY, mSelectedId, context);
            SharedPreference.saveToSharedPreference(TITLE, mTitle, context);
        }
    }
}
