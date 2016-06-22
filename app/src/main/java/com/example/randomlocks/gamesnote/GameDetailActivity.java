package com.example.randomlocks.gamesnote;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.example.randomlocks.gamesnote.Fragments.GameDetailFragment;
import com.example.randomlocks.gamesnote.Fragments.ImprovedWebViewFragment;

public class GameDetailActivity extends AppCompatActivity implements GameDetailFragment.CommunicationInterface {

    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);
        fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag("GameDetail");
        if(fragment == null){
            fragment = GameDetailFragment.newInstance(getIntent().getStringExtra(GameDetailFragment.API_URL), getIntent().getStringExtra(GameDetailFragment.IMAGE_URL));
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment, "GameDetail");
            fragmentTransaction.commit();

        }






    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void loadWebView(String string) {
        Fragment fragment =   fragmentManager.findFragmentByTag("WebView");

        if(fragment==null){
            fragment = ImprovedWebViewFragment.newInstance(string);
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment, "WebView");
            fragmentTransaction.commit();
        }

    }

    @Override
    public void finish() {
        super.finish();
    }
}
