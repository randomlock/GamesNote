package com.example.randomlocks.gamesnote.interfaces;

import com.example.randomlocks.gamesnote.modals.userReviewModal.UserReviewModalList;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by randomlocks on 7/7/2016.
 */
public interface UserReviewInterface {


    @GET("user_reviews")
    Call<UserReviewModalList> getResult(@QueryMap Map<String, String> options);

}
