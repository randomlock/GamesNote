package com.example.randomlocks.gamesnote.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.WebViewHelper.CustomTabActivityHelper;
import com.example.randomlocks.gamesnote.HelperClass.WebViewHelper.WebViewFallback;
import com.example.randomlocks.gamesnote.Modal.UserReviewModal.UserReviewModal;
import com.example.randomlocks.gamesnote.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserReviewDetailActivity extends AppCompatActivity {

    Toolbar toolbar;
    CircleImageView circleImageView;
    TextView user_name, date, deck;
    RatingBar ratingBar;
    WebView webView;
    UserReviewModal modal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_review_detail);
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        modal = getIntent().getParcelableExtra(GiantBomb.MODAL);
        circleImageView = (CircleImageView) findViewById(R.id.profile_image);
        user_name = (TextView) findViewById(R.id.user_name);
        ratingBar = (RatingBar) findViewById(R.id.myRatingBar);
        date = (TextView) findViewById(R.id.date);
        deck = (TextView) findViewById(R.id.deck);
        webView = (WebView) findViewById(R.id.web_view);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            modal = savedInstanceState.getParcelable(GiantBomb.MODAL);

            fillDescription(modal.description);

        } else {

            if (modal != null) {
                if (modal.reviewer != null) {
                    user_name.setText(modal.reviewer);
                }
                if (modal.score >= 0) {
                    ratingBar.setRating(modal.score);
                }
                String dateArray[];
                if (modal.dateAdded != null) {
                    dateArray = modal.dateAdded.split(" ");
                    date.setText(dateArray[0]);

                }
                if (modal.deck != null) {
                    deck.setText(modal.deck);
                }

                fillDescription(modal.description);

            }


        }


    }

    private void fillDescription(String description) {

        if (description != null) {
            StringBuilder builder = new StringBuilder(description.length() + 100);
            String color;
            int night_mode = AppCompatDelegate.getDefaultNightMode();
            if (night_mode == AppCompatDelegate.MODE_NIGHT_YES) {
                color = "white";
            } else
                color = "black";

            builder.append("<HTML><HEAD><LINK href=\"style.css\" type=\"text/css\" rel=\"stylesheet\"/></HEAD><body style=\"color:").append(color).append(";\">");
            builder.append(description);
            builder.append("</body></HTML>");

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {

                    //  ((MainActivity)(getActivity())).loadWebView(url);

                    runBrowser(url);


                    return true;
                }
            });
            webView.loadDataWithBaseURL("file:///android_asst/", builder.toString().replaceAll("\\+", "%20"), "text/html", "UTF-8", null);
            webView.setBackgroundColor(ContextCompat.getColor(this, R.color.webviewbackground));


        }


    }

    void runBrowser(String url) {
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().setShowTitle(true).build();
        CustomTabActivityHelper.openCustomTab(
                this, customTabsIntent, Uri.parse(url), new WebViewFallback());
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
                //    supportFinishAfterTransition();
                return true;

            case R.id.internet:
                if (modal.siteDetailUrl != null) {
                    runBrowser(modal.siteDetailUrl);
                }


        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(GiantBomb.MODAL, modal);

    }
}
