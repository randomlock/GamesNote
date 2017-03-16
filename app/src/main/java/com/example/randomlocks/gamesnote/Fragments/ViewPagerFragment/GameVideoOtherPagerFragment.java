package com.example.randomlocks.gamesnote.Fragments.ViewPagerFragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.example.randomlocks.gamesnote.Adapter.GameVideoOtherAdapter;
import com.example.randomlocks.gamesnote.DialogFragment.VideoOptionFragment;
import com.example.randomlocks.gamesnote.HelperClass.CustomView.ConsistentLinearLayoutManager;
import com.example.randomlocks.gamesnote.HelperClass.DividerItemDecoration;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.SharedPreference;
import com.example.randomlocks.gamesnote.HelperClass.WebViewHelper.CustomTabActivityHelper;
import com.example.randomlocks.gamesnote.HelperClass.WebViewHelper.WebViewFallback;
import com.example.randomlocks.gamesnote.Interface.VideoPlayInterface;
import com.example.randomlocks.gamesnote.Modal.GamesVideoModal.GamesVideoModal;
import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.RealmDatabase.WatchedVideoDatabase;

import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmResults;

import static android.app.Activity.RESULT_OK;

/**
 * Created by randomlocks on 7/19/2016.
 */
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
    FloatingSearchView floatingSearchView;
    ConsistentLinearLayoutManager manager;

    VideoPlayInterface videoPlayInterface;
    HashMap<Integer, Integer> realmMap;

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
        setHasOptionsMenu(true);
        position = getArguments().getInt(GiantBomb.POSITION);
        isReduced = SharedPreference.getFromSharedPreferences(GiantBomb.REDUCE_VIEW, false, getContext());
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


    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_games_video_pager,container,false);

        floatingSearchView = (FloatingSearchView) getActivity().findViewById(R.id.floating_search_view);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
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
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));


        if (listModals != null) {
            fillRecyclerView(listModals);
        } else {
            if (position == LIKE_TYPE) {
                listModals = realm.where(GamesVideoModal.class).equalTo("isFavorite", true).findAll();
            } else {
                listModals = realm.where(GamesVideoModal.class).equalTo("isWatchLater", true).findAll();
            }
            fillRecyclerView(listModals);

        }



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


    private void fillRecyclerView(RealmResults<GamesVideoModal> listModals) {

        //TODO add error text for no video

        if (adapter == null) {
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
        }

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
        if (!realm.isClosed()) {
            realm.close();
        }
    }

    @Override
    public void onPlay(GamesVideoModal modal, int video_option, boolean use_inbuilt, int elapsed_time) {
        String url;
        switch (video_option) {
            case 0:
                url = modal.lowUrl + "?api_key=" + GiantBomb.API_KEY;
                if (use_inbuilt)
                    videoPlayInterface.onVideoClick(url, modal.id, elapsed_time, position);
                else {
                    videoPlayInterface.onExternalPlayerVideoClick(url, modal.id);

                }

                break;

            case 1:
                url = modal.highUrl + "?api_key=" + GiantBomb.API_KEY;
                if (use_inbuilt)
                    videoPlayInterface.onVideoClick(url, modal.id, elapsed_time, position);
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
}
