package com.example.randomlocks.gamesnote.interfaces;

import com.example.randomlocks.gamesnote.modals.apiTokenModel.ApiTokenModel;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by randomlock on 8/12/2017.
 */

public interface GiantBombApiTokenInteraface {

    @GET("https://www.giantbomb.com/app/myapp/get-result")
    Call<ApiTokenModel> getResult(@QueryMap Map<String, String> options);


}
