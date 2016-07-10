package com.example.randomlocks.gamesnote.Interface;

import com.example.randomlocks.gamesnote.Modal.GameReviewModal.GameReviewModalList;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by randomlocks on 7/7/2016.
 */
public interface GameReviewInterface {

    @GET("review/{id}")
    Call<GameReviewModalList> getResult(@Path("id") String gameId, @QueryMap Map<String, String> options);


}
