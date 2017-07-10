package com.example.randomlocks.gamesnote.Interface;

import com.example.randomlocks.gamesnote.Modal.GamesVideoModal.GamesVideoModal;

/**
 * Created by randomlock on 3/14/2017.
 */

public interface VideoPlayInterface {

    void onVideoClick(GamesVideoModal modal, String url, int id, int elapsed_time, int request_code);

    void onYoutubeVideoClick(String url, int id);

    void onExternalPlayerVideoClick(String url, int id);

}
