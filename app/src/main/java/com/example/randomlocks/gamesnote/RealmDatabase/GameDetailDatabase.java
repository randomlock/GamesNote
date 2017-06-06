package com.example.randomlocks.gamesnote.RealmDatabase;

import com.github.mikephil.charting.data.realm.base.RealmBaseDataSet;
import com.github.mikephil.charting.data.realm.implementation.RealmBarDataSet;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.Index;

/**
 * Created by randomlock on 4/5/2017.
 */

public class GameDetailDatabase extends RealmObject {

    public static final int DEVELOPER_TYPE = 1;
    public static final int PUBLISHER_TYPE = 2;
    public static final int THEME_TYPE = 3;
    public static final int FRANCHISE_TYPE = 4;
    public static final int GENRE_TYPE = 5;
    public static final int SIMILAR_GAME_TYPE = 6;

    public static final String TYPE = "type";
    public static final String NAME = "name";
    public static final String COUNT = "count";
    public static final String AUTO_INCREMENT_X_VALUE = "x_value";


    @Index
    private int type;
    @Index
    private String name;

    private RealmList<RealmInteger> games_id;

    private int x_value;

    private int count;



    public GameDetailDatabase() {
    }

    public GameDetailDatabase(int type, String name,int count, int x_value, RealmList<RealmInteger> games_id) {
        this.type = type;
        this.name = name;
        this.count = count;
        this.x_value = x_value;
        this.games_id = games_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RealmList<RealmInteger> getGames_id() {
        return games_id;
    }

    public void setGames_id(RealmList<RealmInteger> games_id) {
        this.games_id = games_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getX_value() {
        return x_value;
    }

    public void setX_value(int x_value) {
        this.x_value = x_value;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
