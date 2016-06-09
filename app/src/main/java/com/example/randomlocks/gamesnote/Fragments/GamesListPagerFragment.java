package com.example.randomlocks.gamesnote.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.randomlocks.gamesnote.Adapter.MyRecyclerAdapter;
import com.example.randomlocks.gamesnote.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by randomlocks on 3/17/2016.
 */
public class GamesListPagerFragment extends Fragment {

    private static final String ARG_PAGE = "total page";
    RecyclerView recyclerView;
    List<String> stringList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pager_games_list,container,false);
    }

    public static GamesListPagerFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        GamesListPagerFragment fragment = new GamesListPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_view);
        stringList = new ArrayList<>();

        for (int i = 0; i < 40 ; i++) {

            stringList.add("hello brother");

        }

        recyclerView.setAdapter(new MyRecyclerAdapter(stringList, getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



    }


}
