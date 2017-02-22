package com.example.randomlocks.gamesnote.HelperClass.CustomView;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;

/**
 * Created by randomlock on 2/21/2017.
 */

public class ConsistentGridLayoutManager extends GridLayoutManager {
    public ConsistentGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ConsistentGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public ConsistentGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }


}
