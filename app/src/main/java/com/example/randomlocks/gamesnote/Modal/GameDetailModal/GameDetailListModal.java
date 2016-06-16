package com.example.randomlocks.gamesnote.Modal.GameDetailModal;

import com.example.randomlocks.gamesnote.Modal.GameWikiModal;
import com.google.gson.annotations.Expose;

import java.util.List;

import io.realm.RealmObject;

/**
 * Created by randomlocks on 5/29/2016.
 */
public class GameDetailListModal extends RealmObject {

    @Expose
    public GameDetailModal results;

}
