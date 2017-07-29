package com.example.randomlocks.gamesnote.helperClass.CustomView;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController;

import com.example.randomlocks.gamesnote.R;

/**
 * Created by randomlock on 1/24/2017.
 */

public class CustomMediaController  extends MediaController {

    private OnMediaControllerInteractionListener mListener;
    private Context context;


    public CustomMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public CustomMediaController(Context context, boolean useFastForward) {
        super(context, useFastForward);
        this.context = context;

    }

    public CustomMediaController(Context context) {
        super(context);
        this.context = context;

    }

    public void setListener(OnMediaControllerInteractionListener listener) {
        mListener = listener;
    }

    @Override
    public void setAnchorView(View view) {
        super.setAnchorView(view);

        FrameLayout.LayoutParams frameParams1 = new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        frameParams1.gravity = Gravity.END|Gravity.TOP;

        FrameLayout.LayoutParams frameParams2 = new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        frameParams2.gravity = Gravity.START|Gravity.TOP;

        ImageButton fullscreenButton = new ImageButton(context);
        fullscreenButton.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_fullscreen_white_36dp));
        fullscreenButton.setBackground(null);
        fullscreenButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if(mListener != null) {
                    mListener.onRequestFullScreen();
                }
            }
        });
        ImageButton dimmerButton = new ImageButton(context);
        dimmerButton.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_lightbulb_outline_white_36dp));
        dimmerButton.setBackground(null);
        dimmerButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if(mListener != null) {
                    mListener.onRequestDimmerView();
                }
            }
        });




        addView(fullscreenButton, frameParams1);
        addView(dimmerButton,frameParams2);
    }

    public static interface OnMediaControllerInteractionListener {
        void onRequestFullScreen();

        void onRequestDimmerView();
    }


}
