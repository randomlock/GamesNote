package com.example.randomlocks.gamesnote;


import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.randomlocks.gamesnote.Fragments.GamesHomeFragment;
import com.example.randomlocks.gamesnote.Fragments.GamesListFragment;
import com.example.randomlocks.gamesnote.Fragments.GamesNewsFragment;
import com.example.randomlocks.gamesnote.Fragments.GamesVideoFragment;
import com.example.randomlocks.gamesnote.Fragments.GamesWikiFragment;


/*

add share preference so that when user open app he is automatically in correct fragment

 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private Toolbar mytoolbar;
    public static final String KEY = "NAViGATION_SELECTED_ID"; //FOR SAVING MENU ITEM
    public static final String TITLE = "NAVIGATION_SELECTED_TITLE"; //FOR MENU TOOLBAR TITLE
    public static final String API_Key = "b318d66445bfc79e6d74a65fe52744b45b345948";
    public static final String DEFAULT_TITLE = "My list";
    private FrameLayout frameLayout;
    public String mtitle;
    int mselectedId;
    private DrawerLayout mDrawer;
    private NavigationView mNavigation;
    private ActionBarDrawerToggle mDrawableToggle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        frameLayout = (FrameLayout) findViewById(R.id.fragment_parent_layout);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigation = (NavigationView) findViewById(R.id.navigation_view);
        mytoolbar = (Toolbar) findViewById(R.id.my_toolbar);

        setSupportActionBar(mytoolbar);



        mDrawableToggle = setupDrawerToggle(mDrawer);
        mDrawer.addDrawerListener(mDrawableToggle);
        mNavigation.setNavigationItemSelectedListener(this);


mselectedId = savedInstanceState == null ? R.id.nav_wiki : savedInstanceState.getInt(KEY);
        mtitle = savedInstanceState == null ? DEFAULT_TITLE : savedInstanceState.getString(TITLE);
        selectDrawerItem(mselectedId, mtitle);



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
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



    public  ActionBarDrawerToggle setupDrawerToggle(DrawerLayout drawerLayout) {
      return  new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open,  R.string.drawer_close);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

    item.setChecked(true);
        mselectedId = item.getItemId();
    mtitle = (String) item.getTitle();
        selectDrawerItem(mselectedId, (String) item.getTitle());
        return  true ;


    }

    //Selecting Item from Nav Drawer

    private void selectDrawerItem(int mselectedId,String title) {




        switch (mselectedId){

            case R.id.nav_home :
                FragmentTransactionHelper("replace",new GamesHomeFragment(),"GamesHome");
                break;

            case R.id.nav_mylist :
                Toast.makeText(this,"My list",Toast.LENGTH_SHORT).show();
                FragmentTransactionHelper("replace",new GamesListFragment(),"GamesList");
                break;

            case R.id.nav_news :
                Toast.makeText(this,"Games News",Toast.LENGTH_SHORT).show();
                FragmentTransactionHelper("replace", new GamesNewsFragment(), "GamesNews");
                break;

            case R.id.nav_trailer :
                Toast.makeText(this,"Games trailer",Toast.LENGTH_SHORT).show();
                FragmentTransactionHelper("replace", new GamesVideoFragment(), "GamesVideo");
                break;

            case R.id.nav_wiki :
                Toast.makeText(this,"Game Wiki restaring ?",Toast.LENGTH_SHORT).show();
                FragmentTransactionHelper("replace", new GamesWikiFragment(), "GamesWiki");
                break;

            default:
                Toast.makeText(this,"default",Toast.LENGTH_SHORT).show();

        }

        setTitle(title);
        mDrawer.closeDrawer(GravityCompat.START);


    }

    // Fragment Transaction Method


    private void FragmentTransactionHelper(String operation,Fragment fragment,String tag) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        if(operation.equals("replace")){
            ft.replace(R.id.fragment_parent_layout, fragment, tag);
            ft.addToBackStack(null);

        }

       else if(operation.equals("add")){
            ft.add(R.id.fragment_parent_layout,fragment,tag);

        }

       else if(operation.equals("remove")){
            ft.add(R.id.fragment_parent_layout,fragment,tag);

        }

        ft.commit();

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY, mselectedId);
        outState.putString(TITLE,mtitle);
    }


    @Override
    public void onBackPressed() {
        if(mDrawer.isDrawerOpen(GravityCompat.START)){
            mDrawer.closeDrawers();
        }
        else
            super.onBackPressed();

    }
}
