package com.example.randomlocks.gamesnote.helperClass;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by randomlocks on 5/1/2016.
 */
public class InputMethodHelper {


    public static void hideKeyBoard(View v, Context context) {

        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);


    }

    public static void showKeybaord(View v, Context context) {

        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInputFromInputMethod(v.getWindowToken(), 0);

    }


}
