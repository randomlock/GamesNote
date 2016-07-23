package com.example.randomlocks.gamesnote.Modal.GameDetailModal;

import com.google.gson.annotations.Expose;

import io.realm.RealmObject;

/**
 * Created by randomlocks on 5/29/2016.
 */
public class GameDetailImages extends RealmObject {

    @Expose
    public String thumbUrl;
    @Expose
    public String mediumUrl;
}
