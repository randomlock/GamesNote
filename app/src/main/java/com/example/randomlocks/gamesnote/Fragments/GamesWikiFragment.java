package com.example.randomlocks.gamesnote.Fragments;


import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.Adapter.GameWikiAdapter;
import com.example.randomlocks.gamesnote.DialogFragment.SearchFilterFragment;
import com.example.randomlocks.gamesnote.HelperClass.CustomView.AVLoadingIndicatorView;
import com.example.randomlocks.gamesnote.HelperClass.CustomView.ConsistentLinearLayoutManager;
import com.example.randomlocks.gamesnote.HelperClass.EndlessRecyclerOnScrollListener;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.InputMethodHelper;
import com.example.randomlocks.gamesnote.HelperClass.SharedPreference;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.Interface.GameWikiListInterface;
import com.example.randomlocks.gamesnote.Interface.OnLoadMoreListener;
import com.example.randomlocks.gamesnote.Modal.GameWikiListModal;
import com.example.randomlocks.gamesnote.Modal.GameWikiModal;
import com.example.randomlocks.gamesnote.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class GamesWikiFragment extends Fragment implements SearchView.OnQueryTextListener, SearchFilterFragment.SearchFilterInterface {

    private static final String MODAL = "list_modal";
    private static final String SCROLL_POSITION = "recyclerScrollPosition";
    ViewPager viewPager;
    Toolbar toolbar;
    RecyclerView recyclerView;
    AVLoadingIndicatorView progressBar;
    ConsistentLinearLayoutManager manager;
    List<GameWikiModal> listModals = null;
    GameWikiAdapter adapter;
    Map<String, String> map;
    GameWikiListInterface gameWikiListInterface;
    TextView errorText;
    CoordinatorLayout coordinatorLayout;
    Context context;

    private static final String LIMIT = "50";


    public GamesWikiFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        listModals = new ArrayList<>();
        context = getContext();
        map = new HashMap<>(7);
        map.put(GiantBomb.KEY, GiantBomb.API_KEY);
        map.put(GiantBomb.FORMAT, "JSON");
        map.put(GiantBomb.LIMIT, LIMIT);
        map.put(GiantBomb.OFFSET, "0");
        String field_list = "api_detail_url,deck,expected_release_day,expected_release_month,expected_release_year,image,name,original_release_date,platforms";
        map.put(GiantBomb.FIELD_LIST, field_list);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_game_wiki, container, false);
        coordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.root_coordinator);
        toolbar = (Toolbar) coordinatorLayout.findViewById(R.id.my_toolbar);
        viewPager = (ViewPager) coordinatorLayout.findViewById(R.id.viewpager);
        recyclerView = (RecyclerView) coordinatorLayout.findViewById(R.id.recycler_view);
        progressBar = (AVLoadingIndicatorView) coordinatorLayout.findViewById(R.id.progressBar);
        errorText = (TextView) coordinatorLayout.findViewById(R.id.errortext);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


/***************************  SETTING THE VIEW PAGER ***********************/
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("http://www.trbimg.com/img-568482b1/turbine/la-et-hc-1231-the-player-2016-20151231-002/650/650x366");
        arrayList.add("http://images.medicaldaily.com/sites/medicaldaily.com/files/styles/headline/public/2016/03/08/video-games.jpg");
        arrayList.add("http://i.investopedia.com/inv/articles/slideshow/5-top-video-game-characters/game-characters.jpg");
        arrayList.add("http://img3.rnkr-static.com/list_img_v2/3654/283654/full/the-best-female-video-game-characters-u1.jpg");

    //    viewPager.setAdapter(new ImageViewerPagerAdapter(context, 4, arrayList, true));
        CircleIndicator indicator = (CircleIndicator) coordinatorLayout.findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);
        // pageSwitcher(5);


        /***************************  SETTING THE TOOLBAR ***********************/

        AppCompatActivity actionBar = (AppCompatActivity) getActivity();
        actionBar.setSupportActionBar(toolbar);
        actionBar.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        DrawerLayout drawer = (DrawerLayout) actionBar.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        /*************** SAVE INSTANCE *************************/


        if (savedInstanceState != null) {
            listModals = savedInstanceState.getParcelableArrayList(MODAL);
            fillRecycler(listModals, savedInstanceState.getParcelable(SCROLL_POSITION));
        } else {
            if (listModals != null && !listModals.isEmpty()) {
                fillRecycler(listModals, null);
            } else {


                /***************************** MAKING THE API CALL **************************/

                gameWikiListInterface = GiantBomb.createGameWikiService();


                String sort = sortValue(SharedPreference.getFromSharedPreferences(GiantBomb.WHICH, 4, context));
                boolean asc = SharedPreference.getFromSharedPreferences(GiantBomb.ASCENDING, true, context);

                if (!asc) {
                    sort += ":desc";
                }
                map.put(GiantBomb.SORT, sort);


                manager = new ConsistentLinearLayoutManager(context);
                recyclerView.setLayoutManager(manager);
                getGameWiki(gameWikiListInterface, map);

            }


        }


       /* if (manager != null) {
            recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(manager) {
                @Override
                public void onLoadMore(int current_page) {

                    int offset = Integer.parseInt(map.get(GiantBomb.OFFSET));
                    offset += Integer.parseInt(LIMIT);
                    map.put(GiantBomb.OFFSET, String.valueOf(offset));

                    getGameWiki(gameWikiListInterface, map);


                }
            });
        }
*/





        /************************ PROGRESS BAR ANIMATION ****************************/


        /*************************TOOLBAR PROPERTIES************************************/


    }

    private void fillRecycler(List<GameWikiModal> listModals, Parcelable parcelable) {
        recyclerView.setLayoutManager(new ConsistentLinearLayoutManager(context));
        recyclerView.setAdapter(new GameWikiAdapter(listModals, context, recyclerView.getChildCount(),recyclerView));
        if (parcelable != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(parcelable);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.game_wiki_menu, menu);
        getSearchManager(context, menu, false);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                getActivity().onBackPressed();
                return true;

            case R.id.search:


                return true;

            case R.id.filter:

                SearchFilterFragment filterFragment = SearchFilterFragment.newInstance(R.array.search_filter);
                filterFragment.setTargetFragment(this, 0);
                filterFragment.show(getActivity().getSupportFragmentManager(), "seach filter");
                return true;

            default:
                super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }


    /***********************
     * SEARCH MANAGER FUNCTION
     ********************************/


    public void getSearchManager(final Context context, Menu menu, boolean isDefaultIconified) {

        SearchManager searchManager = (SearchManager) context.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint(Html.fromHtml("<font color = #ffffff>" + getResources().getString(R.string.search) + "</font>"));

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(((AppCompatActivity) context).getComponentName()));
        searchView.setIconifiedByDefault(isDefaultIconified); // Do not iconify the widget; expand it by default

        MenuItem searchMenuItem = menu.findItem(R.id.search);
       /* MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {


            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                if (toolbar != null) {
                    toolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
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
        });*/

        searchView.setOnQueryTextListener(this);


    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        InputMethodHelper.hideKeyBoard(getActivity().getWindow().getCurrentFocus(), context);

        String field = "name:" + query;

        map.put(GiantBomb.FIELD, field);
        map.put(GiantBomb.OFFSET, "0");

        if (!listModals.isEmpty()) {
            listModals.clear();
        }


        if (errorText.getVisibility() == View.VISIBLE) {
            errorText.setVisibility(View.GONE);
        }

        getGameWiki(gameWikiListInterface, map);


        return true;
    }



    /*****************************
     * ACTUAL ASYNCHRONOUS API CALL
     ******************************/


    public void getGameWiki(final GameWikiListInterface gameWikiListInterface, final Map<String, String> map) {

        if ((listModals.isEmpty() && recyclerView.getAdapter()!=null)||listModals.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
        }

        gameWikiListInterface.getResult(map).enqueue(new Callback<GameWikiListModal>() {
            @Override
            public void onResponse(Call<GameWikiListModal> call, Response<GameWikiListModal> response) {

                if (progressBar.getVisibility()==View.VISIBLE) {
                    progressBar.setVisibility(View.GONE);
                }

                if (listModals.isEmpty() && recyclerView.getAdapter() != null) { //searching for a game
                    listModals = response.body().results;
                    adapter.swap(listModals);
                } else if (listModals.isEmpty()) {          //loading initial games at start
                    listModals = response.body().results;
                    adapter = new GameWikiAdapter(listModals, context, recyclerView.getChildCount(),recyclerView);
                    recyclerView.setAdapter(adapter);
                } else {            // loading more games
                    listModals.remove(listModals.size() - 1);
                    adapter.notifyItemRemoved(listModals.size());
                    int size = adapter.getItemCount();
                    listModals.addAll(response.body().results);
                    adapter.notifyItemRangeInserted(size, listModals.size());
                    adapter.setLoaded();
                }

                if (listModals.isEmpty()) {
                    errorText.setVisibility(View.VISIBLE);
                } else {
                    errorText.setVisibility(View.GONE);
                }


            /*    if (listModals.isEmpty()) {
                    //coming for first time
                    if (recyclerView.getAdapter() != null) {
                        //search return 0 result
                        listModals = response.body().results;

                        if (listModals.isEmpty()) {
                            errorText.setVisibility(View.VISIBLE);
                        } else {
                            errorText.setVisibility(View.GONE);
                            adapter.swap(listModals);

                        }
                    } else {
                        //adapter is null . setting for first time
                        listModals = response.body().results;
                        fillRecycler(listModals, null);
                    }


                } else {
                    int size = adapter.getItemCount();
                    listModals.addAll(response.body().results);
                    adapter.notifyItemRangeInserted(size, listModals.size());
                }*/


                if(adapter!=null){
                    adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                        @Override
                        public void onLoadMore() {
                            listModals.add(null);
                            adapter.notifyItemInserted(listModals.size()-1);

                            //removing bottom view & Load data
                            int offset = Integer.parseInt(map.get(GiantBomb.OFFSET));
                            offset += Integer.parseInt(LIMIT);
                            map.put(GiantBomb.OFFSET, String.valueOf(offset));
                            getGameWiki(gameWikiListInterface, map);





                        }
                    });




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
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onSelect(int which, boolean asc) {

        List<String> arrayList = Arrays.asList(context.getResources().getStringArray(R.array.search_filter));

        String sort = sortValue(which);

        if (!asc) {
            sort += ":desc";
        }

        map.put(GiantBomb.SORT, sort);
        map.put(GiantBomb.OFFSET, "0");
        listModals.clear();

        getGameWiki(gameWikiListInterface, map);


    }

    String sortValue(int which) {

        switch (which) {

            case 0:
                return "original_release_date";

            case 1:
                return "date_added";

            case 2:
                return "date_last_updated";

            case 3:
                return "number_of_user_reviews";

            case 4:

            default:
                return "none";


        }

    }


    Timer timer;
    int page = 0;

    public void pageSwitcher(int seconds) {
        timer = new Timer(); // At this line a new Thread will be created
        timer.scheduleAtFixedRate(new RemindTask(), 0, seconds * 1000); // delay
        // in
        // milliseconds
    }

    // this is an inner class...
    class RemindTask extends TimerTask {

        @Override
        public void run() {

            // As the TimerTask run on a seprate thread from UI thread we have
            // to call runOnUiThread to do work on UI thread.
            getActivity().runOnUiThread(new Runnable() {
                public void run() {

                    if (page > 3) { // In my case the number of pages are 5
                        timer.cancel();
                        // Showing a toast for just testing purpose

                    } else {
                        viewPager.setCurrentItem(page++);
                    }
                }
            });

        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        outState.putParcelableArrayList(MODAL, new ArrayList<>(listModals));
        outState.putParcelable(SCROLL_POSITION, recyclerView.getLayoutManager().onSaveInstanceState());


    }


    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
    }
}
