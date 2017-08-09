package com.example.randomlocks.gamesnote.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.adapter.GameWikiAdapter;
import com.example.randomlocks.gamesnote.dialogFragment.SearchFilterFragment;
import com.example.randomlocks.gamesnote.helperClass.CustomView.AVLoadingIndicatorView;
import com.example.randomlocks.gamesnote.helperClass.CustomView.ConsistentGridLayoutManager;
import com.example.randomlocks.gamesnote.helperClass.GiantBomb;
import com.example.randomlocks.gamesnote.helperClass.SharedPreference;
import com.example.randomlocks.gamesnote.helperClass.Toaster;
import com.example.randomlocks.gamesnote.interfaces.GameWikiDetailInterface;
import com.example.randomlocks.gamesnote.interfaces.GameWikiListInterface;
import com.example.randomlocks.gamesnote.interfaces.OnLoadMoreListener;
import com.example.randomlocks.gamesnote.modals.GameDetailModal.GameDetailIInnerJson;
import com.example.randomlocks.gamesnote.modals.GameDetailModal.GameDetailListModal;
import com.example.randomlocks.gamesnote.modals.GameDetailModal.GameDetailModal;
import com.example.randomlocks.gamesnote.modals.GameWikiListModal;
import com.example.randomlocks.gamesnote.modals.GameWikiModal;
import com.example.randomlocks.gamesnote.modals.GameWikiPlatform;
import com.example.randomlocks.gamesnote.modals.SearchSuggestionModel;
import com.example.randomlocks.gamesnote.realmDatabase.GameDetailDatabase;
import com.example.randomlocks.gamesnote.realmDatabase.GameListDatabase;
import com.example.randomlocks.gamesnote.realmDatabase.RealmInteger;
import com.example.randomlocks.gamesnote.realmDatabase.SearchHistoryDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmList;
import io.realm.RealmResults;
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
    private static final String LIMIT = "50";
    ViewPager viewPager;
    RecyclerView recyclerView;
    DividerItemDecoration itemDecoration;
    AVLoadingIndicatorView progressBar;
    FloatingSearchView floatingSearchView;
    List<GameWikiModal> listModals = null;
    GameWikiAdapter adapter;
    ConsistentGridLayoutManager manager;
    Map<String, String> map;
    GameWikiListInterface gameWikiListInterface;
    GameWikiDetailInterface gameWikiDetailInterface;
    Call<GameDetailListModal> gameDetailCall;
    Call<GameWikiListModal> call;
    TextView errorText;
    CoordinatorLayout coordinatorLayout;
    AppBarLayout appBarLayout;
    Context context;
    int viewType;
    int spanCount;
    boolean isLoadingMore = false;
    Timer timer;
    int page = 0;
    String apiUrl;
    Realm realm;
    RealmAsyncTask realmAsyncTask;
    RealmResults<SearchHistoryDatabase> search_results;
    List<SearchSuggestionModel> search_list;
    private int game_id;
    private String mLastQuery = "";



    public GamesWikiFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        viewType = SharedPreference.getFromSharedPreferences(GiantBomb.VIEW_TYPE,0,context);
        itemDecoration = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        spanCount = viewType==2 ? 2 : 1;
        map = new HashMap<>(7);
        map.put(GiantBomb.KEY, GiantBomb.API_KEY);
        map.put(GiantBomb.FORMAT, "JSON");
        map.put(GiantBomb.LIMIT, LIMIT);
        map.put(GiantBomb.OFFSET, "0");
        String field_list = "api_detail_url,deck,expected_release_day,expected_release_month,expected_release_year,image,name,id,original_release_date,platforms";
        map.put(GiantBomb.FIELD_LIST, field_list);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_game_wiki, container, false);
        realm = Realm.getDefaultInstance();

        coordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.root_coordinator);
        floatingSearchView = (FloatingSearchView) v.findViewById(R.id.floating_search_view);
        appBarLayout = (AppBarLayout) coordinatorLayout.findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(this);
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
        floatingSearchView.setShowMoveUpSuggestion(true);
        manager = new ConsistentGridLayoutManager(getContext(),spanCount);

        recyclerView.setLayoutManager(manager);
        if(viewType==1)
            recyclerView.addItemDecoration(itemDecoration);
        //Disable the animation
        //((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);



        /**************************** HANDLING SEARCH ********************************/

        if (AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES) {
            floatingSearchView.setViewTextColor(Color.parseColor("#e9e9e9"));
        }

        floatingSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {



                if (!oldQuery.equals("") && newQuery.equals("")) {
                    floatingSearchView.clearSuggestions();
                } else {

                    //this shows the top left circular progress
                    //you can call it where ever you want, but
                    //it makes sense to do it when loading something in
                    //the background.

                    //simulates a query call to a data source
                    //with a new query.
                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            search_results = SearchHistoryDatabase.search(realm,newQuery,SearchHistoryDatabase.GAME_WIKI, true);
                            search_list = new ArrayList<>();
                            int i=0;
                            for(SearchHistoryDatabase search_result : search_results){
                                search_list.add(new SearchSuggestionModel(search_result.getTitle()));
                                if(++i > 5)
                                    break;
                            }

                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            if (search_list.size() == 1 && search_list.get(0).getBody().equals(newQuery)) {
                                floatingSearchView.clearSuggestions();
                                floatingSearchView.clearSearchFocus();
                            } else {
                                floatingSearchView.swapSuggestions(search_list);
                            }
                        }
                    });

                }
            }
        });

        floatingSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {

                //show suggestions when search bar gains focus (typically history suggestions)
                floatingSearchView.swapSuggestions(SearchHistoryDatabase.getHistory(realm,SearchHistoryDatabase.GAME_WIKI,3));

            }

            @Override
            public void onFocusCleared() {
               // floatingSearchView.setSearchBarTitle(mLastQuery);
            }
        });




        floatingSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                floatingSearchView.setSearchBarTitle(searchSuggestion.getBody());
                performSearch(searchSuggestion.getBody(),false);

            }

            @Override
            public void onSearchAction(final String currentQuery) {

                if (currentQuery.trim().length() > 0) {
                    mLastQuery = currentQuery;

                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            SearchHistoryDatabase is_added_database = realm.where(SearchHistoryDatabase.class).equalTo(SearchHistoryDatabase.SEARCH_TYPE,SearchHistoryDatabase.GAME_WIKI)
                                    .equalTo(SearchHistoryDatabase.TITLE,currentQuery).findFirst();
                            if(is_added_database==null){
                                SearchHistoryDatabase database = new SearchHistoryDatabase(SearchHistoryDatabase.GAME_WIKI,currentQuery);
                                realm.copyToRealm(database);
                            }else {
                                is_added_database.setDate_added(new Date());
                            }

                        }
                    });
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
                        builder.setSingleChoiceItems(items,viewType, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(adapter!=null && adapter.getItemCount()>0){
                                    viewType = i;

                                    if(viewType==2){
                                        manager.setSpanCount(2);
                                    }else{
                                        manager.setSpanCount(1);
                                    }
                                    if(viewType==1)
                                        recyclerView.addItemDecoration(itemDecoration);
                                    else
                                        recyclerView.removeItemDecoration(itemDecoration);

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
            if(adapter!=null){
                adapter.removeAll();
            }
        }

        if (errorText.getVisibility() == View.VISIBLE) {
            errorText.setVisibility(View.GONE);
        }
        progressBar.setVisibility(View.VISIBLE);

        String filter = null;
        if (!allSearch) {
            filter = "name:" + name;
            map.put(GiantBomb.FILTER, filter);

        }
        map.put(GiantBomb.OFFSET, "0");
        String sort = sortValue(SharedPreference.getFromSharedPreferences(GiantBomb.WHICH, 3, context));
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
            adapter = new GameWikiAdapter(listModals, viewType, getContext(), recyclerView.getChildCount(), recyclerView, realm, new GameWikiAdapter.OnPopupClickInterface() {
                @Override
                public void onRemove(GameWikiModal gameWikiModal) {
                    removeFromDatabase(gameWikiModal);
                }

                @Override
                public void onAdd(GameWikiModal gameWikiModal,int addTypeId) {
                        addToDatabase(gameWikiModal,addTypeId);
                }
            });

            recyclerView.setAdapter(adapter);
        }

        if (parcelable != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(parcelable);
        }
    }

    private void addToDatabase(final GameWikiModal modal, final int id) {

        String str[] = modal.apiDetailUrl.split("/");
        apiUrl = str[str.length-1];


        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmList<GameWikiPlatform> platforms = new RealmList<>();
                if (modal.platforms != null) {
                    platforms.addAll(modal.platforms);
                }
                final GameListDatabase newListDatabase = new GameListDatabase(modal.id, apiUrl, modal.name, modal.image != null ? modal.image.mediumUrl : null, platforms);
                if (id == R.id.replaying)
                    newListDatabase.setStatus(GiantBomb.REPLAYING);
                else if (id == R.id.planning)
                    newListDatabase.setStatus(GiantBomb.PLANNING);
                else if (id == R.id.dropped)
                    newListDatabase.setStatus(GiantBomb.DROPPED);
                else if (id == R.id.playing)
                    newListDatabase.setStatus(GiantBomb.PLAYING);
                else
                    newListDatabase.setStatus(GiantBomb.COMPLETED);
                realm.insertOrUpdate(newListDatabase);

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Toasty.success(context,"added", Toast.LENGTH_SHORT,true).show();
            }
        });



        if (gameWikiDetailInterface==null) {
            gameWikiDetailInterface = GiantBomb.createGameDetailService();
            map = new HashMap<>();
            map.put(GiantBomb.KEY, GiantBomb.API_KEY);
            map.put(GiantBomb.FORMAT, "JSON");
            String field_list = "id,developers,franchises,genres,publishers,similar_games,themes";
            map.put(GiantBomb.FIELD_LIST, field_list);
        }
        getGameDetail(gameWikiDetailInterface, map,modal,id);



    }

    private void getGameDetail(GameWikiDetailInterface gameWikiDetailInterface, Map<String, String> map, final GameWikiModal modal, final int id) {

        if (gameDetailCall != null)
            gameDetailCall.cancel();

        gameDetailCall = gameWikiDetailInterface.getResult(apiUrl,map);
        gameDetailCall.enqueue(new Callback<GameDetailListModal>() {
            @Override
            public void onResponse(Call<GameDetailListModal> call, Response<GameDetailListModal> response) {
                final GameDetailModal gameDetailModal = response.body().results;
                game_id = gameDetailModal.id;

              realmAsyncTask =   realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        //now add gameDetail

                        updateGameDetail(gameDetailModal.developers, GameDetailDatabase.DEVELOPER_TYPE,realm);
                        updateGameDetail(gameDetailModal.publishers, GameDetailDatabase.PUBLISHER_TYPE,realm);
                        updateGameDetail(gameDetailModal.themes, GameDetailDatabase.THEME_TYPE,realm);
                        updateGameDetail(gameDetailModal.franchises, GameDetailDatabase.FRANCHISE_TYPE,realm);
                        updateGameDetail(gameDetailModal.genres, GameDetailDatabase.GENRE_TYPE,realm);
                        updateGameDetail(gameDetailModal.similarGames, GameDetailDatabase.SIMILAR_GAME_TYPE,realm);
                    }
                });

            }

            @Override
            public void onFailure(Call<GameDetailListModal> call, Throwable t) {

            }
        });


    }

    private void updateGameDetail(List<GameDetailIInnerJson> gameDetailDatabase, int type, Realm realm) {
        if (gameDetailDatabase != null) {
            for (GameDetailIInnerJson gameDatabase : gameDetailDatabase) {
                GameDetailDatabase gameDeveloperDatabase = realm.where(GameDetailDatabase.class)
                        .equalTo(GameDetailDatabase.TYPE, type)
                        .equalTo(GameDetailDatabase.NAME, gameDatabase.name).findFirst();
                if (gameDeveloperDatabase != null) {
                    gameDeveloperDatabase.getGames_id().add(new RealmInteger(game_id));
                    gameDeveloperDatabase.setCount(gameDeveloperDatabase.getCount()+1);
                } else {
                    RealmList<RealmInteger> games_id = new RealmList<RealmInteger>();
                    games_id.add(new RealmInteger(game_id));
                    int auto_increment_x=0;
                    Number number = realm.where(GameDetailDatabase.class).equalTo(GameDetailDatabase.TYPE,type).max(GameDetailDatabase.AUTO_INCREMENT_X_VALUE);
                    if(number!=null)
                        auto_increment_x = number.intValue()+1;
                    gameDeveloperDatabase = new GameDetailDatabase(type, gameDatabase.name,1,auto_increment_x, games_id);
                    realm.insertOrUpdate(gameDeveloperDatabase);
                }
            }
        }
    }

    private void removeFromDatabase(final GameWikiModal modal) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                //Remove gamelist info from database
                GameListDatabase database =   realm.where(GameListDatabase.class).equalTo(GameListDatabase.GAME_ID,modal.id).findFirst();
                if (database.getPlatform_list()!=null) {
                    database.getPlatform_list().deleteAllFromRealm();
                }
                database.deleteFromRealm();
                //Remove gamedetail infro from database
                RealmInteger gameId  = realm.where(RealmInteger.class).equalTo(GameListDatabase.GAME_ID,modal.id).findFirst();
                if (gameId!=null) {
                    gameId.deleteFromRealm();
                }
                RealmResults<GameDetailDatabase> gameDetailDatabases =  realm.where(GameDetailDatabase.class).isEmpty("games_id").findAll();
                if(gameDetailDatabases!=null)
                    gameDetailDatabases.deleteAllFromRealm();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Toasty.warning(context,"deleted", Toast.LENGTH_SHORT,true).show();
            }
        });
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
        Toaster.make(getContext(),"coming here");
        if (call != null)
            call.cancel();
        call = gameWikiListInterface.getResult(map);
        call.enqueue(new Callback<GameWikiListModal>() {
            @Override
            public void onResponse(Call<GameWikiListModal> call, Response<GameWikiListModal> response) {
                Log.d("tag1", "onresponse");
                if (progressBar.getVisibility() == View.VISIBLE) {
                    progressBar.setVisibility(View.GONE);
                }
                // coming to load more data
                if(isLoadingMore){
                    GameWikiListModal listModal = response.body();
                    adapter.updateModal(listModal.results);
                    Toaster.makeSnackBar(coordinatorLayout, "Showing " + listModals.size() + " of " + listModal.numberOfTotalResults + " games");
                }else {

                    if (response.body().results.isEmpty()) {
                        if (AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES) {
                            Drawable drawable = ContextCompat.getDrawable(getContext(),R.drawable.ic_error);
                            drawable = DrawableCompat.wrap(drawable);
                            DrawableCompat.setTint(drawable, Color.WHITE);
                            DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_ATOP);
                            errorText.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
                        }
                        errorText.setVisibility(View.VISIBLE);


                        //result is not empty
                    } else {
                        //searching the data for first time
                        if(adapter==null){
                            Toaster.make(getContext(),"adapter is null");
                            GameWikiListModal listModal = response.body();
                            listModals = listModal.results;
                            if (!listModals.isEmpty()) {
                                Toaster.makeSnackBar(coordinatorLayout, "Showing " + listModals.size() + " of " + listModal.numberOfTotalResults + " games");
                            }
                            adapter = new GameWikiAdapter(listModals, viewType, getContext(), recyclerView.getChildCount(), recyclerView, realm, new GameWikiAdapter.OnPopupClickInterface() {
                                @Override
                                public void onRemove(GameWikiModal gameWikiModal) {
                                    removeFromDatabase(gameWikiModal);
                                }

                                @Override
                                public void onAdd(GameWikiModal gameWikiModal,int addToType) {
                                    addToDatabase(gameWikiModal,addToType);
                                }
                            });
                            recyclerView.setAdapter(adapter);

                        }else {  //searching the data after first time
                            GameWikiListModal listModal = response.body();
                            listModals = listModal.results;
                            Toaster.makeSnackBar(coordinatorLayout, "Showing " + listModals.size() + " of " + listModal.numberOfTotalResults + " games");
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

                if (!call.isCanceled()) {
                    progressBar.setVisibility(View.GONE);
                    Toaster.makeSnackBar(coordinatorLayout, "Connectivity Problem", "RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            progressBar.setVisibility(View.VISIBLE);
                            getGameWiki(gameWikiListInterface,map);
                        }
                    });
                }



            }
        });


    }

    @Override
    public void onSelect(int which, boolean asc) {

        String sort = sortValue(which);

        if (!asc) {
            sort += ":desc";
        }

        map.put(GiantBomb.SORT, sort);
        map.put(GiantBomb.OFFSET, "0");

        if (adapter != null) {
            adapter.removeAll();
        }


        progressBar.setVisibility(View.VISIBLE);
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



    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        floatingSearchView.setTranslationY(verticalOffset);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (call != null)
            call.cancel();
        if(gameDetailCall!=null)
            gameDetailCall.cancel();

        if (realmAsyncTask != null && !realmAsyncTask.isCancelled()) {
            realmAsyncTask.cancel();
        }

        if(realm!=null && !realm.isClosed())
            realm.close();

    }


  /*  @Override
    public void onDetach() {
        super.onDetach();
        context = null;
    }*/

    // this is an inner class...
    private class RemindTask extends TimerTask {

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
}