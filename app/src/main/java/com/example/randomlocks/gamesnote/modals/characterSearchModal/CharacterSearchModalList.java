package com.example.randomlocks.gamesnote.modals.characterSearchModal;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by randomlocks on 7/11/2016.
 */
public class CharacterSearchModalList {

    @Expose
    public int numberOfTotalResults;

    @Expose
    public List<CharacterSearchModal> results;
}
