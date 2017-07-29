package com.example.randomlocks.gamesnote.modals.GameDetailModal;

import com.example.randomlocks.gamesnote.modals.GamesVideoModal.GameVideoImage;
import com.google.gson.annotations.Expose;

/**
 * Created by randomlock on 1/24/2017.
 */

public class GameVideoModalMinimal  {

    @Expose
    public String highUrl;
    @Expose
    public String lowUrl;
    @Expose
    public String youtubeId;
    @Expose
    public String siteDetailUrl;
    @Expose
    public int lengthSeconds;
    @Expose
    public String name;
    @Expose
    public GameVideoImage image;




}
