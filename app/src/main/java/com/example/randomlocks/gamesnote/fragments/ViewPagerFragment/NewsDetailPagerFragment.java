package com.example.randomlocks.gamesnote.fragments.ViewPagerFragment;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.fragments.NewsDetailFragment;
import com.example.randomlocks.gamesnote.helperClass.GiantBomb;
import com.example.randomlocks.gamesnote.helperClass.SharedPreference;
import com.example.randomlocks.gamesnote.helperClass.Toaster;
import com.example.randomlocks.gamesnote.helperClass.WebViewHelper.CustomTabActivityHelper;
import com.example.randomlocks.gamesnote.helperClass.WebViewHelper.VideoEnabledWebChromeClient;
import com.example.randomlocks.gamesnote.helperClass.WebViewHelper.VideoEnabledWebView;
import com.example.randomlocks.gamesnote.helperClass.WebViewHelper.WebViewFallback;
import com.example.randomlocks.gamesnote.modals.newsModal.NewsModal;
import com.github.clans.fab.FloatingActionMenu;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by randomlock on 7/21/2017.
 */

public class NewsDetailPagerFragment extends Fragment implements View.OnClickListener, FloatingActionMenu.OnMenuToggleListener {


    public static final String TITLE = "title";
    public static final String IMAGE_URL = "image_url";
    public static final String LINK = "news_link";
    public static final String DESCRIPTION = "news_description";
    private static final String CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome";
    private static String SMART_VIEW = "smart_view";
    TextView title;
    VideoEnabledWebView webView;
    VideoEnabledWebChromeClient webChromeClient;
    LinearLayout parentLayout;
    ImageView titleImage;
    NewsModal newsModal;
    CardView cardView;
    CustomTabActivityHelper customTabActivityHelper;
    ViewGroup videoLayout;
    View loadingView;
    boolean isSmartView;
    FloatingActionMenu floatingActionMenu;
    Toolbar toolbar;


    public NewsDetailPagerFragment() {
        // Required empty public constructor
    }


    public static NewsDetailPagerFragment newInstance(NewsModal modal) {

        Bundle args = new Bundle();
        args.putParcelable(GiantBomb.MODAL, modal);
        NewsDetailPagerFragment fragment = new NewsDetailPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("tag1", "oncreate");
        setHasOptionsMenu(true);
        newsModal = getArguments().getParcelable(GiantBomb.MODAL);
        isSmartView = SharedPreference.getFromSharedPreferences(SMART_VIEW, true, getContext());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pager_news_detail, container, false);
        floatingActionMenu = (FloatingActionMenu) view.findViewById(R.id.floating_menu);
        floatingActionMenu.setOnMenuToggleListener(this);
        floatingActionMenu.findViewById(R.id.share).setOnClickListener(this);
        floatingActionMenu.findViewById(R.id.view_in_browser).setOnClickListener(this);
        parentLayout = (LinearLayout) view.findViewById(R.id.parent_layout);
        title = (TextView) parentLayout.findViewById(R.id.news_heading);
        cardView = (CardView) parentLayout.findViewById(R.id.image_card);
        titleImage = (ImageView) parentLayout.findViewById(R.id.appbar_image);
        if (newsModal.content != null) {
            Picasso.with(getContext()).load(newsModal.content).into(titleImage, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {

                    cardView.setVisibility(View.GONE);
                }
            });
        } else {

            cardView.setVisibility(View.GONE);
        }

        if (newsModal.title != null) {
            title.setText(newsModal.title);
        }

        if (newsModal.description != null) {
            if (isSmartView) {
                new ParseJsoup().execute(newsModal.description);
            } else {
                setUpWebView();
            }
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    private void setUpWebView() {
        webView = (VideoEnabledWebView) parentLayout.findViewById(R.id.web_view);
        webView.setVisibility(View.VISIBLE);
        videoLayout = (ViewGroup) getActivity().findViewById(R.id.videoLayout); // Your own view, read class comments
        loadingView = getActivity().getLayoutInflater().inflate(R.layout.view_loading_video, null); // Your own view, read class comments
        webView = (VideoEnabledWebView) parentLayout.findViewById(R.id.web_view);
        webChromeClient = new VideoEnabledWebChromeClient(parentLayout, videoLayout, loadingView, webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {


                try {
                    runBrowser(url);
                } catch (Exception e) {
                }


                return true;
            }

        });
        StringBuilder builder = null;
        String color;
        builder = new StringBuilder(newsModal.description.length() + 100);
        int night_mode = AppCompatDelegate.getDefaultNightMode();
        if (night_mode == AppCompatDelegate.MODE_NIGHT_YES) {
            color = "white";
        } else
            color = "black";

        builder.append("<HTML><HEAD><meta name=viewport content=target-densitydpi=medium-dpi, width=device-width/><script type=\"text/javascript\" src=\"https://platform.twitter.com/widgets.js\"></script><LINK href=\"style.css\" type=\"text/css\" rel=\"stylesheet\"/></HEAD><body style=\"color:").append(color).append(";\"max-width: 100%;>");
        builder.append(newsModal.description);
        builder.append("</body></HTML>");
        webView.loadDataWithBaseURL("file:///android_asset/.", builder.toString(), "text/html", "UTF-8", null);
        webView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.webviewbackground));

        webChromeClient.setOnToggledFullscreen(new VideoEnabledWebChromeClient.ToggledFullscreenCallback() {
            @Override
            public void toggledFullscreen(boolean fullscreen) {
                // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
                if (fullscreen) {
                    WindowManager.LayoutParams attrs = getActivity().getWindow().getAttributes();
                    attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getActivity().getWindow().setAttributes(attrs);
                    //noinspection all
                    getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                } else {
                    WindowManager.LayoutParams attrs = getActivity().getWindow().getAttributes();
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getActivity().getWindow().setAttributes(attrs);
                    //noinspection all
                    getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                }

            }
        });
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem checkable = menu.findItem(R.id.checkable_menu);
        checkable.setChecked(isSmartView);

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.game_news_detail_menu, menu);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                return true;

            case R.id.checkable_menu:
                isSmartView = !isSmartView;
                item.setChecked(isSmartView);
                String str = isSmartView ? "enabled" : "disabled";
                Toaster.make(getContext(), "Smart view wil be " + str + " after going back");
                SharedPreference.saveToSharedPreference(SMART_VIEW, isSmartView, getContext());
                return true;


        }

        return super.onOptionsItemSelected(item);
    }


    void runBrowser(String url) {
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().setShowTitle(true).addDefaultShareMenuItem().build();
        CustomTabActivityHelper.openCustomTab(
                getActivity(), customTabsIntent, Uri.parse(url), new WebViewFallback());
    }


    @Override
    public void onPause() {
        super.onPause();
        if (webView != null) {
            webView.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();
        }
    }

    private TextView getTextView(String text) {
        if (getContext() == null)
            Log.d("tag1", "null");
        else
            Log.d("tag1", "not null");
        TextView textView = new TextView(getContext(), null, R.style.SubTitleText);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = (int) GiantBomb.dipToPixels(getContext(), 12);
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
        Resources.Theme theme = getContext().getTheme();
        theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
        TypedArray arr =
                getContext().obtainStyledAttributes(typedValue.data, new int[]{
                        android.R.attr.textColorPrimary});
        int primaryColor = arr.getColor(0, -1);
        arr.recycle();
        return primaryColor;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share:
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                // Add data to the intent, the receiving app will decide
                // what to do with it.
                share.putExtra(Intent.EXTRA_SUBJECT, newsModal.title);
                share.putExtra(Intent.EXTRA_TEXT, newsModal.link);

                startActivity(Intent.createChooser(share, "Share news..."));
                break;
            case R.id.view_in_browser:
                runBrowser(newsModal.link);
                break;
            default:
                break;
        }
    }

    @Override
    public void onMenuToggle(boolean opened) {
        NewsDetailFragment fragment = (NewsDetailFragment) getParentFragment();
        fragment.animateToolbar(opened);

    }

    private class ParseJsoup extends AsyncTask<String, String, Void> {

        int i = 0;

        @Override
        protected Void doInBackground(String... params) {
            String desc = params[0];

            Document document = Jsoup.parse(desc);
            if (document != null) {
                Elements elements = document.select("*");

                for (Element element : elements) {

                    if (element.tagName().equals("p")) {
                        i++;
                        publishProgress(element.text(), String.valueOf(true));
                    } else if (element.tagName().equals("figure") || element.tagName().equals("img")) {
                        if (element.hasAttr("src")) {
                            publishProgress(element.absUrl("src"), String.valueOf(false));
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
                CardView cardView = new CardView(getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER;
                int margin = (int) GiantBomb.dipToPixels(getContext(), 12);
                params.setMargins(margin, margin, margin, margin);
                cardView.setLayoutParams(params);
                cardView.setRadius(GiantBomb.dipToPixels(getContext(), 4));
                ImageView imageView = new ImageView(getContext());
                imageView.setLayoutParams(new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setAdjustViewBounds(true);
                if (!result.isEmpty()) {
                    Picasso.with(getContext()).load(result).placeholder(R.drawable.news_image_drawable).into(imageView);
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
            View view = new View(getContext());
            int height = (int) GiantBomb.dipToPixels(getContext(), getResources().getDimension(R.dimen.line));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            int margin = (int) GiantBomb.dipToPixels(getContext(), 12);
            params.setMargins(0, margin, 0, margin);
            view.setLayoutParams(params);
            view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.linecolor));
            parentLayout.addView(view);
        }
    }
}