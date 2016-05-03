package com.example.randomlocks.gamesnote.DialogFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.R;

/**
 * Created by randomlocks on 5/2/2016.
 */
public class SearchFilterFragment extends DialogFragment {

    boolean isAscending = true;
    CheckBox checkbox;
    View view;
    SearchFilterInterface searchFilterInterface=null;


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

    }



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        view = getActivity().getLayoutInflater().inflate(R.layout.search_option_layout,null);

       return new AlertDialog.Builder(getContext(),R.style.MyDialogTheme)
                .setCancelable(true)
                .setTitle("Sort Result")

                .setSingleChoiceItems(getResources().getStringArray(R.array.search_filter), 4, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        searchFilterInterface.onSelect(which,!checkbox.isChecked());
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


