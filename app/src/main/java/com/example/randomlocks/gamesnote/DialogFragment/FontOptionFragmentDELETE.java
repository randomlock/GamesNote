package com.example.randomlocks.gamesnote.DialogFragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.SharedPreference;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.R;

/**
 * Created by randomlocks on 6/9/2016.
 */
public class FontOptionFragmentDELETE extends DialogFragment {


    public interface FontOptionInterface {

        void onSelect(int which);
    }

    FontOptionInterface mFontOptionInterface;
    int which_one;


    public static FontOptionFragmentDELETE newInstance() {

        Bundle args = new Bundle();

        FontOptionFragmentDELETE fragment = new FontOptionFragmentDELETE();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mFontOptionInterface = (FontOptionInterface) getTargetFragment();
        } catch (Exception e) {
            Toaster.make(getContext(), "interface cast exception");
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        which_one = SharedPreference.getFromSharedPreferences(GiantBomb.FONT, 1, getContext());
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext(), R.style.MyDialogTheme)
                .setCancelable(true)
                .setTitle("Choose Font")

                .setSingleChoiceItems(getResources().getStringArray(R.array.Font_Option), which_one, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == which_one)
                            return;

                        SharedPreference.saveToSharedPreference(GiantBomb.FONT, which, getContext());
                        mFontOptionInterface.onSelect(which);
                        dismiss();
                    }
                })
                .setCancelable(true)
                .create();
    }
}
