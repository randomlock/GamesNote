package com.example.randomlocks.gamesnote.modals.apiTokenModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by randomlock on 8/12/2017.
 */

public class ApiTokenModel {

    @Expose
    private String status;
    @Expose
    @SerializedName("regToken")
    private String regToken;

    ApiTokenModel() {

    }

    public String getStatus() {
        return status;
    }

    public String getRegToken() {
        return regToken;
    }
}
