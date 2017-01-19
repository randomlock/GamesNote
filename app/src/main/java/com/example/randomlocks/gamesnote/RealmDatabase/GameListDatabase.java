package com.example.randomlocks.gamesnote.RealmDatabase;

import com.example.randomlocks.gamesnote.Modal.GameWikiPlatform;

import java.util.ArrayList;
import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;


/**
 * Created by randomlock on 1/5/2017.
 */

public class GameListDatabase extends RealmObject {

    @PrimaryKey
    private String apiDetailUrl; //required
    private String name; //required
    private String imageUrl;
    @Index
    private int status;
    private int score;
    private String startDate;
    private String endDate;
    private String platform;
    private String gameplay_hours;
    private String medium;
    private String price;
    private RealmList<GameWikiPlatform> platform_list;
    private Date date_added;
    private Date last_updated;


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

    public GameListDatabase(){

    }

    public GameListDatabase(String apiDetailUrl, String name, String imageUrl, int status, RealmList<GameWikiPlatform> platform_list) {
        this.apiDetailUrl = apiDetailUrl;
        this.name = name;
        this.imageUrl = imageUrl;
        this.status = status;
        this.platform_list = platform_list;
        date_added = new Date();
        last_updated = new Date();

    }

    public GameListDatabase(String apiDetailUrl, String name, String imageUrl, RealmList<GameWikiPlatform> platform_list) {
        this.apiDetailUrl = apiDetailUrl;
        this.name = name;
        this.imageUrl = imageUrl;
        this.platform_list = platform_list;
        score = 0;
        startDate = "-";
        endDate = "-";
        platform = "-";
        gameplay_hours = "-";
        medium = "-";
        price = "-";
        date_added = new Date();
        last_updated = new Date();
    }

    public GameListDatabase(String apiDetailUrl, String name, String imageUrl, int status, int score, String startDate, String endDate, String platform, RealmList<GameWikiPlatform> platform_list) {
        this.apiDetailUrl = apiDetailUrl;
        this.name = name;
        this.imageUrl = imageUrl;
        this.platform_list = platform_list;
        this.status = status;
        this.score = score;
        this.startDate = startDate;
        this.endDate = endDate;
        this.platform = platform;
        gameplay_hours = "-";
        medium = "-";
        price = "-";
        date_added = new Date();
        last_updated = new Date();
    }


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

    public void setPlatform_list(RealmList<GameWikiPlatform> platform_list) {
        this.platform_list = platform_list;
    }

    public RealmList<GameWikiPlatform> getPlatform_list() {
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


    public Date getDate_added() {
        return date_added;
    }

    public Date getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(Date last_updated) {
        this.last_updated = last_updated;
    }
}
