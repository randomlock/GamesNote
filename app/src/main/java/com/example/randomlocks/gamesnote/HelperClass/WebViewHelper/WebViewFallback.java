package com.example.randomlocks.gamesnote.HelperClass.WebViewHelper;

import android.app.Activity;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.example.randomlocks.gamesnote.Fragments.ImprovedWebViewFragment;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.R;

/**
 * Created by randomlocks on 7/3/2016.
 */
public class WebViewFallback implements CustomTabActivityHelper.CustomTabFallback {
    @Override
    public void openUri(Activity activity, Uri uri) {
        Toaster.make(activity,"hello web");

        FragmentManager fragmentManager = ((AppCompatActivity)activity).getSupportFragmentManager();

        Fragment fragment =  fragmentManager.findFragmentByTag("WebView");

        if(fragment==null) {
            fragment = ImprovedWebViewFragment.newInstance(uri.toString());

        }
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_parent_layout, fragment, "WebView");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

