package com.example.randomlocks.gamesnote.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.randomlocks.gamesnote.Activity.GameDetailActivity;
import com.example.randomlocks.gamesnote.Adapter.CharacterDetailImageAdapter;
import com.example.randomlocks.gamesnote.Adapter.GameDetailCharacterAdapter;
import com.example.randomlocks.gamesnote.Adapter.GameDetailOverviewAdapter;
import com.example.randomlocks.gamesnote.Adapter.GameDetailVideoCharacter;
import com.example.randomlocks.gamesnote.Adapter.SimilarGameAdapter;
import com.example.randomlocks.gamesnote.AsyncTask.JsoupCharacters;
import com.example.randomlocks.gamesnote.AsyncTask.JsoupGames;
import com.example.randomlocks.gamesnote.DialogFragment.AddToBottomFragment;
import com.example.randomlocks.gamesnote.DialogFragment.ReviewListDialogFragment;
import com.example.randomlocks.gamesnote.HelperClass.CustomView.AVLoadingIndicatorView;
import com.example.randomlocks.gamesnote.HelperClass.CustomView.ConsistentLinearLayoutManager;
import com.example.randomlocks.gamesnote.HelperClass.CustomView.CustomMediaController;
import com.example.randomlocks.gamesnote.HelperClass.CustomView.CustomVideoView;
import com.example.randomlocks.gamesnote.HelperClass.CustomView.PicassoNestedScrollView;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.MyAnimation;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.HelperClass.WebViewHelper.CustomTabActivityHelper;
import com.example.randomlocks.gamesnote.HelperClass.WebViewHelper.WebViewFallback;
import com.example.randomlocks.gamesnote.Interface.GameDetailVideoInterface;
import com.example.randomlocks.gamesnote.Interface.GameWikiDetailInterface;
import com.example.randomlocks.gamesnote.Modal.GameCharacterModal.CharacterImage;
import com.example.randomlocks.gamesnote.Modal.GameDetailModal.CharacterGamesImage;
import com.example.randomlocks.gamesnote.Modal.GameDetailModal.GameDetailIInnerJson;
import com.example.randomlocks.gamesnote.Modal.GameDetailModal.GameDetailListModal;
import com.example.randomlocks.gamesnote.Modal.GameDetailModal.GameDetailModal;
import com.example.randomlocks.gamesnote.Modal.GameDetailModal.GameDetailVideo;
import com.example.randomlocks.gamesnote.Modal.GameDetailModal.GameVideoMinimal;
import com.example.randomlocks.gamesnote.Modal.GameDetailModal.GameVideoModalMinimal;
import com.example.randomlocks.gamesnote.Modal.GameWikiPlatform;
import com.example.randomlocks.gamesnote.Modal.GamesVideoModal.GamesVideoModal;
import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.RealmDatabase.GameDetailDatabase;
import com.example.randomlocks.gamesnote.RealmDatabase.GameListDatabase;
import com.example.randomlocks.gamesnote.RealmDatabase.RealmInteger;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
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

import es.dmoral.toasty.Toasty;
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


public class GameDetailFragment extends Fragment implements View.OnClickListener, ReviewListDialogFragment.CommunicationInterface, AddToBottomFragment.AddToBottomInterface {


    public static final String API_URL = "apiUrl";
    public static final String IMAGE_URL = "imageUrl";
    public static final String NAME = "name";
    private static final String GAME_CHARACTER = "game_detail_character" ;
    private static final String  GAME_SIMILAR = "game_detail_similar";
    private static final String GAME_ID = "game_id" ;
    private static final String IMAGE_QUALITY_KEY = "video_preference";
    private static final int LOW_VIDEO_URL = 0;
    private static final int HIGH_VIDEO_URL = 1;
    private static final int OPEN_IN_BROWSER = 2;
    private static final int OPEN_IN_YOUTUBE = 3;
    Realm realm;
    int list_category;
    int game_id;
    Toolbar toolbar;
    String apiUrl,imageUrl;
    GameWikiDetailInterface gameWikiDetailInterface;
    Map<String, String> map = null;
    Map<String,String> videoMap;
    GameDetailModal gameDetailModal = null;
    GamesVideoModal videoModal;
    CollapsingToolbarLayout toolbarLayout;
    AppBarLayout appBarLayout;
    RecyclerView recyclerView, similarGameRecycleView, characterRecycleView, imageRecycleView, videoRecyclerView;
    PicassoNestedScrollView nestedScrollView;
    TextView description;
    GameDetailOverviewAdapter adapter;
    List<CharacterGamesImage> characterImage = null, similarGameImage = null;
    List<GameWikiPlatform> platforms = null;
    boolean isAdded=false;
    CoordinatorLayout coordinatorLayout;
    SimilarGameAdapter similarGameAdapter;
    GameDetailCharacterAdapter characterAdapter;
    CommunicationInterface mCommunicationInterface;
    JsoupCharacters asyncCharacters;
    JsoupGames asyncGames;
    ImageView appbarImage;
    TextView  review, userReview;
    TextView status , score , platform , hours;
    TextView stats_heading,overview_heading,description_heading,characters_heading,image_heading,related_video_heading,similar_game_heading;
    TextView description_open_in_internet;
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
    View videoDim;
    Matrix matrix=new Matrix();
    GameDetailVideoInterface videoInterface;
    CustomVideoView videoView;
    CustomMediaController media_Controller;
    View playViewButton;
    ProgressBar videoProgress;
    int current_video_pos = 0;
    private int stopPosition;
    private DisplayMetrics metrics;
    private String video_url;
    private int drawable_color;
    private int videoType;
    private RealmChangeListener statsCallback = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            if (statsDatabase.isLoaded()) {
                if (statsDatabase.isValid()) {
                    isAdded = true;
                    statsCardView.setVisibility(View.VISIBLE);
                    floatingActionsMenu.hideMenu(true);

                    //inflate views and update stats

                    status = (TextView) statsDetailView.findViewById(R.id.status_value);
                    score = (TextView) statsDetailView.findViewById(R.id.score_value);
                    platform = (TextView) statsDetailView.findViewById(R.id.platfom_value);
                    hours = (TextView) statsDetailView.findViewById(R.id.hours_value);

                    status.setText(getResources().getStringArray(R.array.status)[statsDatabase.getStatus() - 1]);
                    score.setText(String.valueOf(statsDatabase.getScore()));
                    platform.setText(statsDatabase.getPlatform());
                    hours.setText(statsDatabase.getGameplay_hours());


                }

            }
        }

    };


    public GameDetailFragment() {
        // Required empty public constructor

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

    public void getInit(final GameDetailVideo gameDetailVideo) {
        videoProgress = (ProgressBar) toolbarLayout.findViewById(R.id.video_progress);
        //geting video url from the api
        if (videoMap==null) {
            videoMap = new HashMap<>();
            videoMap.put(GiantBomb.KEY, GiantBomb.API_KEY);
            String field_list = "site_detail_url,youtube_id,high_url,low_url,length_seconds,name,image";
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
                GameVideoModalMinimal minimalVideoModal = response.body().results;
                videoModal = new GamesVideoModal(minimalVideoModal);
                if (videoType == LOW_VIDEO_URL || videoType == HIGH_VIDEO_URL) {
                    setUpVideoView();
                } else if (videoType == OPEN_IN_YOUTUBE) {
                    if (videoModal.youtubeId != null) {
                        Intent intent = YouTubeStandalonePlayer.createVideoIntent(getActivity(), GiantBomb.YOUTUBE_API_KEY, videoModal.youtubeId, 0, true, false);
                        startActivity(intent);
                    } else {
                        Toasty.error(getContext(), "Not available on youtube").show();
                        setUpVideoView();
                    }
                } else if (videoType == OPEN_IN_BROWSER) {
                    CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().setShowTitle(true).addDefaultShareMenuItem().build();
                    CustomTabActivityHelper.openCustomTab(
                            getActivity(), customTabsIntent, Uri.parse(videoModal.siteDetailUrl), new WebViewFallback());
                }

            }

            @Override
            public void onFailure(Call<GameVideoMinimal> call, Throwable t) {
                Toaster.makeSnackBar(coordinatorLayout, "error cannot play");
            }
        });

    }

    private void setUpVideoView() {
        video_url = videoType == LOW_VIDEO_URL ? videoModal.lowUrl : videoModal.highUrl + "?api_key=" + GiantBomb.API_KEY;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mCommunicationInterface.onVideoClick(video_url, false, 0, videoModal);
            return;
        }
/*
                if (videoView==null) {
*/
        videoView = (CustomVideoView) toolbarLayout.findViewById(R.id.play_video_texture);
        videoDim = nestedScrollView.findViewById(R.id.video_dimmer_view);
        media_Controller = new CustomMediaController(getContext(), false);


        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) videoView.getLayoutParams();
        params.width = metrics.widthPixels;
        params.height = metrics.heightPixels / 3;
        params.leftMargin = 0;
        params.rightMargin = 0;
        videoView.setLayoutParams(params);

        media_Controller.setAnchorView(videoView);
        media_Controller.setPrevNextListeners(new View.OnClickListener() {
                                                  //for next video
                                                  @Override
                                                  public void onClick(View view) {
                                                      if (current_video_pos + 1 < gameDetailModal.videos.size()) {
                                                          getInit(gameDetailModal.videos.get(++current_video_pos));
                                                      } else {
                                                          Toasty.warning(getContext(), "no next video", Toast.LENGTH_SHORT, true).show();


                                                      }
                                                  }
                                              }, //for prev video
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (current_video_pos > 0) {
                            getInit(gameDetailModal.videos.get(--current_video_pos));
                        } else {
                            Toasty.warning(getContext(), "no previous video", Toast.LENGTH_SHORT, true).show();
                        }
                    }
                });
        videoView.setMediaController(media_Controller);
        videoView.setVideoPath(video_url);
        videoView.requestFocus();
        videoView.start();
        videoProgress.setVisibility(View.VISIBLE);
        setVideoListener();
               /* }else {
                    videoView.stopPlayback();
                    videoView.setVideoPath(videoUrl);
                    videoView.start();
                    videoProgress.setVisibility(View.GONE);

                }*/
    }

    private void setVideoListener() {
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoProgress.setVisibility(View.GONE);
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START)
                            videoProgress.setVisibility(View.VISIBLE);
                        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END)
                            videoProgress.setVisibility(View.GONE);
                        return false;
                    }
                });
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                videoProgress.setVisibility(View.GONE);
                return false;
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
                mCommunicationInterface.onVideoClick(video_url, false, seek_position, videoModal);
            }

            @Override
            public void onRequestDimmerView() {
                if (videoDim.getAlpha() == 0) {
                    videoDim.animate().alpha(1).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            floatingActionsMenu.hideMenuButton(true);

                        }
                    });
                    //setBackgroundDimming(videoDim,true);
                } else {
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

            case R.id.description_internet:
                String str = "http://www.giantbomb.com/game/" + apiUrl;
                CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().setShowTitle(true).build();

                CustomTabActivityHelper.openCustomTab(
                        getActivity(), customTabsIntent, Uri.parse(str), new WebViewFallback());

                return;



        }

        if (gameDetailModal != null) {
            BottomSheetDialogFragment addToBottomFragment = AddToBottomFragment.newInstance(platforms);
            addToBottomFragment.setTargetFragment(this, 0);
            Toaster.make(getContext(),"hello");
            addToBottomFragment.show(getActivity().getSupportFragmentManager(), "addto");
        } else {
            Toaster.makeSnackBar(coordinatorLayout,"waiting to load data");
        }

    }

    //When adding game to the list
    @Override
    public void onAdd(int score, String startDate, String endDate, String platform) {

        RealmList<GameWikiPlatform> realmPlatformList = new RealmList<>();
        for(GameWikiPlatform p : platforms)
            realmPlatformList.add(p);
        final GameListDatabase gameListDatabase = new GameListDatabase(game_id,apiUrl,title,imageUrl,list_category,score,startDate,endDate,platform,realmPlatformList);
        //adding developer





        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insertOrUpdate(gameListDatabase);
                if (!isAdded) {
                    updateGameDetail(gameDetailModal.developers, GameDetailDatabase.DEVELOPER_TYPE,realm);
                    updateGameDetail(gameDetailModal.publishers, GameDetailDatabase.PUBLISHER_TYPE,realm);
                    updateGameDetail(gameDetailModal.themes, GameDetailDatabase.THEME_TYPE,realm);
                    updateGameDetail(gameDetailModal.franchises, GameDetailDatabase.FRANCHISE_TYPE,realm);
                    updateGameDetail(gameDetailModal.genres, GameDetailDatabase.GENRE_TYPE,realm);
                    updateGameDetail(gameDetailModal.similarGames, GameDetailDatabase.SIMILAR_GAME_TYPE,realm);



                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Toasty.success(getContext(),"Game added", Toast.LENGTH_SHORT,true).show();

            }
        });
    }

    private void updateGameDetail(List<GameDetailIInnerJson> gameDetailDatabase, int type,Realm realm) {

        if (gameDetailDatabase!=null) {
            //already added in database . Here we just update the game list
            for(GameDetailIInnerJson gameDatabase : gameDetailDatabase){
                GameDetailDatabase gameDeveloperDatabase = realm.where(GameDetailDatabase.class)
                        .equalTo(GameDetailDatabase.TYPE,type)
                        .equalTo(GameDetailDatabase.NAME,gameDatabase.name).findFirst();
                if(gameDeveloperDatabase!=null){
                    gameDeveloperDatabase.getGames_id().add(new RealmInteger(game_id));
                    gameDeveloperDatabase.setCount(gameDeveloperDatabase.getCount()+1);
                }else {
                    //adding for the first time
                    RealmList<RealmInteger> games_id = new RealmList<RealmInteger>();
                    games_id.add(new RealmInteger(game_id));
                    int auto_increment_x=0;
                    Number number = realm.where(GameDetailDatabase.class).equalTo(GameDetailDatabase.TYPE,type).max(GameDetailDatabase.AUTO_INCREMENT_X_VALUE);
                    if(number!=null)
                        auto_increment_x = number.intValue()+1;
                    gameDeveloperDatabase = new GameDetailDatabase(type,gameDatabase.name,1,auto_increment_x,games_id);
                    realm.insertOrUpdate(gameDeveloperDatabase);
                }
            }
        }

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
        drawable_color = ContextCompat.getColor(getContext(),R.color.primary);
        videoType = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getContext()).getString(IMAGE_QUALITY_KEY, "1"));

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
                mCommunicationInterface.onVideoClick(video_url, false, videoView.getCurrentPosition(), videoModal);
            }
        }


    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        /************************************** FindViewById *********************************/
        coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(R.id.root_coordinator);
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
                    Toaster.makeSnackBar(coordinatorLayout,"waiting to load data");
                }else if(gameDetailModal.videos.size()==0){
                    Toaster.makeSnackBar(coordinatorLayout,"no video found");
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
        stats_heading = (TextView) statsCardView.findViewById(R.id.stats_heading);
        setTextViewDrawableColor(stats_heading,drawable_color);
        statsDetailView = (LinearLayout) statsCardView.findViewById(R.id.hide_parent_layout);
        toggleArrow = (ImageView) statsCardView.findViewById(R.id.stats_toggle);
        toggleArrow.setColorFilter(ContextCompat.getColor(getContext(), R.color.primary));
        statsCardView.setOnClickListener(this);
        similarGameRecycleView = (RecyclerView) nestedScrollView.findViewById(R.id.similar_game_list);
        similar_game_heading = (TextView) nestedScrollView.findViewById(R.id.similar_game_heading);
        setTextViewDrawableColor(similar_game_heading,drawable_color);
        characterRecycleView = (RecyclerView) nestedScrollView.findViewById(R.id.character_game_list);
        characters_heading = (TextView) nestedScrollView.findViewById(R.id.character_heading);
        setTextViewDrawableColor(characters_heading,drawable_color);
        description_heading = (TextView) nestedScrollView.findViewById(R.id.description_heading);
        description_open_in_internet = (TextView) nestedScrollView.findViewById(R.id.description_internet);
        setTextViewDrawableColor(description_open_in_internet,drawable_color);
        description_open_in_internet.setOnClickListener(this);
        setTextViewDrawableColor(description_heading,drawable_color);
        description = (TextView) nestedScrollView.findViewById(R.id.description);
        overview_heading = (TextView) nestedScrollView.findViewById(R.id.overview_heading);
        setTextViewDrawableColor(overview_heading,drawable_color);
        recyclerView = (RecyclerView) nestedScrollView.findViewById(R.id.list);
        imageRecycleView = (RecyclerView) nestedScrollView.findViewById(R.id.image_recycler_view);
        image_heading = (TextView) nestedScrollView.findViewById(R.id.image_heading);
        setTextViewDrawableColor(image_heading,drawable_color);
        videoRecyclerView = (RecyclerView) nestedScrollView.findViewById(R.id.video_recycler_view);
        related_video_heading = (TextView) nestedScrollView.findViewById(R.id.video_heading);
        setTextViewDrawableColor(related_video_heading,drawable_color);
        review = (TextView) nestedScrollView.findViewById(R.id.review);
        setTextViewDrawableColor(review,drawable_color);
        userReview = (TextView) nestedScrollView.findViewById(R.id.user_review);
        setTextViewDrawableColor(userReview,drawable_color);
        pacman = (AVLoadingIndicatorView) nestedScrollView.findViewById(R.id.progressBar);

     /*   if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
            setIconColor(characters_heading);
            setIconColor(image_heading);
            setIconColor(related_video_heading);
            setIconColor(similar_game_heading);
        }*/
        recyclerView.setNestedScrollingEnabled(false);
        characterRecycleView.setNestedScrollingEnabled(false);
        similarGameRecycleView.setNestedScrollingEnabled(false);


        /************************** APPBARLATOUT ********************************/

        if (imageUrl != null) {
            Picasso.with(getContext()).load(imageUrl).fit().centerCrop().into(appbarImage);
        }

        appbarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });






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
            characterImage = savedInstanceState.getParcelableArrayList(GAME_CHARACTER);
            similarGameImage = savedInstanceState.getParcelableArrayList(GAME_SIMILAR);
            fillData(gameDetailModal);
        } else {
            if (gameDetailModal != null) {
                fillData(gameDetailModal);
            }else {
            gameWikiDetailInterface = GiantBomb.createGameDetailService();
            map = new HashMap<>();
            map.put(GiantBomb.KEY, GiantBomb.API_KEY);
            map.put(GiantBomb.FORMAT, "JSON");
            String field_list = "description,name,platforms,images,id,videos,characters,developers,franchises,genres,publishers,similar_games,themes,reviews,releases";
            map.put(GiantBomb.FIELD_LIST, field_list);
            getGameDetail(gameWikiDetailInterface, map);




            runAsyncTask();

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
    private void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(drawable_color, PorterDuff.Mode.SRC_IN));
            }
        }
    }



    private void runAsyncTask() {

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



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(GiantBomb.MODAL,gameDetailModal);
        if (characterImage!=null) {
            outState.putParcelableArrayList(GAME_CHARACTER,new ArrayList<>(characterImage));
        }
        if (similarGameImage!=null) {
            outState.putParcelableArrayList(GAME_SIMILAR,new ArrayList<>(similarGameImage));
        }
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

                if (!call.isCanceled()) {
                    Toaster.makeSnackBar(coordinatorLayout, "Connectivity Problem", "RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getGameDetail(gameWikiDetailInterface,map);
                            runAsyncTask();
                        }
                    });
                }

            }
        });
    }

    void fillData(final GameDetailModal gameDetailModal) {
        pacman.setVisibility(View.GONE);
        parentLayout.setVisibility(View.VISIBLE);
        game_id = gameDetailModal.id;

        if (imageUrl == null && gameDetailModal.images.size() > 0) {
            Picasso.with(getContext()).load(gameDetailModal.images.get(0).mediumUrl).fit().centerCrop().into(appbarImage);
        }

        platforms = gameDetailModal.platforms;
        if(platforms==null)
            platforms = new ArrayList<>();
        //Toaster.make(getContext(),platforms.size()+"5");
        List<CharacterImage> image = gameDetailModal.images;
        List<GameDetailVideo> videos = gameDetailModal.videos;

        if(gameDetailModal.characters==null||gameDetailModal.characters.isEmpty())
            characters_heading.setVisibility(View.GONE);
        if(gameDetailModal.images==null||gameDetailModal.images.isEmpty())
            image_heading.setVisibility(View.GONE);
        if(gameDetailModal.videos==null||gameDetailModal.videos.isEmpty())
            related_video_heading.setVisibility(View.GONE);
        if(gameDetailModal.similarGames==null||gameDetailModal.similarGames.isEmpty())
            similar_game_heading.setVisibility(View.GONE);




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
        adapter = new GameDetailOverviewAdapter(key, values);
        recyclerView.setAdapter(adapter);


        /************************** SETTING THE DESCRIPTION And BottomSheet*****************************/

        if (gameDetailModal.description != null) {


            Document doc = null;

            doc = Jsoup.parse(gameDetailModal.description);


            Element info = doc.getElementsByTag("p").first();
            if (info != null)
                description.setText(info.text());
            }else {
            description.setText("No available info");
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




        /******************* SETTING THE CHARACTER **************************************/

        List<GameDetailIInnerJson> characters = gameDetailModal.characters;

        if (characters != null) {
            Collections.sort(characters, new Comparator<GameDetailIInnerJson>() {
                @Override
                public int compare(GameDetailIInnerJson lhs, GameDetailIInnerJson rhs) {
                    return lhs.name.compareTo(rhs.name);
                }
            });


            characterRecycleView.setLayoutManager(new ConsistentLinearLayoutManager(getContext(), ConsistentLinearLayoutManager.HORIZONTAL, false));
            characterAdapter = new GameDetailCharacterAdapter(characters, characterImage, getContext(), new GameDetailCharacterAdapter.OnClickInterface() {
                @Override
                public void onItemClick(String apiUrl, String imageUrl) {
                    ((GameDetailActivity) getActivity()).startCharacterActivity(apiUrl, imageUrl,title);
                }
            });
            characterRecycleView.setAdapter(characterAdapter);
        }

        /******************** SETTING  THE SIMILAR GAMES ******************************/

        List<GameDetailIInnerJson> games = gameDetailModal.similarGames;
        if (games != null) {
            Collections.sort(games, new Comparator<GameDetailIInnerJson>() {
                @Override
                public int compare(GameDetailIInnerJson lhs, GameDetailIInnerJson rhs) {
                    return lhs.name.compareTo(rhs.name);
                }
            });


            similarGameRecycleView.setLayoutManager(new ConsistentLinearLayoutManager(getContext(), ConsistentLinearLayoutManager.HORIZONTAL, false));
            similarGameAdapter = new SimilarGameAdapter(games, similarGameImage, GameDetailFragment.this, getContext(), new SimilarGameAdapter.OnClickInterface() {
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
                    Toasty.error(getContext(),"No Review Found", Toast.LENGTH_SHORT,true).show();

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
                    Toasty.error(getContext(),"No User Reviews Found", Toast.LENGTH_SHORT,true).show();

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
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {




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




    public interface CommunicationInterface {
        void onReviewClick(String apiUrl, String gameTitle, String imageUrl);

        void onUserReviewClick(String apiUrl, String gameTitle, String imageUrl);

        void onVideoClick(String url, boolean needRequest, int seek_position, GamesVideoModal modal);

    }

    /*private DemoPlayer.RendererBuilder getRendererBuilder() {
        String userAgent = Util.getUserAgent(getContext(), "ExoPlayerDemo");
        return new ExtractorRendererBuilder(getContext(), userAgent, Uri.parse("http://v.giantbomb.com/2016/07/28/vf_giantbomb_bestof_103_1800.mp4?api_key=b3" +
                "18d66445bfc79e6d74a65fe52744b45b345948&format=JSON"));

    }*/








}
