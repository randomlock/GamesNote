package com.example.randomlocks.gamesnote.Activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.randomlocks.gamesnote.HelperClass.CustomView.CustomMediaControllerFullscreen;
import com.example.randomlocks.gamesnote.HelperClass.CustomView.CustomVideoView;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.Modal.GamesVideoModal.GamesVideoModal;
import com.example.randomlocks.gamesnote.R;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;
import com.squareup.picasso.Picasso;

public class VideoPlayerActivity extends AppCompatActivity {

    ProgressBar progressBar;
    ImageView mCoverArt;
    CustomVideoView videoView;
    CustomMediaControllerFullscreen mediaController;

    String class_name;
    String url;
    private int seek_position;
    private GamesVideoModal mSelectedMedia;
    private PlaybackState mPlaybackState;
    private PlaybackLocation mLocation;
    private CastContext mCastContext;
    private CastSession mCastSession;
    private SessionManagerListener<CastSession> mSessionManagerListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        setUpVideoView();
        setVideoViewListener();
        setupCastListener();
        mCastContext = CastContext.getSharedInstance(this);
        mCastSession = mCastContext.getSessionManager().getCurrentCastSession();

        if (mCastSession != null && mCastSession.isConnected()) {
            updatePlaybackLocation(PlaybackLocation.REMOTE);
        } else {
            updatePlaybackLocation(PlaybackLocation.LOCAL);
        }
        mPlaybackState = PlaybackState.IDLE;


    }

    private void setUpVideoView() {

        url = getIntent().getStringExtra(GiantBomb.API_URL);
        seek_position = getIntent().getIntExtra(GiantBomb.SEEK_POSITION,0);
        mSelectedMedia = getIntent().getParcelableExtra(GiantBomb.MODAL);
        class_name = getIntent().getStringExtra("Activity");
        mCoverArt = (ImageView) findViewById(R.id.coverArtView);
        videoView = (CustomVideoView) findViewById(R.id.myvideoview);
        progressBar = (ProgressBar) findViewById(R.id.video_progress);
        progressBar.setVisibility(View.VISIBLE);
        mediaController = new CustomMediaControllerFullscreen(this, true);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setVideoPath(url);
        videoView.requestFocus();
        videoView.seekTo(seek_position);

    }

    private void setVideoViewListener() {


        videoView.setPlayPauseListener(new CustomVideoView.PlayPauseListener() {
            @Override
            public void onPlay() {

                progressBar.setVisibility(View.GONE);
                switch (mLocation) {
                    case LOCAL:
                        videoView.start();
                        mPlaybackState = PlaybackState.PLAYING;
                        updatePlaybackLocation(PlaybackLocation.LOCAL);
                        break;
                    case REMOTE:
                        finish();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPause() {

                mPlaybackState = PlaybackState.PAUSED;


            }
        });


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
                            if (progressBar.getVisibility() == View.GONE) {
                                progressBar.setVisibility(View.VISIBLE);
                            }
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

    /*@Override
    protected void onResume() {
        mCastContext.getSessionManager().addSessionManagerListener(
                mSessionManagerListener, CastSession.class);
        if (mCastSession != null && mCastSession.isConnected()) {
            updatePlaybackLocation(PlaybackLocation.REMOTE);
        } else {
            updatePlaybackLocation(PlaybackLocation.LOCAL);
        }
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLocation == PlaybackLocation.LOCAL) {

            // since we are playing locally, we need to stop the playback of
            // video (if user is not watching, pause it!)
            videoView.pause();
            mPlaybackState = PlaybackState.PAUSED;
        }
        mCastContext.getSessionManager().removeSessionManagerListener(
                mSessionManagerListener, CastSession.class);
    }*/

    private void setupCastListener() {
        mSessionManagerListener = new SessionManagerListener<CastSession>() {

            @Override
            public void onSessionEnded(CastSession session, int error) {
                onApplicationDisconnected();
            }

            @Override
            public void onSessionResumed(CastSession session, boolean wasSuspended) {
                onApplicationConnected(session);
            }

            @Override
            public void onSessionResumeFailed(CastSession session, int error) {
                onApplicationDisconnected();
            }

            @Override
            public void onSessionStarted(CastSession session, String sessionId) {
                onApplicationConnected(session);
            }

            @Override
            public void onSessionStartFailed(CastSession session, int error) {
                onApplicationDisconnected();
            }

            @Override
            public void onSessionStarting(CastSession session) {
            }

            @Override
            public void onSessionEnding(CastSession session) {
            }

            @Override
            public void onSessionResuming(CastSession session, String sessionId) {
            }

            @Override
            public void onSessionSuspended(CastSession session, int reason) {
            }

            private void onApplicationConnected(CastSession castSession) {
                mCastSession = castSession;
                if (null != mSelectedMedia) {

                    if (mPlaybackState == PlaybackState.PLAYING) {
                        videoView.pause();
                        loadRemoteMedia(videoView.getCurrentPosition(), true);
                        return;
                    } else {
                        mPlaybackState = PlaybackState.IDLE;
                        updatePlaybackLocation(PlaybackLocation.REMOTE);
                    }
                }
            }

            private void onApplicationDisconnected() {
                updatePlaybackLocation(PlaybackLocation.LOCAL);
                mPlaybackState = PlaybackState.IDLE;
                mLocation = PlaybackLocation.LOCAL;
            }
        };

    }

    private void updatePlaybackLocation(PlaybackLocation location) {
        mLocation = location;
        if (location == PlaybackLocation.LOCAL) {
            if (mPlaybackState == PlaybackState.PLAYING
                    || mPlaybackState == PlaybackState.BUFFERING) {
                setCoverArtStatus(null);
            } else {
                setCoverArtStatus(mSelectedMedia.image.mediumUrl);
            }
        } else {
            setCoverArtStatus(mSelectedMedia.image.mediumUrl);
        }
    }

    private void togglePlayback() {
        switch (mPlaybackState) {
            case PAUSED:
                switch (mLocation) {
                    case LOCAL:
                        videoView.start();
                        mPlaybackState = PlaybackState.PLAYING;
                        updatePlaybackLocation(PlaybackLocation.LOCAL);
                        break;
                    case REMOTE:
                        finish();
                        break;
                    default:
                        break;
                }
                break;

            case PLAYING:
                mPlaybackState = PlaybackState.PAUSED;
                videoView.pause();
                break;

            case IDLE:
                switch (mLocation) {
                    case LOCAL:
                        videoView.setVideoPath(url);
                        videoView.seekTo(0);
                        videoView.start();
                        mPlaybackState = PlaybackState.PLAYING;
                        updatePlaybackLocation(PlaybackLocation.LOCAL);
                        break;
                    case REMOTE:
                        if (mCastSession != null && mCastSession.isConnected()) {
                            loadRemoteMedia(videoView.getCurrentPosition(), true);
                        }
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    private void setCoverArtStatus(String url) {
        if (url != null) {
            Picasso.with(this).load(url).into(mCoverArt);
            mCoverArt.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.INVISIBLE);
        } else {
            mCoverArt.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
        }
    }

    private void loadRemoteMedia(int position, boolean autoPlay) {
        if (mCastSession == null) {
            return;
        }
        final RemoteMediaClient remoteMediaClient = mCastSession.getRemoteMediaClient();
        if (remoteMediaClient == null) {
            return;
        }
        remoteMediaClient.addListener(new RemoteMediaClient.Listener() {
            @Override
            public void onStatusUpdated() {
                Intent intent = new Intent(VideoPlayerActivity.this, ExpandedControlsActivity.class);
                startActivity(intent);
                remoteMediaClient.removeListener(this);
            }

            @Override
            public void onMetadataUpdated() {
            }

            @Override
            public void onQueueStatusUpdated() {
            }

            @Override
            public void onPreloadStatusUpdated() {
            }

            @Override
            public void onSendingRemoteMediaRequest() {
            }

            @Override
            public void onAdBreakStatusUpdated() {

            }
        });
        remoteMediaClient.load(buildMediaInfo(), autoPlay, position);
    }

    private MediaInfo buildMediaInfo() {
        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);

        movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, mSelectedMedia.deck);
        movieMetadata.putString(MediaMetadata.KEY_TITLE, mSelectedMedia.name);
        movieMetadata.addImage(new WebImage(Uri.parse(mSelectedMedia.image.mediumUrl)));

        return new MediaInfo.Builder(mSelectedMedia.highUrl)
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setContentType("videos/mp4")
                .setMetadata(movieMetadata)
                .setStreamDuration(mSelectedMedia.lengthSeconds * 1000)
                .build();
    }

    @Override
    public void onBackPressed() {
            Intent intent = new Intent();
            intent.putExtra(GiantBomb.SEEK_POSITION, videoView == null ? 0 : videoView.getCurrentPosition());
            setResult(RESULT_OK, intent);
            finish();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        if (item.getItemId() == android.R.id.home) {
            ActivityCompat.finishAfterTransition(this);
        }
        return true;
    }


    public enum PlaybackLocation {
        LOCAL,
        REMOTE
    }

    /**
     * List of various states that we can be in
     */
    public enum PlaybackState {
        PLAYING, PAUSED, BUFFERING, IDLE
    }


}
