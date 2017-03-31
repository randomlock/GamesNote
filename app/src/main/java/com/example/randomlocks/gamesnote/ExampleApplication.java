package com.example.randomlocks.gamesnote;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import io.realm.Realm;

/**
 * Created by randomlock on 4/1/2017.
 */

//TODO realm configuration
public class ExampleApplication extends Application {

    private static ExampleApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        Realm.init(this);
    }

    public static ExampleApplication getInstance() {
        return sInstance;
    }

    public static boolean hasNetwork() {
        ConnectivityManager cm =
                (ConnectivityManager) sInstance.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
