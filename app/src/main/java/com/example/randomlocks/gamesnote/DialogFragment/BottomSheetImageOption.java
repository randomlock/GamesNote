package com.example.randomlocks.gamesnote.DialogFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.randomlocks.gamesnote.R;

/**
 * Created by randomlocks on 6/17/2016.
 */
public class BottomSheetImageOption extends BottomSheetDialogFragment {

    BottomSheetBehavior sheetBehavior;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_to_list, container, false);
        RelativeLayout linearLayout = (RelativeLayout) view.findViewById(R.id.image);


        sheetBehavior = BottomSheetBehavior.from(linearLayout);
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        return view;
    }
}
