package com.example.randomlocks.gamesnote.Interface;

import com.example.randomlocks.gamesnote.Modal.GameDetailModal.GameDetailVideo;
import com.example.randomlocks.gamesnote.Modal.GamesVideoModal.GameVideoMinimal;
import com.example.randomlocks.gamesnote.Modal.GamesVideoModal.GameVideoModalMinimal;
import com.example.randomlocks.gamesnote.Modal.GamesVideoModal.GamesVideoModal;
import com.example.randomlocks.gamesnote.Modal.GamesVideoModal.GamesVideoModalList;

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