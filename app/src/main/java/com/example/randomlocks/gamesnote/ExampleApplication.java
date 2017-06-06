package com.example.randomlocks.gamesnote;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

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

    private static ExampleApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        Realm.init(this);
        RealmConfiguration configuration = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().schemaVersion(13).build();
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
