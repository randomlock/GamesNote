package com.example.randomlocks.gamesnote.Activity;


import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.randomlocks.gamesnote.DialogFragment.ImageUrlFragment;
import com.example.randomlocks.gamesnote.Fragments.GameStatsFragment;
import com.example.randomlocks.gamesnote.Fragments.GamesCharacterWikiFragment;
import com.example.randomlocks.gamesnote.Fragments.GamesListFragment;
import com.example.randomlocks.gamesnote.Fragments.GamesNewsFragment;
import com.example.randomlocks.gamesnote.Fragments.GamesVideoFragment;
import com.example.randomlocks.gamesnote.Fragments.GamesWikiFragment;
import com.example.randomlocks.gamesnote.Fragments.SettingFragment;
import com.example.randomlocks.gamesnote.Fragments.ViewPagerFragment.NewsDetailPagerFragment;
import com.example.randomlocks.gamesnote.HelperClass.CustomView.PicassoFrameLayout;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.InputMethodHelper;
import com.example.randomlocks.gamesnote.HelperClass.InputMethodLeak.IMMLeaks;
import com.example.randomlocks.gamesnote.HelperClass.SharedPreference;
import com.example.randomlocks.gamesnote.Interface.VideoPlayInterface;
import com.example.randomlocks.gamesnote.Modal.NewsModal.NewsModal;
import com.example.randomlocks.gamesnote.R;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.squareup.picasso.Picasso;

import java.util.List;

import es.dmoral.toasty.Toasty;


/*

add share preference so that when user open app he is automatically in correct fragment

 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ImageUrlFragment.ImageUrlInterface, VideoPlayInterface {


    public static final String KEY = "NAViGATION_SELECTED_ID"; //FOR SAVING MENU ITEM
    public static final String TITLE = "NAVIGATION_SELECTED_TITLE"; //FOR MENU TOOLBAR TITLE
    public static final String DEFAULT_TITLE = "Game Wiki";
    private static final String FRAGMENT_KEY = "restored_fragment";
    public String mtitle;
    public ActionBarDrawerToggle mDrawableToggle;
    PicassoFrameLayout navHeaderLayout;
    int mselectedId;
    Fragment fragment;
    private DrawerLayout mDrawer;




  /*  static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IMMLeaks.fixFocusedViewLeak(getApplication());
        fragment = null;
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView mNavigation = (NavigationView) findViewById(R.id.navigation_view);
        View headerView = null;
        if (mNavigation != null) {
            headerView = mNavigation.inflateHeaderView(R.layout.navigaion_header);

            navHeaderLayout = (PicassoFrameLayout) headerView.findViewById(R.id.nav_header);


            final String imageUrl = SharedPreference.getFromSharedPreferences(GiantBomb.NAV_HEADER_URL, null, this);
            if (imageUrl != null) {
                navHeaderLayout.setUrl(imageUrl);
                navHeaderLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        navHeaderLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        Picasso.with(MainActivity.this).load(imageUrl).resize(navHeaderLayout.getWidth(), navHeaderLayout.getHeight()).centerCrop().error(R.drawable.news_image_drawable).into(navHeaderLayout);
                    }
                });
            } else
                navHeaderLayout.setBackgroundResource(R.drawable.news_image_drawable);


            mDrawableToggle = setupDrawerToggle(mDrawer);
            mDrawer.addDrawerListener(mDrawableToggle);
            mNavigation.setNavigationItemSelectedListener(this);
            mDrawer.addDrawerListener(new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {

                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    InputMethodHelper.hideKeyBoard(getWindow().getCurrentFocus(), MainActivity.this);

                }

                @Override
                public void onDrawerClosed(View drawerView) {

                }

                @Override
                public void onDrawerStateChanged(int newState) {

                }
            });

        }
        mselectedId = savedInstanceState == null ? R.id.nav_statistics : savedInstanceState.getInt(KEY);
        mtitle = savedInstanceState == null ? DEFAULT_TITLE : savedInstanceState.getString(TITLE);

        navHeaderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageUrlFragment imageUrlFragment = new ImageUrlFragment();
                imageUrlFragment.show(getSupportFragmentManager(), "imageUrl");
            }
        });




        /*     if(savedInstanceState!=null){
            fragment = getSupportFragmentManager().findFragmentByTag("GamesHome");
            if(fragment==null){
                fragment = getSupportFragmentManager().findFragmentByTag("GamesList");
            }
            if(fragment==null){
                fragment = getSupportFragmentManager().findFragmentByTag("GamesNews");

            }
            if(fragment==null){
                fragment = getSupportFragmentManager().findFragmentByTag("GamesWiki");
            }

            if(fragment==null){
                fragment = getSupportFragmentManager().findFragmentByTag("GamesVideo");
            }

        } */


        selectDrawerItem(mselectedId, mtitle);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawableToggle.onOptionsItemSelected(item)) {
            return true;
        }



        switch (item.getItemId()) {
            case android.R.id.home:
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                } else {

                    InputMethodHelper.hideKeyBoard(getWindow().getCurrentFocus(), this);
                    mDrawer.openDrawer(GravityCompat.START);

                }

                return true;
        }

        return super.onOptionsItemSelected(item);


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawableToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawableToggle.syncState();
    }


    public ActionBarDrawerToggle setupDrawerToggle(DrawerLayout drawerLayout) {
        return new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        item.setChecked(true);
        mselectedId = item.getItemId();
        mtitle = (String) item.getTitle();
        fragment = null;
        selectDrawerItem(mselectedId, (String) item.getTitle());
        mDrawableToggle.setDrawerIndicatorEnabled(true);
        return true;


    }

    //Selecting Item from Nav Drawer

    private void selectDrawerItem(int mselectedId, String title) {


        switch (mselectedId) {


            case R.id.nav_character:
                fragment = getSupportFragmentManager().findFragmentByTag("GamesHome");

                if (fragment == null) {
                    fragment = new GamesCharacterWikiFragment();
                }
                FragmentTransactionHelper("replace", fragment, "GamesHome");

                break;

            case R.id.nav_mylist:
                fragment = getSupportFragmentManager().findFragmentByTag("GamesList");

                if (fragment == null) {
                    fragment = new GamesListFragment();
                }
                FragmentTransactionHelper("replace", fragment, "GamesList");

                break;

            case R.id.nav_news:
                fragment = getSupportFragmentManager().findFragmentByTag("GamesNews");

                if (fragment == null) {
                    fragment = new GamesNewsFragment();
                }

                FragmentTransactionHelper("replace", fragment, "GamesNews");

                break;

            case R.id.nav_trailer:
                fragment = getSupportFragmentManager().findFragmentByTag("GamesVideo");


                if (fragment == null) {
                    fragment = new GamesVideoFragment();
                }
                FragmentTransactionHelper("replace", fragment, "GamesVideo");

                break;

            case R.id.nav_wiki:
                fragment = getSupportFragmentManager().findFragmentByTag("GamesWiki");

                if (fragment == null) {

                    fragment = new GamesWikiFragment();
                }
                FragmentTransactionHelper("replace", fragment, "GamesWiki");


                break;

            case R.id.nav_statistics :
                fragment = getSupportFragmentManager().findFragmentByTag("GameStats");

                if (fragment == null) {

                    fragment = new GameStatsFragment();
                }
                FragmentTransactionHelper("replace", fragment, "GamesStats");


                break;



            case R.id.nav_settings:

                fragment = getSupportFragmentManager().findFragmentByTag("GamesSetting");

                if (fragment == null) {

                    fragment = new SettingFragment();
                }
                FragmentTransactionHelper("replace", fragment, "GamesSetting");

                break;

            default:
                Toast.makeText(this, "default", Toast.LENGTH_SHORT).show();

        }

        setTitle(title);
        mDrawer.closeDrawer(GravityCompat.START);


    }

    // Fragment Transaction Method


    private void FragmentTransactionHelper(String operation, Fragment fragment, String tag) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        switch (operation) {
            case "replace":
                ft.replace(R.id.fragment_parent_layout, fragment, tag);


                break;
            case "add":
                ft.add(R.id.fragment_parent_layout, fragment, tag);

                break;
            case "remove":
                ft.add(R.id.fragment_parent_layout, fragment, tag);

                break;
        }


        ft.commit();

    }


    @Override
    protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        out.putInt(KEY, mselectedId);
        out.putString(TITLE, mtitle);

    }

    @Override
    public void onBackPressed() {


        GamesNewsFragment gamesNewsFragment = (GamesNewsFragment) getSupportFragmentManager().findFragmentByTag("GamesNews");
        GamesVideoFragment gamesVideoFragment = (GamesVideoFragment) getSupportFragmentManager().findFragmentByTag("GamesVideo");

        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawers();
        } else if (gamesNewsFragment != null && gamesNewsFragment.mDrawer.isDrawerOpen(GravityCompat.END)) {
            gamesNewsFragment.mDrawer.closeDrawers();
        } else if (gamesVideoFragment != null && gamesVideoFragment.mDrawer.isDrawerOpen(GravityCompat.END)) {
            gamesVideoFragment.mDrawer.closeDrawers();
        } else
            super.onBackPressed();

    }


    @Override
    public void onSelect(String url) {

        if (url == null) {
            navHeaderLayout.setBackgroundResource(R.drawable.news_image_drawable);
            SharedPreference.removeFromSharedPreference(GiantBomb.NAV_HEADER_URL, this);
        } else {
            navHeaderLayout.setUrl(url);
            Picasso.with(MainActivity.this).load(url).resize(navHeaderLayout.getWidth(), navHeaderLayout.getHeight()).centerCrop().placeholder(R.drawable.news_image_drawable).error(R.drawable.news_image_drawable).into(navHeaderLayout);
        }


    }


    public void startCharacterActivity(String apiUrl, String imageUrl,String title) {

       /* Fragment fragment = getSupportFragmentManager().findFragmentByTag("characterDetail");

        if (fragment == null) {
            fragment = CharacterDetailFragmentDELETE.newInstance(apiUrl, imageUrl);
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_parent_layout, fragment, "characterDetail");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();*/


        Intent intent = new Intent(this, CharacterDetailActivity.class);
        intent.putExtra(GiantBomb.API_URL, apiUrl);
        intent.putExtra(GiantBomb.IMAGE_URL, imageUrl);
        intent.putExtra(GiantBomb.TITLE,title);
        startActivity(intent);


    }


    public void setDarkTheme(boolean setDarkTheme) {
        if (setDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        Intent intent = getIntent();
        finish();

        startActivity(intent);


    }

    public void startNewsFragment(List<NewsModal> modalList, int position) {

        mDrawableToggle.setDrawerIndicatorEnabled(false);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("NewsDetail");

        if (fragment == null) {
            fragment = NewsDetailPagerFragment.newInstance(modalList,position);
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_parent_layout, fragment, "NewsDetail");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


    }

    @Override
    public void onVideoClick(String url, int id, int elapsed_time, int request_code) {
        Intent intent = new Intent(this, VideoPlayerActivity.class);
        intent.putExtra(GiantBomb.API_URL, url);
        intent.putExtra(GiantBomb.SEEK_POSITION, elapsed_time);
        startActivityForResult(intent, request_code);
    }

    @Override
    public void onYoutubeVideoClick(String url, int id) {
        if (url!=null) {
            Intent intent = YouTubeStandalonePlayer.createVideoIntent(this, GiantBomb.YOUTUBE_API_KEY, url, 0, true, false);
            startActivity(intent);
        }else {
            Toasty.error(this,"Not available on youtube").show();
        }

    }

    @Override
    public void onExternalPlayerVideoClick(String url, int id) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setDataAndType(Uri.parse(url), "video/mp4");
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("GamesVideo");
        fragment.onActivityResult(requestCode, resultCode, data);


    }


}
