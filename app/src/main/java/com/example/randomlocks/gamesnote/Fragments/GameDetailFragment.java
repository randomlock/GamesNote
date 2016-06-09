package com.example.randomlocks.gamesnote.Fragments;


import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.Adapter.GameDetailCharacterAdapter;
import com.example.randomlocks.gamesnote.Adapter.GameDetailRecyclerAdapter;
import com.example.randomlocks.gamesnote.Adapter.SimilarGameAdapter;
import com.example.randomlocks.gamesnote.Adapter.WikiPagerAdapter;
import com.example.randomlocks.gamesnote.DialogFragment.FontOptionFragment;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.PagerDepthAnimation;
import com.example.randomlocks.gamesnote.HelperClass.SharedPreference;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.Interface.GameWikiDetailInterface;
import com.example.randomlocks.gamesnote.Modal.GameDetailModal.GameDetailImages;
import com.example.randomlocks.gamesnote.Modal.GameDetailModal.GameDetailListModal;
import com.example.randomlocks.gamesnote.Modal.GameDetailModal.GameDetailModal;
import com.example.randomlocks.gamesnote.R;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
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
    LinearLayout container;
    RecyclerView recyclerView, similarGameRecycleView, characterRecycleView;
    ImageView imageView;
    TextView description;
    GameDetailRecyclerAdapter adapter;
    int style;





    public GameDetailFragment() {
        // Required empty public constructor
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
        Toaster.make(getContext(), getArguments().getString(IMAGE_URL));
        return inflater.inflate(R.layout.fragment_game_detail, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        /************************************** TOOLBAR SET *********************************/
        toolbar = (Toolbar) getActivity().findViewById(R.id.my_toolbar);
        toolbarLayout = (CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsing_toolbar_layout);
        appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.app_bar_layout);
        similarGameRecycleView = (RecyclerView) getActivity().findViewById(R.id.similar_game_list);
        characterRecycleView = (RecyclerView) getActivity().findViewById(R.id.character_game_list);
        imageView = (ImageView) getActivity().findViewById(R.id.image_background);
        description = (TextView) getActivity().findViewById(R.id.description);
        style = selectStyle(SharedPreference.getFromSharedPreferences(GiantBomb.FONT, 1, getContext()) + 1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            description.setTextAppearance(style);
        } else {
            description.setTextAppearance(getContext(), style);
        }
        Picasso.with(getContext()).load(getArguments().getString(IMAGE_URL)).into(imageView);
     /*   container = (LinearLayout) getActivity().findViewById(R.id.layout_container);
        Picasso.with(getContext()).load(getArguments().getString(IMAGE_URL)).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                container.setBackground(new BitmapDrawable(getResources(),bitmap));
                container.getBackground().setAlpha((110));
                Toaster.make(getContext(),"done");
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Toaster.make(getContext(),"failed");

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Toaster.make(getContext(),"onload");

            }
        }); */

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


        recyclerView = (RecyclerView) getActivity().findViewById(R.id.list);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);


        // toolbar.setTitle("Game Detail");   //to be changed to game title

       /************************************** VIEWPAGER SET *********************************/
        viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);



        /**************** RETROFIT ************************/

        gameWikiDetailInterface = GiantBomb.createService(GameWikiDetailInterface.class);
        map = new HashMap<>();
        map.put(GiantBomb.KEY,GiantBomb.API_KEY);
        map.put(GiantBomb.FORMAT,"JSON");
        getGameDetail(gameWikiDetailInterface, map);

    }

    private void getGameDetail(final GameWikiDetailInterface gameWikiDetailInterface, Map<String, String> map) {

           gameWikiDetailInterface.getResult(apiUrl,map).enqueue(new Callback<GameDetailListModal>() {
               @Override
               public void onResponse(Call<GameDetailListModal> call, Response<GameDetailListModal> response) {
                  gameDetailModal =  response.body().results;
                   List<GameDetailImages> image = gameDetailModal.images;
                   ArrayList<String> images = new ArrayList<>();
                   for (int i = 0; i <image.size() ; i++) {
                       images.add(image.get(i).thumbUrl);
                   }

                   WikiPagerAdapter pagerAdapter = new WikiPagerAdapter(getContext(),images.size(),images);
                   viewPager.setAdapter(pagerAdapter);
                   viewPager.setPageTransformer(true, new PagerDepthAnimation());

                   toolbarLayout.setTitle(gameDetailModal.name);

                   /*********************** SETTING RECYCLER VIEW ****************************/


                   String developer = listToString(gameDetailModal.developers);
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


                   /************************** SETTING THE DESCRIPTION*****************************/


                   if (gameDetailModal.description != null) {


                       Document doc = Jsoup.parse(gameDetailModal.description);

                       if (doc != null) {

                           Elements info = doc.getElementsByTag("p");
                           description.setText(info.get(0).text());

                       }

                   }


                   /******************* SETTING THE CHARACTER **************************************/

                   characterRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
                   characterRecycleView.setAdapter(new GameDetailCharacterAdapter(gameDetailModal.characters, style, getContext()));


                   /******************** SETTING  THE SIMILAR GAMES ******************************/


                   similarGameRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));

                   similarGameRecycleView.setAdapter(new SimilarGameAdapter(gameDetailModal.similarGames, style, getContext()));





               }

               @Override
               public void onFailure(Call<GameDetailListModal> call, Throwable t) {
                   Toaster.make(getContext(), "connectivity problem");
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


        if (adapter != null) {
            int style = selectStyle(which + 1);
            adapter.setStyle(style);
            adapter.notifyDataSetChanged();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                description.setTextAppearance(style);
            } else {
                description.setTextAppearance(getContext(), style);
            }


        }


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
}
