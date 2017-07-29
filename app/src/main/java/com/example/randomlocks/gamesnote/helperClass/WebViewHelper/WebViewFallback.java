package com.example.randomlocks.gamesnote.helperClass.WebViewHelper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.example.randomlocks.gamesnote.activity.ImprovedWebViewActivity;
import com.example.randomlocks.gamesnote.helperClass.GiantBomb;

/**
 * Created by randomlocks on 7/3/2016.
 */
public class WebViewFallback implements CustomTabActivityHelper.CustomTabFallback {
    @Override
    public void openUri(Activity activity, Uri uri) {

        Intent intent = new Intent(activity, ImprovedWebViewActivity.class);
        intent.putExtra(GiantBomb.BASE_URL, uri.toString());
        activity.startActivity(intent);
    }


}



