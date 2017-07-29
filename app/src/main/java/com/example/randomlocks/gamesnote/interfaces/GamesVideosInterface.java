package com.example.randomlocks.gamesnote.interfaces;

import com.example.randomlocks.gamesnote.modals.GamesVideoModal.GamesVideoModalList;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by randomlocks on 7/18/2016.
 */
public interface GamesVideosInterface {


    @GET("videos")
    Call<GamesVideoModalList> getResult(@QueryMap Map<String, String> options);


}
