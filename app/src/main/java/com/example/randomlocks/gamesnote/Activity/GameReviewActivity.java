package com.example.randomlocks.gamesnote.Activity;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.PicassoCoordinatorLayout;
import com.example.randomlocks.gamesnote.HelperClass.WebViewHelper.CustomTabActivityHelper;
import com.example.randomlocks.gamesnote.HelperClass.WebViewHelper.WebViewFallback;
import com.example.randomlocks.gamesnote.Interface.GameReviewInterface;
import com.example.randomlocks.gamesnote.Modal.GameReviewModal.GameReviewModal;
import com.example.randomlocks.gamesnote.Modal.GameReviewModal.GameReviewModalList;
import com.example.randomlocks.gamesnote.R;
import com.jaeger.library.StatusBarUtil;
import com.squareup.picasso.Picasso;

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
    WebView webView;
    ImageView imageView;
    GameReviewInterface gameReviewInterface = null;
    GameReviewModal modals = null;
    Map<String, String> map;
    String review_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_review);
        int color = ContextCompat.getColor(this, R.color.alpha_color);
        int alpha = Color.alpha(color);
        StatusBarUtil.setColor(this, color, 50);

        coodinatorLayout = (PicassoCoordinatorLayout) findViewById(R.id.root_coordinator);
        toolbar = (Toolbar) coodinatorLayout.findViewById(R.id.my_toolbar);
        imageView = (ImageView) findViewById(R.id.appbar_image);
        parentLinearLayout = (LinearLayout) findViewById(R.id.parent_linear_layout);
        ratingBar = (RatingBar) parentLinearLayout.findViewById(R.id.myRatingBar);
        webView = (WebView) parentLinearLayout.findViewById(R.id.webView);
        deck = (TextView) parentLinearLayout.findViewById(R.id.deck);
        reviewer = (TextView) parentLinearLayout.findViewById(R.id.reviewer);
        publishDate = (TextView) parentLinearLayout.findViewById(R.id.date);
        gameTitle = (TextView) parentLinearLayout.findViewById(R.id.title);
        gameTitle.setText(getIntent().getStringExtra(GiantBomb.TITLE));
        String full_id = getIntent().getStringExtra(GiantBomb.REVIEW);
        String str[] = full_id.split("/");
        review_id = str[str.length - 1];

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Picasso.with(this).load(getIntent().getStringExtra(GiantBomb.IMAGE_URL)).into(coodinatorLayout);

        if (savedInstanceState != null) {
            modals = savedInstanceState.getParcelable(MODAL);
            fillData(modals);
        } else {

            if (modals == null) {
                //coming for first time
                gameReviewInterface = GiantBomb.createGameReviewInterface();
                map = new HashMap<>();
                map.put(GiantBomb.KEY, GiantBomb.API_KEY);
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

    private void fillData(GameReviewModal modals) {

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
            StringBuilder builder = new StringBuilder(modals.description.length() + 100);
            String color;
            int night_mode = AppCompatDelegate.getDefaultNightMode();
            if (night_mode == AppCompatDelegate.MODE_NIGHT_YES)
                color = "white";
            else
                color = "black";

            builder.append("<HTML><HEAD><LINK href=\"style.css\" type=\"text/css\" rel=\"stylesheet\"/></HEAD><body style=\"color:" + color + ";\">");
            builder.append(modals.description);
            builder.append("</body></HTML>");
            webView.getSettings().setJavaScriptEnabled(true);

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {

                    //  ((MainActivity)(getActivity())).loadWebView(url);

                    runBrowser(url);


                    return true;
                }
            });
            webView.loadDataWithBaseURL("file:///android_asset/", builder.toString(), "text/html", "UTF-8", null);
            webView.setBackgroundColor(Color.TRANSPARENT);

            // need to set harware layer for api>=21

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.game_news_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.internet:
                runBrowser(modals.siteDetailUrl);
                break;

            default:
                return super.onOptionsItemSelected(item);


        }

        return true;
    }

    void runBrowser(String url) {
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
        CustomTabActivityHelper.openCustomTab(
                this, customTabsIntent, Uri.parse(url), new WebViewFallback());
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MODAL, modals);

    }
}
