package com.example.randomlocks.gamesnote.HelperClass;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).connectTimeout(30, TimeUnit.SECONDS);
    private static OkHttpClient client = new OkHttpClient();
    private static Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();




    private static Retrofit.Builder builder =
            new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson));

    public static <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);

    }


    public static String runOkHttp(String url) throws IOException {


        Request request = new Request.Builder()
                .url(url)
                .build();
        final String[] result = new String[1];

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                result[0] = response.body().string();


            }
        });


        return result[0];


    }





}
