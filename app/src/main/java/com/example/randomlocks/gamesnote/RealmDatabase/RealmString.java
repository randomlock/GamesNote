package com.example.randomlocks.gamesnote.RealmDatabase;

import io.realm.RealmObject;

/**
 * Created by randomlock on 1/7/2017.
 */

public class RealmString extends RealmObject {
    public String name;
    public String abbreviation;

    public RealmString(){

    }


    public RealmString(String name, String abbreviation) {
        this.name = name;
        this.abbreviation = abbreviation;
    }
}