package com.example.randomlocks.gamesnote.Fragments;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.example.randomlocks.gamesnote.Activity.MainActivity;
import com.example.randomlocks.gamesnote.Adapter.GameCharacterSearchAdapter;
import com.example.randomlocks.gamesnote.HelperClass.CustomView.AVLoadingIndicatorView;
import com.example.randomlocks.gamesnote.HelperClass.CustomView.ConsistentLinearLayoutManager;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.Interface.GameCharacterSearchWikiInterface;
import com.example.randomlocks.gamesnote.Interface.OnLoadMoreListener;
import com.example.randomlocks.gamesnote.Modal.CharacterSearchModal.CharacterSearchModal;
import com.example.randomlocks.gamesnote.Modal.CharacterSearchModal.CharacterSearchModalList;
import com.example.randomlocks.gamesnote.Modal.SearchSuggestionModel;
import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.RealmDatabase.SearchHistoryDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmResults;
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
    private static final java.lang.String SEARCH_QUERY = "search_query" ;


    CoordinatorLayout coordinator;
    AppBarLayout appBarLayout;
    ImageView imageView;
    RecyclerView recyclerView;
    AVLoadingIndicatorView pacman;
    TextView errorText;
    GameCharacterSearchWikiInterface gameCharacterSearchWikiInterface = null;
    Call<CharacterSearchModalList> call;
    List<CharacterSearchModal> modals;
    Map<String, String> map;
    GameCharacterSearchAdapter adapter;
    FloatingSearchView floatingSearchView;
    LinearLayoutManager manager;
    DividerItemDecoration itemDecoration;
    int scrollToPosition = 0;
    boolean isLoadingMore = false;
    Realm realm;
//    ImageView imageView;

    RealmResults<SearchHistoryDatabase> search_results;
    List<SearchSuggestionModel> search_list;







    public GamesCharacterWikiFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        realm = Realm.getDefaultInstance();
        return inflater.inflate(R.layout.fragment_games_character, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        coordinator = (CoordinatorLayout) getActivity().findViewById(R.id.root_coordinator);
        appBarLayout = (AppBarLayout) coordinator.findViewById(R.id.app_bar_layout);
        imageView = (ImageView) appBarLayout.findViewById(R.id.imageView);
        floatingSearchView = (FloatingSearchView) getActivity().findViewById(R.id.floating_search_view);
        recyclerView = (RecyclerView) coordinator.findViewById(R.id.recycler_view);
        pacman = (AVLoadingIndicatorView) coordinator.findViewById(R.id.progressBar);
        errorText = (TextView) coordinator.findViewById(R.id.errortext);

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        CollapsingToolbarLayout.LayoutParams params = new CollapsingToolbarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,metrics.heightPixels/3);
        imageView.setLayoutParams(params);
        imageView.setImageResource(R.drawable.character_wiki_image);


       ;
        DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        floatingSearchView.attachNavigationDrawerToMenuButton(drawer);
        floatingSearchView.setShowMoveUpSuggestion(true);


        manager = new ConsistentLinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        itemDecoration = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);







        if (savedInstanceState != null) {
            modals = savedInstanceState.getParcelableArrayList(GiantBomb.MODAL);
            if (modals != null) {
                loadRecycler(modals, savedInstanceState.getParcelable(SCROLL_POSITION));
            }else {
                performSearch(savedInstanceState.getString(SEARCH_QUERY),false);
            }
        } else {

            if (modals != null) {
                loadRecycler(modals, null);
            }

        }

        if (AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES) {
            floatingSearchView.setViewTextColor(Color.parseColor("#e9e9e9"));
        }

        floatingSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {
                if (!oldQuery.equals("") && newQuery.equals("")) {
                    floatingSearchView.clearSuggestions();
                } else {

                    //this shows the top left circular progress
                    //you can call it where ever you want, but
                    //it makes sense to do it when loading something in
                    //the background.

                    //simulates a query call to a data source
                    //with a new query.
                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            search_results = SearchHistoryDatabase.search(realm,newQuery,SearchHistoryDatabase.CHARACTER_WIKI, true);

                            search_list = new ArrayList<>();
                            int i=0;
                            for(SearchHistoryDatabase search_result : search_results){
                                search_list.add(new SearchSuggestionModel(search_result.getTitle()));
                                if(++i > 5)
                                    break;
                            }

                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            if (search_list.size() == 1 && search_list.get(0).getBody().equals(newQuery)) {
                                floatingSearchView.clearSuggestions();
                                floatingSearchView.clearSearchFocus();
                            } else {
                                floatingSearchView.swapSuggestions(search_list);
                            }
                        }
                    });

                }
            }
        });

        floatingSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {

                //show suggestions when search bar gains focus (typically history suggestions)
                floatingSearchView.swapSuggestions(SearchHistoryDatabase.getHistory(realm,SearchHistoryDatabase.CHARACTER_WIKI,3));

            }

            @Override
            public void onFocusCleared() {
                // floatingSearchView.setSearchBarTitle(mLastQuery);
            }
        });




        floatingSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                floatingSearchView.setSearchBarTitle(searchSuggestion.getBody());
                performSearch(searchSuggestion.getBody(),false);
            }

            @Override
            public void onSearchAction(final String currentQuery) {
                if (currentQuery.trim().length() > 0) {

                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            SearchHistoryDatabase is_added_database = realm.where(SearchHistoryDatabase.class).equalTo(SearchHistoryDatabase.SEARCH_TYPE,SearchHistoryDatabase.CHARACTER_WIKI)
                                    .equalTo(SearchHistoryDatabase.TITLE,currentQuery).findFirst();
                            if(is_added_database==null){
                                SearchHistoryDatabase database = new SearchHistoryDatabase(SearchHistoryDatabase.CHARACTER_WIKI,currentQuery);
                                realm.copyToRealm(database);
                            }else {
                                is_added_database.setDate_added(new Date());
                            }

                        }
                    });
                    performSearch(currentQuery,false);
                }else {
                    Toasty.warning(getContext(),"no search text entered", Toast.LENGTH_SHORT,true).show();
                }

            }
        });



    }

    private void performSearch(String text,boolean allSearch) {

    //    InputMethodHelper.hideKeyBoard(getActivity().getWindow().getCurrentFocus(), getContext());

        if (modals!=null && !modals.isEmpty()) {
            if(adapter!=null){
                Toaster.make(getContext(),"clear modal");
                adapter.removeAll();

            }

        }

        if (errorText.getVisibility() == View.VISIBLE) {
            errorText.setVisibility(View.GONE);
        }
        pacman.setVisibility(View.VISIBLE);


        String filter = null;
        if (!allSearch) {
            filter = "name:" + text;
            map.put(GiantBomb.FILTER, filter);

        }
        map.put(GiantBomb.OFFSET, "0");

        gameCharacterSearchWikiInterface = GiantBomb.createGameCharacterSearchService();
        isLoadingMore = false;
        getCharacterWiki(gameCharacterSearchWikiInterface, map);

    }

    private void getCharacterWiki(final GameCharacterSearchWikiInterface gameCharacterSearchWikiInterface, final Map<String, String> map) {
        call = gameCharacterSearchWikiInterface.getResult(map);
        call.enqueue(new Callback<CharacterSearchModalList>() {
            @Override
            public void onResponse(Call<CharacterSearchModalList> call, Response<CharacterSearchModalList> response) {

                if (pacman.getVisibility() == View.VISIBLE) {
                    pacman.setVisibility(View.GONE);
                }
                // coming to load more data
                if(isLoadingMore){
                    CharacterSearchModalList listModal = response.body();
                    adapter.updateModal(listModal.results);
                    Toaster.makeSnackBar(coordinator, "Showing " + modals.size() + " of " + listModal.numberOfTotalResults + " characters");
                }else {

                    if (response.body().results.isEmpty()) {
                        if (AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES) {
                            Drawable drawable = ContextCompat.getDrawable(getContext(),R.drawable.ic_error);
                            drawable = DrawableCompat.wrap(drawable);
                            DrawableCompat.setTint(drawable, Color.WHITE);
                            DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_ATOP);
                            errorText.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
                        }
                        errorText.setVisibility(View.VISIBLE);


                        //result is not empty
                    } else {
                        //searching the data for first time
                        if(adapter==null){
                            Toaster.make(getContext(),"adapter is null");
                            CharacterSearchModalList listModal = response.body();
                            modals = listModal.results;
                            if (!modals.isEmpty()) {
                                Toaster.makeSnackBar(coordinator, "Showing " + modals.size() + " of " + listModal.numberOfTotalResults + " characters");
                            }
                            adapter = new GameCharacterSearchAdapter(modals, getContext(),recyclerView, new GameCharacterSearchAdapter.OnClickInterface() {
                                @Override
                                public void onItemClick(String apiUrl, String imageUrl, String name) {
                                    ((MainActivity) getActivity()).startCharacterActivity(apiUrl, imageUrl,name);
                                }
                            });
                            recyclerView.setAdapter(adapter);

                        }else {  //searching the data after first time
                            CharacterSearchModalList listModal = response.body();
                            modals = listModal.results;
                            Toaster.makeSnackBar(coordinator, "Showing " + modals.size() + " of " + listModal.numberOfTotalResults + " characters");
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

                if (!call.isCanceled()) {
                    pacman.setVisibility(View.GONE);
                    Toaster.makeSnackBar(coordinator, "Connectivity Problem", "RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pacman.setVisibility(View.VISIBLE);
                            getCharacterWiki(gameCharacterSearchWikiInterface,map);
                        }
                    });
                }



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
       // inflater.inflate(R.menu.empty_menu, menu);
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
        outState.putString(SEARCH_QUERY,floatingSearchView.getQuery());
        if (recyclerView != null && recyclerView.getLayoutManager() != null) {
            outState.putParcelable(SCROLL_POSITION, recyclerView.getLayoutManager().onSaveInstanceState());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(call!=null)
            call.cancel();
        if (realm != null && !realm.isClosed())
            realm.close();

    }
}
