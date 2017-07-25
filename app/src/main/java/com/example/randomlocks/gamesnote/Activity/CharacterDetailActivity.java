package com.example.randomlocks.gamesnote.Activity;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
    TextView mSmallDescription, mFirstAppearance, mAlias, mTitle;
    RecyclerView imageRecyclerView;
    LinearLayout parentLayout;
    GameCharacterInterface mGameCharacterInterface;
    Call<CharacterListModal> call;
    Map<String, String> map;
    CharacterModal characterDetailModal = null;
    ProgressBar progressBar;
    TextView mGender, mBirthDay, mTotalGames, mFriends, mEnemies, mEnemiesTitle, mFriendsTitle, mTotalGamesTitle;
    TextView image_heading;
    View above_image_line;
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
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        coverImage.getLayoutParams().height = metrics.heightPixels / 3;
        coverImage.requestLayout();
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
        mFirstAppearance = (TextView) parentLayout.findViewById(R.id.first_appearance);
        mAlias = (TextView) parentLayout.findViewById(R.id.alias);
        above_image_line = parentLayout.findViewById(R.id.above_image_line);
        image_heading = (TextView) parentLayout.findViewById(R.id.image_heading);



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
                if (verticalOffset > 0) {
                    coverImage2.setTranslationY(verticalOffset);
                }
            }

        });


        if (imageUrl != null && imageUrl.trim().length() > 0) {
            Picasso.with(this).load(imageUrl).fit().centerCrop().into(coverImage);
            Picasso.with(this).load(imageUrl).fit().centerInside().into(coverImage2);
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
                    above_image_line.setVisibility(View.GONE);
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
            Picasso.with(this).load(characterDetailModal.image.thumbUrl).fit().centerInside().into(coverImage2);
        }

        mGender.setText(getGender(characterDetailModal.gender));

        if (characterDetailModal.enemies != null) {
            mEnemies.setText(String.valueOf(characterDetailModal.enemies.size()));
            mEnemies.setOnClickListener(this);
            mEnemiesTitle.setOnClickListener(this);

        } else {
            mFriends.setText("-");
        }

        if (characterDetailModal.friends != null) {

            mFriends.setText(String.valueOf(characterDetailModal.friends.size()));
            mFriends.setOnClickListener(this);
            mFriendsTitle.setOnClickListener(this);

        } else {
            mFriends.setText("-");
        }

        if (characterDetailModal.games != null) {

            mTotalGames.setText(String.valueOf(characterDetailModal.games.size()));
            mTotalGames.setOnClickListener(this);
            mTotalGamesTitle.setOnClickListener(this);
        } else {
            mFriends.setText("-");
        }
        if (characterDetailModal.birthday != null) {
            mBirthDay.setText(characterDetailModal.birthday);
        } else {
            mBirthDay.setText("-");
        }
        String description = null;
        if (characterDetailModal.deck != null) {
            description = "\"" + characterDetailModal.deck + "\"";
            mSmallDescription.setText(description);
        } else {
            mSmallDescription.setText("No info");
        }
        if (characterDetailModal.firstAppearedInGame != null) {
            mFirstAppearance.setText(characterDetailModal.firstAppearedInGame.name);
        } else {
            mFirstAppearance.setText("-");
        }
        if (characterDetailModal.aliases != null) {
            mAlias.setText(characterDetailModal.aliases);
        } else {
            mFirstAppearance.setText("-");
        }
        Document doc = null;
        if (characterDetailModal.description != null) {
            fillDescription(characterDetailModal.description);
        }


    }

    private void fillDescription(String description) {
        new ParseJsoup().execute(description);
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

    private TextView getTextView(String text) {
        TextView textView = new TextView(CharacterDetailActivity.this, null, R.style.SubTitleText);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = (int) GiantBomb.dipToPixels(CharacterDetailActivity.this, 12);
        params.setMargins(margin, margin, margin, 0);
        textView.setLayoutParams(params);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setTextColor(getTextColor());
        textView.setLineSpacing(0, (float) 1.40);
        textView.setText(text);
        return textView;
    }

    private int getTextColor() {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = CharacterDetailActivity.this.getTheme();
        theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
        TypedArray arr =
                CharacterDetailActivity.this.obtainStyledAttributes(typedValue.data, new int[]{
                        android.R.attr.textColorPrimary});
        int primaryColor = arr.getColor(0, -1);
        arr.recycle();
        return primaryColor;
    }

    private class ParseJsoup extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String desc = params[0];

            Document document = Jsoup.parse(desc);
            if (document != null) {
                Elements elements = document.select("*");

                for (Element element : elements) {
                    if (element.tagName().equals("p")) {
                        publishProgress(element.text(), String.valueOf(true));
                    } else if (element.tagName().equals("figure")) {
                        Elements innerElements = element.getElementsByTag("img");
                        if (innerElements != null) {
                            Element innerElement = innerElements.first();
                            if (innerElement.hasAttr("alt"))
                                publishProgress(element.absUrl("data-img-src"), String.valueOf(false), innerElement.attr("alt"));
                            else
                                publishProgress(element.absUrl("data-img-src"), String.valueOf(false));
                        } else {
                            publishProgress(element.absUrl("data-img-src"), String.valueOf(false));
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            String result = values[0];
            boolean isDescription = Boolean.parseBoolean(values[1]);
            if (isDescription) {
                parentLayout.addView(getTextView(result));
            } else {
                CardView cardView = new CardView(CharacterDetailActivity.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER;
                int margin = (int) GiantBomb.dipToPixels(CharacterDetailActivity.this, 12);
                params.setMargins(margin, margin, margin, margin);
                cardView.setLayoutParams(params);
                cardView.setRadius(GiantBomb.dipToPixels(CharacterDetailActivity.this, 4));
                ImageView imageView = new ImageView(CharacterDetailActivity.this);
                imageView.setLayoutParams(new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setAdjustViewBounds(true);
                if (!result.isEmpty()) {
                    Picasso.with(CharacterDetailActivity.this).load(result).placeholder(R.drawable.news_image_drawable).into(imageView);
                }
                cardView.addView(imageView);
                parentLayout.addView(cardView);

                TextView textView;
                if (values.length == 3 && !values[2].equals("No Caption Provided")) {
                    textView = getTextView(values[2]);
                    ((LinearLayout.LayoutParams) textView.getLayoutParams()).gravity = Gravity.CENTER;
                    ((LinearLayout.LayoutParams) textView.getLayoutParams()).setMargins(margin, 0, margin, margin);
                    textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
                    textView.setGravity(Gravity.CENTER);

                    parentLayout.addView(textView);

                }


            }

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            View view = new View(CharacterDetailActivity.this);
            int height = (int) GiantBomb.dipToPixels(CharacterDetailActivity.this, getResources().getDimension(R.dimen.line));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            int margin = (int) GiantBomb.dipToPixels(CharacterDetailActivity.this, 12);
            params.setMargins(0, margin, 0, margin);
            view.setLayoutParams(params);
            view.setBackgroundColor(ContextCompat.getColor(CharacterDetailActivity.this, R.color.linecolor));
            parentLayout.addView(view);
        }
    }





}
