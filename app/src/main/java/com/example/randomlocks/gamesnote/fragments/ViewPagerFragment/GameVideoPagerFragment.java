package com.example.randomlocks.gamesnote.fragments.ViewPagerFragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
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
import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.adapter.GameVideoAdapter;
import com.example.randomlocks.gamesnote.dialogFragment.VideoOptionFragment;
import com.example.randomlocks.gamesnote.fragments.GamesVideoFragment;
import com.example.randomlocks.gamesnote.helperClass.CustomView.AVLoadingIndicatorView;
import com.example.randomlocks.gamesnote.helperClass.CustomView.ConsistentLinearLayoutManager;
import com.example.randomlocks.gamesnote.helperClass.GiantBomb;
import com.example.randomlocks.gamesnote.helperClass.SharedPreference;
import com.example.randomlocks.gamesnote.helperClass.Toaster;
import com.example.randomlocks.gamesnote.helperClass.WebViewHelper.CustomTabActivityHelper;
import com.example.randomlocks.gamesnote.helperClass.WebViewHelper.WebViewFallback;
import com.example.randomlocks.gamesnote.interfaces.GamesVideosInterface;
import com.example.randomlocks.gamesnote.interfaces.OnLoadMoreListener;
import com.example.randomlocks.gamesnote.interfaces.VideoPlayInterface;
import com.example.randomlocks.gamesnote.modals.SearchSuggestionModel;
import com.example.randomlocks.gamesnote.modals.gamesVideoModal.GamesVideoModal;
import com.example.randomlocks.gamesnote.modals.gamesVideoModal.GamesVideoModalList;
import com.example.randomlocks.gamesnote.realmDatabase.SearchHistoryDatabase;
import com.example.randomlocks.gamesnote.realmDatabase.WatchedVideoDatabase;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManagerListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;



//TODO EDIT TEXT FIX for keyboard and cursor . Cancel the realmasynctask if query is not completed

public class GameVideoPagerFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener, GameVideoAdapter.OnClickInterface, VideoOptionFragment.OnPlayInterface {

    public static final String VIDEO_KEY = "navigation_video_id"; //FOR SAVING MENU ITEM
    public static final String VIDEO_TITLE = "navigation_video_title"; //FOR MENU TOOLBAR TITLE
    private static final String MODAL = "list_modals";
    private static final String SCROLL_POSITION = "scroll_position";
    private static final java.lang.String SEARCH_QUERY = "search_query" ;
    private static final String LIMIT = "50";
    public DrawerLayout mDrawer;
    ConsistentLinearLayoutManager manager;
    GamesVideosInterface gamesVideoInterface = null;
    RecyclerView recyclerView;
    List<GamesVideoModal> listModals;
    GameVideoAdapter adapter;
    Map<String, String> map;
    boolean isReduced = false, isAllVideo = false;
    AVLoadingIndicatorView pacman;
    Realm realm;
    RealmAsyncTask realmAsyncTask;
    NavigationView mNavigation;
    String mTitle;
    int mSelectedId;
    TextView errorText;
    CoordinatorLayout coordinatorLayout;
    FloatingSearchView floatingSearchView;
    boolean isLoadingMore = false;
    HashMap<Integer, Integer> realmMap;
    Call<GamesVideoModalList> call;

    RealmResults<SearchHistoryDatabase> search_results;
    List<SearchSuggestionModel> search_list;

    VideoPlayInterface videoPlayInterface;
    int video_id;
    int adapterPosition;
    GamesVideoFragment parentFragment;
    private CastContext mCastContext;
    private CastSession mCastSession;
    private SessionManagerListener<CastSession> mSessionManagerListener;




    public GameVideoPagerFragment() {
    }


    public static GameVideoPagerFragment newInstance(int position) {

        Bundle args = new Bundle();
        GameVideoPagerFragment fragment = new GameVideoPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isReduced = SharedPreference.getFromSharedPreferences(GiantBomb.REDUCE_VIEW, false, getContext());

        map = new HashMap<>();
        map.put(GiantBomb.KEY, SharedPreference.getFromSharedPreferences(GiantBomb.API_KEY, GiantBomb.DEFAULT_API_KEY, getContext()));
        map.put(GiantBomb.FORMAT, "JSON");
        map.put(GiantBomb.OFFSET, "0");
        map.put(GiantBomb.LIMIT,LIMIT); //fix on endless scroll listener
        realmMap = new HashMap<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<WatchedVideoDatabase> realmResults = realm.where(WatchedVideoDatabase.class).findAll();
                for (WatchedVideoDatabase modal : realmResults) {
                    realmMap.put(modal.id, modal.time_elapsed);
                }
            }
        });
        return inflater.inflate(R.layout.fragment_games_video_pager, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        parentFragment = (GamesVideoFragment) getParentFragment();
        mDrawer = (DrawerLayout) getActivity().findViewById(R.id.drawer);
        mNavigation = (NavigationView) getActivity().findViewById(R.id.navigation);
        disableNavigationViewScrollbars(mNavigation);
        coordinatorLayout = (CoordinatorLayout) mDrawer.findViewById(R.id.root_coordinator);
        floatingSearchView = (FloatingSearchView) getActivity().findViewById(R.id.floating_search_view);
        floatingSearchView.setShowMoveUpSuggestion(true);
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_view);
        errorText = (TextView) coordinatorLayout.findViewById(R.id.errortext);
        pacman = (AVLoadingIndicatorView) coordinatorLayout.findViewById(R.id.progressBar);
        setupCastListener();


        mSelectedId = savedInstanceState == null ? SharedPreference
                .getFromSharedPreferences(VIDEO_KEY, R.id.nav_all_videos,
                        getContext()) : savedInstanceState.getInt(VIDEO_KEY);
        mTitle = savedInstanceState == null ? SharedPreference.
                getFromSharedPreferences(VIDEO_TITLE, getResources().getString(R.string.all_video),
                        getContext()) : savedInstanceState.getString(VIDEO_TITLE);
        mNavigation.setNavigationItemSelectedListener(this);
        mNavigation.setCheckedItem(mSelectedId);

        manager = new ConsistentLinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        if(isReduced)
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));



        floatingSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {
                if (!oldQuery.equals("") && newQuery.equals("")) {
                    floatingSearchView.clearSuggestions();
                } else {

                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            search_results = SearchHistoryDatabase.search(realm,newQuery,SearchHistoryDatabase.VIDEO_WIKI, true);
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
                            floatingSearchView.swapSuggestions(search_list);
                        }
                    });

                }
            }
        });

        floatingSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {

                //show suggestions when search bar gains focus (typically history suggestions)
                floatingSearchView.swapSuggestions(SearchHistoryDatabase.getHistory(realm,SearchHistoryDatabase.VIDEO_WIKI,3));

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

                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            SearchHistoryDatabase is_added_database = realm.where(SearchHistoryDatabase.class).equalTo(SearchHistoryDatabase.SEARCH_TYPE,SearchHistoryDatabase.VIDEO_WIKI)
                                    .equalTo(SearchHistoryDatabase.TITLE,currentQuery).findFirst();
                            if(is_added_database==null){
                                SearchHistoryDatabase database = new SearchHistoryDatabase(SearchHistoryDatabase.VIDEO_WIKI,currentQuery);
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


                    case R.id.view:

                        if(adapter==null || adapter.getItemCount()==0){
                            Toasty.info(getContext(),"waiting to load video").show();
                            break;
                        }


                        if (item.getTitle().equals(getString(R.string.card_view))) {
                            isReduced = false;
                            item.setTitle(getString(R.string.list_view));
                            item.setIcon(R.drawable.ic_gamelist_white);

                        } else {
                            item.setTitle(getString(R.string.card_view));
                            item.setIcon(R.drawable.ic_compat_white);
                            isReduced = true;
                        }

                        if (recyclerView!=null) {
                            if(isReduced)
                                recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
                            else
                                recyclerView.removeItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
                        }


                        if (adapter != null) {
                            SharedPreference.saveToSharedPreference(GiantBomb.REDUCE_VIEW, isReduced, getContext());
                            adapter.setSimple(isReduced);
                            parentFragment.updateViewPagerFragment(isReduced);
                        }

                        break;

                    case R.id.nav_open:
                        mDrawer.openDrawer(GravityCompat.END);
                        break;

                }


            }
        });



        if (savedInstanceState != null) {
            listModals = savedInstanceState.getParcelableArrayList(MODAL);
            if(listModals!=null)
                fillRecyclerView(listModals, savedInstanceState.getParcelable(SCROLL_POSITION));
            else
                performSearch(savedInstanceState.getString(SEARCH_QUERY),false);
        } else if (listModals != null) {
            fillRecyclerView(listModals, null);
        } else {
            performSearch("",true);
        }


    }

    private void disableNavigationViewScrollbars(NavigationView navigationView) {
        if (navigationView != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
            if (navigationMenuView != null) {
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }
        }
    }

    private void setupCastListener() {
        mSessionManagerListener = new SessionManagerListener<CastSession>() {

            @Override
            public void onSessionEnded(CastSession session, int error) {
                onApplicationDisconnected();
            }

            @Override
            public void onSessionResumed(CastSession session, boolean wasSuspended) {
                onApplicationConnected(session);
            }

            @Override
            public void onSessionResumeFailed(CastSession session, int error) {
                onApplicationDisconnected();
            }

            @Override
            public void onSessionStarted(CastSession session, String sessionId) {
                onApplicationConnected(session);
            }

            @Override
            public void onSessionStartFailed(CastSession session, int error) {
                onApplicationDisconnected();
            }

            @Override
            public void onSessionStarting(CastSession session) {
            }

            @Override
            public void onSessionEnding(CastSession session) {
            }

            @Override
            public void onSessionResuming(CastSession session, String sessionId) {
            }

            @Override
            public void onSessionSuspended(CastSession session, int reason) {
            }

            private void onApplicationConnected(CastSession castSession) {
                mCastSession = castSession;

            }

            private void onApplicationDisconnected() {

            }
        };
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        videoPlayInterface = (VideoPlayInterface) getActivity();
    }

    private void performSearch(String text,boolean allSearch) {

        pacman.setVisibility(View.VISIBLE);
        if (listModals!=null && !listModals.isEmpty()) {
            if(adapter!=null){
                adapter.removeAll();
            }

        }

        if (errorText.getVisibility() == View.VISIBLE) {
            errorText.setVisibility(View.GONE);
        }

        String filter;
        if (!allSearch) {
            filter = "name:" + text;
            map.put(GiantBomb.FILTER, filter);

        }
        map.put(GiantBomb.OFFSET, "0");
        gamesVideoInterface = GiantBomb.createGameVideoService();
        isLoadingMore = false;
        selectDrawer(mSelectedId, mTitle);
        getGameVideos(gamesVideoInterface, map);

    }


    private void selectDrawer(int mSelectedId, String mTitle) {


        switch (mSelectedId) {


            case R.id.nav_all_videos:
                if (map.containsKey(GiantBomb.VIDEO_TYPE))
                    map.remove(GiantBomb.VIDEO_TYPE);
                break;

            case R.id.nav_reviews:
                map.put(GiantBomb.VIDEO_TYPE, "2");
                break;

            case R.id.nav_quick_looks:
                map.put(GiantBomb.VIDEO_TYPE, "3");

                break;


            case R.id.nav_tang:
                map.put(GiantBomb.VIDEO_TYPE, "4");
                break;

            case R.id.nav_endurance_run:
                map.put(GiantBomb.VIDEO_TYPE, "5");
                break;

            case R.id.nav_events:
                map.put(GiantBomb.VIDEO_TYPE, "6");

                break;

            case R.id.nav_trailer:
                map.put(GiantBomb.VIDEO_TYPE, "7");

                break;

            case R.id.nav_features:
                map.put(GiantBomb.VIDEO_TYPE, "8");
                break;

            case R.id.nav_premium:
                map.put(GiantBomb.VIDEO_TYPE, "10");
                break;

            case R.id.nav_extra_life:
                map.put(GiantBomb.VIDEO_TYPE, "11");
                break;

            case R.id.nav_encyclopedia:
                map.put(GiantBomb.VIDEO_TYPE, "12");
                break;

            case R.id.nav_unfinished:
                map.put(GiantBomb.VIDEO_TYPE, "13");
                break;

            case R.id.nav_metal_gear:
                map.put(GiantBomb.VIDEO_TYPE, "17");
                break;

            case R.id.nav_vinny:
                map.put(GiantBomb.VIDEO_TYPE, "18");
                break;

            case R.id.nav_breaking_bad:
                map.put(GiantBomb.VIDEO_TYPE, "19");
                break;

            case R.id.nav_best:
                map.put(GiantBomb.VIDEO_TYPE, "20");
                break;


            case R.id.nav_game_tapes:
                map.put(GiantBomb.VIDEO_TYPE, "21");
                break;

            case R.id.nav_kerbal:
                map.put(GiantBomb.VIDEO_TYPE, "22");
                break;


        }


        if (mDrawer.isDrawerOpen(GravityCompat.END)) {
            mDrawer.closeDrawers();
        }
        //((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mTitle);


    }

    private void getGameVideos(final GamesVideosInterface gamesVideoInterface, final Map<String, String> map) {
        call =  gamesVideoInterface.getResult(map);
        call.enqueue(new Callback<GamesVideoModalList>() {
            @Override
            public void onResponse(Call<GamesVideoModalList> call, Response<GamesVideoModalList> response) {
                if (pacman.getVisibility() == View.VISIBLE) {
                    pacman.setVisibility(View.GONE);
                }
                if(isLoadingMore){

                    GamesVideoModalList listModal = response.body();
                    adapter.updateModal(listModal.results);
                    Toaster.makeSnackBar(coordinatorLayout, "Showing " + listModals.size() + " of " + listModal.numberOfTotalResults + " videos");

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

                            GamesVideoModalList listModal = response.body();
                            listModals = listModal.results;
                            if (!listModals.isEmpty()) {
                                Toaster.makeSnackBar(coordinatorLayout, "Showing " + listModals.size() + " of " + listModal.numberOfTotalResults + " videos");
                            }
                            adapter = new GameVideoAdapter(listModals, getContext(), isReduced,realm,GameVideoPagerFragment.this,realmMap,recyclerView);
                            recyclerView.setAdapter(adapter);

                        }else {  //searching the data after first time
                            GamesVideoModalList listModal = response.body();
                            listModals = listModal.results;
                            Toaster.makeSnackBar(coordinatorLayout, "Showing " + listModals.size() + " of " + listModal.numberOfTotalResults + " videos");
                            adapter.swap(listModals);

                        }


                    }

                }  //outer else

                if (adapter!=null) {
                    adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                        @Override
                        public void onLoadMore() {
                            // modals.add(null);
                            adapter.addNull();
                            //   adapter.notifyItemInserted(modals.size()-1);

                            //removing bottom view & Load data
                            int offset = Integer.parseInt(map.get(GiantBomb.OFFSET));
                            offset += Integer.parseInt(LIMIT);
                            map.put(GiantBomb.OFFSET, String.valueOf(offset));
                            isLoadingMore = true;
                            getGameVideos(gamesVideoInterface, map);

                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<GamesVideoModalList> call, Throwable t) {
                pacman.setVisibility(View.GONE);
                if (!call.isCanceled()) {
                    Snackbar.make(coordinatorLayout, "Connectivity Problem", Snackbar.LENGTH_INDEFINITE)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    pacman.setVisibility(View.VISIBLE);
                                    getGameVideos(gamesVideoInterface, map);

                                }
                            }).show();
                }
            }
        });
    }

    private void fillRecyclerView(final List<GamesVideoModal> listModals, final Parcelable parcelable) {


        if(adapter==null){
            adapter = new GameVideoAdapter(listModals, getContext(), isReduced,realm,GameVideoPagerFragment.this,realmMap,recyclerView);
        } else {
            adapter.setRealm(realm);
            adapter.setSimple(isReduced);
            adapter.updateModal(realmMap);
        }
        if (recyclerView.getAdapter() == null) {
            recyclerView.setAdapter(adapter);
        }
        if (parcelable != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(parcelable);
        }






    } //function end

    private void deleteFromRealm(final GamesVideoModal modal) {

        // if its in favourite or watch later then dont custom_game_detail_stats , just update
        if (modal.isFavorite || modal.isWatchLater) {
            realmAsyncTask = realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(modal);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    Toasty.error(getContext(),"Deleted").show();
                }
            });
        } else {  // if not in fav and watch later then custom_game_detail_stats
            realmAsyncTask = realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.where(GamesVideoModal.class).equalTo("id", modal.id).findFirst().deleteFromRealm();
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    Toasty.error(getContext(),"Deleted").show();

                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    Toaster.make(getContext(), error.toString());
                }
            });
        }


    }

    private void writeToRealm(final GamesVideoModal modal) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(modal);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Toasty.success(getContext(),"Added").show();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Toaster.make(getContext(), error.toString());
            }
        });


    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (isReduced) {
            menu.getItem(0).setTitle(getString(R.string.reduce_view));
        } else {
            menu.getItem(0).setTitle(getString(R.string.compact_view));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //    inflater.inflate(R.menu.game_video_menu, menu);

    }



    @Override
    public void onPause() {
        super.onPause();
        Context context = getContext();
        if (context != null) {
            SharedPreference.saveToSharedPreference(VIDEO_KEY, mSelectedId, context);
            SharedPreference.saveToSharedPreference(VIDEO_TITLE, mTitle, context);
        }

        if (realm != null && !realm.isClosed() && realm.isInTransaction()) {
            realm.cancelTransaction();
        }

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (listModals != null) {
            outState.putParcelableArrayList(MODAL, new ArrayList<>(listModals));
        }
        if (recyclerView.getLayoutManager() != null) {
            outState.putParcelable(SCROLL_POSITION, recyclerView.getLayoutManager().onSaveInstanceState());
        }
        outState.putString(VIDEO_TITLE, mTitle);
        outState.putInt(VIDEO_KEY, mSelectedId);
    }


    @Override
    public void onStop() {
        super.onStop();
        if (realmAsyncTask != null && !realmAsyncTask.isCancelled()) {
            realmAsyncTask.cancel();
        }
    }




    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        mSelectedId = item.getItemId();
        mTitle = item.getTitle().toString();




        selectDrawer(mSelectedId, mTitle);

        changeVideoSource();

        return true;
    }

    //TODO analyze this function
    private void changeVideoSource() {


        pacman.setVisibility(View.VISIBLE);
        if (gamesVideoInterface != null && map != null) {
            performSearch("",true);
        }
    }




    @Override
    public void onWatchLater(GamesVideoModal modal) {
        if (modal.isWatchLater) {
            modal.dateAdded = new Date();
            writeToRealm(modal);
        } else {
            deleteFromRealm(modal);
        }
    }

    @Override
    public void onLike(GamesVideoModal modal) {
        if (modal.isFavorite) {
            modal.dateAdded = new Date();
            writeToRealm(modal);
        } else {
            deleteFromRealm(modal);
        }
    }

    @Override
    public void onShare(GamesVideoModal modal) {
        shareVideo(modal.siteDetailUrl);
    }

    @Override
    public void onVideoClick(GamesVideoModal modal, int adapterPosition, int elapsed_time) {
        //call the video option dialog
        this.adapterPosition = adapterPosition;
        this.video_id = modal.id;
        VideoOptionFragment videoOptionFragment = VideoOptionFragment.newInstance(modal, elapsed_time);
        videoOptionFragment.setTargetFragment(this, 0);
        videoOptionFragment.setCancelable(false);
        videoOptionFragment.show(getActivity().getSupportFragmentManager(), "video_option_fragment");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(call!=null)
            call.cancel();
        if (realm != null && !realm.isClosed())
            realm.close();
    }


    @Override
    public void onPlay(GamesVideoModal modal, int video_option, boolean use_inbuilt, int elapsed_time) {
        String url;
        switch (video_option) {
            case 0:
                url = modal.lowUrl + "?api_key=" + SharedPreference
                        .getFromSharedPreferences(GiantBomb.API_KEY, GiantBomb.DEFAULT_API_KEY, getContext());
                if (use_inbuilt)
                    videoPlayInterface.onVideoClick(modal, url, modal.id, elapsed_time, 0);
                else {
                    videoPlayInterface.onExternalPlayerVideoClick(url, modal.id);

                }

                break;

            case 1:
                url = modal.highUrl + "?api_key=" + SharedPreference
                        .getFromSharedPreferences(GiantBomb.API_KEY, GiantBomb.DEFAULT_API_KEY, getContext());
                if (use_inbuilt)
                    videoPlayInterface.onVideoClick(modal, url, modal.id, elapsed_time, 0);
                else {
                    videoPlayInterface.onExternalPlayerVideoClick(url, modal.id);

                }
                break;
            case 2:
                url = modal.youtubeId;
                videoPlayInterface.onYoutubeVideoClick(url, modal.id);
                break;

            case 3:
                url = modal.siteDetailUrl;
                runBrowser(url);
                break;

            default:
                break;


        }


    }

    void runBrowser(String url) {
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().setShowTitle(true).build();
        CustomTabActivityHelper.openCustomTab(
                getActivity(), customTabsIntent, Uri.parse(url), new WebViewFallback());
    }


    private void shareVideo(String bmpUri) {
        if (bmpUri != null) {
            // Construct a ShareIntent with link to image

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, bmpUri);
            //shareIntent.setType("image/png");
            // Launch sharing dialog for image
            startActivity(Intent.createChooser(shareIntent, "Share Video"));
        } else {
            Toasty.error(getContext(), "cannot share", Toast.LENGTH_SHORT, true).show();
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                final int time_elapsed = data.getIntExtra(GiantBomb.SEEK_POSITION, 0);
                if (time_elapsed > 0) {

                    saveWatchedVideo(time_elapsed);

                }


            }
        } else if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                saveWatchedVideo(0);
            }
        }


    }

    private void saveWatchedVideo(final int time_elapsed) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                WatchedVideoDatabase database = new WatchedVideoDatabase(video_id, time_elapsed);
                realm.copyToRealmOrUpdate(database);
                RealmResults<WatchedVideoDatabase> realmResults = realm.where(WatchedVideoDatabase.class).findAll();
                for (WatchedVideoDatabase modal : realmResults) {
                    realmMap.put(modal.id, modal.time_elapsed);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                parentFragment.updateViewPager();
                //  adapter.updateModal(adapterPosition, realmMap);
            }
        });
    }


}