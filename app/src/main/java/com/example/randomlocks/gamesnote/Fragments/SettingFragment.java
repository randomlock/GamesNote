package com.example.randomlocks.gamesnote.Fragments;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.example.randomlocks.gamesnote.Activity.SettingsActivity;
import com.example.randomlocks.gamesnote.HelperClass.RealmBackupRestore;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.R;

/**
 * Created by randomlocks on 6/17/2016.
 */
public class SettingFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    public static final String DARK_THEME_KEY = "dark_theme_preference";
    private static final String IMAGE_QUALITY_KEY = "image_preference";
    private static final String BACKUP_KEY = "backup_preference";
    private static final String RESTORE_KEY = "restore_preference";
    private static final String BACKUP_ONLINE_KEY = "backup_online_preference";
    private static final String RESTORE_ONLINE_KEY = "restore_online_preference";
    RealmBackupRestore realmBackupRestore = null;







    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preference_settings);
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
                        Toaster.make(getContext(), "backup restored");
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case BACKUP_KEY:
                if (realmBackupRestore == null)
                    realmBackupRestore = new RealmBackupRestore(getActivity());
                realmBackupRestore.backup();
                return true;
            case RESTORE_KEY:
                setUpDialog();
                return true;

            default:
                return false;

        }
    }
}
