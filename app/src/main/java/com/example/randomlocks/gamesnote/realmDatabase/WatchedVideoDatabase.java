package com.example.randomlocks.gamesnote.realmDatabase;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class WatchedVideoDatabase extends RealmObject {

    @PrimaryKey
    public int id;

    public int time_elapsed;

    public WatchedVideoDatabase() {

    }

    public WatchedVideoDatabase(int id) {
        this.id = id;
    }

    public WatchedVideoDatabase(int id, int time_elapsed) {
        this.id = id;
        this.time_elapsed = time_elapsed;
    }

    public void setId(int id) {
        this.id = id;
    }
}
