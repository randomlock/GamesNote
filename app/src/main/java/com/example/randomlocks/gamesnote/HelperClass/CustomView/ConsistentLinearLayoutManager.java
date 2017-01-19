package com.example.randomlocks.gamesnote.HelperClass.CustomView;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

/**
 * Created by randomlocks on 6/16/2016.
 */
public class ConsistentLinearLayoutManager extends LinearLayoutManager {


    public ConsistentLinearLayoutManager(Context context) {
        super(context);
    }

    public ConsistentLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public ConsistentLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }


}
