package com.example.randomlocks.gamesnote.Fragments.ViewPagerFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.example.randomlocks.gamesnote.Adapter.GameVideoOtherAdapter;
import com.example.randomlocks.gamesnote.HelperClass.CustomView.ConsistentLinearLayoutManager;
import com.example.randomlocks.gamesnote.HelperClass.DividerItemDecoration;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.SharedPreference;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.Modal.GamesVideoModal.GamesVideoModal;
import com.example.randomlocks.gamesnote.R;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by randomlocks on 7/19/2016.
 */
public class GameVideoOtherPagerFragment extends Fragment {

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

    private void fillRecyclerView(RealmResults<GamesVideoModal> listModals) {

        //TODO add error text for no video

        if (adapter == null) {
            adapter = new GameVideoOtherAdapter(getContext(), listModals, true, isReduced,realm,position);
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
}
