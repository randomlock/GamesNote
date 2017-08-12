package com.example.randomlocks.gamesnote.activity;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.helperClass.GiantBomb;
import com.example.randomlocks.gamesnote.helperClass.WebViewHelper.CustomTabActivityHelper;
import com.example.randomlocks.gamesnote.helperClass.WebViewHelper.WebViewFallback;
import com.example.randomlocks.gamesnote.modals.UserReviewModal.UserReviewModal;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserReviewDetailActivity extends AppCompatActivity {

    Toolbar toolbar;
    CircleImageView circleImageView;
    TextView user_name, date, deck;
    //  TextView description;
    RatingBar ratingBar;
    UserReviewModal modal;
    LinearLayout descriptionLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_review_detail);
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        modal = getIntent().getParcelableExtra(GiantBomb.MODAL);
        circleImageView = (CircleImageView) findViewById(R.id.profile_image);
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            circleImageView.setColorFilter(Color.WHITE);
        }
        user_name = (TextView) findViewById(R.id.user_name);
        ratingBar = (RatingBar) findViewById(R.id.myRatingBar);
        date = (TextView) findViewById(R.id.date);
        deck = (TextView) findViewById(R.id.deck);
        descriptionLayout = (LinearLayout) findViewById(R.id.description_parent);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(modal.reviewer + "'s review");

        if (savedInstanceState != null) {
            modal = savedInstanceState.getParcelable(GiantBomb.MODAL);
        }
        fillData(modal);

    }

    private void fillData(UserReviewModal modal) {
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
                String str = "<b> Publish date : </b> " + dateArray[0];
                date.setText(Html.fromHtml(str));

            }
            if (modal.deck != null) {
                deck.setText(modal.deck);
            }

            if (modal.description != null) {
                fillDescription(modal.description);
            }

        }
    }

    private void fillDescription(String desc) {
        new ParseJsoup().execute(desc);
    }

    void runBrowser(String url) {
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().setShowTitle(true).build();
        CustomTabActivityHelper.openCustomTab(
                this, customTabsIntent, Uri.parse(url), new WebViewFallback());
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
                //    supportFinishAfterTransition();
                if (item.getItemId() == android.R.id.home) {
                    onBackPressed();
                }
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

    private TextView getTextView(String text) {
        TextView textView = new TextView(UserReviewDetailActivity.this, null, R.style.SubTitleText);
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setTextColor(getTextColor());
        textView.setLineSpacing(0, (float) 1.40);
        textView.setText(text);
        return textView;
    }

    private int getTextColor() {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = UserReviewDetailActivity.this.getTheme();
        theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
        TypedArray arr =
                UserReviewDetailActivity.this.obtainStyledAttributes(typedValue.data, new int[]{
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
                descriptionLayout.addView(getTextView(result));
            } else {
                CardView cardView = new CardView(UserReviewDetailActivity.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER;
                int margin = (int) GiantBomb.dipToPixels(UserReviewDetailActivity.this, 12);
                params.setMargins(margin, margin, margin, margin);
                cardView.setLayoutParams(params);
                cardView.setRadius(GiantBomb.dipToPixels(UserReviewDetailActivity.this, 4));
                ImageView imageView = new ImageView(UserReviewDetailActivity.this);
                imageView.setLayoutParams(new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setAdjustViewBounds(true);
                if (!result.isEmpty()) {
                    Picasso.with(UserReviewDetailActivity.this).load(result).placeholder(R.drawable.news_image_drawable).into(imageView);
                }
                cardView.addView(imageView);
                descriptionLayout.addView(cardView);

                TextView textView;
                if (values.length == 3 && !values[2].equals("No Caption Provided")) {
                    textView = getTextView(values[2]);
                    ((LinearLayout.LayoutParams) textView.getLayoutParams()).gravity = Gravity.CENTER;
                    ((LinearLayout.LayoutParams) textView.getLayoutParams()).setMargins(margin, 0, margin, margin);
                    textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
                    textView.setGravity(Gravity.CENTER);

                    descriptionLayout.addView(textView);

                }


            }

        }

    }




}
