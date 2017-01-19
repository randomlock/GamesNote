package com.example.randomlocks.gamesnote.DialogFragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.SharedPreference;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.R;

/**
 * Created by randomlocks on 5/2/2016.
 */
public class SearchFilterFragment extends DialogFragment {

    boolean isAscending;
    CheckBox checkbox;
    View view;
    SearchFilterInterface searchFilterInterface = null;
    int which_one;
    int array_id;


    public interface SearchFilterInterface {

        void onSelect(int which, boolean asc);
    }


    public static SearchFilterFragment newInstance(@ArrayRes int array_id) {

        Bundle args = new Bundle();
        args.putInt(GiantBomb.ARRAY,array_id);
        SearchFilterFragment fragment = new SearchFilterFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            searchFilterInterface = (SearchFilterInterface) getTargetFragment();
        } catch (Exception e) {
            Toaster.make(getContext(), "interface cast exception");
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        array_id = getArguments().getInt(GiantBomb.ARRAY);
        if (array_id==R.array.search_filter) {
            which_one = SharedPreference.getFromSharedPreferences(GiantBomb.WHICH, 4, getContext());
            isAscending = SharedPreference.getFromSharedPreferences(GiantBomb.ASCENDING, true, getContext());
        }else {
            which_one = SharedPreference.getFromSharedPreferences(GiantBomb.SORT_WHICH,1,getContext());
            isAscending = SharedPreference.getFromSharedPreferences(GiantBomb.SORT_ASCENDING,true,getContext());
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        view = getActivity().getLayoutInflater().inflate(R.layout.search_option_layout, null);

        final AlertDialog dialog =  new AlertDialog.Builder(getContext(),R.style.MyDialogTheme)
                .setCancelable(true)
                .setTitle("Sort Result")

                .setSingleChoiceItems(getResources().getStringArray(array_id), which_one, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        which_one = which;
                    }
                })
                .setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                })
                .setPositiveButton("Sort", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (array_id == R.array.search_filter) {
                            SharedPreference.saveToSharedPreference(GiantBomb.WHICH, which_one, getContext());
                            SharedPreference.saveToSharedPreference(GiantBomb.ASCENDING, !checkbox.isChecked(), getContext());
                        }else {
                            SharedPreference.saveToSharedPreference(GiantBomb.SORT_WHICH, which_one, getContext());
                            SharedPreference.saveToSharedPreference(GiantBomb.SORT_ASCENDING, !checkbox.isChecked(), getContext());
                        }
                        searchFilterInterface.onSelect(which_one, !checkbox.isChecked());
                        dismiss();
                    }
                }).create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(),R.color.primary));
                    }
                });
                return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        checkbox = (CheckBox) view.findViewById(R.id.checkbox);
        final int check_color = ContextCompat.getColor(getContext(),R.color.accent);
        final int uncheck_color = ContextCompat.getColor(getContext(),R.color.black_white);
        checkbox.setChecked(!isAscending);
        if (!checkbox.isChecked()) { checkbox.setTextColor(uncheck_color) ;}



    }


}


