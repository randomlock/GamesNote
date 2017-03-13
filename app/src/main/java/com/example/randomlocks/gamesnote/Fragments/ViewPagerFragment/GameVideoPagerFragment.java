package com.example.randomlocks.gamesnote.Fragments.ViewPagerFragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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
import com.example.randomlocks.gamesnote.Adapter.GameVideoAdapter;
import com.example.randomlocks.gamesnote.DialogFragment.VideoOptionFragment;
import com.example.randomlocks.gamesnote.HelperClass.CustomView.AVLoadingIndicatorView;
import com.example.randomlocks.gamesnote.HelperClass.CustomView.ConsistentLinearLayoutManager;
import com.example.randomlocks.gamesnote.HelperClass.DividerItemDecoration;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.SharedPreference;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.HelperClass.WebViewHelper.CustomTabActivityHelper;
import com.example.randomlocks.gamesnote.HelperClass.WebViewHelper.WebViewFallback;
import com.example.randomlocks.gamesnote.Interface.GamesVideosInterface;
import com.example.randomlocks.gamesnote.Interface.OnLoadMoreListener;
import com.example.randomlocks.gamesnote.Interface.VideoPlayInterface;
import com.example.randomlocks.gamesnote.Modal.GamesVideoModal.GamesVideoModal;
import com.example.randomlocks.gamesnote.Modal.GamesVideoModal.GamesVideoModalList;
import com.example.randomlocks.gamesnote.R;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

import java.util.ArrayList;
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

/**
 * Created by randomlocks on 7/19/2016.
 */

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
    HashMap<Integer, GamesVideoModal> realmMap;
    Call<GamesVideoModalList> call;


    VideoPlayInterface videoPlayInterface;


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
        realm = Realm.getDefaultInstance();

        map = new HashMap<>();
        map.put(GiantBomb.KEY, GiantBomb.API_KEY);
        map.put(GiantBomb.FORMAT, "JSON");
        map.put(GiantBomb.OFFSET, "0");
        map.put(GiantBomb.LIMIT,LIMIT); //fix on endless scroll listener

        realmMap = new HashMap<>();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<GamesVideoModal> realmResults = realm.where(GamesVideoModal.class).findAll();
                for (GamesVideoModal modal : realmResults) {
                    realmMap.put(modal.id, modal);
                }
            }
        });



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_games_video_pager, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDrawer = (DrawerLayout) getActivity().findViewById(R.id.drawer);
        mNavigation = (NavigationView) getActivity().findViewById(R.id.navigation);
        coordinatorLayout = (CoordinatorLayout) mDrawer.findViewById(R.id.root_coordinator);
        floatingSearchView = (FloatingSearchView) getActivity().findViewById(R.id.floating_search_view);
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_view);
        errorText = (TextView) coordinatorLayout.findViewById(R.id.errortext);
        pacman = (AVLoadingIndicatorView) coordinatorLayout.findViewById(R.id.progressBar);


        /************* SWIPE TO REFRESH ************************/


        /********* SET NAVIGATION VIEW **********************/

        mSelectedId = SharedPreference.getFromSharedPreferences(VIDEO_KEY, R.id.nav_all_videos, getContext());
        mTitle = SharedPreference.getFromSharedPreferences(VIDEO_TITLE, getResources().getString(R.string.all_video), getContext());
        mNavigation.setCheckedItem(mSelectedId);
        mNavigation.setNavigationItemSelectedListener(this);
        manager = new ConsistentLinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        if(isReduced)
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));



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


                    case R.id.view:


                        if (item.getTitle().equals(getString(R.string.compact_view))) {
                            isReduced = false;
                            item.setTitle(getString(R.string.reduce_view));


                        } else {
                            item.setTitle(getString(R.string.compact_view));
                            isReduced = true;
                        }

                        if(isReduced)
                            recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
                        else
                            recyclerView.removeItemDecoration(new DividerItemDecoration(getContext()));




                        if (adapter != null) {
                            adapter.setSimple(isReduced);


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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        videoPlayInterface = (VideoPlayInterface) getActivity();
    }

    private void performSearch(String text,boolean allSearch) {

        pacman.setVisibility(View.VISIBLE);
        if (listModals!=null && !listModals.isEmpty()) {
            listModals.clear();
            if(adapter!=null){
                adapter.removeAll();

            }

        }

        if (errorText.getVisibility() == View.VISIBLE) {
            errorText.setVisibility(View.GONE);
        }

        String filter = null;
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

                    adapter.updateModal(response.body().results);

                }else {

                    if (response.body().results.isEmpty()) {
                        errorText.setVisibility(View.VISIBLE);


                        //result is not empty
                    } else {
                        //searching the data for first time
                        if(adapter==null){



                            listModals = response.body().results;
                            adapter = new GameVideoAdapter(listModals, getContext(), isReduced,realm,GameVideoPagerFragment.this,realmMap,recyclerView);
                            recyclerView.setAdapter(adapter);

                        }else {  //searching the data after first time
                            listModals = response.body().results;
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
            SharedPreference.saveToSharedPreference(GiantBomb.REDUCE_VIEW, isReduced, context);
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

        if (recyclerView!=null && recyclerView.getAdapter() != null) {
            adapter.removeAll();
        }
        pacman.setVisibility(View.VISIBLE);
        if (gamesVideoInterface != null && map != null) {
            performSearch("",true);
        }
    }




    @Override
    public void onWatchLater(GamesVideoModal modal) {
        if (modal.isWatchLater) {
            writeToRealm(modal);
        } else {
            deleteFromRealm(modal);
        }
    }

    @Override
    public void onLike(GamesVideoModal modal) {
        if (modal.isFavorite) {
            writeToRealm(modal);
        } else {
            deleteFromRealm(modal);
        }
    }

    @Override
    public void onShare() {

    }

    @Override
    public void onVideoClick(GamesVideoModal modal) {
        //call the video option dialog
        VideoOptionFragment videoOptionFragment = VideoOptionFragment.newInstance(modal);
        videoOptionFragment.setTargetFragment(this, 0);
        videoOptionFragment.setCancelable(false);
        videoOptionFragment.show(getActivity().getSupportFragmentManager(), "video_option_fragment");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(call!=null)
            call.cancel();
    }


    @Override
    public void onPlay(GamesVideoModal modal, int video_option, boolean use_inbuilt) {
        String url;
        switch (video_option) {
            case 0:
                url = modal.lowUrl + "?api_key=" + GiantBomb.API_KEY;
                if (use_inbuilt)
                    videoPlayInterface.onVideoClick(url);
                else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.setDataAndType(Uri.parse(url), "video/mp4");
                    startActivity(intent);
                }

                break;

            case 1:
                url = modal.highUrl + "?api_key=" + GiantBomb.API_KEY;
                if (use_inbuilt)
                    videoPlayInterface.onVideoClick(url);
                else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.setDataAndType(Uri.parse(url), "video/mp4");
                    startActivity(intent);
                }
                break;
            case 2:
                url = modal.youtubeId;
                Intent intent = YouTubeStandalonePlayer.createVideoIntent(getActivity(), GiantBomb.YOUTUBE_API_KEY, url, 0, true, false);
                startActivity(intent);
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



}
