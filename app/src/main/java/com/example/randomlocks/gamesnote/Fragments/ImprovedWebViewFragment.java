package com.example.randomlocks.gamesnote.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.R;

/**
 * Created by randomlocks on 6/17/2016.
 */
public class ImprovedWebViewFragment extends Fragment {

    private WebView mWebView;
    private boolean mIsWebViewAvailable;
    private String mUrl = null;
    private Toolbar mToolbar;
    private ProgressBar progressBar;

    /**
     * Creates a new fragment which loads the supplied url as soon as it can
     *
     *
     */
    public ImprovedWebViewFragment() {

    }


    public static ImprovedWebViewFragment newInstance(String string) {

        Bundle args = new Bundle();
        args.putString(GiantBomb.BASE_URL,string);
        ImprovedWebViewFragment fragment = new ImprovedWebViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mUrl = getArguments().getString(GiantBomb.BASE_URL);
    }

    /**
     * Called to instantiate the view. Creates and returns the WebView.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.webview,container,false);

        if (mWebView != null) {
            mWebView.destroy();
        }
        mWebView = (WebView) view.findViewById(R.id.web_view);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setMax(100);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(view.getTitle());
                ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(view.getUrl());


                progressBar.setProgress(newProgress);
            }


        });
        mWebView.loadUrl(mUrl);
        mIsWebViewAvailable = true;
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        return view;
    }

    /**
     * Convenience method for loading a url. Will fail if {@link View} is not initialised (but won't throw an {@link Exception})
     *
     * @param url
     */
    public void loadUrl(String url) {
        if (mIsWebViewAvailable) getWebView().loadUrl(mUrl = url);
        else
            Toaster.make(getContext(),"cannot load page");
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
    @Override
    public void onDestroyView() {
        mIsWebViewAvailable = false;
        super.onDestroyView();
    }

    /**
     * Called when the fragment is no longer in use. Destroys the internal state of the WebView.
     */
    @Override
    public void onDestroy() {
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

    /* To ensure links open within the application */
    private class InnerWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }



    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(item.getItemId()==android.R.id.home){
            getActivity().onBackPressed();
        }


        return true;
    }
}
