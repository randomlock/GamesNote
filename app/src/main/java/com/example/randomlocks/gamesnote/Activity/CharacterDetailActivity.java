package com.example.randomlocks.gamesnote.Activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.Adapter.CharacterDetailImageAdapter;
import com.example.randomlocks.gamesnote.AsyncTask.JsoupCharacterWikiImage;
import com.example.randomlocks.gamesnote.HelperClass.AVLoadingIndicatorView;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.PicassoNestedScrollView;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.Interface.GameCharacterInterface;
import com.example.randomlocks.gamesnote.Modal.GameCharacterModal.CharacterImage;
import com.example.randomlocks.gamesnote.Modal.GameCharacterModal.CharacterListModal;
import com.example.randomlocks.gamesnote.Modal.GameCharacterModal.CharacterModal;
import com.example.randomlocks.gamesnote.R;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CharacterDetailActivity extends AppCompatActivity {


    private static final String API_URL = "api_url";
    private static final String IMAGE_URL = "image_url";
    String apiUrl, imageUrl;
    ImageView coverImage;
    TextView mSmallDescription, mBigDescription, mFirstAppearance, mAlias;
    RecyclerView recyclerview, imageRecyclerView;
    LinearLayout parentLayout;
    GameCharacterInterface mGameCharacterInterface;
    Map<String, String> map;
    CharacterModal characterDetailModal;
    TextView mGender, mBirthDay, mTotalGames;
    PicassoNestedScrollView scrollView;
    Toolbar toolbar;
    AVLoadingIndicatorView pacman;
    CoordinatorLayout coordinatorLayout;
    AsyncTask asyncCharacterWikiImage = null;
    CollapsingToolbarLayout collapsingToolbarLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_detail);
        String str[] = getIntent().getStringExtra(GiantBomb.API_URL).split("/");
        apiUrl = str[str.length - 1];
        imageUrl = getIntent().getStringExtra(GiantBomb.IMAGE_URL);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.root_coordinator);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbarLayout.setTitle("");
        toolbar = (Toolbar) coordinatorLayout.findViewById(R.id.my_toolbar);
        coverImage = (ImageView) coordinatorLayout.findViewById(R.id.character_image);
        scrollView = (PicassoNestedScrollView) coordinatorLayout.findViewById(R.id.scroll_view);
        pacman = (AVLoadingIndicatorView) scrollView.findViewById(R.id.progressBar);
        parentLayout = (LinearLayout) coordinatorLayout.findViewById(R.id.parentLinearLayout);
        mGender = (TextView) parentLayout.findViewById(R.id.gender);
        mBirthDay = (TextView) parentLayout.findViewById(R.id.birthday);
        mTotalGames = (TextView) parentLayout.findViewById(R.id.games);
        mSmallDescription = (TextView) parentLayout.findViewById(R.id.deck);
        imageRecyclerView = (RecyclerView) parentLayout.findViewById(R.id.image_recycler_view);
        mBigDescription = (TextView) parentLayout.findViewById(R.id.description);
        mFirstAppearance = (TextView) parentLayout.findViewById(R.id.first_appearance);
        mAlias = (TextView) parentLayout.findViewById(R.id.alias);
        recyclerview = (RecyclerView) parentLayout.findViewById(R.id.recycler_view);
        recyclerview.setNestedScrollingEnabled(false);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (imageUrl != null) {
            Picasso.with(this).load(imageUrl).into(coverImage);
        }

        if (savedInstanceState != null) {
            Toaster.make(this, "hello save instance");
            characterDetailModal = savedInstanceState.getParcelable(GiantBomb.MODAL);
            fillData(characterDetailModal);
        } else {

            if (characterDetailModal != null) {
                fillData(characterDetailModal);
            } else {

                mGameCharacterInterface = GiantBomb.createGameCharacterService();
                map = new HashMap<>();
                map.put(GiantBomb.KEY, GiantBomb.API_KEY);
                map.put(GiantBomb.FORMAT, "JSON");
                getCharacterDetail(mGameCharacterInterface, map);

                asyncCharacterWikiImage = new JsoupCharacterWikiImage(new JsoupCharacterWikiImage.AsyncResponse() {
                    @Override
                    public void processFinish(List<CharacterImage> imageUrls) {
                        if (imageUrls != null) {
                            imageRecyclerView.setLayoutManager(new LinearLayoutManager(CharacterDetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            imageRecyclerView.setAdapter(new CharacterDetailImageAdapter(imageUrls, CharacterDetailActivity.this));
                        }
                    }
                }).execute("http://www.giantbomb.com/character/" + apiUrl);


            }

        }

    }


    private void getCharacterDetail(final GameCharacterInterface mGameCharacterInterface, final Map<String, String> map) {

        mGameCharacterInterface.getResult(apiUrl, map).enqueue(new Callback<CharacterListModal>() {
            @Override
            public void onResponse(Call<CharacterListModal> call, Response<CharacterListModal> response) {
                characterDetailModal = response.body().results;
                fillData(characterDetailModal);
            }

            @Override
            public void onFailure(Call<CharacterListModal> call, Throwable t) {
                pacman.setVisibility(View.GONE);
                parentLayout.setVisibility(View.VISIBLE);

                Snackbar.make(coordinatorLayout, "Connectivity Problem", Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getCharacterDetail(mGameCharacterInterface, map);

                            }
                        }).show();
            }
        });
    }

    private void fillData(CharacterModal characterDetailModal) {
        pacman.setVisibility(View.GONE);
        parentLayout.setVisibility(View.VISIBLE);
        collapsingToolbarLayout.setTitle(characterDetailModal.name);

        if (imageUrl == null && characterDetailModal.image != null && characterDetailModal.image.mediumUrl != null) {
            Picasso.with(this).load(characterDetailModal.image.mediumUrl).into(coverImage);
        }

        mGender.setText(getGender(characterDetailModal.gender));

        mTotalGames.setText(String.valueOf(characterDetailModal.games.size()));
        if (characterDetailModal.birthday != null) {
            mBirthDay.setText(characterDetailModal.birthday);
        }
        String description = "\"" + characterDetailModal.deck + "\"";
        mSmallDescription.setText(description);
        mFirstAppearance.setText(characterDetailModal.firstAppearedInGame.name);
        if (characterDetailModal.aliases != null) {
            mAlias.setText(characterDetailModal.aliases);
        }
        Document doc = null;
        if (characterDetailModal.description != null) {
            doc = Jsoup.parse(characterDetailModal.description);
            Elements info = doc.getElementsByTag("p");
            if (info != null) {
                StringBuilder builder = new StringBuilder();

                for (Element element : info) {
                    builder.append(element.text()).append("\n\n");
                }
                mBigDescription.setText(builder.toString());


            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {


            case android.R.id.home:
                onBackPressed();
                break;

        }


        return true;
    }

    private String getGender(int gender) {

        String gen;

        switch (gender) {
            case 1:
                gen = "Male";
                break;
            case 2:
                gen = "Female";
                break;
            default:
                gen = "-";

        }
        return gen;

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (characterDetailModal != null) {
            outState.putParcelable(GiantBomb.MODAL, characterDetailModal);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (asyncCharacterWikiImage != null) {
            asyncCharacterWikiImage.cancel(true);
        }
    }

}
