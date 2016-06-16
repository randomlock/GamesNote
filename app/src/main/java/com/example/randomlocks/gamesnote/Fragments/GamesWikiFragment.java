package com.example.randomlocks.gamesnote.Fragments;


import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
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
import android.widget.TextView;

import com.example.randomlocks.gamesnote.Adapter.GameDetailPagerAdapter;
import com.example.randomlocks.gamesnote.Adapter.GameWikiAdapter;
import com.example.randomlocks.gamesnote.DialogFragment.SearchFilterFragment;
import com.example.randomlocks.gamesnote.HelperClass.AVLoadingIndicatorView;
import com.example.randomlocks.gamesnote.HelperClass.EndlessRecyclerOnScrollListener;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.InputMethodHelper;
import com.example.randomlocks.gamesnote.HelperClass.SharedPreference;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.Interface.GameWikiListInterface;
import com.example.randomlocks.gamesnote.MainActivity;
import com.example.randomlocks.gamesnote.Modal.GameWikiListModal;
import com.example.randomlocks.gamesnote.Modal.GameWikiModal;
import com.example.randomlocks.gamesnote.R;

import java.util.ArrayList;
import java.util.Arrays;
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
public class GamesWikiFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener,SearchFilterFragment.SearchFilterInterface {

    private static final String MODAL = "list_modal" ;
    private static final String SCROLL_POSITION = "recyclerScrollPosition" ;
    ViewPager viewPager;
    View customView;
    Toolbar toolbar;
    DrawerLayout mDrawer;
    NavigationView mNavigation;
    RecyclerView recyclerView;
    AVLoadingIndicatorView progressBar;
    LinearLayoutManager manager;
    List<GameWikiModal> listModals=null;
    GameWikiAdapter adapter;
    ObjectAnimator animation;
     Map<String,String> map;
    GameWikiListInterface gameWikiListInterface;
    TextView errorText;
    CoordinatorLayout coordinatorLayout;
    View v;

    public static final String LIMIT = "50";




    public GamesWikiFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        listModals = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

         v = inflater.inflate(R.layout.fragment_games_wiki, container, false);




        return v ;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        coordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.custom_view);
        mDrawer = (DrawerLayout) v.findViewById(R.id.drawer);
        mNavigation = (NavigationView) v.findViewById(R.id.navigation);
        viewPager = (ViewPager) coordinatorLayout.findViewById(R.id.viewpager);
        recyclerView = (RecyclerView) coordinatorLayout.findViewById(R.id.recycler_view);
        progressBar = (AVLoadingIndicatorView) coordinatorLayout.findViewById(R.id.progressBar);
        errorText = (TextView) coordinatorLayout.findViewById(R.id.errortext);




        /*************** SAVE INSTANCE *************************/

        if(savedInstanceState!=null && listModals!=null){
            listModals =  savedInstanceState.getParcelableArrayList(MODAL);
            if (!listModals.isEmpty()) {
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(new GameWikiAdapter(listModals, getContext(), recyclerView.getChildCount()));
                recyclerView.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable(SCROLL_POSITION));
            }
            Toaster.make(getContext(),"fragment saveinstancve");

        }


        mNavigation.setNavigationItemSelectedListener(this);


/***************************  SETTING THE VIEW PAGER ***********************/
        viewPager.setAdapter(new GameDetailPagerAdapter(getContext(), 4));
        CircleIndicator indicator = (CircleIndicator) coordinatorLayout.findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);



        /***************************  SETTING THE TOOLBAR ***********************/


        toolbar = (Toolbar) coordinatorLayout.findViewById(R.id.my_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);



        /***************************** MAKING THE API CALL **************************/

         gameWikiListInterface =  GiantBomb.createService(GameWikiListInterface.class);
        map = new HashMap<>();
        map.put(GiantBomb.KEY,GiantBomb.API_KEY);

        map.put(GiantBomb.FORMAT,"JSON");
        map.put(GiantBomb.LIMIT, LIMIT);
        map.put(GiantBomb.OFFSET,"0");

        String sort = sortValue(SharedPreference.getFromSharedPreferences(GiantBomb.WHICH,4,getContext()));
        boolean asc = SharedPreference.getFromSharedPreferences(GiantBomb.ASCENDING,true,getContext());

        if(!asc){
            sort+=":desc";
        }

        map.put(GiantBomb.SORT,sort);


        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        if (savedInstanceState == null || (listModals.isEmpty() && savedInstanceState!=null)) {
         //   Toaster.make(getContext(),"its null");
            getGameWiki(gameWikiListInterface, map);
        }

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




                break;

            case R.id.filter :

                SearchFilterFragment filterFragment = SearchFilterFragment.newInstance();
                filterFragment.setTargetFragment(this,0);
                filterFragment.show(getActivity().getSupportFragmentManager(),"seach filter");

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

   searchView.setOnQueryTextListener(this);


    }



    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }



/***************************** ACTUAL ASYNCHRONOUS API CALL ******************************/


    public void getGameWiki(final GameWikiListInterface gameWikiListInterface, final Map<String,String> map){

progressBar.setVisibility(View.VISIBLE);

        gameWikiListInterface.getResult(map).enqueue(new Callback<GameWikiListModal>() {
            @Override
            public void onResponse(Call<GameWikiListModal> call, Response<GameWikiListModal> response) {


                progressBar.setVisibility(View.GONE);


                if (listModals.isEmpty()) {
                    listModals = response.body().results;
                    adapter = new GameWikiAdapter(listModals, getContext(), recyclerView.getChildCount());
                    recyclerView.setAdapter(adapter);
                } else {

                    int size = adapter.getItemCount();
                    listModals.addAll(response.body().results);
                    adapter.notifyItemRangeInserted(size, listModals.size());

                }

                if (listModals.isEmpty()) {
                    errorText.setVisibility(View.VISIBLE);
                } else {
                    errorText.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<GameWikiListModal> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Snackbar.make(coordinatorLayout, "Connectivity Problem", Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getGameWiki(gameWikiListInterface, map);

                            }
                        }).show();

            }
        });



    }


    @Override
    public boolean onQueryTextSubmit(String query) {

        String field = "name:"+query;
        InputMethodHelper.hideKeyBoard(getActivity().getWindow().getCurrentFocus(), getContext());

            map.put(GiantBomb.FIELD,field);
            map.put(GiantBomb.OFFSET, "0");

        listModals.clear();
        if(errorText.getVisibility()==View.VISIBLE){
            errorText.setVisibility(View.GONE);
        }

        getGameWiki(gameWikiListInterface, map);


        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onSelect(int which, boolean asc) {

        List<String> arrayList =  Arrays.asList(getContext().getResources().getStringArray(R.array.search_filter));

    String sort = sortValue(which);

        if(!asc){
            sort+=":desc";
        }

        map.put(GiantBomb.SORT,sort);
        map.put(GiantBomb.OFFSET,"0");
        listModals.clear();
        Toaster.make(getContext(), map.toString());

        getGameWiki(gameWikiListInterface,map);



    }

    String sortValue(int which){

        switch (which){

            case 0 : return "original_release_date";

            case 1 : return  "date_added";

            case 2 : return "date_last_updated";

            case 3 : return "number_of_user_reviews";

            case 4 :

            default: return "none";


        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


            outState.putParcelableArrayList(MODAL, new ArrayList<>(listModals));
            outState.putParcelable(SCROLL_POSITION, recyclerView.getLayoutManager().onSaveInstanceState());



    }




}
