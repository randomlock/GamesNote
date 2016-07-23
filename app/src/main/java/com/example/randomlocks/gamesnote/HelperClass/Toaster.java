package com.example.randomlocks.gamesnote.HelperClass;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by randomlocks on 3/10/2016.
 */
public class Toaster {


    public static void make(Context context, String title) {
        Toast.makeText(context, title, Toast.LENGTH_SHORT).show();
    }
}
