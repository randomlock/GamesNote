package com.example.randomlocks.gamesnote.RealmDatabase;

import io.realm.RealmObject;

/**
 * Created by randomlock on 1/7/2017.
 */

public class RealmString extends RealmObject {
    private String name;
    private String abbreviation;

    public RealmString(){

    }


    public RealmString(String name, String abbreviation) {
        this.name = name;
        this.abbreviation = abbreviation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }
}