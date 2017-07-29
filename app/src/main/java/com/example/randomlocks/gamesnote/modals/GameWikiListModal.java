package com.example.randomlocks.gamesnote.modals;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by randomlocks on 4/25/2016.
 */
public class GameWikiListModal {

    @Expose
    public int numberOfTotalResults;

    @Expose
    public List<GameWikiModal> results;

}
