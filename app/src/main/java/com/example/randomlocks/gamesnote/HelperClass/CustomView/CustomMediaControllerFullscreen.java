package com.example.randomlocks.gamesnote.HelperClass.CustomView;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController;

import com.example.randomlocks.gamesnote.R;

/**
 * Created by randomlock on 1/26/2017.
 */

public class CustomMediaControllerFullscreen extends MediaController {

    Context context;
    private OnMediaControllerInteractionListener mListener;




    public CustomMediaControllerFullscreen(Context context) {
        super(context);
        this.context = context;
    }

    public CustomMediaControllerFullscreen(Context context, boolean useFastForward) {
        super(context, useFastForward);
        this.context = context;
    }

    public void setListener(OnMediaControllerInteractionListener listener) {
        mListener = listener;
    }

    @Override
    public void setAnchorView(View view) {
        super.setAnchorView(view);

        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        frameParams.gravity = Gravity.END|Gravity.TOP;


        ImageButton fullscreenButton = new ImageButton(context);
        fullscreenButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_fullscreen_exit_white_36dp));
        fullscreenButton.setBackground(null);
        fullscreenButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if(mListener != null) {
                    mListener.onRequestHalfScreen();
                }
            }
        });




        addView(fullscreenButton, frameParams);
    }

    public static interface OnMediaControllerInteractionListener {
        void onRequestHalfScreen();
    }


}
