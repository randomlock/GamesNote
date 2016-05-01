package com.example.randomlocks.gamesnote.Fragments;


import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.randomlocks.gamesnote.Adapter.GameWikiAdapter;
import com.example.randomlocks.gamesnote.Adapter.WikiPagerAdapter;
import com.example.randomlocks.gamesnote.HelperClass.EndlessRecyclerOnScrollListener;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.Interface.GameWikiListInterface;
import com.example.randomlocks.gamesnote.MainActivity;
import com.example.randomlocks.gamesnote.Modal.GameWikiListModal;
import com.example.randomlocks.gamesnote.Modal.GameWikiModal;
import com.example.randomlocks.gamesnote.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class GamesWikiFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

ViewPager viewPager;
    View customView;
    Activity activity;
    Toolbar toolbar;
    DrawerLayout mDrawer;
    NavigationView mNavigation;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    LinearLayoutManager manager;
    List<GameWikiModal> listModals;
    GameWikiAdapter adapter;
    ObjectAnimator animation;

    public static final String LIMIT = "50";




    public GamesWikiFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activity = getActivity();
        listModals = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_games_wiki, container, false);
        customView = v.findViewById(R.id.custom);
        mDrawer = (DrawerLayout) v.findViewById(R.id.drawer);
        mNavigation = (NavigationView) v.findViewById(R.id.navigation);
        viewPager = (ViewPager) customView.findViewById(R.id.viewpager);
        recyclerView = (RecyclerView) customView.findViewById(R.id.recycler_view);
        progressBar = (ProgressBar) customView.findViewById(R.id.progressBar);


        setRetainInstance(true);
        return v ;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);




        mNavigation.setNavigationItemSelectedListener(this);


/***************************  SETTING THE VIEW PAGER ***********************/
        viewPager.setAdapter(new WikiPagerAdapter(getContext()));
        CircleIndicator indicator = (CircleIndicator) customView.findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);



        /***************************  SETTING THE TOOLBAR ***********************/


        toolbar = (Toolbar) customView.findViewById(R.id.my_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);



        /***************************** MAKING THE API CALL **************************/

       final GameWikiListInterface gameWikiListInterface =  GiantBomb.createService(GameWikiListInterface.class);
        final Map<String,String> map = new HashMap<>();
        map.put(GiantBomb.KEY,GiantBomb.API_KEY);
        map.put(GiantBomb.FORMAT,"JSON");
        map.put(GiantBomb.LIMIT, LIMIT);
        map.put(GiantBomb.OFFSET,"0");

        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        getGameWiki(gameWikiListInterface, map);

        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(manager) {
            @Override
            public void onLoadMore(int current_page) {

                int offset = Integer.parseInt(map.get(GiantBomb.OFFSET));
                offset+=Integer.parseInt(LIMIT);
                map.put(GiantBomb.OFFSET,String.valueOf(offset));

                getGameWiki(gameWikiListInterface, map);


            }
        });


        /************************ PROGRESS BAR ANIMATION ****************************/

        ;











        /*************************TOOLBAR PROPERTIES************************************/





    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.game_wiki_menu, menu);
       getSearchManager(getContext(), menu, false);
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.search :

                //handle the search
                break;
            default:
                super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }







    /*********************** SEARCH MANAGER FUNCTION ********************************/


    public void getSearchManager(Context context, Menu menu,boolean isDefaultIconified) {

        SearchManager searchManager = (SearchManager)context. getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(((AppCompatActivity) context).getComponentName()));
        searchView.setIconifiedByDefault(isDefaultIconified); // Do not iconify the widget; expand it by default

        MenuItem searchMenuItem = menu.findItem(R.id.search);
        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {


            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                if (toolbar != null) {
                    toolbar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (toolbar != null) {
                    toolbar.setBackgroundColor(Color.TRANSPARENT);
                }
                return true;
            }
        });
    }



    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }



/***************************** ACTUAL ASYNCHRONOUS API CALL ******************************/


    public void getGameWiki(GameWikiListInterface gameWikiListInterface,Map<String,String> map){

progressBar.setVisibility(View.VISIBLE);

        gameWikiListInterface.getResult(map).enqueue(new Callback<GameWikiListModal>() {
            @Override
            public void onResponse(Call<GameWikiListModal> call, Response<GameWikiListModal> response) {

                progressBar.setVisibility(View.GONE);


                if (listModals.isEmpty()) {
                    listModals = response.body().results;
                    adapter = new GameWikiAdapter(listModals,getContext(),recyclerView.getChildCount());
                    recyclerView.setAdapter(adapter);
                }
                else {
                    int size = adapter.getItemCount();
                    listModals.addAll(response.body().results);
                    adapter.notifyItemRangeInserted(size,listModals.size());

                }

            }

            @Override
            public void onFailure(Call<GameWikiListModal> call, Throwable t) {
                Toaster.make(getContext(), "Connectivity Problem");
            }
        });



    }


}
