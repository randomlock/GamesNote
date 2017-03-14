package com.example.randomlocks.gamesnote.Fragments.ViewPagerFragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.example.randomlocks.gamesnote.Adapter.GameVideoOtherAdapter;
import com.example.randomlocks.gamesnote.DialogFragment.VideoOptionFragment;
import com.example.randomlocks.gamesnote.HelperClass.CustomView.ConsistentLinearLayoutManager;
import com.example.randomlocks.gamesnote.HelperClass.DividerItemDecoration;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.SharedPreference;
import com.example.randomlocks.gamesnote.HelperClass.WebViewHelper.CustomTabActivityHelper;
import com.example.randomlocks.gamesnote.HelperClass.WebViewHelper.WebViewFallback;
import com.example.randomlocks.gamesnote.Interface.VideoPlayInterface;
import com.example.randomlocks.gamesnote.Modal.GamesVideoModal.GamesVideoModal;
import com.example.randomlocks.gamesnote.R;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by randomlocks on 7/19/2016.
 */
public class GameVideoOtherPagerFragment extends Fragment implements VideoOptionFragment.OnPlayInterface {

    private static final String MODAL = "list_modals";
    private static final String SCROLL_POSITION = "scroll_position";
    private static final int LIKE_TYPE = 1;
    Realm realm;
    RecyclerView recyclerView;
    boolean isReduced = false;
    int position;
    GameVideoOtherAdapter adapter;
    RealmResults<GamesVideoModal> listModals;
    FloatingSearchView floatingSearchView;
    ConsistentLinearLayoutManager manager;

    VideoPlayInterface videoPlayInterface;


    public GameVideoOtherPagerFragment() {
    }

    public static GameVideoOtherPagerFragment newInstance(int position) {

        Bundle args = new Bundle();
        args.putInt(GiantBomb.POSITION, position);
        GameVideoOtherPagerFragment fragment = new GameVideoOtherPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        position = getArguments().getInt(GiantBomb.POSITION);
        isReduced = SharedPreference.getFromSharedPreferences(GiantBomb.REDUCE_VIEW, false, getContext());
        realm = Realm.getDefaultInstance();


    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_games_video_pager,container,false);

        floatingSearchView = (FloatingSearchView) getActivity().findViewById(R.id.floating_search_view);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
      /*  swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (adapter != null) {
                    boolean isSimple = SharedPreference.getFromSharedPreferences(GiantBomb.REDUCE_VIEW, false, getContext());
                    adapter.setSimple(isSimple);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });*/
        manager = new ConsistentLinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        if(isReduced)
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));


        if (listModals != null) {
            fillRecyclerView(listModals);
        } else {
            if (position == LIKE_TYPE) {
                listModals = realm.where(GamesVideoModal.class).equalTo("isFavorite", true).findAll();
            } else {
                listModals = realm.where(GamesVideoModal.class).equalTo("isWatchLater", true).findAll();
            }
            fillRecyclerView(listModals);

        }



        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        videoPlayInterface = (VideoPlayInterface) getActivity();
    }


    private void fillRecyclerView(RealmResults<GamesVideoModal> listModals) {

        //TODO add error text for no video

        if (adapter == null) {
            adapter = new GameVideoOtherAdapter(getContext(), listModals, true, isReduced, realm, position, new GameVideoOtherAdapter.OnClickInterface() {
                @Override
                public void onVideoClick(GamesVideoModal modal) {
                    VideoOptionFragment videoOptionFragment = VideoOptionFragment.newInstance(modal);
                    videoOptionFragment.setTargetFragment(GameVideoOtherPagerFragment.this, 0);
                    videoOptionFragment.setCancelable(false);
                    videoOptionFragment.show(getActivity().getSupportFragmentManager(), "video_option_fragment");
                }
            });
        }

        if(recyclerView.getLayoutManager()==null){
            recyclerView.setLayoutManager(manager);
        }

        if (recyclerView.getAdapter() == null) {
            recyclerView.setAdapter(adapter);
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (!realm.isClosed()) {
            realm.close();
        }
    }

    @Override
    public void onPlay(GamesVideoModal modal, int video_option, boolean use_inbuilt) {
        String url;
        switch (video_option) {
            case 0:
                url = modal.lowUrl + "?api_key=" + GiantBomb.API_KEY;
                if (use_inbuilt)
                    videoPlayInterface.onVideoClick(url);
                else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.setDataAndType(Uri.parse(url), "video/mp4");
                    startActivity(intent);
                }

                break;

            case 1:
                url = modal.highUrl + "?api_key=" + GiantBomb.API_KEY;
                if (use_inbuilt)
                    videoPlayInterface.onVideoClick(url);
                else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.setDataAndType(Uri.parse(url), "video/mp4");
                    startActivity(intent);
                }
                break;
            case 2:
                url = modal.youtubeId;
                Intent intent = YouTubeStandalonePlayer.createVideoIntent(getActivity(), GiantBomb.YOUTUBE_API_KEY, url, 0, true, false);
                startActivity(intent);
                break;

            case 3:
                url = modal.siteDetailUrl;
                runBrowser(url);
                break;

            default:
                break;


        }

    }

    void runBrowser(String url) {
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().setShowTitle(true).build();
        CustomTabActivityHelper.openCustomTab(
                getActivity(), customTabsIntent, Uri.parse(url), new WebViewFallback());
    }



}
