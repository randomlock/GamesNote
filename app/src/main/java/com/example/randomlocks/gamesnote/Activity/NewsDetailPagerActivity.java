package com.example.randomlocks.gamesnote.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.PagerZoomOutSlideAnimation;
import com.example.randomlocks.gamesnote.HelperClass.WebViewHelper.CustomTabActivityHelper;
import com.example.randomlocks.gamesnote.HelperClass.WebViewHelper.VideoEnabledWebChromeClient;
import com.example.randomlocks.gamesnote.HelperClass.WebViewHelper.VideoEnabledWebView;
import com.example.randomlocks.gamesnote.HelperClass.WebViewHelper.WebViewFallback;
import com.example.randomlocks.gamesnote.Modal.NewsModal.NewsModal;
import com.example.randomlocks.gamesnote.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsDetailPagerActivity extends AppCompatActivity {


    ViewPager viewPager;
    List<NewsModal> modalList;
    int position;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        position = getIntent().getExtras().getInt(GiantBomb.POSITION);
        modalList = getIntent().getExtras().getParcelableArrayList(GiantBomb.MODAL);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        viewPager.setAdapter(new NewsDetailPagerAdapter(getSupportFragmentManager(), modalList));
        viewPager.setPageTransformer(false, new PagerZoomOutSlideAnimation());
        viewPager.setCurrentItem(position, true);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public static class NewsDetailPagerAdapter extends FragmentStatePagerAdapter {

        List<NewsModal> modalList;

        public NewsDetailPagerAdapter(FragmentManager fm, List<NewsModal> modalList) {
            super(fm);
            this.modalList = modalList;
        }

        @Override
        public Fragment getItem(int position) {
            return GameNewsDetailFragment.newInstance(modalList.get(position));
        }

        @Override
        public int getCount() {
            return modalList.size();
        }

    }


    public static class GameNewsDetailFragment extends Fragment {


        public static final String TITLE = "title";
        public static final String IMAGE_URL = "image_url";
        private static final String CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome";
        public static final String LINK = "news_link";
        public static final String DESCRIPTION = "news_description";

        String description;
        TextView title;
        VideoEnabledWebView webView;
        VideoEnabledWebChromeClient webChromeClient;
        LinearLayout parentLayout;
        ImageView titleImage;
        NewsModal newsModal;
        CardView cardView;
        Button button;
        CustomTabActivityHelper customTabActivityHelper;
        ViewGroup videoLayout;
        View loadingView;


        public GameNewsDetailFragment() {
            // Required empty public constructor
        }


        public static GameNewsDetailFragment newInstance(NewsModal modal) {

            Bundle args = new Bundle();
            args.putParcelable(GiantBomb.MODAL, modal);
            GameNewsDetailFragment fragment = new GameNewsDetailFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);

        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_game_news_detail, container, false);
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            newsModal = getArguments().getParcelable(GiantBomb.MODAL);
            if (newsModal != null) {
                description = newsModal.description;
            }
            parentLayout = (LinearLayout) getView().findViewById(R.id.parent_layout);
            videoLayout = (ViewGroup) getActivity().findViewById(R.id.videoLayout); // Your own view, read class comments
            loadingView = getActivity().getLayoutInflater().inflate(R.layout.view_loading_video, null); // Your own view, read class comments
            webChromeClient = new VideoEnabledWebChromeClient(parentLayout, videoLayout, loadingView, webView);
            title = (TextView) parentLayout.findViewById(R.id.news_heading);
            cardView = (CardView) parentLayout.findViewById(R.id.image_card);
            titleImage = (ImageView) cardView.findViewById(R.id.appbar_image);
            webView = (VideoEnabledWebView) parentLayout.findViewById(R.id.web_view);
            button = (Button) parentLayout.findViewById(R.id.view_in_browser1);


            webChromeClient.setOnToggledFullscreen(new VideoEnabledWebChromeClient.ToggledFullscreenCallback() {
                @Override
                public void toggledFullscreen(boolean fullscreen) {
                    // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
                    if (fullscreen) {
                        WindowManager.LayoutParams attrs = getActivity().getWindow().getAttributes();
                        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                        attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                        getActivity().getWindow().setAttributes(attrs);
                        if (android.os.Build.VERSION.SDK_INT >= 14) {
                            //noinspection all
                            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                        }
                    } else {
                        WindowManager.LayoutParams attrs = getActivity().getWindow().getAttributes();
                        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                        attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                        getActivity().getWindow().setAttributes(attrs);
                        if (android.os.Build.VERSION.SDK_INT >= 14) {
                            //noinspection all
                            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                        }
                    }

                }
            });


            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    runBrowser(newsModal.link);
                }
            });

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


            StringBuilder builder = null;
            if (newsModal.description != null) {
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
            if (builder != null) {
                webView.loadDataWithBaseURL("file:///android_asset/.", builder.toString(), "text/html", "UTF-8", null);
            }
            webView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.webviewbackground));

            /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                webView.setBackgroundColor(Color.argb(1, 0, 0, 0));
            }*/
    /*    if (Build.VERSION.SDK_INT >= 19) {
            // chromium, enable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        } */
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
                    getActivity().onBackPressed();
                    return true;

                case R.id.internet:
                    runBrowser(newsModal.link);
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
            webView.onPause();
        }

        @Override
        public void onResume() {
            super.onResume();
            webView.onResume();
        }

    }


}
