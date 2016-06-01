package com.example.randomlocks.gamesnote.DialogFragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
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
    SearchFilterInterface searchFilterInterface=null;
    int which_one;


    public interface SearchFilterInterface{

        public void onSelect(int which,boolean asc);
    }


    public static final SearchFilterFragment newInstance() {

        Bundle args = new Bundle();

        SearchFilterFragment fragment = new SearchFilterFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            searchFilterInterface = (SearchFilterInterface) getTargetFragment();
        } catch (Exception e){
            Toaster.make(getContext(),"interface cast exception");
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        which_one = SharedPreference.getFromSharedPreferences(GiantBomb.WHICH,4,getContext());
        isAscending = SharedPreference.getFromSharedPreferences(GiantBomb.ASCENDING,true,getContext());
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        view = getActivity().getLayoutInflater().inflate(R.layout.search_option_layout,null);

       return new AlertDialog.Builder(getContext(),R.style.MyDialogTheme)
                .setCancelable(true)
                .setTitle("Sort Result")

                .setSingleChoiceItems(getResources().getStringArray(R.array.search_filter), which_one, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == which_one )
                            dismiss();

                        SharedPreference.saveToSharedPreference(GiantBomb.WHICH,which,getContext());
                        SharedPreference.saveToSharedPreference(GiantBomb.ASCENDING,!checkbox.isChecked(),getContext());
                        searchFilterInterface.onSelect(which, !checkbox.isChecked());
                        dismiss();
                    }
                })
                .setView(view)
                .create();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        checkbox = (CheckBox) view.findViewById(R.id.checkbox);


    }


}


