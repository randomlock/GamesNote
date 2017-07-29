package com.example.randomlocks.gamesnote.realmDatabase;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by randomlock on 4/7/2017.
 */

public class RealmInteger extends RealmObject {

    @PrimaryKey
    private int game_id;

    public RealmInteger() {
    }

    public RealmInteger(int game_id) {
        this.game_id = game_id;
    }

    public int getGame_id() {
        return game_id;
    }

    public void setGame_id(int game_id) {
        this.game_id = game_id;
    }


}
