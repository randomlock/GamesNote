package com.example.randomlocks.gamesnote.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.randomlocks.gamesnote.Activity.MainActivity;
import com.example.randomlocks.gamesnote.Activity.SettingsActivity;
import com.example.randomlocks.gamesnote.R;

/**
 * Created by randomlocks on 6/17/2016.
 */
public class SettingFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String DARK_THEME_KEY = "dark_theme_preference";
    private static final String IMAGE_QUALITY_KEY = "image_preference";






    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preference_settings);

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
        if (key.equals(DARK_THEME_KEY)) {
            ((SettingsActivity) getActivity()).restartMainActivity(sharedPreferences.getBoolean(key, false));
        }

    }
}
