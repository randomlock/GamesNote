package com.example.randomlocks.gamesnote.RealmDatabase;

import com.example.randomlocks.gamesnote.Modal.GameWikiPlatform;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;


/**
 * Created by randomlock on 1/5/2017.
 */

public class GameListDatabase extends RealmObject {

    public static final String GAME_ID = "game_id";

    @PrimaryKey
    private int game_id; //required
    private String name; //required
    private String apiDetailUrl;
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

    @Ignore
    private boolean isAnimated;


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

    public GameListDatabase(int game_id,String apiDetailUrl, String name, String imageUrl, int status, RealmList<GameWikiPlatform> platform_list) {
        this.game_id = game_id;
        this.apiDetailUrl = apiDetailUrl;
        this.name = name;
        this.imageUrl = imageUrl;
        this.status = status;
        this.platform_list = platform_list;
        date_added = new Date();
        last_updated = new Date();

    }



    public GameListDatabase(int game_id,String apiDetailUrl, String name, String imageUrl, RealmList<GameWikiPlatform> platform_list) {
        this.game_id = game_id;
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

    public GameListDatabase(int game_id,String apiDetailUrl, String name, String imageUrl, int status, int score, String startDate, String endDate, String platform, RealmList<GameWikiPlatform> platform_list) {
        this.game_id = game_id;
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

    public RealmList<GameWikiPlatform> getPlatform_list() {
        return platform_list;
    }

    public void setPlatform_list(RealmList<GameWikiPlatform> platform_list) {
        this.platform_list = platform_list;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getPlatform() { return platform; }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getGameplay_hours() {
        return gameplay_hours;
    }

    public void setGameplay_hours(String gameplay_hours) {
        this.gameplay_hours = gameplay_hours;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String difficulty) {
        this.price = difficulty;
    }

    public Date getDate_added() {
        return date_added;
    }

    public void setDate_added(Date date_added) {
        this.date_added = date_added;
    }

    public Date getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(Date last_updated) {
        this.last_updated = last_updated;
    }

    public int getGame_id() {
        return game_id;
    }

    public void setGame_id(int game_id) {
        this.game_id = game_id;
    }

    public String getApiDetailUrl() {
        return apiDetailUrl;
    }

    public void setApiDetailUrl(String apiDetailUrl) {
        this.apiDetailUrl = apiDetailUrl;
    }

    public boolean isAnimated() {
        return isAnimated;
    }

    public void setAnimated(boolean animated) {
        isAnimated = animated;
    }
}
