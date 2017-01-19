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
import com.example.randomlocks.gamesnote.Adapter.CharacterSearchAdapter;
import com.example.randomlocks.gamesnote.HelperClass.CustomView.AVLoadingIndicatorView;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.InputMethodHelper;
import com.example.randomlocks.gamesnote.HelperClass.SharedPreference;
import com.example.randomlocks.gamesnote.Interface.GameCharacterSearchWikiInterface;
import com.example.randomlocks.gamesnote.Modal.CharacterSearchModal.CharacterSearchModal;
import com.example.randomlocks.gamesnote.Modal.CharacterSearchModal.CharacterSearchModalList;
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
    CoordinatorLayout coordinator;
    EditText searchCharacter;
    RecyclerView recyclerView;
    AVLoadingIndicatorView pacman;
    TextView errorText;
    GameCharacterSearchWikiInterface gameCharacterSearchWikiInterface = null;
    List<CharacterSearchModal> modals;
    Map<String, String> map;
    CharacterSearchAdapter adapter;
    Toolbar toolbar;
    private boolean isSimple;
    LinearLayoutManager manager;
    int scrollToPosition = 0;
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
        map.put(GiantBomb.FIELD_LIST, "image,name,api_detail_url");
        isSimple = SharedPreference.getFromSharedPreferences(RECYCLER_STYLE, false, getContext());
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
        coordinator = (CoordinatorLayout) getView().findViewById(R.id.root_coordinator);
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

        if (modals != null && adapter != null) {
            modals.clear();
            adapter.notifyDataSetChanged();
        }

        if (errorText.getVisibility() == View.VISIBLE) {
            errorText.setVisibility(View.GONE);
        }
        String filter = "name:" + text;
        map.put(GiantBomb.FILTER, filter);
        gameCharacterSearchWikiInterface = GiantBomb.createGameCharacterSearchService();
        getCharacterWiki(gameCharacterSearchWikiInterface, map);

    }

    private void getCharacterWiki(final GameCharacterSearchWikiInterface gameCharacterSearchWikiInterface, final Map<String, String> map) {
        pacman.setVisibility(View.VISIBLE);
        gameCharacterSearchWikiInterface.getResult(map).enqueue(new Callback<CharacterSearchModalList>() {
            @Override
            public void onResponse(Call<CharacterSearchModalList> call, Response<CharacterSearchModalList> response) {
                modals = response.body().results;
                if (modals.size() == 0) {
                    if (pacman.getVisibility() == View.VISIBLE) {
                        pacman.setVisibility(View.GONE);
                    }
                    errorText.setVisibility(View.VISIBLE);

                    if (recyclerView.getAdapter() != null && recyclerView.getAdapter().getItemCount() != 0) {
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }


                } else {
                    loadRecycler(modals, null);
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


    private void loadRecycler(List<CharacterSearchModal> modals, Parcelable parcelable) {
        if (pacman.getVisibility() == View.VISIBLE) {
            pacman.setVisibility(View.GONE);
        }

        if (errorText.getVisibility() == View.VISIBLE) {
            errorText.setVisibility(View.GONE);
        }

        if (isSimple) {
            manager = new LinearLayoutManager(getContext());
        } else {
            manager = new GridLayoutManager(getContext(), 2);
        }

        recyclerView.setLayoutManager(manager);
        if (parcelable != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(parcelable);
        }
        //  recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
        adapter = new CharacterSearchAdapter(modals, getContext(), new CharacterSearchAdapter.OnClickInterface() {
            @Override
            public void onItemClick(String apiUrl, String imageUrl) {
                ((MainActivity) getActivity()).startCharacterActivity(apiUrl, imageUrl);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (isSimple) {
            menu.getItem(0).setTitle(getString(R.string.grid_view));
        } else {
            menu.getItem(0).setTitle(getString(R.string.list_view));
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.game_character_wiki_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


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

        }


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
