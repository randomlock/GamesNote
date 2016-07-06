package com.example.randomlocks.gamesnote.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;

/**
 * Created by randomlocks on 6/16/2016.
 */
public class GameDetailBottomSheet extends BottomSheetDialog {
    public GameDetailBottomSheet(@NonNull Context context) {
        super(context);
    }

    public GameDetailBottomSheet(@NonNull Context context, @StyleRes int theme) {
        super(context, theme);
    }

    protected GameDetailBottomSheet(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }



    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };









}
