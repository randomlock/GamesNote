package com.example.randomlocks.gamesnote.Fragments.ViewPagerFragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.Adapter.GameVideoAdapter;
import com.example.randomlocks.gamesnote.HelperClass.AVLoadingIndicatorView;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.InputMethodHelper;
import com.example.randomlocks.gamesnote.HelperClass.SharedPreference;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.Interface.GamesVideoInterface;
import com.example.randomlocks.gamesnote.Modal.GamesVideoModal.GamesVideoModal;
import com.example.randomlocks.gamesnote.Modal.GamesVideoModal.GamesVideoModalList;
import com.example.randomlocks.gamesnote.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class GameVideoPagerFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String MODAL = "list_modals";
    private static final String SCROLL_POSITION = "scroll_position";
    public static final String VIDEO_KEY = "navigation_video_id"; //FOR SAVING MENU ITEM
    public static final String VIDEO_TITLE = "navigation_video_title"; //FOR MENU TOOLBAR TITLE


    GamesVideoInterface gamesVideoInterface = null;
    RecyclerView recyclerView;
    List<GamesVideoModal> listModals;
    GameVideoAdapter adapter;
    Map<String, String> map;
    boolean isReduced = false, isAllVideo = false;
    AVLoadingIndicatorView pacman;
    Realm realm;
    RealmAsyncTask realmAsyncTask;
    public DrawerLayout mDrawer;
    NavigationView mNavigation;
    String mTitle;
    int mSelectedId;
    EditText searchVideos;
    TextView errorText;
    CoordinatorLayout coordinatorLayout;
    SwipeRefreshLayout swipeRefreshLayout;


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
        setHasOptionsMenu(true);
        isReduced = SharedPreference.getFromSharedPreferences(GiantBomb.REDUCE_VIEW, false, getContext());
        realm = Realm.getDefaultInstance();

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
        searchVideos = (EditText) coordinatorLayout.findViewById(R.id.search_video);
        swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipeContainer);
        recyclerView = (RecyclerView) swipeRefreshLayout.findViewById(R.id.recycler_view);
        errorText = (TextView) getView().findViewById(R.id.errortext);

        pacman = (AVLoadingIndicatorView) getView().findViewById(R.id.progressBar);
        pacman.setVisibility(View.VISIBLE);


        /************* SWIPE TO REFRESH ************************/
        swipeRefreshLayout.setOnRefreshListener(this);


        /********* SET NAVIGATION VIEW **********************/

        mSelectedId = SharedPreference.getFromSharedPreferences(VIDEO_KEY, R.id.nav_all_videos, getContext());
        mTitle = SharedPreference.getFromSharedPreferences(VIDEO_TITLE, getResources().getString(R.string.all_video), getContext());
        mNavigation.setCheckedItem(mSelectedId);
        mNavigation.setNavigationItemSelectedListener(this);


        if (savedInstanceState != null) {
            listModals = savedInstanceState.getParcelableArrayList(MODAL);
            fillRecyclerView(listModals, savedInstanceState.getParcelable(SCROLL_POSITION));
        } else if (listModals != null) {

            fillRecyclerView(listModals, null);
        } else {

            gamesVideoInterface = GiantBomb.createGameVideoService();
            map = new HashMap<>();
            map.put(GiantBomb.KEY, GiantBomb.API_KEY);
            map.put(GiantBomb.FORMAT, "JSON");
            map.put(GiantBomb.LIMIT, "50"); //fix on endless scroll listener
            selectDrawer(mSelectedId, mTitle);
            getGameVideos(gamesVideoInterface, map);


        }


        searchVideos.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (searchVideos.getText().toString().trim().length() > 0) {
                        performSearch(searchVideos.getText().toString());
                        return true;

                    }
                }

                if (actionId == EditorInfo.IME_ACTION_PREVIOUS) {
                    Toaster.make(getContext(), "hello");
                }

                return false;
            }
        });

        searchVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!searchVideos.isCursorVisible()) {
                    searchVideos.setCursorVisible(true);
                }
            }
        });


    }


    private void performSearch(String text) {

        InputMethodHelper.hideKeyBoard(getActivity().getWindow().getCurrentFocus(), getContext());
        if (listModals != null) {
            listModals.clear();
            adapter.notifyDataSetChanged();
        }

        if (errorText.getVisibility() == View.VISIBLE) {
            errorText.setVisibility(View.GONE);
        }
        String filter = "name:" + text;
        if (map != null && gamesVideoInterface != null) {
            map.put(GiantBomb.FILTER, filter);
            getGameVideos(gamesVideoInterface, map);
        }

    }


    private void selectDrawer(int mSelectedId, String mTitle) {

        isAllVideo = false;

        switch (mSelectedId) {


            case R.id.nav_all_videos:
                if (map.containsKey(GiantBomb.VIDEO_TYPE))
                    map.remove(GiantBomb.VIDEO_TYPE);
                isAllVideo = true;
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mTitle);

    }

    private void getGameVideos(final GamesVideoInterface gamesVideoInterface, final Map<String, String> map) {
        pacman.setVisibility(View.VISIBLE);
        gamesVideoInterface.getResult(map).enqueue(new Callback<GamesVideoModalList>() {
            @Override
            public void onResponse(Call<GamesVideoModalList> call, Response<GamesVideoModalList> response) {
                listModals = response.body().results;
                fillRecyclerView(listModals, null);
            }

            @Override
            public void onFailure(Call<GamesVideoModalList> call, Throwable t) {
                pacman.setVisibility(View.GONE);
                Snackbar.make(coordinatorLayout, "Connectivity Problem", Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getGameVideos(gamesVideoInterface, map);

                            }
                        }).show();
            }
        });
    }

    private void fillRecyclerView(List<GamesVideoModal> listModals, Parcelable parcelable) {

        final HashMap<Integer, GamesVideoModal> realmMap = new HashMap<>();
        ;
        pacman.setVisibility(View.GONE);
        if (listModals.size() == 0) {
            errorText.setVisibility(View.VISIBLE);
        }
        if (adapter == null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<GamesVideoModal> realmResults = realm.where(GamesVideoModal.class).findAll();
                    for (GamesVideoModal modal : realmResults) {
                        realmMap.put(modal.id, modal);

                    }

                }
            });
            adapter = new GameVideoAdapter(listModals, getContext(), isReduced, new GameVideoAdapter.OnClickInterface() {
                @Override
                public void onWatchLater(GamesVideoModal modal, int viewId) {
                    if (modal.isWatchLater) {
                        writeToRealm(modal, viewId);
                    } else {
                        deleteFromRealm(modal);
                    }
                }

                @Override
                public void onLike(GamesVideoModal modal, int viewId) {
                    if (modal.isFavorite) {
                        writeToRealm(modal, viewId);
                    } else {
                        deleteFromRealm(modal);
                    }
                }

                @Override
                public void onShare() {

                }
            }, realm, isAllVideo, realmMap);
        }

        if (recyclerView.getLayoutManager() == null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        if (recyclerView.getAdapter() == null) {
            recyclerView.setAdapter(adapter);
        } else {
            adapter.swapModal(listModals, isAllVideo);
        }
        if (parcelable != null && recyclerView.getLayoutManager() != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(parcelable);
        }

    }

    private void deleteFromRealm(final GamesVideoModal modal) {

        // if its in favourite or watch later then dont delete , just update
        if (modal.isFavorite || modal.isWatchLater) {
            realmAsyncTask = realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(modal);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    Toaster.make(getContext(), "Deleted");
                }
            });
        } else {  // if not in fav and watch later then delete
            realmAsyncTask = realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.where(GamesVideoModal.class).equalTo("id", modal.id).findFirst().deleteFromRealm();
                    Log.d("tag", realm.where(GamesVideoModal.class).findAll().size() + "");
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    Toaster.make(getContext(), "Deleted");

                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    Toaster.make(getContext(), error.toString());
                }
            });
        }


    }

    private void writeToRealm(final GamesVideoModal modal, final int viewId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                /* GamesVideoModal realmModal =   realm.where(GamesVideoModal.class).equalTo("id",modal.id).findFirst();

                if(realmModal!=null){

                    if(viewId==R.id.watch_later){
                        modal.isFavorite = realmModal.isFavorite;
                    }else {
                        modal.isWatchLater = realmModal.isWatchLater;
                    }


                }*/

                realm.copyToRealmOrUpdate(modal);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Toaster.make(getContext(), "Added");
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
        inflater.inflate(R.menu.game_video_menu, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                getActivity().onBackPressed();
                return true;

            case R.id.view:


                if (item.getTitle().equals(getString(R.string.compact_view))) {
                    isReduced = true;
                    item.setTitle(getString(R.string.reduce_view));


                } else {
                    item.setTitle(getString(R.string.compact_view));
                    isReduced = false;
                }


                if (adapter != null) {
                    adapter.setSimple(isReduced);
                }

                return true;

            case R.id.nav_open:
                mDrawer.openDrawer(GravityCompat.END);
                return true;


        }


        return super.onOptionsItemSelected(item);

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

        if (realm != null && realm.isInTransaction()) {
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
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        item.setChecked(true);
        mSelectedId = item.getItemId();
        mTitle = item.getTitle().toString();
        selectDrawer(mSelectedId, mTitle);

        changeVideoSource();

        return true;
    }

    private void changeVideoSource() {

        pacman.setVisibility(View.VISIBLE);
        if (listModals != null && recyclerView.getAdapter() != null) {
            listModals.clear();
            adapter.notifyDataSetChanged();
        }

        if (gamesVideoInterface != null && map != null) {
            Log.d("tag", map.toString());
            getGameVideos(gamesVideoInterface, map);
        }
    }


    @Override
    public void onRefresh() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        Toaster.make(getContext(), "hello");
        swipeRefreshLayout.setRefreshing(false);
    }
}
