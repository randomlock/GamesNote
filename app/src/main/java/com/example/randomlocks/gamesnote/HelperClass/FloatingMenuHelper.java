package com.example.randomlocks.gamesnote.HelperClass;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;

import com.github.clans.fab.FloatingActionMenu;

/**
 * Created by randomlocks on 7/29/2016.
 */
public class FloatingMenuHelper extends CoordinatorLayout.Behavior<FloatingActionMenu> {
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private boolean isAnimatingOut = false;

    public FloatingMenuHelper(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionMenu child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }


    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionMenu child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed,
                dyUnconsumed);

        if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
            child.hideMenuButton(true);
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            child.showMenuButton(true);
        }
    }

    @Override
    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, FloatingActionMenu child, View target, float velocityX, float velocityY, boolean consumed) {

        if (child != null) {
            FloatingActionMenu fabMenu = (FloatingActionMenu) child;
            if (velocityY > 0) {
                fabMenu.hideMenuButton(true);
            } else if (velocityY < 0) {
                fabMenu.showMenuButton(true);
            }
        }
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);

    }


    private void animateIn(FloatingActionMenu menu) {
        menu.setVisibility(View.VISIBLE);

        ViewCompat.animate(menu).scaleX(1.0F).scaleY(1.0F).alpha(1.0F).setInterpolator(INTERPOLATOR).withLayer().setListener(null).start();
    }
}