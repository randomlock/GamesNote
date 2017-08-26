package com.example.randomlocks.gamesnote.interfaces;

import com.example.randomlocks.gamesnote.modals.gamesVideoModal.GamesVideoModal;

/**
 * Created by randomlock on 3/14/2017.
 */

public interface VideoPlayInterface {

    void onVideoClick(GamesVideoModal modal, String url, int id, int elapsed_time, int request_code);

    void onYoutubeVideoClick(String url, int id);

    void onExternalPlayerVideoClick(String url, int id);

}
