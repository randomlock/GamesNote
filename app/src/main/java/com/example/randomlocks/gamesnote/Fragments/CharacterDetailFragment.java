package com.example.randomlocks.gamesnote.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.PicassoNestedScrollView;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.Interface.GameCharacterInterface;
import com.example.randomlocks.gamesnote.Modal.GameCharacterModal.CharacterListModal;
import com.example.randomlocks.gamesnote.Modal.GameCharacterModal.CharacterModal;
import com.example.randomlocks.gamesnote.R;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by randomlocks on 6/25/2016.
 */
public class CharacterDetailFragment extends Fragment {

    private static final String API_URL = "api_url" ;
    private static final String IMAGE_URL = "image_url" ;
    String apiUrl,imageUrl;
    ImageView coverImage;
    TextView mSmallDescription,mBigDescription,mFirstAppearance,mAlias;
    RecyclerView recyclerview;
    LinearLayout parentLayout;
    GameCharacterInterface mGameCharacterInterface;
    Map<String,String> map;
    CharacterModal characterDetailModal;
    TextView mGender,mBirthDay,mTotalGames;
    PicassoNestedScrollView scrollView;
    Toolbar toolbar;



    public CharacterDetailFragment(){
        //empty construtor
    }


    public static CharacterDetailFragment newInstance(String apiUrl,String imageUrl) {

        Bundle args = new Bundle();
        args.putString(API_URL,apiUrl);
        args.putString(IMAGE_URL,imageUrl);
        CharacterDetailFragment fragment = new CharacterDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        String str[] = getArguments().getString(API_URL).split("/");
        apiUrl = str[str.length- 1];
        imageUrl = getArguments().getString(IMAGE_URL);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_character_detail,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



        scrollView = (PicassoNestedScrollView) getView().findViewById(R.id.scroll_view);
        parentLayout = (LinearLayout) getView().findViewById(R.id.parentLinearLayout);
        toolbar = (Toolbar) parentLayout.findViewById(R.id.my_toolbar);
        coverImage = (ImageView) parentLayout.findViewById(R.id.character_image);
        mGender = (TextView) parentLayout.findViewById(R.id.gender);
        mBirthDay = (TextView) parentLayout.findViewById(R.id.birthday);
        mTotalGames = (TextView) parentLayout.findViewById(R.id.games);
        mSmallDescription = (TextView) parentLayout.findViewById(R.id.deck);
        mBigDescription = (TextView) parentLayout.findViewById(R.id.description);
        mFirstAppearance = (TextView) parentLayout.findViewById(R.id.first_appearance);
        mAlias = (TextView) parentLayout.findViewById(R.id.alias);
        recyclerview = (RecyclerView) parentLayout.findViewById(R.id.recycler_view);
        recyclerview.setNestedScrollingEnabled(false);




        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        Picasso.with(getContext()).load(imageUrl).fit().centerCrop().into(coverImage);

        mGameCharacterInterface = GiantBomb.createGameCharacterService();
        map = new HashMap<>();
        map.put(GiantBomb.KEY,GiantBomb.API_KEY);
        map.put(GiantBomb.FORMAT, "JSON");
        getCharacterDetail(mGameCharacterInterface, map);


    }

    private void getCharacterDetail(final GameCharacterInterface mGameCharacterInterface, final Map<String, String> map) {

       mGameCharacterInterface.getResult(apiUrl,map).enqueue(new Callback<CharacterListModal>() {
           @Override
           public void onResponse(Call<CharacterListModal> call, Response<CharacterListModal> response) {
               characterDetailModal = response.body().results;

               Picasso.with(getContext()).load(characterDetailModal.image.mediumUrl).fit().centerCrop().noFade().into(coverImage);

               mGender.setText(getGender(characterDetailModal.gender));

               mTotalGames.setText(String.valueOf(characterDetailModal.games.size()));
               if (characterDetailModal.birthday!=null) {
                   mBirthDay.setText(characterDetailModal.birthday);
               }
               String description = "\""+characterDetailModal.deck+"\"";
               mSmallDescription.setText(description);
               mFirstAppearance.setText(characterDetailModal.firstAppearedInGame.name);
               if (characterDetailModal.aliases!=null) {
                   mAlias.setText(characterDetailModal.aliases);
               }
               Document doc = null;
               if (characterDetailModal.description!=null) {
                   doc = Jsoup.parse(characterDetailModal.description);
                   Element info = doc.getElementsByTag("p").first();
                   if(info!=null)
                       mBigDescription.setText(info.text());
               }






           }

           @Override
           public void onFailure(Call<CharacterListModal> call, Throwable t) {
               Toaster.make(getContext(),"Cannot Connect .Try Later");
           }
       });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {


            case android.R.id.home :
                getActivity().getSupportFragmentManager().popBackStack();
                break;

        }


        return true;
    }

    private String getGender(int gender) {

        String gen;

        switch (gender){
            case 1 : gen = "Male";
                break;
            case 2 : gen = "Female";
                break;
                default: gen="-";

        }
        return gen;

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }




}
