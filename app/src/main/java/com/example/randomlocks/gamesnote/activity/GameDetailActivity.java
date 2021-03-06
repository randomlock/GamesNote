package com.example.randomlocks.gamesnote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.fragments.GameDetailFragment;
import com.example.randomlocks.gamesnote.helperClass.GiantBomb;
import com.example.randomlocks.gamesnote.helperClass.Toaster;
import com.example.randomlocks.gamesnote.modals.gamesVideoModal.GamesVideoModal;

public class GameDetailActivity extends AppCompatActivity implements GameDetailFragment.CommunicationInterface {

    public int seek_position = 0;
    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);
        fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag("GameDetail");
        if (fragment == null) {
            fragment = GameDetailFragment.newInstance(getIntent().getStringExtra(GameDetailFragment.API_URL), getIntent().getStringExtra(GameDetailFragment.NAME), getIntent().getStringExtra(GameDetailFragment.IMAGE_URL));
        }
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_parent_layout, fragment, "GameDetail");

        fragmentTransaction.commit();


    }





   /* public void loadWebView(String string) {
        Fragment fragment =   fragmentManager.findFragmentByTag("WebView");

        if(fragment==null) {
            fragment = ImprovedWebViewFragment.newInstance(string);
        }
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment, "WebView");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();


    } */


    @Override
    public void finish() {
        super.finish();
    }

    public void startCharacterActivity(String apiUrl, String imageUrl,String title) {




        Intent intent = new Intent(this, CharacterDetailActivity.class);
        intent.putExtra(GiantBomb.API_URL, apiUrl);
        intent.putExtra(GiantBomb.IMAGE_URL, imageUrl);
        intent.putExtra(GiantBomb.TITLE,title);
        startActivity(intent);


    }


    public void restartGameDetail(String apiUrl, String imageUrl) {

        Fragment fragment = fragmentManager.findFragmentByTag("GameDetail");

        if (fragment != null) {
            fragment.getArguments().putString(GameDetailFragment.API_URL, apiUrl);
            fragment.getArguments().putString(GameDetailFragment.IMAGE_URL, imageUrl);
            fragmentTransaction.replace(R.id.fragment_parent_layout, fragment, "GameDetail").addToBackStack(null).commit();

        }


    }

    @Override
    public void onReviewClick(String apiUrl, String gameTitle, String imageUrl) {
        Intent intent = new Intent(this, GameReviewActivity.class);
        intent.putExtra(GiantBomb.TITLE, gameTitle);
        intent.putExtra(GiantBomb.REVIEW, apiUrl);
        intent.putExtra(GiantBomb.IMAGE_URL, imageUrl);
        startActivity(intent);
    }

    @Override
    public void onUserReviewClick(String reviews_id, String gameTitle, String imageUrl) {
        Intent intent = new Intent(this, UserReviewActivity.class);
        intent.putExtra(GiantBomb.REVIEW, reviews_id);
        intent.putExtra(GiantBomb.TITLE, gameTitle);
        intent.putExtra(GiantBomb.IMAGE_URL, imageUrl);
        startActivity(intent);
    }

    @Override
    public void onVideoClick(String url, boolean needRequest, int seek_position, GamesVideoModal modal) {
        Intent intent = new Intent(this, VideoPlayerActivity.class);
        intent.putExtra(GiantBomb.API_URL, url);
        intent.putExtra(GiantBomb.SEEK_POSITION, seek_position);
        intent.putExtra(GiantBomb.MODAL, modal);
        startActivityForResult(intent,1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                Toaster.make(this, seek_position + "");
                seek_position =data.getIntExtra(GiantBomb.SEEK_POSITION,0);
            }
        }
    }
}
