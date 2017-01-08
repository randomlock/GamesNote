package com.example.randomlocks.gamesnote.RealmDatabase;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


/**
 * Created by randomlock on 1/5/2017.
 */

public class GameListDatabase extends RealmObject {

    @PrimaryKey
    private String apiDetailUrl; //required
    private String name; //required
    private String imageUrl;
    private int status;
    private int score;
    private String startDate;
    private String endDate;
    private String platform;
    private String gameplay_hours;
    private String medium;
    private String price;
    private RealmList<RealmString> platform_list;


   /* public static class Builder{

        private String apiDetailUrl;
        private String name;
        private int status;
        private String imageUrl;


        public Builder(String apiDetailUrl, String name, int status, String imageUrl) {
            this.apiDetailUrl = apiDetailUrl;
            this.name = name;
            this.status = status;
            this.imageUrl = imageUrl;
        }

        public Builder scores(int val){
            score = val;
        }

    }*/


    public void setApiDetailUrl(String apiDetailUrl) {
        this.apiDetailUrl = apiDetailUrl;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGameplay_hours(String gameplay_hours) {this.gameplay_hours = gameplay_hours;}

    public void setMedium(String medium) {this.medium = medium;}

    public void setPrice(String difficulty) {this.price = difficulty;}

    public void setPlatform_list(RealmList<RealmString> platform_list) {
        this.platform_list = platform_list;
    }

    public RealmList<RealmString> getPlatform_list() {
        return platform_list;
    }

    public String getApiDetailUrl() {
        return apiDetailUrl;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getStatus() {
        return status;
    }

    public int getScore() {
        return score;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getPlatform() { return platform; }

    public String getGameplay_hours() {
        return gameplay_hours;
    }

    public String getMedium() {
        return medium;
    }

    public String getPrice() {
        return price;
    }


}
