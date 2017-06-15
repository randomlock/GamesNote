package com.example.randomlocks.gamesnote.Fragments;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
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
import android.widget.Toast;

import com.example.randomlocks.gamesnote.Adapter.GameNewsAdapter;
import com.example.randomlocks.gamesnote.HelperClass.CustomView.AVLoadingIndicatorView;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.SharedPreference;
import com.example.randomlocks.gamesnote.Modal.NewsModal.NewsModal;
import com.example.randomlocks.gamesnote.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class GamesNewsFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    public static final String KEY = "navigation_news_id"; //FOR SAVING MENU ITEM
    public static final String TITLE = "navigation_news_title"; //FOR MENU TOOLBAR TITLE
    private static final String TAG = "okhttp_tag";
    private static final String MODAL = "news_modal";
    private static final String SCROLL_POSITION = "recycler_scroll_position";
    private static final String DEFAULT_TITLE = "GiantBomb";
    public DrawerLayout mDrawer;
    public String URL = "http://www.eurogamer.net/?format=rss";
    public String BASE_URL;
    NavigationView mNavigation;
    RecyclerView recyclerView;
    AVLoadingIndicatorView progressBar;
    Toolbar toolbar;
    CoordinatorLayout coordinatorLayout;
    List<NewsModal> modals;
    DividerItemDecoration itemDecoration;
    GameNewsAdapter gameNewsAdapter;
    LinearLayoutManager layoutManager;
    String mTitle;
    int mSelectedId;
    boolean isReduced;
    SwipeRefreshLayout refreshLayout;

    OkHttpClient okHttpClient;


    public GamesNewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        itemDecoration = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        isReduced = SharedPreference.getFromSharedPreferences(GiantBomb.REDUCE_NEWS_VIEW, true, getContext());

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
        refreshLayout = (SwipeRefreshLayout) coordinatorLayout.findViewById(R.id.swipeContainer);
        recyclerView = (RecyclerView) refreshLayout.findViewById(R.id.news_recycler_view);
        toolbar = (Toolbar) coordinatorLayout.findViewById(R.id.my_toolbar);
        progressBar = (AVLoadingIndicatorView) coordinatorLayout.findViewById(R.id.indicator);
        layoutManager = new LinearLayoutManager(getContext());
        /****************** toolbar ************************/

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();



        mSelectedId = SharedPreference.getFromSharedPreferences(KEY, R.id.nav_kotaku, getContext());
        mTitle = SharedPreference.getFromSharedPreferences(TITLE, getResources().getString(R.string.kotaku), getContext());
        mNavigation.setCheckedItem(mSelectedId);
        selectDrawer(mSelectedId, mTitle);
        refreshLayout.setColorSchemeResources(R.color.primary, R.color.primary_dark, R.color.accent, R.color.primary_character);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    runOkHttp();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        if (savedInstanceState != null) {
            modals = savedInstanceState.getParcelableArrayList(MODAL);
            loadRecycler(modals, savedInstanceState.getParcelable(SCROLL_POSITION));
        } else {
            if (modals == null) {


                progressBar.setVisibility(View.VISIBLE);


                try {
                    runOkHttp();
                } catch (IOException e) {
                    Toasty.error(getContext(),"connectivity problem", Toast.LENGTH_SHORT,true).show();

                }
            } else {
                loadRecycler(modals, null);
            }
        }

        mNavigation.setNavigationItemSelectedListener(this);


    }


    public void runOkHttp() throws IOException {

        if (okHttpClient != null) {
            GiantBomb.cancelCallWithTag(okHttpClient, TAG);
        }

        okHttpClient = GiantBomb.getHttpClient();
        okHttpClient.newCall(GiantBomb.getRequest(URL, TAG)).enqueue(new Callback() {

            Handler mainHandler = new Handler(Looper.getMainLooper());

            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (refreshLayout.isRefreshing()) {
                            refreshLayout.setRefreshing(false);
                        }
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

                if (response != null) {
                    String str = response.body().string();
                    NewsModal mod = new NewsModal();
                    try {
                        modals = mod.parse(str);
                        for (int i = 0, size = modals.size(); i < size; i++) {
                            Document document = Jsoup.parse(modals.get(i).description);
                            modals.get(i).smallDescription = document.text();
                            if (modals.get(i).content == null) {
                                Element element = null;
                                Elements elements = document.getElementsByTag("img");
                                String jsoupImageUrl = null;
                                if (elements != null && elements.size() > 0) {
                                    element = elements.get(0);
                                    if (element.hasAttr("src")) {
                                        jsoupImageUrl = element.attr("src");
                                        element.remove();
                                        modals.get(i).description = document.toString();
                                    }
                                }


                                if (jsoupImageUrl != null) {
                                    //for eurogamer relative img
                                    if (jsoupImageUrl.substring(0, 2).equals("//")) {
                                        jsoupImageUrl = "http:" + jsoupImageUrl;
                                    }

                                    modals.get(i).content = jsoupImageUrl;

                                }
                            }

                            Elements iframeElements = document.getElementsByTag("iframe");

                            if (iframeElements != null && iframeElements.size() > 0) {
                                for (Element iframeElement : iframeElements) {

                                    if (iframeElement.hasAttr("src")) {
                                        String iframeSrc = iframeElement.attr("src");
                                        if (!iframeSrc.contains("http")) {
                                            iframeSrc = BASE_URL + iframeSrc;
                                            iframeElement.attr("src", iframeSrc);
                                            modals.get(i).description = document.toString();
                                            modals.get(i).description = document.toString();
                                        }

                                    }
                                }
                            }


                        }

                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (refreshLayout.isRefreshing()) {
                                    refreshLayout.setRefreshing(false);
                                }
                                loadRecycler(modals, null);
                            }
                        });


                    } catch (XmlPullParserException e) {
                        Log.d("tag", e.toString());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                Toasty.error(getContext(), "Unable to get the news feed", Toast.LENGTH_SHORT, true).show();

                            }
                        });

                    }
                }
            } //function end
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
            menu.getItem(menu.size()-1).setIcon(R.drawable.ic_gamelist_white);

        } else {
            menu.getItem(0).setTitle(getString(R.string.compact_view));
            menu.getItem(menu.size()-1).setIcon(R.drawable.ic_compat_white);

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


                if (gameNewsAdapter!=null) {
                    if (item.getTitle().equals(getString(R.string.compact_view))) {
                        isReduced = false;
                        item.setTitle(getString(R.string.reduce_view));
                        item.setIcon(R.drawable.ic_compat_white);



                    } else {
                        item.setTitle(getString(R.string.compact_view));
                        item.setIcon(R.drawable.ic_gamelist_white);

                        isReduced = true;
                    }

                    gameNewsAdapter.setSimple(isReduced);


                    if (recyclerView != null) {
                        if (isReduced) {
                            recyclerView.addItemDecoration(itemDecoration);
                        } else {
                            recyclerView.removeItemDecoration(itemDecoration);
                        }
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


            case R.id.nav_gamespot:
                URL = "http://www.gamespot.com/feeds/news/";
                BASE_URL = "http://www.gamespot.com";
                break;

            case R.id.nav_eurogamer:
                URL = "http://www.eurogamer.net/?format=rss&type=news";
                BASE_URL = "http://www.eurogamer.net";
                break;

            case R.id.nav_g4tv:
                URL = "http://feeds.g4tv.com/g4tv/thefeed?format=xml";
                BASE_URL = "http://g4tv.com";

                break;

            case R.id.nav_gameinformer:
                URL = "https://www.gameinformer.com/b/mainfeed.aspx?Tags=news";
                BASE_URL = "https://www.gameinformer.com";

                break;

            case R.id.nav_videogamer:
                URL = "http://feeds.videogamer.com/rss/news.xml";
                BASE_URL = "http://www.videogamer.com";

                break;

            case R.id.nav_pcworld:
                URL = "http://www.pcworld.com/index.rss";
                BASE_URL = "http://www.pcworld.com";

                break;

            case R.id.nav_venturebeat:
                BASE_URL = "http://venturebeat.com";
                URL = "http://venturebeat.com/category/games/feed/";

                break;

            case R.id.toms_harware:
                URL = "http://www.tomshardware.com/feeds/rss2/all.xml";
                BASE_URL = "http://www.tomshardware.com";

                break;

            case R.id.ign:
                URL = "http://feeds.ign.com/ign/games-all?format=xml";
                //  URL = "http://feeds.ign.com/ign/all?format=xml";
                BASE_URL = "http://www.ign.com";

                break;

            case R.id.nav_playstation4:
                URL = "http://cdn.us.playstation.com/pscomauth/groups/public/documents/webasset/rss/playstation/Games_PS4.rss";
                BASE_URL = "";

                break;

            case R.id.nav_purexbox:
                URL = "http://www.purexbox.com/feeds/news";
                BASE_URL = "http://www.purexbox.com";

                break;

            case R.id.nav_news_gamespot:
                URL = "http://www.gamespot.com/feeds/reviews/";
                BASE_URL = "http://www.gamespot.com";

                break;


            case R.id.nav_news_ign:
                URL = "http://feeds.ign.com/ign/reviews?format=xml";
                BASE_URL = "http://www.ign.com";

                break;

            case R.id.nav_news_gameinformer:
                URL = "https://www.gameinformer.com/b/mainfeed.aspx?Tags=review";
                BASE_URL = "https://www.gameinformer.com";

                break;

            case R.id.nav_news_pcworld:
                URL = "http://www.pcworld.com/reviews/index.rss";
                BASE_URL = "http://www.pcworld.com";

                break;

            case R.id.nav_news_metaleater:
                URL = "http://metaleater.com/rss-feeds/game-reviews";
                BASE_URL = "http://metaleater.com";

                break;


            case R.id.nav_kotaku:
                URL = "http://feeds.gawker.com/kotaku/vip";
                BASE_URL = "http://kotaku.com";

                break;

            case R.id.nac_pc_gamer:
                URL = "http://www.pcgamer.com/rss/?feed=rss";
                BASE_URL = "http://www.pcgamer.com";
                break;

            case R.id.nav_dsogaming:
                URL = "http://www.dsogaming.com/feed/";
                BASE_URL = "http://dsogaming.com";
                break;

            case R.id.nav_giantbomb:
                URL = "http://www.giantbomb.com/feeds/news/";
                BASE_URL = "http://www.giantbomb.com/";
                break;

            case R.id.nav_giantbomb_release :
                URL = "http://www.giantbomb.com/feeds/new_releases/";
                BASE_URL = "http://www.giantbomb.com/";
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
        if (modals != null) {
            outState.putParcelableArrayList(MODAL, new ArrayList<>(modals));
        }
        if (recyclerView != null && recyclerView.getLayoutManager() != null) {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (okHttpClient != null) {
            GiantBomb.cancelCallWithTag(okHttpClient, TAG);
        }

    }
}
