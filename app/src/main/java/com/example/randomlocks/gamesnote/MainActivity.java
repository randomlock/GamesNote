package com.example.randomlocks.gamesnote;


import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.randomlocks.gamesnote.DialogFragment.ImageUrlFragment;
import com.example.randomlocks.gamesnote.Fragments.GamesHomeFragment;
import com.example.randomlocks.gamesnote.Fragments.GamesListFragment;
import com.example.randomlocks.gamesnote.Fragments.GamesNewsFragment;
import com.example.randomlocks.gamesnote.Fragments.GamesVideoFragment;
import com.example.randomlocks.gamesnote.Fragments.GamesWikiFragment;
import com.example.randomlocks.gamesnote.Fragments.ImprovedWebViewFragment;
import com.example.randomlocks.gamesnote.Fragments.NewsDetailFragment;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.PicassoFrameLayout;
import com.example.randomlocks.gamesnote.HelperClass.SharedPreference;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


/*

add share preference so that when user open app he is automatically in correct fragment

 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,ImageUrlFragment.ImageUrlInterface {


    private Toolbar mytoolbar;
    public static final String KEY = "NAViGATION_SELECTED_ID"; //FOR SAVING MENU ITEM
    public static final String TITLE = "NAVIGATION_SELECTED_TITLE"; //FOR MENU TOOLBAR TITLE
    public static final String DEFAULT_TITLE = "Game Wiki";
    private FrameLayout frameLayout;
    PicassoFrameLayout navHeaderLayout;
    public String mtitle;
    int mselectedId;
    private DrawerLayout mDrawer;
    private NavigationView mNavigation;
    private ActionBarDrawerToggle mDrawableToggle;
    Fragment fragment = null ;

   /* static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        frameLayout = (FrameLayout) findViewById(R.id.fragment_parent_layout);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigation = (NavigationView) findViewById(R.id.navigation_view);
        mytoolbar = (Toolbar) findViewById(R.id.my_toolbar);
        View headerView = mNavigation.inflateHeaderView(R.layout.navigaion_header);

        navHeaderLayout = (PicassoFrameLayout) headerView.findViewById(R.id.nav_header);


        final String imageUrl = SharedPreference.getFromSharedPreferences(GiantBomb.NAV_HEADER_URL,null,this);
        if(imageUrl!=null){
            navHeaderLayout.setUrl(imageUrl);
            navHeaderLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    navHeaderLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    Picasso.with(MainActivity.this).load(imageUrl).resize(navHeaderLayout.getWidth(),navHeaderLayout.getHeight()).centerInside().into(navHeaderLayout);
                }
            });
        } else
        navHeaderLayout.setBackgroundResource(R.drawable.headerbackground);



        setSupportActionBar(mytoolbar);


        mDrawableToggle = setupDrawerToggle(mDrawer);
        mDrawer.addDrawerListener(mDrawableToggle);
        mNavigation.setNavigationItemSelectedListener(this);





        mselectedId = savedInstanceState == null ? R.id.nav_news : savedInstanceState.getInt(KEY);
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


            selectDrawerItem(mselectedId,mtitle);


    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       /*  switch (item.getItemId()) {
           case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        } */

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
        selectDrawerItem(mselectedId, (String) item.getTitle());
        return true;


    }

    //Selecting Item from Nav Drawer

    private void selectDrawerItem(int mselectedId, String title) {

            Toaster.make(this,title);

        switch (mselectedId) {



            case R.id.nav_home:
                fragment = getSupportFragmentManager().findFragmentByTag("GamesHome");
                if (fragment==null) {
                    fragment = new GamesHomeFragment();
                    FragmentTransactionHelper("replace", fragment, "GamesHome");
                }
                break;

            case R.id.nav_mylist:
                 fragment = getSupportFragmentManager().findFragmentByTag("GamesList");

                if (fragment==null) {
                    fragment = new GamesListFragment();
                    FragmentTransactionHelper("replace", fragment, "GamesList");
                }
                break;

            case R.id.nav_news:
                 fragment = getSupportFragmentManager().findFragmentByTag("GamesNews");

                if (fragment==null) {
                    fragment = new GamesNewsFragment();
                    FragmentTransactionHelper("replace", fragment, "GamesNews");
                }
                break;

            case R.id.nav_trailer:
                fragment = getSupportFragmentManager().findFragmentByTag("GamesVideo");

                if (fragment==null) {
                    fragment = new GamesVideoFragment();
                    FragmentTransactionHelper("replace",fragment, "GamesVideo");
                }
                break;

            case R.id.nav_wiki:
                fragment = getSupportFragmentManager().findFragmentByTag("GamesWiki");
                if(fragment==null){

                    fragment = new GamesWikiFragment();
                    FragmentTransactionHelper("replace", fragment, "GamesWiki");
                }

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
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawers();
        } else
            super.onBackPressed();

    }



    @Override
    public void onSelect(String url) {

        if(url==null){
            navHeaderLayout.setBackgroundResource(R.drawable.headerbackground);
            SharedPreference.removeFromSharedPreference(GiantBomb.NAV_HEADER_URL,this);
        }

        else{
            navHeaderLayout.setUrl(url);
            Picasso.with(this).load(url).into(navHeaderLayout);
        }


    }


    public void newsDetailFragmnet(String mDescription,String imageUrl,String title){

        Fragment fragment = getSupportFragmentManager().findFragmentByTag("news_detail");
        if(fragment==null){
            fragment = NewsDetailFragment.newInstance(mDescription,imageUrl,title);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.fragment_parent_layout,fragment,"news_detail");
            transaction.commit();
        }else {
            fragment.getArguments().putString("news_description",mDescription);
        }

    }

    public void loadWebView(String string) {
        Fragment fragment =   getSupportFragmentManager().findFragmentByTag("WebView");

        if(fragment==null){
            fragment = ImprovedWebViewFragment.newInstance(string);
           FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_parent_layout, fragment, "WebView");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

    }




}
