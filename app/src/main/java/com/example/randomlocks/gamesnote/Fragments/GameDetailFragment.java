package com.example.randomlocks.gamesnote.Fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.example.randomlocks.gamesnote.Adapter.GameDetailPagerAdapter;
import com.example.randomlocks.gamesnote.Adapter.WikiPagerAdapter;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.Interface.GameWikiDetailInterface;
import com.example.randomlocks.gamesnote.Modal.GameDetailModal.GameDetailImages;
import com.example.randomlocks.gamesnote.Modal.GameDetailModal.GameDetailListModal;
import com.example.randomlocks.gamesnote.Modal.GameDetailModal.GameDetailModal;
import com.example.randomlocks.gamesnote.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class GameDetailFragment extends Fragment {


    public static final String API_URL = "apiUrl";
    Toolbar toolbar;
    String apiUrl;
    GameWikiDetailInterface gameWikiDetailInterface;
    ViewPager viewPager;
    Map<String,String> map;
    GameDetailModal gameDetailModal;






    public GameDetailFragment() {
        // Required empty public constructor
    }

     public static GameDetailFragment newInstance(String apiUrl) {

        Bundle args = new Bundle();
        args.putString(API_URL,apiUrl);
        GameDetailFragment fragment = new GameDetailFragment();
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
        String str[] = getArguments().getString("apiUrl").split("/");
        apiUrl = str[str.length-1];
        Toaster.make(getContext(), apiUrl);
        return inflater.inflate(R.layout.fragment_game_detail, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        /************************************** TOOLBAR SET *********************************/
        toolbar = (Toolbar) getActivity().findViewById(R.id.my_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);


        // toolbar.setTitle("Game Detail");   //to be changed to game title

       /************************************** VIEWPAGER SET *********************************/
        viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);



        /**************** RETROFIT ************************/

        gameWikiDetailInterface = GiantBomb.createService(GameWikiDetailInterface.class);
        map = new HashMap<>();
        map.put(GiantBomb.KEY,GiantBomb.API_KEY);
        map.put(GiantBomb.FORMAT,"JSON");
        getGameDetail(gameWikiDetailInterface, map);

    }

    private void getGameDetail(GameWikiDetailInterface gameWikiDetailInterface, Map<String, String> map) {

           gameWikiDetailInterface.getResult(apiUrl,map).enqueue(new Callback<GameDetailListModal>() {
               @Override
               public void onResponse(Call<GameDetailListModal> call, Response<GameDetailListModal> response) {
                  gameDetailModal =  response.body().results;
                   List<GameDetailImages> image = gameDetailModal.images;
                   ArrayList<String> images = new ArrayList<>();
                   for (int i = 0; i <image.size() ; i++) {
                       images.add(image.get(i).thumbUrl);
                   }

                   WikiPagerAdapter pagerAdapter = new WikiPagerAdapter(getContext(),images.size(),images);
                   viewPager.setAdapter(pagerAdapter);





               }

               @Override
               public void onFailure(Call<GameDetailListModal> call, Throwable t) {

               }
           });
    }



}
