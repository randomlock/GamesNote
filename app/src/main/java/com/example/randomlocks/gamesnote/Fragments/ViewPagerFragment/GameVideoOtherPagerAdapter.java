package com.example.randomlocks.gamesnote.Fragments.ViewPagerFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.randomlocks.gamesnote.Adapter.GameVideoOtherAdapter;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.SharedPreference;
import com.example.randomlocks.gamesnote.Modal.GamesVideoModal.GamesVideoModal;
import com.example.randomlocks.gamesnote.R;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by randomlocks on 7/19/2016.
 */
public class GameVideoOtherPagerAdapter extends Fragment {

    private static final String MODAL = "list_modals";
    private static final String SCROLL_POSITION = "scroll_position";
    Realm realm;
    RecyclerView recyclerView;
    boolean isReduced = false;
    int position;
    GameVideoOtherAdapter adapter;
    RealmResults<GamesVideoModal> listModals;
    DrawerLayout mDrawer;
    SwipeRefreshLayout swipeRefreshLayout;


    public GameVideoOtherPagerAdapter() {
    }

    public static GameVideoOtherPagerAdapter newInstance(int position) {

        Bundle args = new Bundle();
        args.putInt(GiantBomb.POSITION, position);
        GameVideoOtherPagerAdapter fragment = new GameVideoOtherPagerAdapter();
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


    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_games_video_pager, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipeContainer);
        recyclerView = (RecyclerView) swipeRefreshLayout.findViewById(R.id.recycler_view);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (adapter != null) {
                    boolean isSimple = SharedPreference.getFromSharedPreferences(GiantBomb.REDUCE_VIEW, false, getContext());
                    adapter.setSimple(isSimple);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        if (listModals != null) {
            fillRecyclerView(listModals);
        } else {
            if (position == GamesVideoModal.LIKE_TYPE) {
                listModals = realm.where(GamesVideoModal.class).equalTo("isFavorite", true).findAll();
            } else {
                listModals = realm.where(GamesVideoModal.class).equalTo("isWatchLater", true).findAll();
            }

            fillRecyclerView(listModals);

        }


    }

    private void fillRecyclerView(RealmResults<GamesVideoModal> listModals) {

        //TODO add error text for no video

        if (adapter == null) {
            adapter = new GameVideoOtherAdapter(getContext(), listModals, true, isReduced, new GameVideoOtherAdapter.OnClickInterface() {
                @Override
                public void onWatchLater(GamesVideoModal modal) {

                }

                @Override
                public void onLike(GamesVideoModal modal) {

                }

                @Override
                public void onShare() {

                }
            });
        }

        if (recyclerView.getLayoutManager() == null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        if (recyclerView.getAdapter() == null) {
            recyclerView.setAdapter(adapter);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
