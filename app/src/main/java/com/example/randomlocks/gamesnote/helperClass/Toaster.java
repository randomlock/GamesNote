package com.example.randomlocks.gamesnote.helperClass;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

/**
 * Created by randomlocks on 3/10/2016.
 */
public class Toaster {


    public static void make(Context context, String title) {
        Toast.makeText(context, title, Toast.LENGTH_SHORT).show();
    }

    public static void makeSnackBar(View view, String title,String retryText, View.OnClickListener listener) {
        Snackbar.make(view, title, Snackbar.LENGTH_INDEFINITE).setAction(retryText,listener).show();
    }

   public static void makeSnackBar(View view,String title){
        Snackbar.make(view,title,Snackbar.LENGTH_SHORT).show();
    }

}
