package com.example.randomlocks.gamesnote.Fragments;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

import com.example.randomlocks.gamesnote.Activity.MainActivity;
import com.example.randomlocks.gamesnote.Adapter.GameCharacterSearchAdapter;
import com.example.randomlocks.gamesnote.Adapter.GameWikiAdapter;
import com.example.randomlocks.gamesnote.HelperClass.CustomView.AVLoadingIndicatorView;
import com.example.randomlocks.gamesnote.HelperClass.CustomView.ConsistentLinearLayoutManager;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.InputMethodHelper;
import com.example.randomlocks.gamesnote.HelperClass.SharedPreference;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.Interface.GameCharacterSearchWikiInterface;
import com.example.randomlocks.gamesnote.Interface.OnLoadMoreListener;
import com.example.randomlocks.gamesnote.Modal.CharacterSearchModal.CharacterSearchModal;
import com.example.randomlocks.gamesnote.Modal.CharacterSearchModal.CharacterSearchModalList;
import com.example.randomlocks.gamesnote.Modal.GameWikiModal;
import com.example.randomlocks.gamesnote.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class GamesCharacterWikiFragment extends Fragment {

    private static final String RECYCLER_STYLE = "character_recycler_style";
    private static final String SCROLL_POSITION = "scroll_position";
    private static final String LIMIT = "50";


    CoordinatorLayout coordinator;
    EditText searchCharacter;
    RecyclerView recyclerView;
    AVLoadingIndicatorView pacman;
    TextView errorText;
    GameCharacterSearchWikiInterface gameCharacterSearchWikiInterface = null;
    List<CharacterSearchModal> modals;
    Map<String, String> map;
    GameCharacterSearchAdapter adapter;
    Toolbar toolbar;
    LinearLayoutManager manager;
    int scrollToPosition = 0;
    boolean isLoadingMore = false;
//    ImageView imageView;








    public GamesCharacterWikiFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        map = new HashMap<>();
        map.put(GiantBomb.KEY, GiantBomb.API_KEY);
        map.put(GiantBomb.FORMAT, "JSON");
        map.put(GiantBomb.LIMIT, LIMIT);
        map.put(GiantBomb.OFFSET, "0");
        map.put(GiantBomb.FIELD_LIST, "image,name,api_detail_url,deck");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_games_character, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        coordinator = (CoordinatorLayout) getActivity().findViewById(R.id.root_coordinator);
        toolbar = (Toolbar) coordinator.findViewById(R.id.my_toolbar);
        searchCharacter = (EditText) coordinator.findViewById(R.id.search_character);
        recyclerView = (RecyclerView) coordinator.findViewById(R.id.recycler_view);
        pacman = (AVLoadingIndicatorView) coordinator.findViewById(R.id.progressBar);
        errorText = (TextView) coordinator.findViewById(R.id.errortext);
        //     imageView = (ImageView) coordinator.findViewById(R.id.appbar_image);


        //    Picasso.with(getContext()).load("http://images.popmatters.com/blog_art/m/max_payne_old.jpg").placeholder(R.drawable.headerbackground).fit().centerInside().into(imageView);


        AppCompatActivity actionBar = (AppCompatActivity) getActivity();
        actionBar.setSupportActionBar(toolbar);
        actionBar.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        DrawerLayout drawer = (DrawerLayout) actionBar.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();




        manager = new ConsistentLinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);






        if (savedInstanceState != null) {
            modals = savedInstanceState.getParcelableArrayList(GiantBomb.MODAL);
            if (modals != null) {
                loadRecycler(modals, savedInstanceState.getParcelable(SCROLL_POSITION));
            }
        } else {

            if (modals != null) {
                loadRecycler(modals, null);
            }

        }

        searchCharacter.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (searchCharacter.getText().toString().trim().length() > 0) {
                        performSearch(searchCharacter.getText().toString());
                        return true;

                    }
                }
                return false;
            }
        });


    }

    private void performSearch(String text) {

        InputMethodHelper.hideKeyBoard(getActivity().getWindow().getCurrentFocus(), getContext());

        if (modals!=null && !modals.isEmpty()) {
            modals.clear();
            if(adapter!=null){
                Toaster.make(getContext(),"clear modal");
                adapter.removeAll();

            }

        }

        if (errorText.getVisibility() == View.VISIBLE) {
            errorText.setVisibility(View.GONE);
        }
        pacman.setVisibility(View.VISIBLE);

        String filter = "name:" + text;
        map.put(GiantBomb.FILTER, filter);
        map.put(GiantBomb.OFFSET, "0");

        gameCharacterSearchWikiInterface = GiantBomb.createGameCharacterSearchService();
        isLoadingMore = false;
        getCharacterWiki(gameCharacterSearchWikiInterface, map);

    }

    private void getCharacterWiki(final GameCharacterSearchWikiInterface gameCharacterSearchWikiInterface, final Map<String, String> map) {

        gameCharacterSearchWikiInterface.getResult(map).enqueue(new Callback<CharacterSearchModalList>() {
            @Override
            public void onResponse(Call<CharacterSearchModalList> call, Response<CharacterSearchModalList> response) {

                if (pacman.getVisibility() == View.VISIBLE) {
                    pacman.setVisibility(View.GONE);
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

                            modals = response.body().results;
                            adapter = new GameCharacterSearchAdapter(modals, getContext(),recyclerView, new GameCharacterSearchAdapter.OnClickInterface() {
                                @Override
                                public void onItemClick(String apiUrl, String imageUrl, String name) {
                                    ((MainActivity) getActivity()).startCharacterActivity(apiUrl, imageUrl,name);
                                }
                            });
                            recyclerView.setAdapter(adapter);

                        }else {  //searching the data after first time
                            modals = response.body().results;
                            adapter.swap(modals);
                            Toaster.make(getContext(),"coming to swap"+modals.size());

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
                                getCharacterWiki(gameCharacterSearchWikiInterface, map);

                        }
                    });
                }


            }

            @Override
            public void onFailure(Call<CharacterSearchModalList> call, Throwable t) {
                pacman.setVisibility(View.GONE);
                Snackbar.make(coordinator, "Connectivity Problem", Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getCharacterWiki(gameCharacterSearchWikiInterface, map);
                            }
                        }).show();
            }
        });

    }

    private void loadRecycler(List<CharacterSearchModal> listModals, Parcelable parcelable) {

        if(adapter==null){
            adapter = new GameCharacterSearchAdapter(listModals, getContext(),recyclerView, new GameCharacterSearchAdapter.OnClickInterface() {
                @Override
                public void onItemClick(String apiUrl, String imageUrl, String name) {
                    ((MainActivity) getActivity()).startCharacterActivity(apiUrl, imageUrl,name);
                }
            });
            recyclerView.setAdapter(adapter);
        }

        if (parcelable != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(parcelable);
        }
    }







    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.game_character_wiki_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

/*
        if (item.getItemId() == R.id.view) {

            if (recyclerView != null && recyclerView.getLayoutManager() != null) {

                scrollToPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                        .findFirstCompletelyVisibleItemPosition();
            }

            if (item.getTitle().equals(getString(R.string.grid_view))) {
                isSimple = false;
                manager = new GridLayoutManager(getContext(), 2);
                item.setTitle(getString(R.string.list_view));


            } else {
                item.setTitle(getString(R.string.grid_view));
                manager = new LinearLayoutManager(getContext());
                isSimple = true;
            }


            if (recyclerView != null) {
                recyclerView.setLayoutManager(manager);
                recyclerView.scrollToPosition(scrollToPosition);
            }
            SharedPreference.saveToSharedPreference(RECYCLER_STYLE, isSimple, getContext());

            return true;

        }*/


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (modals != null) {
            outState.putParcelableArrayList(GiantBomb.MODAL, new ArrayList<>(modals));
        }
        if (recyclerView != null && recyclerView.getLayoutManager() != null) {
            outState.putParcelable(SCROLL_POSITION, recyclerView.getLayoutManager().onSaveInstanceState());
        }
    }
}
