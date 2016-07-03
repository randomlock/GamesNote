package com.example.randomlocks.gamesnote.Fragments;

import android.content.ComponentName;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.HelperClass.WebViewHelper.CustomTabActivityHelper;
import com.example.randomlocks.gamesnote.HelperClass.WebViewHelper.WebViewFallback;
import com.example.randomlocks.gamesnote.MainActivity;
import com.example.randomlocks.gamesnote.R;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by randomlocks on 7/1/2016.
 */
public class NewsDetailFragment extends Fragment {

    private static final String TITLE = "title" ;
    private static final String IMAGE_URL = "image_url" ;
    public static final String CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome";

    String description;
    TextView title;
    WebView webView;
    LinearLayout parentLayout;
    ImageView titleImage;
CustomTabActivityHelper customTabActivityHelper;



    private static final String DESCRIPTION = "news_description";

    public NewsDetailFragment(){

    }

    public static NewsDetailFragment newInstance(String description,String imageUrl,String title) {

        Bundle args = new Bundle();
        args.putString(DESCRIPTION,description);
        args.putString(TITLE,title);
        args.putString(IMAGE_URL,imageUrl);
        NewsDetailFragment fragment = new NewsDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        description = getArguments().getString(DESCRIPTION);
        customTabActivityHelper = new CustomTabActivityHelper();
    }

    @Override
    public void onStart() {
        super.onStart();
        customTabActivityHelper.bindCustomTabsService(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        customTabActivityHelper.unbindCustomTabsService(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.news_detail,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        titleImage = (ImageView) getView().findViewById(R.id.appbar_image);
        parentLayout = (LinearLayout) getView().findViewById(R.id.parent_layout);
        webView = (WebView) getView().findViewById(R.id.web_view);
        title = (TextView) getView().findViewById(R.id.news_heading);

        if (getArguments().getString(IMAGE_URL)!=null) {
            Picasso.with(getContext()).load(getArguments().getString(IMAGE_URL)).fit().centerCrop().into(titleImage);
        }
        title.setText(getArguments().getString(TITLE));

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

                CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
                CustomTabActivityHelper.openCustomTab(
                        getActivity(), customTabsIntent, Uri.parse(url), new WebViewFallback());


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




}
