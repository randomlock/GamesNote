package com.example.randomlocks.gamesnote.RealmDatabase;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


/**
 * Created by randomlock on 1/5/2017.
 */

public class GameListDatabase extends RealmObject {

    @PrimaryKey
    public String apiDetailUrl;
    public int status;
    public String imageUrl;
    public int score;
    public String startDate;
    public String endDate;
    public String platform;



}
