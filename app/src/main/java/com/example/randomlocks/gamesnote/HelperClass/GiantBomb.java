package com.example.randomlocks.gamesnote.HelperClass;

import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.example.randomlocks.gamesnote.Interface.GameCharacterInterface;
import com.example.randomlocks.gamesnote.Interface.GameWikiDetailInterface;
import com.example.randomlocks.gamesnote.Interface.GameWikiListInterface;
import com.example.randomlocks.gamesnote.Modal.NewsModal.NewsModal;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.Util;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by randomlocks on 4/24/2016.
 */

public class GiantBomb {


    public static final String BASE_URL = "http://www.giantbomb.com/api/";
    public static final String KEY="api_key";
    public static final String API_KEY="b318d66445bfc79e6d74a65fe52744b45b345948";
    public static final String FORMAT = "format";
    public static final String SORT = "sort";
    public static final String FILTER = "filter";
    public static final String FIELD_LIST="field_list";
    public static final String LIMIT="limit";
    public static final String OFFSET="offset";
    public static final String FIELD="filter";
    public static final String WHICH = "which";
    public static final int SMALL_IMAGE_URL=1;
    public static final int MEDIUM_IMAGE_URL=2;
    public static final String ASCENDING = "ascending";
    public static final String FONT = "fontoption";
    public static final String NAV_HEADER_URL = "navHeaderUrl" ;


    private GiantBomb(){
    }


    private static OkHttpClient httpClient = new OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build();
    private static Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    private static Request request = null;

    public static Request getRequest(String url) {
        if(request==null){
            request = new Request.Builder()
                    .url(url)
                    .build();
        }

        return request;
    }

    private static Retrofit RETROFIT =
            new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient)
            .build();

    private static GameWikiListInterface gameWikiListInterface = null;
    private static GameWikiDetailInterface gameWikiDetailInterface = null;
    private static GameCharacterInterface gameCharacterInterface = null;


    public static OkHttpClient getHttpClient() {
        return httpClient;
    }

    public static GameWikiListInterface createGameWikiService(){
       if(gameWikiListInterface==null){
           gameWikiListInterface = RETROFIT.create(GameWikiListInterface.class);
       }

       return gameWikiListInterface;
   }

    public static GameWikiDetailInterface createGameDetailService(){
        if(gameWikiDetailInterface==null){
            gameWikiDetailInterface = RETROFIT.create(GameWikiDetailInterface.class);
        }

        return gameWikiDetailInterface;
    }

    public static GameCharacterInterface createGameCharacterService(){
        if(gameCharacterInterface==null){
            gameCharacterInterface = RETROFIT.create(GameCharacterInterface.class);
        }

        return gameCharacterInterface;
    }



}
