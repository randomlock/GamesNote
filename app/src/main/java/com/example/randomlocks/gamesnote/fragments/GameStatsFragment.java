package com.example.randomlocks.gamesnote.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.fragments.ViewPagerFragment.GameDetailStatPagerFragment;
import com.example.randomlocks.gamesnote.fragments.ViewPagerFragment.GameStatPagerFragment;
import com.example.randomlocks.gamesnote.helperClass.GiantBomb;
import com.example.randomlocks.gamesnote.helperClass.SharedPreference;
import com.example.randomlocks.gamesnote.helperClass.Toaster;
import com.example.randomlocks.gamesnote.interfaces.GameWikiDetailInterface;
import com.example.randomlocks.gamesnote.modals.GameDetailModal.GameDetailIInnerJson;
import com.example.randomlocks.gamesnote.modals.GameDetailModal.GameDetailListModal;
import com.example.randomlocks.gamesnote.modals.GameDetailModal.GameDetailModal;
import com.example.randomlocks.gamesnote.realmDatabase.GameDetailDatabase;
import com.example.randomlocks.gamesnote.realmDatabase.GameListDatabase;
import com.example.randomlocks.gamesnote.realmDatabase.RealmInteger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameStatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameStatsFragment extends Fragment {

    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    GameStatsPagerAdapter adapter;
    Realm realm;
    int no_of_games_to_download;
    RealmResults<GameListDatabase> not_available_game_stat;
    GameWikiDetailInterface gameWikiDetailInterface;
    Map<String, String> map;
    ProgressDialog progressDialog;





    public GameStatsFragment() {
        // Required empty public constructor
    }

    public static GameStatsFragment newInstance() {
        GameStatsFragment fragment = new GameStatsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        realm = Realm.getDefaultInstance();
        no_of_games_to_download = (int) (realm.where(GameListDatabase.class).count()
                -realm.where(RealmInteger.class).count());
        return inflater.inflate(R.layout.fragment_game_stats, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        toolbar = (Toolbar) getActivity().findViewById(R.id.my_toolbar);
        viewPager = (ViewPager) getActivity().findViewById(R.id.my_pager);
        tabLayout = (TabLayout) getActivity().findViewById(R.id.my_tablayout);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        adapter = new GameStatsFragment.GameStatsPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);

        if (no_of_games_to_download>0) {
            Toaster.makeSnackBar(getView(), no_of_games_to_download + " stats need to be downloaded",
                    "Download", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showProgress();
                            downloadStats();
                        }
                    });
        }

    }

    private void showProgress() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Downloading game stats");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(no_of_games_to_download);
        progressDialog.setProgress(0);
        progressDialog.show();
    }

    private void downloadStats() {
        RealmResults<RealmInteger> available_game_stat_id = realm.where(RealmInteger.class).findAll();
        RealmQuery<GameListDatabase> query = realm.where(GameListDatabase.class);
        for(RealmInteger game_id : available_game_stat_id){
            query.notEqualTo(GameListDatabase.GAME_ID,game_id.getGame_id());
        }

        not_available_game_stat = query.findAll();
        getGameStat();


    }

    private void getGameStat() {
        if (gameWikiDetailInterface==null) {
            gameWikiDetailInterface = GiantBomb.createGameDetailService();
            map = new HashMap<>();
            map.put(GiantBomb.KEY, SharedPreference.getFromSharedPreferences(GiantBomb.API_KEY, GiantBomb.DEFAULT_API_KEY, getContext()));
            map.put(GiantBomb.FORMAT, "JSON");
            String field_list = "id,developers,franchises,genres,publishers,similar_games,themes";
            map.put(GiantBomb.FIELD_LIST, field_list);
        }
        getGameDetail(gameWikiDetailInterface, map,not_available_game_stat
                .get(no_of_games_to_download-1).getApiDetailUrl());

    }

    private void getGameDetail(GameWikiDetailInterface gameWikiDetailInterface, Map<String, String> map,String apiUrl) {

        gameWikiDetailInterface.getResult(apiUrl,map).enqueue(new Callback<GameDetailListModal>() {
            @Override
            public void onResponse(Call<GameDetailListModal> call, Response<GameDetailListModal> response) {
                final GameDetailModal gameDetailModal = response.body().results;
               final int game_id = gameDetailModal.id;

               realm.executeTransactionAsync(new Realm.Transaction() {
                   @Override
                   public void execute(Realm realm) {
                       //now add gameDetail

                       updateGameDetail(gameDetailModal.developers, GameDetailDatabase.DEVELOPER_TYPE, realm, game_id);
                       updateGameDetail(gameDetailModal.publishers, GameDetailDatabase.PUBLISHER_TYPE, realm, game_id);
                       updateGameDetail(gameDetailModal.themes, GameDetailDatabase.THEME_TYPE, realm, game_id);
                       updateGameDetail(gameDetailModal.franchises, GameDetailDatabase.FRANCHISE_TYPE, realm, game_id);
                       updateGameDetail(gameDetailModal.genres, GameDetailDatabase.GENRE_TYPE, realm, game_id);
                       updateGameDetail(gameDetailModal.similarGames, GameDetailDatabase.SIMILAR_GAME_TYPE, realm, game_id);
                   }
               }, new Realm.Transaction.OnSuccess() {
                   @Override
                   public void onSuccess() {
                       no_of_games_to_download = no_of_games_to_download-1;
                       progressDialog.incrementProgressBy(1);
                       if(no_of_games_to_download>0){
                           getGameStat();
                       }else {
                           progressDialog.dismiss();
                           if (adapter!=null) {
                               adapter.notifyDataSetChanged();
                           }
                       }
                   }
               });

            }

            @Override
            public void onFailure(Call<GameDetailListModal> call, Throwable t) {

            }
        });


    }

    private void updateGameDetail(List<GameDetailIInnerJson> gameDetailDatabase, int type, Realm realm,int game_id) {
        if (gameDetailDatabase != null) {
            for (GameDetailIInnerJson gameDatabase : gameDetailDatabase) {
                GameDetailDatabase gameDeveloperDatabase = realm.where(GameDetailDatabase.class)
                        .equalTo(GameDetailDatabase.TYPE, type)
                        .equalTo(GameDetailDatabase.NAME, gameDatabase.name).findFirst();
                if (gameDeveloperDatabase != null) {
                    gameDeveloperDatabase.getGames_id().add(new RealmInteger(game_id));
                    gameDeveloperDatabase.setCount(gameDeveloperDatabase.getCount()+1);
                } else {
                    RealmList<RealmInteger> games_id = new RealmList<RealmInteger>();
                    games_id.add(new RealmInteger(game_id));
                    int auto_increment_x=0;
                    Number number = realm.where(GameDetailDatabase.class).equalTo(GameDetailDatabase.TYPE,type).max(GameDetailDatabase.AUTO_INCREMENT_X_VALUE);
                    if(number!=null)
                        auto_increment_x = number.intValue()+1;
                    gameDeveloperDatabase = new GameDetailDatabase(type, gameDatabase.name,1,auto_increment_x, games_id);
                    realm.insertOrUpdate(gameDeveloperDatabase);
                }
            }
        }
    }



    private class GameStatsPagerAdapter extends FragmentStatePagerAdapter {

        private final int PAGE_COUNT = 2;
        private String pageTitle[] = {"basic","detail"};
        private ArrayList<Fragment> fragments;

        private GameStatsPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<>();
            fragments.add(new GameStatPagerFragment());
            fragments.add(new GameDetailStatPagerFragment());
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pageTitle[position];
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(realm!=null && !realm.isClosed())
            realm.close();
    }


}
