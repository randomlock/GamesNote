package com.example.randomlocks.gamesnote.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.Adapter.CharacterDetailImageAdapter;
import com.example.randomlocks.gamesnote.AsyncTask.JsoupCharacterWikiImage;
import com.example.randomlocks.gamesnote.HelperClass.CustomView.PicassoNestedScrollView;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CharacterDetailActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String API_URL = "api_url";
    private static final String IMAGE_URL = "image_url";
    String apiUrl, imageUrl,title;
    ImageView coverImage;
    CircleImageView coverImage2;
    TextView mSmallDescription, mBigDescription, mFirstAppearance, mAlias, mTitle;
    RecyclerView recyclerview, imageRecyclerView;
    LinearLayout parentLayout;
    GameCharacterInterface mGameCharacterInterface;
    Call<CharacterListModal> call;
    Map<String, String> map;
    CharacterModal characterDetailModal = null;
    ProgressBar progressBar;
    TextView mGender, mBirthDay, mTotalGames, mFriends, mEnemies, mEnemiesTitle, mFriendsTitle, mTotalGamesTitle;
    TextView image_heading;
    PicassoNestedScrollView scrollView;
    Toolbar toolbar;
    CoordinatorLayout coordinatorLayout;
    AsyncTask asyncCharacterWikiImage = null;
    AppBarLayout appBarLayout;
    CollapsingToolbarLayout collapsingToolbarLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_detail);
        String str[] = getIntent().getStringExtra(GiantBomb.API_URL).split("/");
        apiUrl = str[str.length - 1];
        imageUrl = getIntent().getStringExtra(GiantBomb.IMAGE_URL);
        title = getIntent().getStringExtra(GiantBomb.TITLE);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.root_coordinator);
        appBarLayout = (AppBarLayout) coordinatorLayout.findViewById(R.id.app_bar_layout);
        collapsingToolbarLayout = (CollapsingToolbarLayout) appBarLayout.findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbarLayout.setTitle("");
        toolbar = (Toolbar) collapsingToolbarLayout.findViewById(R.id.my_toolbar);
        coverImage = (ImageView) collapsingToolbarLayout.findViewById(R.id.character_image);
        coverImage2 = (CircleImageView) coordinatorLayout.findViewById(R.id.character_image2);
        scrollView = (PicassoNestedScrollView) coordinatorLayout.findViewById(R.id.scroll_view);
        parentLayout = (LinearLayout) coordinatorLayout.findViewById(R.id.parentLinearLayout);
        progressBar = (ProgressBar) parentLayout.findViewById(R.id.progressBar);
        mTitle = (TextView) parentLayout.findViewById(R.id.character_name);
        mEnemies = (TextView) parentLayout.findViewById(R.id.enemies);
        mEnemiesTitle = (TextView) parentLayout.findViewById(R.id.enemies_title);
        mFriends = (TextView) parentLayout.findViewById(R.id.friends);
        mFriendsTitle = (TextView) parentLayout.findViewById(R.id.friends_title);
        mTotalGames = (TextView) parentLayout.findViewById(R.id.total_games);
        mTotalGamesTitle = (TextView) parentLayout.findViewById(R.id.total_games_titles);
        mGender = (TextView) parentLayout.findViewById(R.id.gender);
        mBirthDay = (TextView) parentLayout.findViewById(R.id.birthday);
        mSmallDescription = (TextView) parentLayout.findViewById(R.id.deck);
        imageRecyclerView = (RecyclerView) parentLayout.findViewById(R.id.image_recycler_view);
        mBigDescription = (TextView) parentLayout.findViewById(R.id.description);
        mFirstAppearance = (TextView) parentLayout.findViewById(R.id.first_appearance);
        mAlias = (TextView) parentLayout.findViewById(R.id.alias);
        image_heading = (TextView) parentLayout.findViewById(R.id.image_heading);
        recyclerview = (RecyclerView) parentLayout.findViewById(R.id.recycler_view);
        recyclerview.setNestedScrollingEnabled(false);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        /*coverImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                coverImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                DisplayMetrics displaymetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                int width = displaymetrics.widthPixels;
                Picasso.with(CharacterDetailActivity.this)
                        .load(imageUrl)
                        .into(coverImage);
            }
        });*/


        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (collapsingToolbarLayout.getHeight() + verticalOffset < 2 * ViewCompat.getMinimumHeight(collapsingToolbarLayout)) {
                    if (coverImage2.getAlpha() == 1) {
                        coverImage2.animate().setDuration(400).alpha(0);
                    }
                } else {
                    if (coverImage2.getAlpha() == 0) {
                        coverImage2.animate().setDuration(400).alpha(1);
                    }
                }
            }

        });


        if (imageUrl != null && imageUrl.trim().length() > 0) {
            Picasso.with(this).load(imageUrl).fit().centerCrop().into(coverImage);
            Picasso.with(this).load(imageUrl).fit().centerCrop().into(coverImage2);
        }

        /* Picasso.with(this).load(imageUrl).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                expandToolbar(bitmap);
                coverImage.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });*/


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

                runAsyncTask();


            }

        }

    }

    private void runAsyncTask() {

        asyncCharacterWikiImage = new JsoupCharacterWikiImage(new JsoupCharacterWikiImage.AsyncResponse() {
            @Override
            public void processFinish(List<CharacterImage> imageUrls) {
                if (imageUrls != null) {
                    Toaster.make(CharacterDetailActivity.this,"image loaded");
                    imageRecyclerView.setLayoutManager(new LinearLayoutManager(CharacterDetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    imageRecyclerView.setAdapter(new CharacterDetailImageAdapter(imageUrls, CharacterDetailActivity.this,title));
                }else {
                    image_heading.setVisibility(View.GONE);
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"http://www.giantbomb.com/character/" + apiUrl);

    }


    private void getCharacterDetail(final GameCharacterInterface mGameCharacterInterface, final Map<String, String> map) {
        call = mGameCharacterInterface.getResult(apiUrl,map);
        call.enqueue(new Callback<CharacterListModal>() {
            @Override
            public void onResponse(Call<CharacterListModal> call, Response<CharacterListModal> response) {
                characterDetailModal = response.body().results;
                fillData(characterDetailModal);
            }

            @Override
            public void onFailure(Call<CharacterListModal> call, Throwable t) {
                parentLayout.setVisibility(View.VISIBLE);

                if(!call.isCanceled()){
                    Toaster.makeSnackBar(coordinatorLayout, "Connectivity Problem", "RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getCharacterDetail(mGameCharacterInterface,map);
                            runAsyncTask();
                        }
                    });


                }


            }
        });
    }

    private void fillData(CharacterModal characterDetailModal) {
        progressBar.setVisibility(View.GONE);
        if (characterDetailModal.name != null) {
            collapsingToolbarLayout.setTitle(characterDetailModal.name);
            mTitle.setText(characterDetailModal.name);
        }

        if (imageUrl == null && characterDetailModal.image != null) {
            Picasso.with(this).load(characterDetailModal.image.mediumUrl).fit().centerCrop().into(coverImage);
            Picasso.with(this).load(characterDetailModal.image.thumbUrl).fit().centerCrop().into(coverImage2);
        }

        mGender.setText(getGender(characterDetailModal.gender));

        if (characterDetailModal.enemies != null) {
            mEnemies.setText(String.valueOf(characterDetailModal.enemies.size()));
            mEnemies.setOnClickListener(this);
            mEnemiesTitle.setOnClickListener(this);

        }

        if (characterDetailModal.friends != null) {

            mFriends.setText(String.valueOf(characterDetailModal.friends.size()));
            mFriends.setOnClickListener(this);
            mFriendsTitle.setOnClickListener(this);

        }

        if (characterDetailModal.games != null) {

            mTotalGames.setText(String.valueOf(characterDetailModal.games.size()));
            mTotalGames.setOnClickListener(this);
            mTotalGamesTitle.setOnClickListener(this);

        }
        if (characterDetailModal.birthday != null) {
            mBirthDay.setText(characterDetailModal.birthday);
        }
        String description = null;
        if (characterDetailModal.deck != null) {
            description = "\"" + characterDetailModal.deck + "\"";
            mSmallDescription.setText(description);

        }
        if (characterDetailModal.firstAppearedInGame != null) {
            mFirstAppearance.setText(characterDetailModal.firstAppearedInGame.name);
        }
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


    @Override
    public void onClick(View v) {

        TextView textView = (TextView) v;


        Intent intent = new Intent(this, CharacterFriendEnemyActivity.class);
        switch (v.getId()) {

            case R.id.enemies:
            case R.id.enemies_title:
                if (characterDetailModal.enemies != null && characterDetailModal.enemies.size() > 0) {
                    intent.putParcelableArrayListExtra(GiantBomb.MODAL, new ArrayList<>(characterDetailModal.enemies));
                    intent.putExtra(GiantBomb.IS_GAME_DETAIL,false);
                    startActivity(intent);
                } else {
                    Snackbar.make(coordinatorLayout, "No enemies :)", Snackbar.LENGTH_SHORT).show();
                }
                break;

            case R.id.friends:
            case R.id.friends_title:
                if (characterDetailModal.friends != null && characterDetailModal.friends.size() > 0) {
                    intent.putParcelableArrayListExtra(GiantBomb.MODAL, new ArrayList<>(characterDetailModal.friends));
                    intent.putExtra(GiantBomb.IS_GAME_DETAIL,false);
                    startActivity(intent);
                } else {
                    Snackbar.make(coordinatorLayout, "No friends :(", Snackbar.LENGTH_SHORT).show();
                }

                break;
            case R.id.total_games:
            case R.id.total_games_titles:
                if (characterDetailModal.games != null && characterDetailModal.games.size() > 0) {
                    intent.putParcelableArrayListExtra(GiantBomb.MODAL, new ArrayList<>(characterDetailModal.games));
                    intent.putExtra(GiantBomb.IS_GAME_DETAIL,true);
                    startActivity(intent);
                } else {
                    Snackbar.make(coordinatorLayout, "No known games :(", Snackbar.LENGTH_SHORT).show();
                }
                break;

        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(call!=null)
            call.cancel();
    }
}
