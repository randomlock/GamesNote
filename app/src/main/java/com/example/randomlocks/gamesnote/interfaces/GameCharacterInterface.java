package com.example.randomlocks.gamesnote.interfaces;

import com.example.randomlocks.gamesnote.modals.GameCharacterModal.CharacterListModal;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by randomlocks on 6/25/2016.
 */
public interface GameCharacterInterface {


    @GET("character/{id}")
    Call<CharacterListModal> getResult(@Path("id") String gameId, @QueryMap Map<String, String> options);


}
