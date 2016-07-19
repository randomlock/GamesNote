package com.example.randomlocks.gamesnote.Activity;

import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.HelperClass.WebViewHelper.CustomTabActivityHelper;
import com.example.randomlocks.gamesnote.HelperClass.WebViewHelper.WebViewFallback;
import com.example.randomlocks.gamesnote.Modal.NewsModal.NewsModal;
import com.example.randomlocks.gamesnote.R;
import com.squareup.picasso.Picasso;

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

      /*  Document doc =  Jsoup.parse(description);
        doc.select("div[data-embed-type=video]").remove();
        doc.head().getElementsByTag("link").remove();
        doc.head().appendElement("link").attr("rel", "stylesheet").attr("type", "text/css").attr("href", "style.css");*/
        StringBuilder builder = null;
        if (newsModal.description != null) {
            String color;
            builder = new StringBuilder(description.length() + 100);
            int night_mode = AppCompatDelegate.getDefaultNightMode();
            if (night_mode == AppCompatDelegate.MODE_NIGHT_YES) {
                color = "white";
            } else
                color = "black";

            builder.append("<HTML><HEAD><LINK href=\"style.css\" type=\"text/css\" rel=\"stylesheet\"/></HEAD><body style=\"color:").append(color).append(";\">");
            builder.append(description);
            builder.append("</body></HTML>");
        }




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
        if (builder != null) {
            webView.loadDataWithBaseURL("file:///android_asset/.", builder.toString(), "text/html", "UTF-8", null);
        }
        webView.setBackgroundColor(ContextCompat.getColor(this, R.color.webviewbackground));

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            webView.setBackgroundColor(Color.argb(1, 0, 0, 0));
        }
    /*    if (Build.VERSION.SDK_INT >= 19) {
            // chromium, enable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        } */






    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.game_news_detail_menu, menu);
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
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().setShowTitle(true).addDefaultShareMenuItem().build();
        CustomTabActivityHelper.openCustomTab(
                this, customTabsIntent, Uri.parse(url), new WebViewFallback());
    }





}
