package com.example.randomlocks.gamesnote.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.helperClass.GiantBomb;

import es.dmoral.toasty.Toasty;

public class ImprovedWebViewActivity extends AppCompatActivity {

    private WebView mWebView;
    private boolean mIsWebViewAvailable;
    private String mUrl = null;
    private Toolbar mToolbar;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_improved_web_view);
        mUrl = getIntent().getStringExtra(GiantBomb.BASE_URL);

        if (mWebView != null) {
            mWebView.destroy();
        }
        mWebView = (WebView) findViewById(R.id.web_view);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(100);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
                    mWebView.goBack();
                    return true;
                }
                return false;
            }


        });
        mWebView.setWebViewClient(new InnerWebViewClient()); // forces it to open in app
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

                getSupportActionBar().setTitle(view.getTitle());
                getSupportActionBar().setSubtitle(view.getUrl());


                progressBar.setProgress(newProgress);
            }


        });
        mWebView.loadUrl(mUrl);
        mIsWebViewAvailable = true;
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);

    }


    public void loadUrl(String url) {
        if (mIsWebViewAvailable) getWebView().loadUrl(mUrl = url);
        else
        Toasty.info(this,"cannot load page", Toast.LENGTH_SHORT,true).show();
    }

    /**
     * Called when the fragment is visible to the user and actively running. Resumes the WebView.
     */
    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    /**
     * Called when the fragment is no longer resumed. Pauses the WebView.
     */
    @Override
    public void onResume() {
        mWebView.onResume();
        super.onResume();
    }

    /**
     * Called when the WebView has been detached from the fragment.
     * The WebView is no longer available after this time.
     */


    /**
     * Called when the fragment is no longer in use. Destroys the internal state of the WebView.
     */
    @Override
    public void onDestroy() {
        mIsWebViewAvailable = false;
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    /**
     * Gets the WebView.
     */
    public WebView getWebView() {
        return mIsWebViewAvailable ? mWebView : null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }


        return true;
    }

    /* To ensure links open within the application */
    private class InnerWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }


    }


}
