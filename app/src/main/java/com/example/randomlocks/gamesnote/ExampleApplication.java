package com.example.randomlocks.gamesnote;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceManager;

import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by randomlock on 4/1/2017.
 */

//TODO realm configuration
public class ExampleApplication extends Application {

    public static final String DARK_THEME_KEY = "dark_theme_preference";
    static RealmConfiguration configuration;

    private static ExampleApplication sInstance;

    public static void setNightTheme() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(sInstance.getApplicationContext());
        setDarkTheme(preferences.getBoolean(DARK_THEME_KEY,false));

    }

    public static void setDarkTheme(boolean setDarkTheme) {
        if (setDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

    }

    public static int getRealmInstanceCount() {
        return Realm.getGlobalInstanceCount(configuration);
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

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        Realm.init(this);
        configuration = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().schemaVersion(21).build();
        Realm.setDefaultConfiguration(configuration);

        /* Initialize Leak canary */
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());

        setNightTheme();

    }


}
