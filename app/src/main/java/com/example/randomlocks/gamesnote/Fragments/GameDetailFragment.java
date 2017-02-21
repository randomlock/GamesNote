package com.example.randomlocks.gamesnote.Fragments;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.randomlocks.gamesnote.Activity.GameDetailActivity;
import com.example.randomlocks.gamesnote.Adapter.CharacterDetailImageAdapter;
import com.example.randomlocks.gamesnote.Adapter.GameDetailCharacterAdapter;
import com.example.randomlocks.gamesnote.Adapter.GameDetailOverviewAdapter;
import com.example.randomlocks.gamesnote.Adapter.GameDetailVideoCharacter;
import com.example.randomlocks.gamesnote.Adapter.SimilarGameAdapter;
import com.example.randomlocks.gamesnote.AsyncTask.JsoupCharacters;
import com.example.randomlocks.gamesnote.AsyncTask.JsoupGames;
import com.example.randomlocks.gamesnote.DialogFragment.AddToBottomFragment;
import com.example.randomlocks.gamesnote.DialogFragment.FontOptionFragment;
import com.example.randomlocks.gamesnote.DialogFragment.ReviewListDialogFragment;
import com.example.randomlocks.gamesnote.HelperClass.CustomView.AVLoadingIndicatorView;
import com.example.randomlocks.gamesnote.HelperClass.CustomView.ConsistentLinearLayoutManager;
import com.example.randomlocks.gamesnote.HelperClass.CustomView.CustomMediaController;
import com.example.randomlocks.gamesnote.HelperClass.CustomView.CustomVideoView;
import com.example.randomlocks.gamesnote.HelperClass.CustomView.PicassoNestedScrollView;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.MyAnimation;
import com.example.randomlocks.gamesnote.HelperClass.SharedPreference;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.HelperClass.WebViewHelper.CustomTabActivityHelper;
import com.example.randomlocks.gamesnote.HelperClass.WebViewHelper.WebViewFallback;
import com.example.randomlocks.gamesnote.Interface.GameDetailVideoInterface;
import com.example.randomlocks.gamesnote.Interface.GameWikiDetailInterface;
import com.example.randomlocks.gamesnote.Modal.GameCharacterModal.CharacterImage;
import com.example.randomlocks.gamesnote.Modal.GameDetailModal.CharacterGamesImage;
import com.example.randomlocks.gamesnote.Modal.GameDetailModal.GameDetailCharacters;
import com.example.randomlocks.gamesnote.Modal.GameDetailModal.GameDetailListModal;
import com.example.randomlocks.gamesnote.Modal.GameDetailModal.GameDetailModal;
import com.example.randomlocks.gamesnote.Modal.GameDetailModal.GameDetailSimilarGames;
import com.example.randomlocks.gamesnote.Modal.GameDetailModal.GameDetailVideo;
import com.example.randomlocks.gamesnote.Modal.GameWikiPlatform;
import com.example.randomlocks.gamesnote.Modal.GamesVideoModal.GameVideoMinimal;
import com.example.randomlocks.gamesnote.Modal.GamesVideoModal.GameVideoModalMinimal;
import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.RealmDatabase.GameListDatabase;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.randomlocks.gamesnote.HelperClass.GiantBomb.MODAL;

/**
 * A simple {@link Fragment} subclass.
 */


/****
 * optimize findviewbyid
 ************/


public class GameDetailFragment extends Fragment implements FontOptionFragment.FontOptionInterface, View.OnClickListener, ReviewListDialogFragment.CommunicationInterface, AddToBottomFragment.AddToBottomInterface {


    public static final String API_URL = "apiUrl";
    public static final String IMAGE_URL = "imageUrl";
    public static final String NAME = "name";
    Realm realm;
    int list_category;
    Toolbar toolbar;
    String apiUrl, imageUrl;
    GameWikiDetailInterface gameWikiDetailInterface;
    Map<String, String> map = null;
    Map<String,String> videoMap;
    GameDetailModal gameDetailModal = null;
    CollapsingToolbarLayout toolbarLayout;
    AppBarLayout appBarLayout;
    RecyclerView recyclerView, similarGameRecycleView, characterRecycleView, imageRecycleView, videoRecyclerView;
    PicassoNestedScrollView nestedScrollView;
    TextView description;
    GameDetailOverviewAdapter adapter;
    int style;
    List<CharacterGamesImage> characterImage = null, similarGameImage = null;
    List<GameWikiPlatform> platforms = null;

    CoordinatorLayout coordinatorLayout;
    SimilarGameAdapter similarGameAdapter;
    GameDetailCharacterAdapter characterAdapter;
    CommunicationInterface mCommunicationInterface;
    JsoupCharacters asyncCharacters;
    JsoupGames asyncGames;
    ImageView appbarImage;
    TextView  review, userReview;
    TextView status , score , platform , hours;
    RelativeLayout relativeLayout;
    FloatingActionMenu floatingActionsMenu;
    FloatingActionButton replaying, planning, dropped, playing, completed;
    String title;
    AVLoadingIndicatorView pacman;
    LinearLayout parentLayout;
    CardView statsCardView;
    LinearLayout statsDetailView;
    GameListDatabase statsDatabase;
    ImageView toggleArrow;
    View mDimmerView;
    Matrix matrix=new Matrix();

    GameDetailVideoInterface videoInterface;
    CustomVideoView videoView;
    CustomMediaController media_Controller;
    View playViewButton;
    ProgressBar videoProgress;
    private int stopPosition;
    private DisplayMetrics metrics;
    private String video_url;
    int current_video_pos=0;


    public GameDetailFragment() {
        // Required empty public constructor

    }


    public void getInit(final GameDetailVideo gameDetailVideo) {
        videoProgress = (ProgressBar) toolbarLayout.findViewById(R.id.video_progress);
        videoProgress.setVisibility(View.VISIBLE);
        //geting video url from the api
        if (videoMap==null) {
            videoMap = new HashMap<>();
            videoMap.put(GiantBomb.KEY, GiantBomb.API_KEY);
            String field_list = "high_url,low_url";
            videoMap.put(GiantBomb.FIELD_LIST, field_list);
            videoMap.put(GiantBomb.FORMAT, "JSON");
        }

        String path = gameDetailVideo.apiDetailUrl;
        String str[] = path.split("/");
        final String videoUrl = str[str.length - 1];
        videoInterface = GiantBomb.createGameDetailVideoService();
        videoInterface.getResult(videoUrl,videoMap).enqueue(new Callback<GameVideoMinimal>() {
            @Override
            public void onResponse(Call<GameVideoMinimal> call, Response<GameVideoMinimal> response) {
                GameVideoModalMinimal videoModal = response.body().results;
                video_url = videoModal.highUrl + "?api_key=" + GiantBomb.API_KEY;

                if(getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE){
                    mCommunicationInterface.onVideoClick(video_url,false,0);
                    return;
                }
/*
                if (videoView==null) {
*/
                    videoView = (CustomVideoView) toolbarLayout.findViewById(R.id.play_video_texture);
                    final View videoDim =  nestedScrollView.findViewById(R.id.video_dimmer_view);
                    media_Controller = new CustomMediaController(getContext(),false);


                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) videoView.getLayoutParams();
                    params.width =  metrics.widthPixels;
                    params.height = metrics.heightPixels / 3;
                    params.leftMargin = 0;
                    params.rightMargin = 0;
                    videoView.setLayoutParams(params);

                    media_Controller.setAnchorView(videoView);
                    media_Controller.setPrevNextListeners(new View.OnClickListener() {
                        //for next video
                        @Override
                        public void onClick(View view) {
                            if (current_video_pos+1 < gameDetailModal.videos.size()) {
                                getInit(gameDetailModal.videos.get(++current_video_pos));
                            }else {
                                Toaster.make(getContext(),"no next video");
                            }
                        }
                    }, //for prev video
                            new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(current_video_pos>0){
                                getInit(gameDetailModal.videos.get(--current_video_pos));
                            }else {
                                Toaster.make(getContext(),"no previous video");
                            }
                        }
                    });
                    videoView.setMediaController(media_Controller);
                    videoView.setVideoPath(video_url);
                    videoView.requestFocus();
                    videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        // Close the progress bar and play the video
                        public void onPrepared(MediaPlayer mp) {
                            videoProgress.setVisibility(View.GONE);
                            videoView.start();
                        }
                    });

                    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            animateToolbar();
                        }
                    });

                    videoView.setPlayPauseListener(new CustomVideoView.PlayPauseListener() {
                        @Override
                        public void onPlay() {
                            animateToolbar();
                        }

                        @Override
                        public void onPause() {
                            animateToolbar();
                        }
                    });


                    media_Controller.setListener(new CustomMediaController.OnMediaControllerInteractionListener() {
                        @Override
                        public void onRequestFullScreen() {
                            int seek_position = videoView.getCurrentPosition();
                           mCommunicationInterface.onVideoClick(video_url,false,seek_position);
                        }

                        @Override
                        public void onRequestDimmerView() {
                            if (videoDim.getAlpha()==0) {
                                videoDim.animate().alpha(1).withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        floatingActionsMenu.hideMenuButton(true);

                                    }
                                });
                                //setBackgroundDimming(videoDim,true);
                            }else {
                                videoDim.animate().alpha(0).withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        floatingActionsMenu.showMenuButton(true);

                                    }
                                });
                                //setBackgroundDimming(videoDim,false);
                            }
                        }
                    });
               /* }else {
                    videoView.stopPlayback();
                    videoView.setVideoPath(videoUrl);
                    videoView.start();
                    videoProgress.setVisibility(View.GONE);

                }*/


            }

            @Override
            public void onFailure(Call<GameVideoMinimal> call, Throwable t) {
                Toaster.makeSnackbar(coordinatorLayout,"error cannot play");
            }
        });

    }

    public void animateToolbar(){
        float alpha = toolbar.getAlpha();
        if (alpha == 1) {
            toolbar.animate().alpha(0).setDuration(200);
        } else {
            toolbar.animate().alpha(1).setDuration(200);

        }
    }






    @Override
    public void onClick(View v) {

        floatingActionsMenu.close(true);

        switch (v.getId()) {

            case R.id.replaying:
                list_category = GiantBomb.REPLAYING;
                break;
            case R.id.planning:
                list_category = GiantBomb.PLANNING;
                break;
            case R.id.dropped:
                list_category = GiantBomb.DROPPED;
                break;
            case R.id.playing:
                list_category = GiantBomb.PLAYING;
                break;
            case R.id.completed:
                list_category = GiantBomb.COMPLETED;
                break;

            case R.id.stats_cardview :
                if(statsDetailView.getVisibility()==View.VISIBLE){
                    MyAnimation.collapse(statsDetailView,getContext());
                    matrix.postRotate((float) 180, toggleArrow.getWidth()/2, toggleArrow.getHeight()/2);
                    toggleArrow.setImageMatrix(matrix);
                }
                else{
                    MyAnimation.expand(statsDetailView,getContext());
                    matrix.postRotate((float) 180, toggleArrow.getWidth()/2, toggleArrow.getHeight()/2);
                    toggleArrow.setImageMatrix(matrix);
                }


                return;



        }

        if (gameDetailModal != null) {
            BottomSheetDialogFragment addToBottomFragment = AddToBottomFragment.newInstance(platforms);
            addToBottomFragment.setTargetFragment(this, 0);
            Toaster.make(getContext(),"hello");
            addToBottomFragment.show(getActivity().getSupportFragmentManager(), "addto");
        } else {
            Toaster.makeSnackbar(coordinatorLayout, "waiting to load data");
        }

    }

    //When adding game to the list
    @Override
    public void onAdd(int score, String startDate, String endDate, String platform) {

        RealmList<GameWikiPlatform> realmPlatformList = new RealmList<>();
        for(GameWikiPlatform p : platforms)
            realmPlatformList.add(p);
        final GameListDatabase gameListDatabase = new GameListDatabase(apiUrl,title,imageUrl,list_category,score,startDate,endDate,platform,realmPlatformList);
        /*gameListDatabase.setApiDetailUrl(apiUrl);
        gameListDatabase.setName(title);
        gameListDatabase.setImageUrl(imageUrl);
        gameListDatabase.setStatus(list_category);*/
        /*gameListDatabase.setGameplay_hours(" ");
        gameListDatabase.setMedium(" ");
        gameListDatabase.setPrice(" ");
        gameListDatabase.setScore(score);
        gameListDatabase.setStartDate(startDate);
        gameListDatabase.setEndDate(endDate);
        gameListDatabase.setPlatform(platform);*/
      /*  RealmList<RealmString> platformList = new RealmList<>();
        for(GameWikiPlatform platform1 : platforms)
        platformList.add(new RealmString(platform1.name,platform1.abbreviation));*/
/*
        gameListDatabase.setPlatform_list(platformRealmList);
*/




        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insertOrUpdate(gameListDatabase);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Toaster.makeSnackbar(coordinatorLayout, "Game added");
            }
        });
    }




    public interface CommunicationInterface {
        void onReviewClick(String apiUrl, String gameTitle, String imageUrl);

        void onUserReviewClick(String apiUrl, String gameTitle, String imageUrl);

        void onVideoClick(String url,boolean needRequest,int seek_position);

    }


    public static GameDetailFragment newInstance(String apiUrl, String name, String imageUrl) {

        Bundle args = new Bundle();
        args.putString(API_URL, apiUrl);
        args.putString(NAME, name);
        args.putString(IMAGE_URL, imageUrl);
        GameDetailFragment fragment = new GameDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCommunicationInterface = (CommunicationInterface) getActivity();
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        String fullUrl = getArguments().getString(API_URL);
        String str[] = fullUrl.split("/");
        apiUrl = str[str.length - 1];
        title = getArguments().getString(NAME);
        imageUrl = getArguments().getString(IMAGE_URL);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        realm = Realm.getDefaultInstance();
        statsDatabase = realm.where(GameListDatabase.class).equalTo("apiDetailUrl",apiUrl).findFirstAsync();
        statsDatabase.addChangeListener(statsCallback);

        return inflater.inflate(R.layout.fragment_game_detail, container, false);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(videoView!=null && videoView.isPlaying()){
            if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
                mCommunicationInterface.onVideoClick(video_url,false,videoView.getCurrentPosition());
            }
        }


    }

    private RealmChangeListener statsCallback = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            if(statsDatabase.isLoaded()){
                if(statsDatabase.isValid()){
                    statsCardView.setVisibility(View.VISIBLE);
                    floatingActionsMenu.hideMenu(true);

                    //inflate views and update stats

                        status = (TextView) statsDetailView.findViewById(R.id.status_value);
                        score = (TextView) statsDetailView.findViewById(R.id.score_value);
                        platform = (TextView) statsDetailView.findViewById(R.id.platfom_value);
                        hours = (TextView) statsDetailView.findViewById(R.id.hours_value);

                        status.setText(getResources().getStringArray(R.array.status)[statsDatabase.getStatus()-1]);
                        score.setText(String.valueOf(statsDatabase.getScore()));
                        platform.setText(statsDatabase.getPlatform());
                        hours.setText(statsDatabase.getGameplay_hours());


                }

            }
        }

    };

    private void setBackgroundDimming(final View view, boolean dimmed) {
        final float targetAlpha = dimmed ? 1f : 0;
        final int endVisibility = dimmed ? View.VISIBLE : View.GONE;
        view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(targetAlpha)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        view.setVisibility(endVisibility);
                    }
                })
                .start();
    }




    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        /************************************** FindViewById *********************************/
        coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(R.id.root_coordinator);
        mDimmerView = coordinatorLayout.findViewById(R.id.dimmer_view);
        floatingActionsMenu = (FloatingActionMenu) coordinatorLayout.findViewById(R.id.floating_menu);
        coordinatorLayout.findViewById(R.id.replaying).setOnClickListener(this);
        coordinatorLayout.findViewById(R.id.planning).setOnClickListener(this);
        coordinatorLayout.findViewById(R.id.dropped).setOnClickListener(this);
        coordinatorLayout.findViewById(R.id.playing).setOnClickListener(this);
        coordinatorLayout.findViewById(R.id.completed).setOnClickListener(this);
        appBarLayout = (AppBarLayout) coordinatorLayout.findViewById(R.id.app_bar_layout);
        toolbarLayout = (CollapsingToolbarLayout) appBarLayout.findViewById(R.id.collapsing_toolbar_layout);

        appbarImage = (ImageView) toolbarLayout.findViewById(R.id.appbar_image);
         metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        appbarImage.getLayoutParams().height = metrics.heightPixels / 3 ;



        playViewButton = toolbarLayout.findViewById(R.id.play);
        playViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(gameDetailModal==null){
                    Toaster.makeSnackbar(coordinatorLayout,"waiting to load data");
                }else if(gameDetailModal.videos.size()==0){
                    Toaster.makeSnackbar(coordinatorLayout,"no video found");
                    playViewButton.setVisibility(View.GONE);
                } else {
                    appbarImage.setVisibility(View.INVISIBLE);
                    playViewButton.setVisibility(View.GONE);
                    getInit(gameDetailModal.videos.get(0));



                }



            }
        });
        toolbar = (Toolbar) toolbarLayout.findViewById(R.id.my_toolbar);
        nestedScrollView = (PicassoNestedScrollView) coordinatorLayout.findViewById(R.id.scroll_view);
        parentLayout = (LinearLayout) nestedScrollView.findViewById(R.id.parent_layout);
        statsCardView = (CardView) parentLayout.findViewById(R.id.stats_cardview);
        statsDetailView = (LinearLayout) statsCardView.findViewById(R.id.hide_parent_layout);
        toggleArrow = (ImageView) statsCardView.findViewById(R.id.stats_toggle);
        statsCardView.setOnClickListener(this);
        similarGameRecycleView = (RecyclerView) nestedScrollView.findViewById(R.id.similar_game_list);
        characterRecycleView = (RecyclerView) nestedScrollView.findViewById(R.id.character_game_list);
        description = (TextView) nestedScrollView.findViewById(R.id.description);
        recyclerView = (RecyclerView) nestedScrollView.findViewById(R.id.list);
        imageRecycleView = (RecyclerView) nestedScrollView.findViewById(R.id.image_recycler_view);
        videoRecyclerView = (RecyclerView) nestedScrollView.findViewById(R.id.video_recycler_view);
        review = (TextView) nestedScrollView.findViewById(R.id.review);
        userReview = (TextView) nestedScrollView.findViewById(R.id.user_review);
        pacman = (AVLoadingIndicatorView) nestedScrollView.findViewById(R.id.progressBar);

        style = selectStyle(SharedPreference.getFromSharedPreferences(GiantBomb.FONT, 0, getContext()) + 1);

        recyclerView.setNestedScrollingEnabled(false);
        characterRecycleView.setNestedScrollingEnabled(false);
        similarGameRecycleView.setNestedScrollingEnabled(false);


        floatingActionsMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                setBackgroundDimming(mDimmerView,opened);

            }
        });

        mDimmerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingActionsMenu.close(true);
            }
        });


        /************************** APPBARLATOUT ********************************/

        if (imageUrl != null) {
            Picasso.with(getContext()).load(imageUrl).fit().centerCrop().into(appbarImage);
        }

        appbarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        /***************************** TYPEFACE ************************************/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            description.setTextAppearance(style);
        } else {
            description.setTextAppearance(getContext(), style);
        }


        //Show toolbar icon after parallex is finished

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if(media_Controller!=null && media_Controller.isShowing())
                    media_Controller.hide();

                if (toolbarLayout.getHeight() + verticalOffset < 2 * ViewCompat.getMinimumHeight(toolbarLayout)) {

                    if(videoView!=null && videoView.isPlaying()){
                        videoView.pause();
                    }
                }
            }

        });

        /******************************* TOOLBAR *************************************/

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // toolbar.setTitle("Game Detail");   //to be changed to game title


        /**************** RETROFIT ************************/

        if (savedInstanceState != null) {
            Toaster.make(getContext(),"hell osave instance");
            gameDetailModal = savedInstanceState.getParcelable(MODAL);
            fillData(gameDetailModal);
        } else {
            if (gameDetailModal != null) {
                fillData(gameDetailModal);
            }else {
            Log.d("tag", "its null");
            gameWikiDetailInterface = GiantBomb.createGameDetailService();
            map = new HashMap<>();
            map.put(GiantBomb.KEY, GiantBomb.API_KEY);
            map.put(GiantBomb.FORMAT, "JSON");
            String field_list = "description,name,platforms,images,videos,characters,developers,franchises,genres,publishers,similar_games,themes,reviews,releases";
            map.put(GiantBomb.FIELD_LIST, field_list);
            getGameDetail(gameWikiDetailInterface, map);


            asyncCharacters = new JsoupCharacters(new JsoupCharacters.AsyncResponse() {
                @Override
                public void processFinish(List<CharacterGamesImage> characters) {

                    if (characters != null) {
                        if (characterRecycleView.getAdapter() != null) {
                            characterAdapter.setImages(characters);
                            characterImage = characters;
                        } else {
                            characterImage = characters;
                        }
                    }

                }
            });
            asyncCharacters.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://www.giantbomb.com/game/" + apiUrl + "/characters/");


            asyncGames = new JsoupGames(new JsoupGames.AsyncResponse() {
                @Override
                public void processFinish(List<CharacterGamesImage> similarGame) {
                    if (similarGame != null) {
                        if (similarGameRecycleView.getAdapter() != null) {
                            similarGameAdapter.setImages(similarGame);
                            similarGameImage = similarGame;
                        } else {
                            similarGameImage = similarGame;
                        }
                    }
                }
            });
            asyncGames.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://www.giantbomb.com/game/" + apiUrl + "/similar-games/");
        }
            }




    /*    new JsoupCharacterWikiImage(new JsoupCharacterWikiImage.AsyncResponse() {
            @Override
            public void processFinish(Elements elements) {


                try {

                    if (elements != null) {


                        if (elements.size() != 0) {
                            characterImage = new ArrayList<>();
                            similarGameImage = new ArrayList<>();
                        }

                        Elements elem[] = new Elements[2];

                        for (int i = 0,size=elements.size(); i < size; i++) {
                            elem[i] = elements.get(i).getElementById("wiki-relatedList")
                                    .select("div.tab-content").first().select("div.tab-pane.active").first().select("div.js-table-pagintor-table").first().select("ul.wiki-relation").first()
                                    .children();

                        }

                        for (Element mElement : elem[0]) {
                            similarGameImage.add(new CharacterGamesImage(mElement.getElementsByTag("img").first().attr("src"), mElement.text()));
                        }

                        for (Element mElement : elem[1]) {
                            characterImage.add(new CharacterGamesImage(mElement.getElementsByTag("img").first().attr("src"), mElement.text()));
                        }


                        Collections.sort(similarGameImage, new Comparator<CharacterGamesImage>() {
                            @Override
                            public int compare(CharacterGamesImage lhs, CharacterGamesImage rhs) {
                                return lhs.name.compareTo(rhs.name);
                            }
                        });

                        if(similarGameRecycleView.getAdapter()!=null && similarGameRecycleView.getAdapter().getItemCount()!=0){
                            similarGameAdapter.setImages(similarGameImage);
                            similarGameRecycleView.getAdapter().notifyDataSetChanged();
                        }

                        Collections.sort(characterImage, new Comparator<CharacterGamesImage>() {
                            @Override
                            public int compare(CharacterGamesImage lhs, CharacterGamesImage rhs) {
                                return lhs.name.compareTo(rhs.name);
                            }
                        });

                        if(characterRecycleView.getAdapter()!=null && characterRecycleView.getAdapter().getItemCount()!=0){
                            characterAdapter.setImages(characterImage);
                            characterRecycleView.getAdapter().notifyDataSetChanged();
                        }





                    }


                } catch (Exception e) {
                    characterImage = null;
                    similarGameImage = null;
                }



            }

        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"http://www.giantbomb.com/game/" + apiUrl + "/"); */

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(GiantBomb.MODAL,gameDetailModal);
    }

    private void getGameDetail(final GameWikiDetailInterface gameWikiDetailInterface, final Map<String, String> map) {


        pacman.setVisibility(View.VISIBLE);

        gameWikiDetailInterface.getResult(apiUrl, map).enqueue(new Callback<GameDetailListModal>() {
            @Override
            public void onResponse(Call<GameDetailListModal> call, Response<GameDetailListModal> response) {

                gameDetailModal = response.body().results;
                fillData(gameDetailModal);
            }

            @Override
            public void onFailure(Call<GameDetailListModal> call, Throwable t) {


                pacman.setVisibility(View.GONE);

                Snackbar.make(coordinatorLayout, "Connectivity Problem", Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getGameDetail(gameWikiDetailInterface, map);

                            }
                        }).show();


            }
        });
    }


    void fillData(final GameDetailModal gameDetailModal) {
        pacman.setVisibility(View.GONE);
        parentLayout.setVisibility(View.VISIBLE);


        if (imageUrl == null && gameDetailModal.images.size() > 0) {
            Picasso.with(getContext()).load(gameDetailModal.images.get(0).mediumUrl).fit().centerCrop().into(appbarImage);
        }

        platforms = gameDetailModal.platforms;
        if(platforms==null)
            platforms = new ArrayList<>();
        //Toaster.make(getContext(),platforms.size()+"5");
        List<CharacterImage> image = gameDetailModal.images;
        List<GameDetailVideo> videos = gameDetailModal.videos;



      /*  ArrayList<String> images = new ArrayList<>(image.size());
        for (int i = 0, size = image.size(); i < size; i++) {
            images.add(image.get(i).mediumUrl);
        }*/

            /*    if (images.size()>0) {
                    Picasso.with(getContext()).load(images.get(0)).fit().centerCrop().into(appbarImage);
                } */

        /************* SETTING GAME IMAGE ***********************************/
        imageRecycleView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        imageRecycleView.setAdapter(new CharacterDetailImageAdapter(image, getContext(),title));


        /************* SETTING GAME VIDEOS *************************************/
        if (videos != null) {
            videoRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            videoRecyclerView.setAdapter(new GameDetailVideoCharacter(image, videos, getContext(), new GameDetailVideoCharacter.GameDetailVideoInterface() {
                @Override
                public void onClickVideo(final GameDetailVideo video, final int next_video_pos) {
                    appbarImage.setVisibility(View.INVISIBLE);
                    playViewButton.setVisibility(View.GONE);
                    coordinatorLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            appBarLayout.setExpanded(true);
                            coordinatorLayout.scrollTo(0,0);
                            nestedScrollView.scrollTo(0,0);
                            GameDetailFragment.this.current_video_pos = next_video_pos;
                            getInit(video);
                        }
                    });
                }
            }));
        }

        toolbarLayout.setTitle(gameDetailModal.name);

        /*********************** SETTING RECYCLER VIEW ****************************/


        final String developer = listToString(gameDetailModal.developers);
        String publisher = listToString(gameDetailModal.publishers);
        String genres = listToString(gameDetailModal.genres);
        final String theme = listToString(gameDetailModal.themes);
        final String franchise = listToString(gameDetailModal.franchises);
        ArrayList<String> values = new ArrayList<>(5);

        values.add(developer);
        values.add(publisher);
        values.add(theme);
        values.add(franchise);
        values.add(genres);

        ArrayList<String> key = new ArrayList<>(5);
        key.add("Developer");
        key.add("Publisher");
        key.add("Theme");
        key.add("Franchise");
        key.add("Genres");


        recyclerView.setLayoutManager(new ConsistentLinearLayoutManager(getContext()));
        adapter = new GameDetailOverviewAdapter(key, values, GameDetailFragment.this, style);
        recyclerView.setAdapter(adapter);


        /************************** SETTING THE DESCRIPTION And BottomSheet*****************************/

        if (gameDetailModal.description != null) {


            Document doc = null;

            doc = Jsoup.parse(gameDetailModal.description);


            Element info = doc.getElementsByTag("p").first();
            if (info != null)
                description.setText(info.text());
            description.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String str = "http://www.giantbomb.com/game/" + apiUrl;
                    CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().setShowTitle(true).build();

                    CustomTabActivityHelper.openCustomTab(
                            getActivity(), customTabsIntent, Uri.parse(str), new WebViewFallback());

                }
            });


        }


                /*   behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        // React to state change
                        if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                            new AsyncTask<String, Void, Spanned>(){
                        @Override
                        protected Spanned doInBackground(String... params) {

                            String desc = params[0];



                            return Html.fromHtml(desc);
                        }

                        @Override
                        protected void onPostExecute(Spanned s) {
                            super.onPostExecute(s);

                            bottomSheetText.setText(s);
                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,completeDescription);

                        }
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                        // React to dragging events
                    }
                }); */


        if (characterImage == null) {
            Log.d("tag", "null character image");
        }


        /******************* SETTING THE CHARACTER **************************************/

        List<GameDetailCharacters> characters = gameDetailModal.characters;

        if (characters != null) {
            Collections.sort(characters, new Comparator<GameDetailCharacters>() {
                @Override
                public int compare(GameDetailCharacters lhs, GameDetailCharacters rhs) {
                    return lhs.name.compareTo(rhs.name);
                }
            });


            characterRecycleView.setLayoutManager(new ConsistentLinearLayoutManager(getContext(), ConsistentLinearLayoutManager.HORIZONTAL, false));
            characterAdapter = new GameDetailCharacterAdapter(characters, characterImage, style, getContext(), new GameDetailCharacterAdapter.OnClickInterface() {
                @Override
                public void onItemClick(String apiUrl, String imageUrl) {
                    ((GameDetailActivity) getActivity()).startCharacterActivity(apiUrl, imageUrl,title);
                }
            });
            characterRecycleView.setAdapter(characterAdapter);
        }

        /******************** SETTING  THE SIMILAR GAMES ******************************/

        List<GameDetailSimilarGames> games = gameDetailModal.similarGames;
        if (games != null) {
            Collections.sort(games, new Comparator<GameDetailSimilarGames>() {
                @Override
                public int compare(GameDetailSimilarGames lhs, GameDetailSimilarGames rhs) {
                    return lhs.name.compareTo(rhs.name);
                }
            });


            similarGameRecycleView.setLayoutManager(new ConsistentLinearLayoutManager(getContext(), ConsistentLinearLayoutManager.HORIZONTAL, false));
            // similarGameRecycleView.addItemDecoration(new VerticalSpaceItemDecoration(6));
            similarGameAdapter = new SimilarGameAdapter(games, similarGameImage, style, GameDetailFragment.this, getContext(), new SimilarGameAdapter.OnClickInterface() {
                @Override
                public void onItemClick(String apiUrl, String imageUrl) {
                    ((GameDetailActivity) getActivity()).restartGameDetail(apiUrl, imageUrl);
                }
            });
            similarGameRecycleView.setAdapter(similarGameAdapter);

        }


        /*************** reviews **************************/
        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gameDetailModal.reviews != null) {

                    if (gameDetailModal.reviews.size() == 1) {
                        mCommunicationInterface.onReviewClick(gameDetailModal.reviews.get(0).apiDetailUrl, title, imageUrl);
                    } else if (gameDetailModal.reviews.size() > 1) {
                        CharSequence item[] = new CharSequence[gameDetailModal.reviews.size()];

                        for (int i = 0, size = gameDetailModal.reviews.size(); i < size; i++) {
                            item[i] = gameDetailModal.reviews.get(i).name;
                        }


                        ReviewListDialogFragment dialogFragment = ReviewListDialogFragment.newInstance(item, true);
                        dialogFragment.setTargetFragment(GameDetailFragment.this, 0);
                        dialogFragment.show(getActivity().getSupportFragmentManager(), "List_Dialog");

                    }

                } else {
                    Toaster.make(getContext(), "No Review Found");
                }
            }
        });

        userReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gameDetailModal.releases != null) {

                    if (gameDetailModal.releases.size() == 1) {
                        mCommunicationInterface.onUserReviewClick(gameDetailModal.releases.get(0).id, title, imageUrl);
                    } else if (gameDetailModal.releases.size() > 1) {
                        CharSequence item[] = new CharSequence[gameDetailModal.releases.size()];

                        for (int i = 0, size = gameDetailModal.releases.size(); i < size; i++) {
                            item[i] = gameDetailModal.releases.get(i).name;
                        }


                        ReviewListDialogFragment dialogFragment = ReviewListDialogFragment.newInstance(item, false);
                        dialogFragment.setTargetFragment(GameDetailFragment.this, 0);
                        dialogFragment.show(getActivity().getSupportFragmentManager(), "List_Dialog");
                    }

                } else {
                    Toaster.make(getContext(), "No User Reviews Found");
                }
            }
        });


    }

    @Override
    public void onItemSelected(int position, boolean isGameReview) {
        if (isGameReview && gameDetailModal.reviews != null) {
            mCommunicationInterface.onReviewClick(gameDetailModal.reviews.get(position).apiDetailUrl, title, imageUrl);
        } else {
            if (gameDetailModal.releases != null)
                mCommunicationInterface.onUserReviewClick(gameDetailModal.releases.get(position).id, title, imageUrl);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.game_detail_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {

            case R.id.fontSelect:

                FontOptionFragment fontOptionFragment = FontOptionFragment.newInstance();
                fontOptionFragment.setTargetFragment(this, 0);
                fontOptionFragment.show(getActivity().getSupportFragmentManager(), "fontoption");


                break;


            case android.R.id.home:
                getActivity().onBackPressed();

        }


        return true;

    }

    public <T> String listToString(List<T> list) {


        StringBuilder builder = new StringBuilder();

        if (list == null) {
            builder.append("null");
            return builder.toString();

        }

        for (int i = 0; i < list.size(); i++) {
            builder.append(list.get(i).toString()).append(", ");
        }

        return builder.toString();

    }


    @Override
    public void onSelect(int which) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            description.setTextAppearance(style);
        } else {
            description.setTextAppearance(getContext(), style);
        }

        Toaster.make(getContext(), "Font will be changed after going back");


    }

    public int selectStyle(int which) {


        switch (which) {

            case 1:
                return R.style.TextStyle1;
            case 2:
                return R.style.TextStyle2;
            case 3:
                return R.style.TextStyle3;
            case 4:
                return R.style.TextStyle4;
            case 5:
                return R.style.TextStyle5;
            case 6:
                return R.style.TextStyle6;
            case 7:
                return R.style.TextStyle7;
            case 8:
                return R.style.TextStyle8;
            case 9:
                return R.style.TextStyle9;
            case 10:
                return R.style.TextStyle10;
            case 11:
                return R.style.TextStyle11;
            case 12:
                return R.style.TextStyle12;


        }

        return R.style.TextStyle1;

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (appBarLayout != null) {
            appBarLayout.addOnOffsetChangedListener(null);
            appBarLayout = null;

        }
        gameWikiDetailInterface = null;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        statsDatabase.removeChangeListener(statsCallback);
        if (!realm.isClosed()) {
            realm.close();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (asyncCharacters != null)
            asyncCharacters.cancel(true);
        if (asyncGames != null)
            asyncGames.cancel(true);

        if (videoView != null) {
            ((GameDetailActivity)getActivity()).seek_position = videoView.getCurrentPosition(); //stopPosition is an int
            videoView.pause();
        }


       
    }

    @Override
    public void onResume() {
        super.onResume();

        if (videoView!=null) {
            videoView.seekTo(((GameDetailActivity)getActivity()).seek_position);
            videoView.resume(); //Or use resume() if it doesn't work. I'm not sure
        }



    }

    /*private DemoPlayer.RendererBuilder getRendererBuilder() {
        String userAgent = Util.getUserAgent(getContext(), "ExoPlayerDemo");
        return new ExtractorRendererBuilder(getContext(), userAgent, Uri.parse("http://v.giantbomb.com/2016/07/28/vf_giantbomb_bestof_103_1800.mp4?api_key=b3" +
                "18d66445bfc79e6d74a65fe52744b45b345948&format=JSON"));

    }*/








}
