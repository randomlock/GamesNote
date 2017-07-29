package com.example.randomlocks.gamesnote.interfaces;


import com.example.randomlocks.gamesnote.modals.CharacterSearchModal.CharacterSearchModalList;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by randomlocks on 7/11/2016.
 */
public interface GameCharacterSearchWikiInterface {


    @GET("characters")
    Call<CharacterSearchModalList> getResult(@QueryMap Map<String, String> options);


}
