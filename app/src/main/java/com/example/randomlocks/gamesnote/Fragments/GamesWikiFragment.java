package com.example.randomlocks.gamesnote.Fragments;


import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.example.randomlocks.gamesnote.Adapter.GameWikiAdapter;
import com.example.randomlocks.gamesnote.DialogFragment.SearchFilterFragment;
import com.example.randomlocks.gamesnote.HelperClass.CustomView.AVLoadingIndicatorView;
import com.example.randomlocks.gamesnote.HelperClass.CustomView.ConsistentGridLayoutManager;
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

import es.dmoral.toasty.Toasty;
import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class GamesWikiFragment extends Fragment implements SearchFilterFragment.SearchFilterInterface, AppBarLayout.OnOffsetChangedListener {

    private static final String MODAL = "list_modal";
    private static final String SCROLL_POSITION = "recyclerScrollPosition";
    private static final String SEARCH_QUERY = "search_query" ;
    ViewPager viewPager;
    RecyclerView recyclerView;
    AVLoadingIndicatorView progressBar;
    FloatingSearchView floatingSearchView;
    List<GameWikiModal> listModals = null;
    GameWikiAdapter adapter;
    ConsistentGridLayoutManager manager;
    Map<String, String> map;
    GameWikiListInterface gameWikiListInterface;
    Call<GameWikiListModal> call;
    TextView errorText;
    CoordinatorLayout coordinatorLayout;
    AppBarLayout appBarLayout;
    Context context;

    int viewType;
    int spanCount;
    boolean isLoadingMore = false;


    private static final String LIMIT = "50";


    public GamesWikiFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        viewType = SharedPreference.getFromSharedPreferences(GiantBomb.VIEW_TYPE,0,context);
        spanCount = viewType==2 ? 3 : 1;
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
        appBarLayout = (AppBarLayout) coordinatorLayout.findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(this);
        floatingSearchView = (FloatingSearchView) coordinatorLayout.findViewById(R.id.floating_search_view);
      //  viewPager = (ViewPager) coordinatorLayout.findViewById(R.id.viewpager);
        recyclerView = (RecyclerView) coordinatorLayout.findViewById(R.id.recycler_view);
        progressBar = (AVLoadingIndicatorView) coordinatorLayout.findViewById(R.id.progressBar);
        errorText = (TextView) coordinatorLayout.findViewById(R.id.errortext);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


/***************************  SETTING THE VIEW PAGER ***********************/
 /*       ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("http://www.trbimg.com/img-568482b1/turbine/la-et-hc-1231-the-player-2016-20151231-002/650/650x366");
        arrayList.add("http://images.medicaldaily.com/sites/medicaldaily.com/files/styles/headline/public/2016/03/08/video-games.jpg");
        arrayList.add("http://i.investopedia.com/inv/articles/slideshow/5-top-video-game-characters/game-characters.jpg");
        arrayList.add("http://img3.rnkr-static.com/list_img_v2/3654/283654/full/the-best-female-video-game-characters-u1.jpg");

    //    viewPager.setAdapter(new ImageViewerPagerAdapter(context, 4, arrayList, true));
        CircleIndicator indicator = (CircleIndicator) coordinatorLayout.findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);
        // pageSwitcher(5);*/


        /***************************  SETTING THE TOOLBAR ***********************/

        AppCompatActivity actionBar = (AppCompatActivity) getActivity();
        DrawerLayout drawer = (DrawerLayout) actionBar.findViewById(R.id.drawer_layout);
        floatingSearchView.attachNavigationDrawerToMenuButton(drawer);
        manager = new ConsistentGridLayoutManager(getContext(),spanCount);

        recyclerView.setLayoutManager(manager);
        //Disable the animation
        ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);



        /**************************** HANDLING SEARCH ********************************/


        floatingSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                //change suggestion hints here
            }
        });






        floatingSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

            }

            @Override
            public void onSearchAction(String currentQuery) {

                if (currentQuery.trim().length() > 0) {
                    performSearch(currentQuery,false);
                }else {
                    Toasty.warning(getContext(),"no search text entered", Toast.LENGTH_SHORT,true).show();
                }
            }
        });

        floatingSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                switch (item.getItemId()) {


                    case R.id.filter:

                        SearchFilterFragment filterFragment = SearchFilterFragment.newInstance(R.array.search_filter);
                        filterFragment.setTargetFragment(GamesWikiFragment.this, 0);
                        filterFragment.show(getActivity().getSupportFragmentManager(), "seach filter");
                        break;


                    case R.id.view :
                        final CharSequence[] items = {"Card+Platforms", "Card+Desc", "Image+Title"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Make your selection");
                        builder.setSingleChoiceItems(items, viewType, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(adapter!=null && adapter.getItemCount()>0){
                                    viewType = i;

                                    if(viewType==2){
                                        manager.setSpanCount(3);
                                    }else{
                                        manager.setSpanCount(1);
                                    }

                                    adapter.changeView(i);

                                }else {
                                    Toasty.warning(getContext(),"game not loaded", Toast.LENGTH_SHORT,true).show();


                                }
                                dialogInterface.dismiss();
                            }

                        });
                        AlertDialog alert = builder.create();
                        alert.setCancelable(true);
                        alert.show();

                        break;



                    default:
                        break;
                }

            }
        });



        /*************** SAVE INSTANCE *************************/


        if (savedInstanceState != null) {
            Toaster.make(getContext(),"saveinstance");
            listModals = savedInstanceState.getParcelableArrayList(MODAL);
            if(listModals==null){
                performSearch(savedInstanceState.getString(SEARCH_QUERY),false);
            }else
            fillRecycler(listModals, savedInstanceState.getParcelable(SCROLL_POSITION));
        } else {
            if (listModals != null && !listModals.isEmpty()) {
                fillRecycler(listModals, null);
            } else {
                performSearch("",true);
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

    private void performSearch(String name, boolean allSearch) {

        if (listModals!=null && !listModals.isEmpty()) {
            listModals.clear();
            if(adapter!=null){
                Toaster.make(getContext(),"clear modal");
                adapter.removeAll();

            }

        }

        if (errorText.getVisibility() == View.VISIBLE) {
            errorText.setVisibility(View.GONE);
        }

        String filter = null;
        if (!allSearch) {
            filter = "name:" + name;
            map.put(GiantBomb.FILTER, filter);

        }
        map.put(GiantBomb.OFFSET, "0");
        String sort = sortValue(SharedPreference.getFromSharedPreferences(GiantBomb.WHICH, 4, context));
        boolean asc = SharedPreference.getFromSharedPreferences(GiantBomb.ASCENDING, true, context);

        if (!asc) {
            sort += ":desc";
        }
        map.put(GiantBomb.SORT, sort);

        gameWikiListInterface = GiantBomb.createGameWikiService();
        isLoadingMore = false;
        getGameWiki(gameWikiListInterface, map);



    }

    private void fillRecycler(List<GameWikiModal> listModals, Parcelable parcelable) {


        if(adapter==null){
            adapter = new GameWikiAdapter(listModals,viewType,getContext(),recyclerView.getChildCount(),recyclerView);
            recyclerView.setAdapter(adapter);
        }

        if (parcelable != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(parcelable);
        }
    }




    /***********************
     * SEARCH MANAGER FUNCTION
     ********************************/






    @Override
    public void onPause() {
        super.onPause();
        if (context != null) {
            SharedPreference.saveToSharedPreference(GiantBomb.VIEW_TYPE,viewType,context);

        }
    }

    /*****************************
     * ACTUAL ASYNCHRONOUS API CALL
     ******************************/


    public void getGameWiki(final GameWikiListInterface gameWikiListInterface, final Map<String, String> map) {
        progressBar.setVisibility(View.VISIBLE);
        Toaster.make(getContext(),"coming here");
        call = gameWikiListInterface.getResult(map);
        call.enqueue(new Callback<GameWikiListModal>() {
            @Override
            public void onResponse(Call<GameWikiListModal> call, Response<GameWikiListModal> response) {
                if (progressBar.getVisibility() == View.VISIBLE) {
                    progressBar.setVisibility(View.GONE);
                }
                // coming to load more data
                if(isLoadingMore){

                    adapter.updateModal(response.body().results);

                }else {

                    if (response.body().results.isEmpty()) {
                        Toaster.make(getContext(),"empty response");
                        errorText.setVisibility(View.VISIBLE);


                        //result is not empty
                    } else {
                        //searching the data for first time
                        if(adapter==null){
                            Toaster.make(getContext(),"adapter is null");

                            listModals = response.body().results;
                            adapter = new GameWikiAdapter(listModals,viewType,getContext(),recyclerView.getChildCount(),recyclerView);
                            recyclerView.setAdapter(adapter);

                        }else {  //searching the data after first time
                            listModals = response.body().results;
                            adapter.swap(listModals);
                            Toaster.make(getContext(),"coming to swap"+listModals.size());

                        }


                    }

                }  //outer else

                if (adapter!=null) {
                    adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                        @Override
                        public void onLoadMore() {


                            Toaster.make(getContext(),"on load more");
                            // modals.add(null);
                            adapter.addNull();
                            //   adapter.notifyItemInserted(modals.size()-1);

                            //removing bottom view & Load data
                            int offset = Integer.parseInt(map.get(GiantBomb.OFFSET));
                            offset += Integer.parseInt(LIMIT);
                            map.put(GiantBomb.OFFSET, String.valueOf(offset));
                            isLoadingMore = true;
                            getGameWiki(gameWikiListInterface, map);

                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<GameWikiListModal> call, Throwable t) {
                progressBar.setVisibility(View.GONE);

                if (!call.isCanceled()) {
                    Toaster.makeSnackBar(coordinatorLayout, "Connectivity Problem", "RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getGameWiki(gameWikiListInterface,map);
                        }
                    });
                }



            }
        });


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

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        floatingSearchView.setTranslationY(verticalOffset);
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


        if (listModals!=null) {
            outState.putParcelableArrayList(MODAL, new ArrayList<>(listModals));
        }
        outState.putString(SEARCH_QUERY,floatingSearchView.getQuery());
        if(recyclerView != null && recyclerView.getLayoutManager() != null)
        outState.putParcelable(SCROLL_POSITION, recyclerView.getLayoutManager().onSaveInstanceState());


    }


  /*  @Override
    public void onDetach() {
        super.onDetach();
        context = null;
    }*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(call!=null)
            call.cancel();
    }
}
