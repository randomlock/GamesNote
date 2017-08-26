package com.example.randomlocks.gamesnote.activity;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.helperClass.CustomView.AVLoadingIndicatorView;
import com.example.randomlocks.gamesnote.helperClass.CustomView.PicassoCoordinatorLayout;
import com.example.randomlocks.gamesnote.helperClass.GiantBomb;
import com.example.randomlocks.gamesnote.helperClass.SharedPreference;
import com.example.randomlocks.gamesnote.helperClass.Toaster;
import com.example.randomlocks.gamesnote.helperClass.WebViewHelper.CustomTabActivityHelper;
import com.example.randomlocks.gamesnote.helperClass.WebViewHelper.WebViewFallback;
import com.example.randomlocks.gamesnote.interfaces.GameReviewInterface;
import com.example.randomlocks.gamesnote.modals.gameReviewModal.GameReviewModal;
import com.example.randomlocks.gamesnote.modals.gameReviewModal.GameReviewModalList;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameReviewActivity extends AppCompatActivity {

    private static final String MODAL = "game_review_model";
    LinearLayout parentLinearLayout;
    PicassoCoordinatorLayout coodinatorLayout;
    Toolbar toolbar;
    RatingBar ratingBar;
    TextView deck, reviewer, publishDate, gameTitle;
    GameReviewInterface gameReviewInterface = null;
    GameReviewModal modals = null;
    Map<String, String> map;
    String review_id;
    AVLoadingIndicatorView progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_review);
        int color = ContextCompat.getColor(this, R.color.alpha_color);
        int alpha = Color.alpha(color);

        coodinatorLayout = (PicassoCoordinatorLayout) findViewById(R.id.root_coordinator);
        toolbar = (Toolbar) coodinatorLayout.findViewById(R.id.my_toolbar);
        progressBar = (AVLoadingIndicatorView) findViewById(R.id.progressBar);
        parentLinearLayout = (LinearLayout) findViewById(R.id.parent_linear_layout);
        ratingBar = (RatingBar) coodinatorLayout.findViewById(R.id.myRatingBar);
        deck = (TextView) coodinatorLayout.findViewById(R.id.deck);
        reviewer = (TextView) coodinatorLayout.findViewById(R.id.reviewer);
        publishDate = (TextView) coodinatorLayout.findViewById(R.id.date);
        gameTitle = (TextView) coodinatorLayout.findViewById(R.id.title);
        gameTitle.setText(getIntent().getStringExtra(GiantBomb.TITLE));
        String full_id = getIntent().getStringExtra(GiantBomb.REVIEW);
        String str[] = full_id.split("/");
        review_id = str[str.length - 1];

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(gameTitle.getText().toString() + " review");

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
        int screenHeight = displaymetrics.heightPixels;
        Picasso.with(this).load(getIntent().getStringExtra(GiantBomb.IMAGE_URL)).resize(screenWidth, screenHeight).centerCrop().into(coodinatorLayout);

        if (savedInstanceState != null) {
            modals = savedInstanceState.getParcelable(MODAL);
            fillData(modals);
        } else {

            if (modals == null) {
                //coming for first time
                gameReviewInterface = GiantBomb.createGameReviewService();
                map = new HashMap<>();
                map.put(GiantBomb.KEY, SharedPreference.getFromSharedPreferences(GiantBomb.API_KEY, GiantBomb.DEFAULT_API_KEY, this));
                map.put(GiantBomb.FORMAT, "JSON");
                getGameReview(gameReviewInterface, map);

            } else {
                fillData(modals);
            }


        }


    }

    private void getGameReview(final GameReviewInterface gameReviewInterface, final Map<String, String> map) {

        gameReviewInterface.getResult(review_id, map).enqueue(new Callback<GameReviewModalList>() {
            @Override
            public void onResponse(Call<GameReviewModalList> call, Response<GameReviewModalList> response) {

                modals = response.body().results;
                fillData(modals);

            }

            @Override
            public void onFailure(Call<GameReviewModalList> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Snackbar.make(coodinatorLayout, "Connectivity Problem", Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getGameReview(gameReviewInterface, map);

                            }
                        }).show();
            }
        });

    }

    private void fillData(final GameReviewModal modals) {
        progressBar.setVisibility(View.GONE);
        if (modals.deck != null) {
            deck.setText(modals.deck);
        }
        if (modals.reviewer != null) {
            reviewer.setText(modals.reviewer);
        }
        if (modals.publishDate != null) {
            String date[] = modals.publishDate.split(" ");
            publishDate.setText(date[0]);
        }
        if (modals.score > 0) {
            ratingBar.setRating(modals.score);
        }

        if (modals.description != null) {
            new ParseJsoup().execute(modals.description);
        }


    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Drawable drawable = menu.findItem(R.id.internet).getIcon();
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.game_review_menu, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.internet:
                if (modals != null) {
                    runBrowser(modals.siteDetailUrl);
                } else {
                    Toaster.makeSnackBar(coodinatorLayout, "Waiting to load data");
                }
                break;

            default:
                return super.onOptionsItemSelected(item);


        }

        return true;
    }

    void runBrowser(String url) {
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().setShowTitle(true).build();
        CustomTabActivityHelper.openCustomTab(
                this, customTabsIntent, Uri.parse(url), new WebViewFallback());
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MODAL, modals);

    }

    private TextView getTextView(String text) {
        TextView textView = new TextView(GameReviewActivity.this, null, R.style.SubTitleText);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = (int) GiantBomb.dipToPixels(GameReviewActivity.this, 12);
        params.setMargins(margin, 0, margin, 0);
        textView.setLayoutParams(params);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setTextColor(getTextColor());
        textView.setLineSpacing(0, (float) 1.40);
        textView.setText(text);
        return textView;
    }

    private int getTextColor() {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = GameReviewActivity.this.getTheme();
        theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
        TypedArray arr =
                GameReviewActivity.this.obtainStyledAttributes(typedValue.data, new int[]{
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
                parentLinearLayout.addView(getTextView(result));
            } else {
                CardView cardView = new CardView(GameReviewActivity.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER;
                int margin = (int) GiantBomb.dipToPixels(GameReviewActivity.this, 12);
                params.setMargins(margin, margin, margin, margin);
                cardView.setLayoutParams(params);
                cardView.setRadius(GiantBomb.dipToPixels(GameReviewActivity.this, 4));
                ImageView imageView = new ImageView(GameReviewActivity.this);
                imageView.setLayoutParams(new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setAdjustViewBounds(true);
                if (!result.isEmpty()) {
                    Picasso.with(GameReviewActivity.this).load(result).placeholder(R.drawable.news_image_drawable).into(imageView);
                }
                cardView.addView(imageView);
                parentLinearLayout.addView(cardView);

                TextView textView;
                if (values.length == 3 && !values[2].equals("No Caption Provided")) {
                    textView = getTextView(values[2]);
                    ((LinearLayout.LayoutParams) textView.getLayoutParams()).gravity = Gravity.CENTER;
                    ((LinearLayout.LayoutParams) textView.getLayoutParams()).setMargins(margin, 0, margin, margin);
                    textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
                    textView.setGravity(Gravity.CENTER);

                    parentLinearLayout.addView(textView);

                }


            }

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            View view = new View(GameReviewActivity.this);
            int height = (int) GiantBomb.dipToPixels(GameReviewActivity.this, getResources().getDimension(R.dimen.line));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            int margin = (int) GiantBomb.dipToPixels(GameReviewActivity.this, 12);
            params.setMargins(0, margin, 0, margin);
            view.setLayoutParams(params);
            view.setBackgroundColor(ContextCompat.getColor(GameReviewActivity.this, R.color.linecolor));
            parentLinearLayout.addView(view);
        }
    }


}
