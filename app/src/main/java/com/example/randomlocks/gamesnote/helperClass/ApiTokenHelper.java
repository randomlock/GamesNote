package com.example.randomlocks.gamesnote.helperClass;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.helperClass.WebViewHelper.CustomTabActivityHelper;
import com.example.randomlocks.gamesnote.helperClass.WebViewHelper.WebViewFallback;
import com.example.randomlocks.gamesnote.modals.apiTokenModel.ApiTokenModel;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by randomlock on 8/9/2017.
 */

public class ApiTokenHelper {

    private Context context;
    private boolean is_browser_open;
    private ApiTokenHelperInterface apiTokenHelperInterface;
    private Map<String, String> map;

    public ApiTokenHelper(Context context, ApiTokenHelperInterface apiTokenHelperInterface) {
        this.context = context;
        this.apiTokenHelperInterface = apiTokenHelperInterface;
    }

    public void getApiTokenCode() {
        openApiDialog();
    }

    private void openApiDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setMessage("A browser will open where you need to sign up a " +
                        "giantbomb account. After successful signup, just close" +
                        " the browser and activate your account and login in next step to generate " +
                        "your api key. If you already have a giantbomb account , skip this")
                .setTitle("Step 1 of 2")
                .setPositiveButton("Open browser", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        runBrowser();
                    }
                })
                .setNegativeButton("Skip", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getLoginInfo();
                    }
                })
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.black_white));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.primary));

            }
        });
        dialog.show();

    }

    private void runBrowser() {
        is_browser_open = true;
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().setShowTitle(true).addDefaultShareMenuItem().build();
        CustomTabActivityHelper.openCustomTab(
                (Activity) context, customTabsIntent, Uri.parse("https://auth.giantbomb.com/signup/"), new WebViewFallback());
    }

    public boolean is_browser_open() {
        return is_browser_open;
    }

    public void setIs_browser_open(boolean is_browser_open) {
        this.is_browser_open = is_browser_open;
    }

    public void getLoginInfo() {
        openLoginDialog();
    }

    private void openLoginDialog() {
        LayoutInflater li = LayoutInflater.from(context);
        View view = li.inflate(R.layout.dialog_login, null);
        final AlertDialog dialog;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(view);
        final TextInputLayout user_name_label = (TextInputLayout) view.findViewById(R.id.input_user_name_label);
        final TextInputEditText user_name = (TextInputEditText) user_name_label.findViewById(R.id.input_user_name);
        final TextInputLayout password_label = (TextInputLayout) view.findViewById(R.id.input_password_label);
        final TextInputEditText password = (TextInputEditText) password_label.findViewById(R.id.input_password);


        alertDialogBuilder.setCancelable(false)
                .setTitle("Step 2 of 2")
                .setPositiveButton("Get Api", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Do nothing since it always dismiss the dialog box
                    }
                });


        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();

            }
        });
        dialog = alertDialogBuilder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.black_white));
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setTextColor(ContextCompat.getColor(context, R.color.primary));
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String username = user_name.getText().toString();
                        String pass = password.getText().toString();
                        if (username.trim().length() == 0) {
                            user_name_label.setErrorEnabled(true);
                            user_name_label.setError("Empty username");
                        } else {
                            user_name_label.setErrorEnabled(false);
                            user_name_label.setError(null);
                        }
                        if (pass.trim().length() == 0) {
                            password_label.setErrorEnabled(true);
                            password_label.setError("Empty password");

                        } else {
                            password_label.setErrorEnabled(false);
                            password_label.setError(null);

                        }
                        if (username.length() > 0 && pass.length() > 0) {
                            new MyAsyncTask().execute(username, pass);
                            dialog.dismiss();
                        }
                    }
                });


            }
        });
        dialog.show();

    }

    private void generateApiTokenFromApiCode(String api_token_code) {
        if (map == null) {
            map = new HashMap<>();
            map.put(GiantBomb.FORMAT, "json");
            String arr[] = api_token_code.split(" ");
            map.put(GiantBomb.REG_CODE, arr[arr.length - 1]);
        }
        GiantBomb.createApiTokenHelper().getResult(map).enqueue(new Callback<ApiTokenModel>() {
            @Override
            public void onResponse(Call<ApiTokenModel> call, Response<ApiTokenModel> response) {
                ApiTokenModel apiTokenModel = response.body();
                Log.d("tag1", response.raw().request().url().toString());
                apiTokenHelperInterface.onApiTest(apiTokenModel);
            }

            @Override
            public void onFailure(Call<ApiTokenModel> call, Throwable t) {

            }
        });
    }


    public interface ApiTokenHelperInterface {
        void onPreApiGenerate();

        void onPostApiGenerate(String api_key_code);

        void onApiTest(ApiTokenModel api_key);
    }

    private class MyAsyncTask extends AsyncTask<String, Void, String> {

        String url = "https://auth.giantbomb.com/app/" + context.getResources().getString(R.string.app_name) + "/";
        Document document;

        @Override
        protected void onPreExecute() {
            apiTokenHelperInterface.onPreApiGenerate();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                System.setProperty("http.keepAlive", "false");
                Connection.Response response = Jsoup.connect("https://auth.giantbomb.com/app/myapp")
                        .method(Connection.Method.GET)
                        .execute();


                Connection.Response res = Jsoup.connect("https://auth.giantbomb.com/check-login/")
                        .data("form[_username]", params[0], "form[_password]", params[1])
                        .cookies(response.cookies())
                        .method(Connection.Method.POST)
                        .execute();


                document = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                        .cookies(res.cookies())
                        .get();
                String title = document.title();
                Log.d("tag1", title);

                if (title.equals("Login"))
                    return null;

                Element outer_section = document.body().getElementsByTag("section").first();
                Element inner_section = outer_section.getElementsByTag("section").first();
                return inner_section.getElementsByTag("h2").text();

            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {

            apiTokenHelperInterface.onPostApiGenerate(s);
            if (s != null) {
                generateApiTokenFromApiCode(s);
            }

        }
    }


}
