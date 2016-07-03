package com.example.randomlocks.gamesnote.Fragments;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.randomlocks.gamesnote.Adapter.GameNewsAdapter;
import com.example.randomlocks.gamesnote.HelperClass.AVLoadingIndicatorView;
import com.example.randomlocks.gamesnote.HelperClass.DividerItemDecoration;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.Modal.NewsModal.NewsModal;
import com.example.randomlocks.gamesnote.R;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class GamesNewsFragment extends Fragment {

        RecyclerView recyclerView;
    AVLoadingIndicatorView progressBar;
    Toolbar toolbar;
    CoordinatorLayout coordinatorLayout;

    List<NewsModal> modals;
    OkHttpClient client;
    Request request;
    public static String URL = "http://www.gamespot.com/feeds/news/";


    public GamesNewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        client = new OkHttpClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_games_news, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        coordinatorLayout = (CoordinatorLayout) getView().findViewById(R.id.coordinator);
        recyclerView = (RecyclerView) coordinatorLayout.findViewById(R.id.news_recycler_view);
        toolbar = (Toolbar) coordinatorLayout.findViewById(R.id.my_toolbar);
        progressBar = (AVLoadingIndicatorView) coordinatorLayout.findViewById(R.id.indicator);

        AppCompatActivity activity = (AppCompatActivity)getActivity();

        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("Game News");


        if (modals==null) {
            progressBar.setVisibility(View.VISIBLE);
            request = new Request.Builder()
                   .url(URL)
                   .build();


            try {
                runOkHttp();
            } catch (IOException e) {
                Toaster.make(getContext(),"connectivity problem");
            }
        }else {
            loadRecycler(modals);
        }


    }




    public  void runOkHttp() throws IOException {






        client.newCall(request).enqueue(new Callback() {

            Handler mainHandler = new Handler(Looper.getMainLooper());


            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        Snackbar.make(coordinatorLayout, "Connectivity Problem", Snackbar.LENGTH_INDEFINITE)
                                .setAction("RETRY", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            runOkHttp();
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }

                                    }
                                }).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String str = response.body().string();
                NewsModal mod = new NewsModal();
                try {
                    modals = mod.parse(str);

                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                           loadRecycler(modals);
                        }
                    });



                } catch (XmlPullParserException e) {

                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            Toaster.make(getContext(),"Unknown Error . Contact Lord kratos");
                        }
                    });

                }
            }
        });



    }

    private void loadRecycler(List<NewsModal> modals) {

        if (progressBar.getVisibility()==View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new GameNewsAdapter(modals,getContext()));


    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case android.R.id.home :
                getActivity().onBackPressed();
                return true ;





        }
        return super.onOptionsItemSelected(item);
    }
}
