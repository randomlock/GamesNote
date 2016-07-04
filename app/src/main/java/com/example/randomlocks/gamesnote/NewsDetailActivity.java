package com.example.randomlocks.gamesnote;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.HelperClass.WebViewHelper.CustomTabActivityHelper;
import com.example.randomlocks.gamesnote.HelperClass.WebViewHelper.WebViewFallback;
import com.example.randomlocks.gamesnote.Modal.NewsModal.NewsModal;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class NewsDetailActivity extends AppCompatActivity {


    public static final String TITLE = "title" ;
    public static final String IMAGE_URL = "image_url" ;
    private static final String CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome";
    public static final String LINK = "news_link" ;
    public static final String DESCRIPTION = "news_description";

    String description;
    TextView title;
    WebView webView;
    LinearLayout parentLayout;
    ImageView titleImage;
    Toolbar toolbar;
    NewsModal newsModal;
    CustomTabActivityHelper customTabActivityHelper;


    @Override
    public void onStart() {
        super.onStart();
        customTabActivityHelper.bindCustomTabsService(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        customTabActivityHelper.unbindCustomTabsService(this);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        customTabActivityHelper = new CustomTabActivityHelper();
        newsModal = getIntent().getParcelableExtra("DATA");
        description = newsModal.description;




        titleImage = (ImageView)findViewById(R.id.appbar_image);
        parentLayout = (LinearLayout)findViewById(R.id.parent_layout);
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        webView = (WebView) parentLayout.findViewById(R.id.web_view);
        title = (TextView) parentLayout.findViewById(R.id.news_heading);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (newsModal.content!=null) {
            Picasso.with(this).load(newsModal.content).fit().centerCrop().into(titleImage);
        }
        if (newsModal.title!=null) {
            title.setText(newsModal.title);
        }

        Document doc =  Jsoup.parse(description);
        doc.select("div[data-embed-type=video]").remove();
        doc.head().getElementsByTag("link").remove();
        doc.head().appendElement("link").attr("rel", "stylesheet").attr("type", "text/css").attr("href", "style.css");




    /*    Elements elements =  doc.getAllElements();
        StringBuilder str = new StringBuilder();

        for(Element element : elements){
            String tag = element.tagName();




            if(tag.equals("figure")||tag.equals("img")){
                ImageView imageview = new ImageView(getContext());
                parentLayout.addView(imageview);
                if (element.attr("src")!=null&& !element.attr("src").equals("")) {
                    Picasso.with(getContext()).load(element.attr("src")).into(imageview);
                }



            }
            else if(tag.equals("p")){
                TextView textview = new TextView(getContext());
                parentLayout.addView(textview);
                textview.setText(element.text());
            }
        } */
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                //  ((MainActivity)(getActivity())).loadWebView(url);

                runBrowser(url);


                return true;
            }
        });
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.loadDataWithBaseURL("file:///android_asset/.", doc.toString(), "text/html", "UTF-8",null);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            webView.setBackgroundColor(Color.argb(1, 0, 0, 0));
        }
        if (Build.VERSION.SDK_INT >= 19) {
            // chromium, enable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }






    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.game_news_menu,menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home :
                onBackPressed();
                return true;

            case R.id.internet :
                runBrowser(newsModal.link);



        }



        return super.onOptionsItemSelected(item);
    }


    void runBrowser(String url){
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
        CustomTabActivityHelper.openCustomTab(
                this, customTabsIntent, Uri.parse(url), new WebViewFallback());
    }





}
