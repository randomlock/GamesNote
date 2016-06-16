package com.example.randomlocks.gamesnote.Fragments;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.Adapter.GameDetailCharacterAdapter;
import com.example.randomlocks.gamesnote.Adapter.GameDetailRecyclerAdapter;
import com.example.randomlocks.gamesnote.Adapter.SimilarGameAdapter;
import com.example.randomlocks.gamesnote.Adapter.WikiPagerAdapter;
import com.example.randomlocks.gamesnote.AsyncTask.JsoupConnect;
import com.example.randomlocks.gamesnote.DialogFragment.FontOptionFragment;
import com.example.randomlocks.gamesnote.HelperClass.DividerItemDecoration;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.PagerDepthAnimation;
import com.example.randomlocks.gamesnote.HelperClass.PicassoNestedScrollView;
import com.example.randomlocks.gamesnote.HelperClass.SharedPreference;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.HelperClass.VerticalSpaceItemDecoration;
import com.example.randomlocks.gamesnote.Interface.GameWikiDetailInterface;
import com.example.randomlocks.gamesnote.Modal.GameDetailModal.CharacterGamesImage;
import com.example.randomlocks.gamesnote.Modal.GameDetailModal.GameDetailCharacters;
import com.example.randomlocks.gamesnote.Modal.GameDetailModal.GameDetailImages;
import com.example.randomlocks.gamesnote.Modal.GameDetailModal.GameDetailListModal;
import com.example.randomlocks.gamesnote.Modal.GameDetailModal.GameDetailModal;
import com.example.randomlocks.gamesnote.Modal.GameDetailModal.GameDetailSimilarGames;
import com.example.randomlocks.gamesnote.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */


/****
 * optimize findviewbyid
 ************/


public class GameDetailFragment extends Fragment implements FontOptionFragment.FontOptionInterface {


    public static final String API_URL = "apiUrl";
    public static final String IMAGE_URL = "imageUrl" ;
    Toolbar toolbar;
    String apiUrl;
    GameWikiDetailInterface gameWikiDetailInterface;
    ViewPager viewPager;
    Map<String,String> map;
    GameDetailModal gameDetailModal;
    CollapsingToolbarLayout toolbarLayout;
    AppBarLayout appBarLayout;
    RecyclerView recyclerView, similarGameRecycleView, characterRecycleView;
    PicassoNestedScrollView nestedScrollView;
    TextView description,bottomSheetText;
    View bottomSheet;
    GameDetailRecyclerAdapter adapter;
    int style;
    String completeDescription;
    List<CharacterGamesImage> characterImage = null, similarGameImage = null;
    ProgressBar overviewProgress;
    CoordinatorLayout coordinatorLayout;
    SimilarGameAdapter similarGameAdapter;
    GameDetailCharacterAdapter characterAdapter;
    CommunicationInterface mCommunicationInterface;




    public GameDetailFragment() {
        // Required empty public constructor

    }

    public interface CommunicationInterface{
        void loadWebView(String string);
    }

    public static GameDetailFragment newInstance(String apiUrl, String imageUrl) {

        Bundle args = new Bundle();
        args.putString(API_URL,apiUrl);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String str[] = getArguments().getString(API_URL).split("/");
        apiUrl = str[str.length- 1];
        return inflater.inflate(R.layout.fragment_game_detail, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);


        /************************************** FindViewById *********************************/
        coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(R.id.root_coordinator);
        nestedScrollView = (PicassoNestedScrollView) coordinatorLayout.findViewById(R.id.scroll_view);
        appBarLayout = (AppBarLayout) coordinatorLayout.findViewById(R.id.app_bar_layout);
        toolbarLayout = (CollapsingToolbarLayout) appBarLayout.findViewById(R.id.collapsing_toolbar_layout);
        toolbar = (Toolbar) toolbarLayout.findViewById(R.id.my_toolbar);
        viewPager = (ViewPager) toolbarLayout.findViewById(R.id.viewpager);
        similarGameRecycleView = (RecyclerView) nestedScrollView.findViewById(R.id.similar_game_list);
        similarGameRecycleView.setNestedScrollingEnabled(false);
        characterRecycleView = (RecyclerView) nestedScrollView.findViewById(R.id.character_game_list);
        characterRecycleView.setNestedScrollingEnabled(false);
        description = (TextView) nestedScrollView.findViewById(R.id.description);
        overviewProgress = (ProgressBar) nestedScrollView.findViewById(R.id.overview_progress);
        recyclerView = (RecyclerView) nestedScrollView.findViewById(R.id.list);
        recyclerView.setNestedScrollingEnabled(false);
        style = selectStyle(SharedPreference.getFromSharedPreferences(GiantBomb.FONT, 1, getContext()) + 1);


        /************************** BACKGROUND ********************************/

        //  Picasso.with(getContext()).load(getArguments().getString(IMAGE_URL)).into(nestedScrollView);

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
                if (toolbarLayout.getHeight() + verticalOffset < 2 * ViewCompat.getMinimumHeight(toolbarLayout)) {
                    toolbar.animate().alpha(1).setDuration(300);
                } else {
                    toolbar.animate().alpha(0).setDuration(300);
                }
            }

        });

        /******************************* TOOLBAR *************************************/

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);


        // toolbar.setTitle("Game Detail");   //to be changed to game title





        /**************** RETROFIT ************************/

        gameWikiDetailInterface = GiantBomb.createService(GameWikiDetailInterface.class);
        map = new HashMap<>();
        map.put(GiantBomb.KEY,GiantBomb.API_KEY);
        map.put(GiantBomb.FORMAT, "JSON");
        getGameDetail(gameWikiDetailInterface, map);



        new JsoupConnect(new JsoupConnect.AsyncResponse() {
            @Override
            public void processFinish(Elements elements) {


                try {

                    if (elements != null) {


                        if (elements.size() != 0) {
                            characterImage = new ArrayList<>();
                            similarGameImage = new ArrayList<>();
                        }

                        Elements elem[] = new Elements[2];

                        for (int i = 0; i < elements.size(); i++) {
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

        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"http://www.giantbomb.com/bioshock-infinite/" + apiUrl + "/");




    }

    private void getGameDetail(final GameWikiDetailInterface gameWikiDetailInterface, final Map<String, String> map) {


        overviewProgress.setVisibility(View.VISIBLE);

        gameWikiDetailInterface.getResult(apiUrl,map).enqueue(new Callback<GameDetailListModal>() {
            @Override
            public void onResponse(Call<GameDetailListModal> call, Response<GameDetailListModal> response) {


                overviewProgress.setVisibility(View.GONE);


                gameDetailModal = response.body().results;
                List<GameDetailImages> image = gameDetailModal.images;
                ArrayList<String> images = new ArrayList<>();
                for (int i = 0; i < image.size(); i++) {
                    images.add(image.get(i).thumbUrl);
                }

                WikiPagerAdapter pagerAdapter = new WikiPagerAdapter(getContext(), images.size(), images);
                viewPager.setAdapter(pagerAdapter);
                viewPager.setPageTransformer(true, new PagerDepthAnimation());

                toolbarLayout.setTitle(gameDetailModal.name);

                /*********************** SETTING RECYCLER VIEW ****************************/


                final String developer = listToString(gameDetailModal.developers);
                String publisher = listToString(gameDetailModal.publishers);
                String genres = listToString(gameDetailModal.genres);
                String theme = listToString(gameDetailModal.themes);
                String franchise = listToString(gameDetailModal.franchises);
                ArrayList<String> values = new ArrayList<String>();

                values.add(developer);
                values.add(publisher);
                values.add(theme);
                values.add(franchise);
                values.add(genres);

                ArrayList<String> key = new ArrayList<String>();
                key.add("Developer");
                key.add("Publisher");
                key.add("Theme");
                key.add("Franchise");
                key.add("Genres");


                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                adapter = new GameDetailRecyclerAdapter(key, values, GameDetailFragment.this, style);
                recyclerView.setAdapter(adapter);


                /************************** SETTING THE DESCRIPTION And BottomSheet*****************************/

                if (gameDetailModal.description != null) {




                    Document doc = Jsoup.parse(gameDetailModal.description);
                    completeDescription = doc.toString();

                    if (doc != null) {

                        Element info = doc.getElementsByTag("p").first();
                        if(info!=null)
                        description.setText(info.text());
                        description.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                mCommunicationInterface.loadWebView("http://www.giantbomb.com/grand-theft-auto-iv/3030-20457/");

                            }
                        });

                    }





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

                List<GameDetailCharacters> characters = gameDetailModal.characters;

                if (characters != null) {
                    Collections.sort(characters, new Comparator<GameDetailCharacters>() {
                        @Override
                        public int compare(GameDetailCharacters lhs, GameDetailCharacters rhs) {
                            return lhs.name.compareTo(rhs.name);
                        }
                    });


                    characterRecycleView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                    characterRecycleView.addItemDecoration(new DividerItemDecoration(getActivity()));
                    characterRecycleView.addItemDecoration(new DividerItemDecoration(getActivity()));
                    characterAdapter = new GameDetailCharacterAdapter(characters, characterImage, style, getContext());
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


                    similarGameRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
                    similarGameRecycleView.addItemDecoration(new VerticalSpaceItemDecoration(6));
                    similarGameAdapter = new SimilarGameAdapter(games, similarGameImage, style, GameDetailFragment.this, getContext());
                    similarGameRecycleView.setAdapter(similarGameAdapter);

                }








            }

            @Override
            public void onFailure(Call<GameDetailListModal> call, Throwable t) {


                overviewProgress.setVisibility(View.GONE);

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


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.game_detail_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {

            case R.id.fontSelect: {

                FontOptionFragment fontOptionFragment = FontOptionFragment.newInstance();
                fontOptionFragment.setTargetFragment(this, 0);
                fontOptionFragment.show(getActivity().getSupportFragmentManager(), "fontoption");


                break;
            }


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

        Toaster.make(getContext(),"Font will be changed after going back");


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
    public void onStop() {
        super.onStop();

        appBarLayout.addOnOffsetChangedListener(null);
        appBarLayout = null;
        gameWikiDetailInterface = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
