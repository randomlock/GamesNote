package com.example.randomlocks.gamesnote.helperClass.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by randomlock on 1/24/2017.
 */

public class CustomVideoView extends VideoView {

    boolean isPause = true;
    private PlayPauseListener mListener;

    public CustomVideoView(Context context) {
        super(context);
    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }



    public void setPlayPauseListener(PlayPauseListener listener) {
        mListener = listener;
    }

    @Override
    public void pause() {
        super.pause();
        isPause = true;
        if (mListener != null) {
            mListener.onPause();
        }
    }



    @Override
    public void start() {
        super.start();
        isPause = false;
        if (mListener != null) {
            mListener.onPlay();
        }
    }

    public boolean isPause() {
        return isPause;
    }

    public static interface PlayPauseListener {
        void onPlay();
        void onPause();
    }

}