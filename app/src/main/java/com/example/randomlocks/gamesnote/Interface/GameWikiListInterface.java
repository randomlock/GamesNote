package com.example.randomlocks.gamesnote.Interface;

import com.example.randomlocks.gamesnote.Modal.GameWikiListModal;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by randomlocks on 4/25/2016.
 */
public interface GameWikiListInterface {


    @GET("games")
    Call<GameWikiListModal> getResult(@QueryMap Map<String, String> options);


}
