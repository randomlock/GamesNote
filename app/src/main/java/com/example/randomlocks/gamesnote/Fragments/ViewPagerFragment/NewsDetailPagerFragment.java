package com.example.randomlocks.gamesnote.Fragments.ViewPagerFragment;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
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

import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.SharedPreference;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.HelperClass.WebViewHelper.CustomTabActivityHelper;
import com.example.randomlocks.gamesnote.HelperClass.WebViewHelper.VideoEnabledWebChromeClient;
import com.example.randomlocks.gamesnote.HelperClass.WebViewHelper.VideoEnabledWebView;
import com.example.randomlocks.gamesnote.HelperClass.WebViewHelper.WebViewFallback;
import com.example.randomlocks.gamesnote.Modal.NewsModal.NewsModal;
import com.example.randomlocks.gamesnote.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by randomlock on 7/21/2017.
 */

public class NewsDetailPagerFragment extends Fragment {


    public static final String TITLE = "title";
    public static final String IMAGE_URL = "image_url";
    public static final String LINK = "news_link";
    public static final String DESCRIPTION = "news_description";
    private static final String CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome";
    private static String SMART_VIEW = "smart_view";
    String description;
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
        setHasOptionsMenu(true);
        isSmartView = SharedPreference.getFromSharedPreferences(SMART_VIEW, true, getContext());
        Toaster.make(getContext(), isSmartView + "");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pager_news_detail, container, false);
        newsModal = getArguments().getParcelable(GiantBomb.MODAL);
        if (newsModal != null) {
            description = newsModal.description;
        }
        parentLayout = (LinearLayout) view.findViewById(R.id.parent_layout);
        title = (TextView) parentLayout.findViewById(R.id.news_heading);
        cardView = (CardView) parentLayout.findViewById(R.id.image_card);
        titleImage = (ImageView) parentLayout.findViewById(R.id.appbar_image);


        if (newsModal.content != null) {
            Toaster.make(getContext(), "hello");
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
                webView.setVisibility(View.VISIBLE);
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
        builder = new StringBuilder(description.length() + 100);
        int night_mode = AppCompatDelegate.getDefaultNightMode();
        if (night_mode == AppCompatDelegate.MODE_NIGHT_YES) {
            color = "white";
        } else
            color = "black";

        builder.append("<HTML><HEAD><meta name=viewport content=target-densitydpi=medium-dpi, width=device-width/><script type=\"text/javascript\" src=\"https://platform.twitter.com/widgets.js\"></script><LINK href=\"style.css\" type=\"text/css\" rel=\"stylesheet\"/></HEAD><body style=\"color:").append(color).append(";\"max-width: 100%;>");
        builder.append(description);
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
        Drawable drawable = menu.findItem(R.id.internet).getIcon();
        if (drawable != null && AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
            drawable.mutate();
            drawable.setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_ATOP);
        }
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
        SharedPreference.saveToSharedPreference(SMART_VIEW, isSmartView, getContext());
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
        TextView textView = new TextView(getActivity(), null, R.style.SubTitleText);
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