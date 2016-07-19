package com.example.randomlocks.gamesnote.Fragments.ViewPagerFragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.randomlocks.gamesnote.Adapter.GameVideoAdapter;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by randomlocks on 7/19/2016.
 */
public class GameVideoPagerFragment extends Fragment {

    private static final String MODAL = "list_modals";
    private static final String SCROLL_POSITION = "scroll_position";
    GamesVideoInterface gamesVideoInterface = null;

    RecyclerView recyclerView;
    List<GamesVideoModal> listModals;
    GameVideoAdapter adapter;
    LinearLayoutManager layoutManager;
    Map<String, String> map;
    boolean isReduced = false;


    public GameVideoPagerFragment() {

    }


    public static GameVideoPagerFragment newInstance(int position) {

        Bundle args = new Bundle();
        Log.d("tag", "position : " + position);
        GameVideoPagerFragment fragment = new GameVideoPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        isReduced = SharedPreference.getFromSharedPreferences(GiantBomb.REDUCE_VIEW, false, getContext());
        Log.d("tag", isReduced + "");
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
        recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
        Log.d("tag", "on activity");


        if (savedInstanceState != null) {
            Log.d("tag", "save instance");
            listModals = savedInstanceState.getParcelableArrayList(MODAL);
            fillRecyclerView(listModals, savedInstanceState.getParcelable(SCROLL_POSITION));
        } else if (listModals != null) {
            Log.d("tag", "modal not null");

            fillRecyclerView(listModals, null);
        } else {
            Log.d("tag", "modal null");

            gamesVideoInterface = GiantBomb.createGameVideoService();
            map = new HashMap<>();
            map.put(GiantBomb.KEY, GiantBomb.API_KEY);
            map.put(GiantBomb.FORMAT, "JSON");
            map.put(GiantBomb.OFFSET, "50"); //fix on endless scroll listener
            getGameVideos(gamesVideoInterface, map);


        }
    }

    private void getGameVideos(GamesVideoInterface gamesVideoInterface, Map<String, String> map) {
        gamesVideoInterface.getResult(map).enqueue(new Callback<GamesVideoModalList>() {
            @Override
            public void onResponse(Call<GamesVideoModalList> call, Response<GamesVideoModalList> response) {
                listModals = response.body().results;
                fillRecyclerView(listModals, null);
            }

            @Override
            public void onFailure(Call<GamesVideoModalList> call, Throwable t) {
                Toaster.make(getContext(), "Connectivity Problem");
            }
        });
    }

    private void fillRecyclerView(List<GamesVideoModal> listModals, Parcelable parcelable) {


        if (adapter == null) {
            adapter = new GameVideoAdapter(listModals, getContext(), isReduced);
        }

        if (recyclerView.getLayoutManager() == null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        if (recyclerView.getAdapter() == null) {
            recyclerView.setAdapter(adapter);
        }
        if (parcelable != null && recyclerView.getLayoutManager() != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(parcelable);
        }

    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (isReduced) {
            menu.getItem(0).setTitle("Card view");
        } else {
            menu.getItem(0).setTitle("Compact view");
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


                if (item.getTitle().equals("Compact view")) {
                    isReduced = true;
                    item.setTitle("Card view");

                    ;

                } else {
                    item.setTitle("Compact view");
                    isReduced = false;
                }


                if (adapter != null) {
                    adapter.setSimple(isReduced);
                }

                return true;


        }


        return super.onOptionsItemSelected(item);

    }


    @Override
    public void onPause() {
        super.onPause();
        SharedPreference.saveToSharedPreference(GiantBomb.REDUCE_VIEW, isReduced, getContext());

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MODAL, new ArrayList<>(listModals));
        outState.putParcelable(SCROLL_POSITION, recyclerView.getLayoutManager().onSaveInstanceState());
    }


}
