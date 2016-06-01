package com.example.randomlocks.gamesnote;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.randomlocks.gamesnote.Fragments.GameDetailFragment;

public class GameDetailActivity extends AppCompatActivity {

    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);
        fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag("GameDetail");
        if(fragment == null){
            fragment = GameDetailFragment.newInstance(getIntent().getStringExtra(GameDetailFragment.API_URL));
        }


        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment, "GameDetail");
        fragmentTransaction.commit();



    }
}
