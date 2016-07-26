package com.example.randomlocks.gamesnote.RealmDatabase;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class VideoListDatabase extends RealmObject {

    @PrimaryKey
    public int id;

    public VideoListDatabase() {

    }

    public VideoListDatabase(int id) {
        this.id = id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
