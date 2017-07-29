package com.example.randomlocks.gamesnote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.fragment.SettingFragment;

/**
 * Created by randomlock on 6/14/2017.
 */

public class SettingsActivity extends AppCompatActivity {

    Fragment fragment;
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Settings");
        fragment = getSupportFragmentManager().findFragmentByTag("GamesSetting");
        if (fragment == null) {
            fragment = new SettingFragment();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_parent_layout,fragment,"GamesSetting").commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }

    public void setDarkTheme(boolean setDarkTheme) {
        if (setDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        Intent intent = getIntent();
        finish();

        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);


    }


    public void restartMainActivity(boolean aBoolean) {
        setDarkTheme(aBoolean);
    }
}
