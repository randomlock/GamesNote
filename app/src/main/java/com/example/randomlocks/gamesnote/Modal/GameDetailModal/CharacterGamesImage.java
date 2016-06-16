package com.example.randomlocks.gamesnote.Modal.GameDetailModal;

import com.google.gson.annotations.Expose;

import io.realm.RealmObject;

/**
 * Created by randomlocks on 6/11/2016.
 */
public class CharacterGamesImage extends RealmObject {

    @Expose
    public String imageUrl;
    @Expose
    public String name;


    public CharacterGamesImage(String imageUrl, String name) {
        this.imageUrl = imageUrl;
        this.name = name;
    }

    public CharacterGamesImage(){

    }
}
