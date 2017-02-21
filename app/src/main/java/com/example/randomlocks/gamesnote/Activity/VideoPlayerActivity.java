package com.example.randomlocks.gamesnote.Activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.example.randomlocks.gamesnote.HelperClass.CustomView.AVLoadingIndicatorView;
import com.example.randomlocks.gamesnote.HelperClass.CustomView.CustomMediaController;
import com.example.randomlocks.gamesnote.HelperClass.CustomView.CustomMediaControllerFullscreen;
import com.example.randomlocks.gamesnote.HelperClass.CustomView.CustomVideoView;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.R;

public class VideoPlayerActivity extends AppCompatActivity {

    private String url;
    private int seek_position;
    ProgressBar progressBar;
    CustomVideoView videoView;
    CustomMediaControllerFullscreen mediaController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
       url =  getIntent().getStringExtra(GiantBomb.API_URL);
        seek_position = getIntent().getIntExtra(GiantBomb.SEEK_POSITION,0);

        videoView = (CustomVideoView) findViewById(R.id.myvideoview);
        progressBar = (ProgressBar) findViewById(R.id.video_progress);
        mediaController = new CustomMediaControllerFullscreen(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setVideoPath(url);
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                progressBar.setVisibility(View.GONE);
                videoView.seekTo(seek_position);
                videoView.start();
            }
        });

        mediaController.setListener(new CustomMediaControllerFullscreen.OnMediaControllerInteractionListener() {
            @Override
            public void onRequestHalfScreen() {
                onBackPressed();
            }
        });

    }


    @Override
    public void onBackPressed() {
      //  super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra(GiantBomb.SEEK_POSITION,videoView==null?0:videoView.getCurrentPosition());
        setResult(RESULT_OK, intent);
        finish();
    }
}
