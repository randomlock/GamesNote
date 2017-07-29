package com.example.randomlocks.gamesnote.chromecast;

import android.content.Context;
import android.text.format.DateUtils;

import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.activity.ExpandedControlsActivity;
import com.google.android.gms.cast.framework.CastOptions;
import com.google.android.gms.cast.framework.OptionsProvider;
import com.google.android.gms.cast.framework.SessionProvider;
import com.google.android.gms.cast.framework.media.CastMediaOptions;
import com.google.android.gms.cast.framework.media.MediaIntentReceiver;
import com.google.android.gms.cast.framework.media.NotificationOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by randomlock on 6/20/2017.
 */

public class CastOptionsProvider implements OptionsProvider {

    @Override
    public CastOptions getCastOptions(Context context) {

        List<String> buttonActions = new ArrayList<>();
        buttonActions.add(MediaIntentReceiver.ACTION_REWIND);
        buttonActions.add(MediaIntentReceiver.ACTION_TOGGLE_PLAYBACK);
        buttonActions.add(MediaIntentReceiver.ACTION_FORWARD);
        buttonActions.add(MediaIntentReceiver.ACTION_STOP_CASTING);
        int[] compatButtonActionsIndicies = new int[]{1, 3};

        NotificationOptions notificationOptions = new NotificationOptions.Builder()
                .setTargetActivityClassName(ExpandedControlsActivity.class.getName())
                .setActions(buttonActions, compatButtonActionsIndicies)
                .setSkipStepMs(30 * DateUtils.SECOND_IN_MILLIS)
                .build();

        CastMediaOptions mediaOptions = new CastMediaOptions.Builder()
                .setNotificationOptions(notificationOptions)
                .setExpandedControllerActivityClassName(ExpandedControlsActivity.class.getName())
                .build();

        return new CastOptions.Builder()
                .setReceiverApplicationId(context.getString(R.string.app_id))
                .setCastMediaOptions(mediaOptions)
                .build();
    }

    @Override
    public List<SessionProvider> getAdditionalSessionProviders(Context context) {
        return null;
    }
}