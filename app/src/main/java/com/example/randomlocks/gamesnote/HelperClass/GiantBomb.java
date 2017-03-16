package com.example.randomlocks.gamesnote.HelperClass;

import android.util.Log;

import com.example.randomlocks.gamesnote.ExampleApplication;
import com.example.randomlocks.gamesnote.Interface.GameCharacterInterface;
import com.example.randomlocks.gamesnote.Interface.GameCharacterSearchWikiInterface;
import com.example.randomlocks.gamesnote.Interface.GameDetailVideoInterface;
import com.example.randomlocks.gamesnote.Interface.GameReviewInterface;
import com.example.randomlocks.gamesnote.Interface.GameWikiDetailInterface;
import com.example.randomlocks.gamesnote.Interface.GameWikiListInterface;
import com.example.randomlocks.gamesnote.Interface.GamesVideosInterface;
import com.example.randomlocks.gamesnote.Interface.UserReviewInterface;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by randomlocks on 4/24/2016.
 */


//TODO run restadapter and okhttp adatper in separate thread to remove skipped frame problem

public class GiantBomb {


    public static final String BASE_URL = "http://www.giantbomb.com/api/";
    public static final String KEY = "api_key";
    public static final String API_KEY = "b318d66445bfc79e6d74a65fe52744b45b345948";
    public static final String YOUTUBE_API_KEY = "AIzaSyBU189k9G4pa1nej-1SsIzLWVihafwLNAA";
    public static final String FORMAT = "format";
    public static final String SORT = "sort";
    public static final String FILTER = "filter";
    public static final String FIELD_LIST = "field_list";
    public static final String LIMIT = "limit";
    public static final String OFFSET = "offset";
    public static final String FIELD = "filter";
    public static final String WHICH = "which";
    public static final int SMALL_IMAGE_URL = 10;
    public static final int MEDIUM_IMAGE_URL = 11;
    public static final String ASCENDING = "ascending";
    public static final String FONT = "fontoption";
    public static final String NAV_HEADER_URL = "navHeaderUrl";
    public static final String TITLE = "gameTitle";
    public static final String REVIEW = "gamereview";
    public static final String IMAGE_URL = "imageUrl";
    public static final String MODAL = "listModal";
    public static final String IS_GAME_REVIEW = "isGameReview";
    public static final String POSITION = "position";
    public static final String VIDEO_TYPE = "video_type";
    public static final String API_URL = "apiUrl";
    public static final String REDUCE_NEWS_VIEW = "reduce_news_view";
    public static final String PLATFORM = "platform";
    public static final String ARRAY = "res_array";
    public static final String SORT_WHICH = "sort_which" ;
    public static final String SORT_ASCENDING = "sort_ascending" ;
    public static final String IS_GAME_DETAIL = "is_game_detail";
    public static final String REDUCE_LIST_VIEW = "reduce_list_view";
    public static final  String SEEK_POSITION = "seek_position";
    public static final String VIEW_TYPE = "view_type" ;
    public static final int ALL_GAMES = 0 ;
    public static final int REPLAYING = 1;
    public static final int PLANNING = 2;
    public static final int DROPPED = 3;
    public static final int PLAYING = 4;
    public static final int COMPLETED = 5;
    public static String REDUCE_VIEW = "reduce_view";
    private static OkHttpClient httpClient = null;
    private static Request request = null;
    private static Retrofit retrofit = null;
    private static GameWikiListInterface gameWikiListInterface = null;
    private static GameWikiDetailInterface gameWikiDetailInterface = null;
    private static GameCharacterInterface gameCharacterInterface = null;
    private static GameReviewInterface gameReviewInterface = null;
    private static UserReviewInterface userReviewInterface = null;
    private static GameCharacterSearchWikiInterface gameCharacterSearchWikiInterface = null;
    private static GamesVideosInterface gamesVideoInterface = null;
    private static GameDetailVideoInterface gameDetailVideoInterface = null;

    private GiantBomb() {
    }

    public static Request getRequest(String url, String tag) {
        if (request == null) {
            request = new Request.Builder()
                    .url(url)
                    .tag(tag)
                    .build();

        } else if (!request.url().toString().equals(url)) {
            request = request.newBuilder().url(url).tag(tag).build();

        }

        return request;
    }

    public static void cancelCallWithTag(OkHttpClient client, String tag) {
        for (Call call : client.dispatcher().queuedCalls()) {
            if (call.request().tag().equals(tag))
                call.cancel();
        }
        for (Call call : client.dispatcher().runningCalls()) {
            if (call.request().tag().equals(tag))
                call.cancel();
        }
    }

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            Log.d("net", "one instance");
            Gson gson = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();


            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(getHttpClient())
                    .build();
        }

        return retrofit;
    }

    public static OkHttpClient getHttpClient() {
        if (httpClient == null) {


            final File httpCacheDirectory = new File(ExampleApplication.getInstance().getCacheDir(), "responses");
            int cacheSize = 20 * 1024 * 1024; // 20 MiB
            final Cache cache = new Cache(httpCacheDirectory, cacheSize);

            httpClient = new OkHttpClient.Builder()
                    .readTimeout(30, TimeUnit.SECONDS)
                    .cache(cache)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(provideOfflineCacheInterceptor())
                    .addNetworkInterceptor(provideCacheInterceptor())
                    .build();

        }

        return httpClient;
    }

    public static Interceptor provideOfflineCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                 /*    Log.d("net", String.valueOf(isOnline(context)));

                            Request request = chain.request();


                            if (isOnline(context)) {
                                request = request.newBuilder().header("Cache-Control", "public, max-age=" + 60).build();
                            } else {

                                request = request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7).build();
                            }


                            return chain.proceed(request); */

                          /*  Request originalRequest = chain.request();
                            String cacheHeaderValue = isOnline(context)
                                    ? "public, max-age=60"
                                    : "public, only-if-cached, max-stale=2419200" ;
                            Request request = originalRequest.newBuilder().header("Cache-Control", cacheHeaderValue).build();
                            Response response = null;
                            try {
                                response = chain.proceed(request);
                            } catch (IOException e) {
                                Log.d("net","exception");
                            }
                            return response.newBuilder()
                                    .removeHeader("Pragma")
                                    .build();*/




        /*        Request request = chain.request();

                if ( !ExampleApplication.hasNetwork() )
                {
                    CacheControl cacheControl = new CacheControl.Builder()
                            .maxStale( 7, TimeUnit.DAYS )
                            .build();

                    request = request.newBuilder()
                            .cacheControl( cacheControl )
                            .build();
                }

                try {
                    return chain.proceed( request );
                } catch (IOException e) {
                    Log.d("tag","exception 1");
                }
                return null; */


                Request request = chain.request();

                Response response = chain.proceed(request);

                if (!ExampleApplication.hasNetwork()) {
                    // 无网络时，设置超时为4周
                    int maxStale = 60 * 60 * 24 * 7;
                    response.newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                            .removeHeader("X-Phoenix-Cache-Expires")
                            .removeHeader("X-Phoenix-Cached")
                            .build();
                }
                return response;


            }
        };
    }

    public static Interceptor provideCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Response response = chain.proceed(chain.request());


                // re-write response header to force use of cache
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxAge(2, TimeUnit.MINUTES)
                        .build();


                return response.newBuilder()
                        .header("Cache-Control", cacheControl.toString())
                        .build();
            }
        };
    }

    public static GameWikiListInterface createGameWikiService() {
        if (gameWikiListInterface == null) {
            gameWikiListInterface = getRetrofit().create(GameWikiListInterface.class);
        }

        return gameWikiListInterface;
    }

    public static GameWikiDetailInterface createGameDetailService() {
        if (gameWikiDetailInterface == null) {
            gameWikiDetailInterface = getRetrofit().create(GameWikiDetailInterface.class);
        }

        return gameWikiDetailInterface;
    }

    public static GameCharacterInterface createGameCharacterService() {
        if (gameCharacterInterface == null) {
            gameCharacterInterface = getRetrofit().create(GameCharacterInterface.class);
        }
        return gameCharacterInterface;
    }

    public static GameReviewInterface createGameReviewService() {
        if (gameReviewInterface == null) {
            gameReviewInterface = getRetrofit().create(GameReviewInterface.class);
        }
        return gameReviewInterface;
    }

    public static UserReviewInterface createUserReviewService() {
        if (userReviewInterface == null) {
            userReviewInterface = getRetrofit().create(UserReviewInterface.class);
        }
        return userReviewInterface;
    }

    public static GameCharacterSearchWikiInterface createGameCharacterSearchService() {
        if (gameCharacterSearchWikiInterface == null) {
            gameCharacterSearchWikiInterface = getRetrofit().create(GameCharacterSearchWikiInterface.class);
        }
        return gameCharacterSearchWikiInterface;
    }

    public static GamesVideosInterface createGameVideoService() {
        if (gamesVideoInterface == null) {
            gamesVideoInterface = getRetrofit().create(GamesVideosInterface.class);
        }
        return gamesVideoInterface;
    }

    public static GameDetailVideoInterface createGameDetailVideoService() {
        if (gameDetailVideoInterface == null) {
            gameDetailVideoInterface = getRetrofit().create(GameDetailVideoInterface.class);
        }
        return gameDetailVideoInterface;
    }


}
