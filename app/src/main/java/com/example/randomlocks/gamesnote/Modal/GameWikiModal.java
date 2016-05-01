package com.example.randomlocks.gamesnote.Modal;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by randomlocks on 4/9/2016.
 */
public class GameWikiModal {

    public String apiDetailUrl;
    public String deck;
    public String description;
    public int expectedReleaseDay;
    public int expectedReleaseMonth;
    public int expectedReleaseYear;
    public GameWikiImage image;
    public String name;
    public String originalReleaseDate;
    public List<GameWikiPlatform> platforms;
    public transient boolean isClicked = false;


}
