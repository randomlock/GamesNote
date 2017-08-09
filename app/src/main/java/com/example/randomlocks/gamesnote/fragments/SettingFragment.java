package com.example.randomlocks.gamesnote.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.activity.SettingsActivity;
import com.example.randomlocks.gamesnote.helperClass.RealmBackupRestore;
import com.example.randomlocks.gamesnote.helperClass.Toaster;
import com.example.randomlocks.gamesnote.helperClass.WebViewHelper.CustomTabActivityHelper;
import com.example.randomlocks.gamesnote.helperClass.WebViewHelper.WebViewFallback;

/**
 * Created by randomlocks on 6/17/2016.
 */
public class SettingFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    public static final String DARK_THEME_KEY = "dark_theme_preference";
    public static final String API_KEY = "api_key_preference";
    private static final String IMAGE_QUALITY_KEY = "image_preference";
    private static final String BACKUP_KEY = "backup_preference";
    private static final String RESTORE_KEY = "restore_preference";
    private static final String BACKUP_ONLINE_KEY = "backup_online_preference";
    private static final String RESTORE_ONLINE_KEY = "restore_online_preference";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    RealmBackupRestore realmBackupRestore = null;
    String operation;
    boolean is_browser_open;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preference_settings);
        findPreference(API_KEY).setOnPreferenceClickListener(this);
        findPreference(BACKUP_KEY).setOnPreferenceClickListener(this);
        findPreference(RESTORE_KEY).setOnPreferenceClickListener(this);
        findPreference(BACKUP_ONLINE_KEY).setOnPreferenceClickListener(this);
        findPreference(RESTORE_ONLINE_KEY).setOnPreferenceClickListener(this);
    }



    @Override
    public void onResume() {
        super.onResume();
        //unregister the preferenceChange listener
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        if (is_browser_open) {
            is_browser_open = false;
            getApikey();
        }
    }


    public void onPause() {
        super.onPause();
        //unregister the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case DARK_THEME_KEY:
                ((SettingsActivity) getActivity()).restartMainActivity(sharedPreferences.getBoolean(key, false));
                break;

        }

    }

    private void setUpDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if (realmBackupRestore == null)
                            realmBackupRestore = new RealmBackupRestore(getActivity());
                        realmBackupRestore.restore();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.black_white));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.primary));

            }
        });
        dialog.show();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {

            case API_KEY:
                openDialog();
                return true;

            case BACKUP_KEY:
                operation = BACKUP_KEY;
                checkStoragePermissions();

                return true;
            case RESTORE_KEY:
                operation = RESTORE_KEY;
                checkStoragePermissions();

                return true;

            default:
                return false;

        }
    }

    private void openDialog() {
        Toaster.make(getContext(), "hello");
        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setMessage("A browser will open where you need to sign up and login to " +
                        "giantbomb account. After successful login, close" +
                        " the browser. If you are already logged in skip this")
                .setPositiveButton("Open browser", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        runBrowser();
                    }
                })
                .setNegativeButton("Skip", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getApikey();
                    }
                })
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.black_white));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.primary));

            }
        });
        dialog.show();

    }

    private void getApikey() {
        Toaster.make(getContext(), "getApiKey");
    }

    private void runBrowser() {
        is_browser_open = true;
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().setShowTitle(true).addDefaultShareMenuItem().build();
        CustomTabActivityHelper.openCustomTab(
                getActivity(), customTabsIntent, Uri.parse("https://auth.giantbomb.com/signup/"), new WebViewFallback());
    }

    private void checkStoragePermissions() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {
            if (operation.equals(BACKUP_KEY))
                createBackup();
            else
                restoreBackup();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (operation.equals(BACKUP_KEY)) {
                        createBackup();
                    } else {
                        restoreBackup();

                    }

                } else {

                    Toaster.make(getContext(), "Need read/write permission");
                }

        }
    }

    private void restoreBackup() {
        setUpDialog();
    }

    private void createBackup() {
        if (realmBackupRestore == null)
            realmBackupRestore = new RealmBackupRestore(getActivity());
        realmBackupRestore.backup();
    }
}
