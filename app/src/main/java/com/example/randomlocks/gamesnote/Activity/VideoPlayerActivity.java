package com.example.randomlocks.gamesnote.Activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.example.randomlocks.gamesnote.HelperClass.CustomView.CustomMediaControllerFullscreen;
import com.example.randomlocks.gamesnote.HelperClass.CustomView.CustomVideoView;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.R;

public class VideoPlayerActivity extends AppCompatActivity {

    ProgressBar progressBar;
    CustomVideoView videoView;
    CustomMediaControllerFullscreen mediaController;
    String class_name;
    private int seek_position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        String url = getIntent().getStringExtra(GiantBomb.API_URL);
        seek_position = getIntent().getIntExtra(GiantBomb.SEEK_POSITION,0);
        class_name = getIntent().getStringExtra("Activity");
        videoView = (CustomVideoView) findViewById(R.id.myvideoview);
        progressBar = (ProgressBar) findViewById(R.id.video_progress);
        mediaController = new CustomMediaControllerFullscreen(this, true);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setVideoPath(url);
        videoView.requestFocus();
        videoView.seekTo(seek_position);
        videoView.start();


        mediaController.setListener(new CustomMediaControllerFullscreen.OnMediaControllerInteractionListener() {
            @Override
            public void onRequestHalfScreen() {
                onBackPressed();
            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START)
                            progressBar.setVisibility(View.VISIBLE);
                        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END)
                            progressBar.setVisibility(View.GONE);
                        return false;
                    }
                });
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                progressBar.setVisibility(View.GONE);
                return false;
            }
        });




    }


    @Override
    public void onBackPressed() {
        if (class_name.equals("GameDetailActivity")) {
            Intent intent = new Intent();
            intent.putExtra(GiantBomb.SEEK_POSITION, videoView == null ? 0 : videoView.getCurrentPosition());
            setResult(RESULT_OK, intent);
            finish();
        } else {
            super.onBackPressed();
        }

    }
}
