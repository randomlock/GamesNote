package com.example.randomlocks.gamesnote.interfaces;

import com.example.randomlocks.gamesnote.modals.GameDetailModal.GameVideoMinimal;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by randomlock on 1/24/2017.
 */

public interface GameDetailVideoInterface {


    @GET("video/{id}")
    Call<GameVideoMinimal> getResult(@Path("id") String videoId, @QueryMap Map<String, String> options);

}
