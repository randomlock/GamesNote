package com.example.randomlocks.gamesnote.interfaces;

import com.example.randomlocks.gamesnote.modals.gameDetailModal.GameDetailListModal;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by randomlocks on 5/30/2016.
 */
public interface GameWikiDetailInterface {

    @GET("game/{id}")
    Call<GameDetailListModal> getResult(@Path("id") String gameId, @QueryMap Map<String, String> options);
}
