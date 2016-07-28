package com.example.randomlocks.gamesnote.HelperClass;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by randomlocks on 7/29/2016.
 */
public class PagerZoomOutSlideAnimation implements ViewPager.PageTransformer {


    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;


    @Override
    public void transformPage(View page, float position) {
        onPreTransform(page, position);
        onTransform(page, position);
        onPostTransform(page, position);
    }

    private void onTransform(View view, float position) {

        if (position >= -1 || position <= 1) {
            // Modify the default slide transition to shrink the page as well
            final float height = view.getHeight();
            final float width = view.getWidth();
            final float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            final float vertMargin = height * (1 - scaleFactor) / 2;
            final float horzMargin = width * (1 - scaleFactor) / 2;

            // Center vertically
            view.setPivotY(0.5f * height);
            view.setPivotX(0.5f * width);

            if (position < 0) {
                view.setTranslationX(horzMargin - vertMargin / 2);
            } else {
                view.setTranslationX(-horzMargin + vertMargin / 2);
            }

            // Scale the page down (between MIN_SCALE and 1)
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

            // Fade the page relative to its size.
            view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
        }
    }


    protected boolean hideOffscreenPages() {
        return true;
    }

    /**
     * Indicates if the default animations of the view pager should be used.
     *
     * @return
     */
    protected boolean isPagingEnabled() {
        return false;
    }

    protected void onPreTransform(View page, float position) {
        final float width = page.getWidth();

        page.setRotationX(0);
        page.setRotationY(0);
        page.setRotation(0);
        page.setScaleX(1);
        page.setScaleY(1);
        page.setPivotX(0);
        page.setPivotY(0);
        page.setTranslationY(0);
        page.setTranslationX(isPagingEnabled() ? 0f : -width * position);

        if (hideOffscreenPages()) {
            page.setAlpha(position <= -1f || position >= 1f ? 0f : 1f);
            page.setEnabled(false);
        } else {
            page.setEnabled(true);
            page.setAlpha(1f);
        }
    }

    protected void onPostTransform(View page, float position) {
    }


}
