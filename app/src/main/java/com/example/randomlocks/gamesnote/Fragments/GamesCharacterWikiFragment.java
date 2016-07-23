package com.example.randomlocks.gamesnote.Fragments;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.Activity.MainActivity;
import com.example.randomlocks.gamesnote.Adapter.CharacterSearchAdapter;
import com.example.randomlocks.gamesnote.HelperClass.AVLoadingIndicatorView;
import com.example.randomlocks.gamesnote.HelperClass.DividerItemDecoration;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.InputMethodHelper;
import com.example.randomlocks.gamesnote.Interface.GameCharacterSearchWikiInterface;
import com.example.randomlocks.gamesnote.Modal.CharacterSearchModal.CharacterSearchModal;
import com.example.randomlocks.gamesnote.Modal.CharacterSearchModal.CharacterSearchModalList;
import com.example.randomlocks.gamesnote.R;
import com.squareup.picasso.Picasso;

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
    ImageView imageView;


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
        imageView = (ImageView) coordinator.findViewById(R.id.appbar_image);


        Picasso.with(getContext()).load("http://images.popmatters.com/blog_art/m/max_payne_old.jpg").placeholder(R.drawable.headerbackground).fit().centerInside().into(imageView);


        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (savedInstanceState != null) {
            modals = savedInstanceState.getParcelableArrayList(GiantBomb.MODAL);
            loadRecycler(modals, savedInstanceState.getParcelable(SCROLL_POSITION));
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

        if (modals != null) {
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (parcelable != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(parcelable);
        }
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
        adapter = new CharacterSearchAdapter(modals, getContext(), new CharacterSearchAdapter.OnClickInterface() {
            @Override
            public void onItemClick(String apiUrl, String imageUrl) {
                ((MainActivity) getActivity()).startCharacterActivity(apiUrl, imageUrl);
            }
        });
        recyclerView.setAdapter(adapter);
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
