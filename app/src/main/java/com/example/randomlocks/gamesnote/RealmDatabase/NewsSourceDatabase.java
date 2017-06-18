package com.example.randomlocks.gamesnote.RealmDatabase;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by randomlock on 6/17/2017.
 */

public class NewsSourceDatabase extends RealmObject{

    public static String ID = "id";


    @PrimaryKey
    private long id;
    private String title;
    private String url;

    public NewsSourceDatabase(long id, String title, String url) {
        this.id = id;
        this.title = title;
        this.url = url;
    }

    public NewsSourceDatabase() {
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
