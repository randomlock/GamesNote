package com.example.randomlocks.gamesnote.activity;


import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.dialogFragment.ImageUrlFragment;
import com.example.randomlocks.gamesnote.fragments.GameStatsFragment;
import com.example.randomlocks.gamesnote.fragments.GamesCharacterWikiFragment;
import com.example.randomlocks.gamesnote.fragments.GamesListFragment;
import com.example.randomlocks.gamesnote.fragments.GamesNewsFragment;
import com.example.randomlocks.gamesnote.fragments.GamesVideoFragment;
import com.example.randomlocks.gamesnote.fragments.GamesWikiFragment;
import com.example.randomlocks.gamesnote.fragments.NewsDetailFragment;
import com.example.randomlocks.gamesnote.helperClass.CustomView.PicassoFrameLayout;
import com.example.randomlocks.gamesnote.helperClass.GiantBomb;
import com.example.randomlocks.gamesnote.helperClass.InputMethodHelper;
import com.example.randomlocks.gamesnote.helperClass.InputMethodLeak.IMMLeaks;
import com.example.randomlocks.gamesnote.helperClass.SharedPreference;
import com.example.randomlocks.gamesnote.helperClass.WebViewHelper.CustomTabActivityHelper;
import com.example.randomlocks.gamesnote.helperClass.WebViewHelper.WebViewFallback;
import com.example.randomlocks.gamesnote.interfaces.VideoPlayInterface;
import com.example.randomlocks.gamesnote.modals.gamesVideoModal.GamesVideoModal;
import com.example.randomlocks.gamesnote.modals.newsModal.NewsModal;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;

import java.util.List;

import es.dmoral.toasty.Toasty;
import io.fabric.sdk.android.Fabric;



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
    ImageView nav_image_view;
    int mselectedId;
    Fragment fragment;
    private DrawerLayout mDrawer;

    private FirebaseAnalytics mFirebaseAnalytics;




  /*  static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IMMLeaks.fixFocusedViewLeak(getApplication());
        // Adding firebase analytics and crashlytics
        Fabric.with(this, new Crashlytics());
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        fragment = null;
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView mNavigation = (NavigationView) findViewById(R.id.navigation_view);
        disableNavigationViewScrollbars(mNavigation);
        View headerView = null;
        if (mNavigation != null) {
            headerView = mNavigation.inflateHeaderView(R.layout.navigaion_header);

            navHeaderLayout = (PicassoFrameLayout) headerView.findViewById(R.id.nav_header);
            nav_image_view = (ImageView) navHeaderLayout.findViewById(R.id.nav_image_view);


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

            } else {
                navHeaderLayout.setBackgroundResource(R.drawable.news_image_drawable);
                nav_image_view.setImageResource(R.drawable.app_icon_high_res);

            }


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
        mselectedId = savedInstanceState == null ? SharedPreference.getFromSharedPreferences(KEY,R.id.nav_wiki,this) : savedInstanceState.getInt(KEY);
        mtitle = savedInstanceState == null ? SharedPreference.getFromSharedPreferences(TITLE,DEFAULT_TITLE,this) : savedInstanceState.getString(TITLE);

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

        assert mNavigation != null;
        Log.d("tag1", mselectedId + "------" + R.id.nav_wiki);
        mNavigation.setCheckedItem(mselectedId);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        item.setChecked(true);
        if (item.getGroupId() != R.id.extra) {
            mselectedId = item.getItemId();
            mtitle = (String) item.getTitle();
        }
        fragment = null;
        selectDrawerItem(item.getItemId(), (String) item.getTitle());
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
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                break;

            case R.id.nav_giantbomb:
                runBrowser("http://www.giantbomb.com/");
                break;

            case R.id.nav_contact_us:
                String email = "abdullahh546@gmail.com";
                ShareCompat.IntentBuilder.from(this)
                        .setType("message/rfc822")
                        .addEmailTo(email)
                        .setSubject("Feedback")
                        //.setHtmlText(body) //If you are using HTML in your body text
                        .setChooserTitle("Send feedback")
                        .startChooser();
                break;


            default:
                fragment = getSupportFragmentManager().findFragmentByTag("GamesWiki");

                if (fragment == null) {

                    fragment = new GamesWikiFragment();
                }
                FragmentTransactionHelper("replace", fragment, "GamesWiki");
        }

        setTitle(title);
        mDrawer.closeDrawer(GravityCompat.START);


    }

    void runBrowser(String url) {
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().setShowTitle(true).addDefaultShareMenuItem().build();
        CustomTabActivityHelper.openCustomTab(
                this, customTabsIntent, Uri.parse(url), new WebViewFallback());
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
    protected void onPause() {
        super.onPause();
        SharedPreference.saveToSharedPreference(KEY,mselectedId,this);
        SharedPreference.saveToSharedPreference(TITLE,mtitle,this);

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
            Picasso.with(MainActivity.this).load(url).resize(navHeaderLayout.getWidth(), navHeaderLayout.getHeight()).centerInside().onlyScaleDown().placeholder(R.drawable.news_image_drawable).error(R.drawable.news_image_drawable).into(navHeaderLayout);
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




    public void startNewsFragment(List<NewsModal> modalList, int position) {

        mDrawableToggle.setDrawerIndicatorEnabled(false);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("NewsDetail");

        if (fragment == null) {
            fragment = NewsDetailFragment.newInstance(modalList, position);
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_parent_layout, fragment, "NewsDetail");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


    }

    @Override
    public void onVideoClick(GamesVideoModal modal, String url, int id, int elapsed_time, int request_code) {
        Intent intent = new Intent(this, VideoPlayerActivity.class);
        intent.putExtra(GiantBomb.API_URL, url);
        intent.putExtra(GiantBomb.SEEK_POSITION, elapsed_time);
        intent.putExtra(GiantBomb.MODAL, modal);
        startActivityForResult(intent, request_code);
    }

    @Override
    public void onYoutubeVideoClick(String url, int id) {
        if (url!=null) {
            Intent intent = YouTubeStandalonePlayer.createVideoIntent(this, GiantBomb.YOUTUBE_API_KEY, url, 0, true, false);
            startActivityForResult(intent, 1);
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

    private void disableNavigationViewScrollbars(NavigationView navigationView) {
        if (navigationView != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
            if (navigationMenuView != null) {
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }
        }
    }


}
