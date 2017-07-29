package com.example.randomlocks.gamesnote.modals.ChromeCastVideoModel;

import com.example.randomlocks.gamesnote.modals.GamesVideoModal.GamesVideoModal;

/**
 * Created by randomlock on 7/9/2017.
 */

public class MediaItem {

    public static final String KEY_TITLE = "title";
    public static final String KEY_LOW_URL = "low_url";
    public static final String KEY_HIGH_URL = "high_url";
    public static final String KEY_DURATION = "duration";
    private String title;
    private String lowUrl;
    private String highUrl;
    private int duration;
    private String image;


    public MediaItem() {
    }

    public MediaItem(GamesVideoModal modal) {
        title = modal.name;
        lowUrl = modal.lowUrl;
        highUrl = modal.highUrl;
        duration = modal.lengthSeconds;
        image = modal.image.mediumUrl;
    }


}
