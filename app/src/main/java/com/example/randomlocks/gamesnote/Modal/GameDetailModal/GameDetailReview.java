package com.example.randomlocks.gamesnote.Modal.GameDetailModal;

import com.google.gson.annotations.Expose;

import io.realm.RealmObject;

/**
 * Created by randomlocks on 7/7/2016.
 */
public class GameDetailReview extends RealmObject {

    @Expose
    public String apiDetailUrl;
    @Expose
    public String name;
}
