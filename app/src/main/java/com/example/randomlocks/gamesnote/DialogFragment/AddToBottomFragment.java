package com.example.randomlocks.gamesnote.DialogFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.randomlocks.gamesnote.R;

/**
 * Created by randomlocks on 6/27/2016.
 */
public class AddToBottomFragment extends BottomSheetDialogFragment {

    private BottomSheetBehavior mBehavior;


    public static AddToBottomFragment newInstance() {

        Bundle args = new Bundle();

        AddToBottomFragment fragment = new AddToBottomFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public AddToBottomFragment() {
        //empty constructor
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = inflater.inflate(R.layout.dialog_add_to_list, container, false);
        dialog.setContentView(view);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
}
