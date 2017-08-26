package com.example.randomlocks.gamesnote.fragments.ViewPagerFragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.adapter.GameVideoOtherAdapter;
import com.example.randomlocks.gamesnote.dialogFragment.VideoOptionFragment;
import com.example.randomlocks.gamesnote.fragments.GamesVideoFragment;
import com.example.randomlocks.gamesnote.helperClass.CustomView.ConsistentLinearLayoutManager;
import com.example.randomlocks.gamesnote.helperClass.GiantBomb;
import com.example.randomlocks.gamesnote.helperClass.SharedPreference;
import com.example.randomlocks.gamesnote.helperClass.WebViewHelper.CustomTabActivityHelper;
import com.example.randomlocks.gamesnote.helperClass.WebViewHelper.WebViewFallback;
import com.example.randomlocks.gamesnote.interfaces.VideoPlayInterface;
import com.example.randomlocks.gamesnote.modals.gamesVideoModal.GamesVideoModal;
import com.example.randomlocks.gamesnote.realmDatabase.WatchedVideoDatabase;

import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

import static android.app.Activity.RESULT_OK;


public class GameVideoOtherPagerFragment extends Fragment implements VideoOptionFragment.OnPlayInterface {

    private static final String MODAL = "list_modals";
    private static final String SCROLL_POSITION = "scroll_position";
    private static final int LIKE_TYPE = 1;
    Realm realm;
    RecyclerView recyclerView;
    boolean isReduced = false;
    int position;
    GameVideoOtherAdapter adapter;
    RealmResults<GamesVideoModal> listModals;
    ConsistentLinearLayoutManager manager;
    TextView errorText;

    VideoPlayInterface videoPlayInterface;
    HashMap<Integer, Integer> realmMap;

    GamesVideoFragment parentFragment;

    int video_id;
    int adapterPosition;


    public GameVideoOtherPagerFragment() {
    }

    public static GameVideoOtherPagerFragment newInstance(int position) {

        Bundle args = new Bundle();
        args.putInt(GiantBomb.POSITION, position);
        GameVideoOtherPagerFragment fragment = new GameVideoOtherPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("tag1", "oncreate");
        setHasOptionsMenu(true);
        position = getArguments().getInt(GiantBomb.POSITION);
        isReduced = SharedPreference.getFromSharedPreferences(GiantBomb.REDUCE_VIEW, false, getContext());

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_games_video_pager, container, false);
        realm = Realm.getDefaultInstance();
        realmMap = new HashMap<>();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<WatchedVideoDatabase> realmResults = realm.where(WatchedVideoDatabase.class).findAll();
                for (WatchedVideoDatabase modal : realmResults) {
                    realmMap.put(modal.id, modal.time_elapsed);
                }
            }
        });

        parentFragment = (GamesVideoFragment) getParentFragment();
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        errorText = (TextView) view.findViewById(R.id.errortext);

      /*  swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (adapter != null) {
                    boolean isSimple = SharedPreference.getFromSharedPreferences(GiantBomb.REDUCE_VIEW, false, getContext());
                    adapter.setSimple(isSimple);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });*/
        manager = new ConsistentLinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        if(isReduced)
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));



            if (position == LIKE_TYPE) {
                listModals = realm.where(GamesVideoModal.class).equalTo("isFavorite", true).findAllSorted("dateAdded", Sort.DESCENDING);
                Log.d("Tag1", "onlike modal");
            } else {
                listModals = realm.where(GamesVideoModal.class).equalTo("isWatchLater", true).findAllSorted("dateAdded", Sort.DESCENDING);
            }
        listModals.addChangeListener(new RealmChangeListener<RealmResults<GamesVideoModal>>() {
            @Override
            public void onChange(RealmResults<GamesVideoModal> element) {
                if (element.size() > 0)
                    errorText.setVisibility(View.GONE);
                else
                    errorText.setVisibility(View.VISIBLE);

            }
        });
            fillRecyclerView(listModals);





        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        videoPlayInterface = (VideoPlayInterface) getActivity();
    }

    public void updateAdapter() {
        if (recyclerView != null && adapter != null) {
            adapter.setSimple(isReduced);
        }
    }


    private void fillRecyclerView(RealmResults<GamesVideoModal> listModals) {

        if (listModals.isEmpty()) {
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_error);
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable, Color.WHITE);
                DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_ATOP);
                errorText.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
            }
            errorText.setVisibility(View.VISIBLE);
        } else
            errorText.setVisibility(View.GONE);

        adapter = new GameVideoOtherAdapter(getContext(), listModals, true, isReduced, realm, position, realmMap, new GameVideoOtherAdapter.OnClickInterface() {
                @Override
                public void onVideoClick(GamesVideoModal modal, int position, int elapsed_time) {
                    adapterPosition = position;
                    video_id = modal.id;
                    VideoOptionFragment videoOptionFragment = VideoOptionFragment.newInstance(modal, elapsed_time);
                    videoOptionFragment.setTargetFragment(GameVideoOtherPagerFragment.this, 0);
                    videoOptionFragment.setCancelable(false);
                    videoOptionFragment.show(getActivity().getSupportFragmentManager(), "video_option_fragment");
                }
            });


        if(recyclerView.getLayoutManager()==null){
            recyclerView.setLayoutManager(manager);
        }

        if (recyclerView.getAdapter() == null) {
            recyclerView.setAdapter(adapter);
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (realm != null && !realm.isClosed()) {
            realm.removeAllChangeListeners();
            realm.close();
        }
    }

    @Override
    public void onPlay(GamesVideoModal modal, int video_option, boolean use_inbuilt, int elapsed_time) {
        String url;
        switch (video_option) {
            case 0:
                url = modal.lowUrl + "?api_key=" + SharedPreference
                        .getFromSharedPreferences(GiantBomb.API_KEY, GiantBomb.DEFAULT_API_KEY, getContext());
                if (use_inbuilt)
                    videoPlayInterface.onVideoClick(modal, url, modal.id, elapsed_time, position);
                else {
                    videoPlayInterface.onExternalPlayerVideoClick(url, modal.id);

                }

                break;

            case 1:
                url = modal.highUrl + "?api_key=" + SharedPreference
                        .getFromSharedPreferences(GiantBomb.API_KEY, GiantBomb.DEFAULT_API_KEY, getContext());
                if (use_inbuilt)
                    videoPlayInterface.onVideoClick(modal, url, modal.id, elapsed_time, position);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == position) {
            if (resultCode == RESULT_OK) {
                final int time_elapsed = data.getIntExtra(GiantBomb.SEEK_POSITION, 0);

                if (time_elapsed > 0) {
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
                            adapter.updateModal(adapterPosition, realmMap);
                        }
                    });
                }


            }
        }
    }

    public void updateAdapter(boolean isReduced) {
        if (adapter != null)
            adapter.setSimple(isReduced);
    }
}
