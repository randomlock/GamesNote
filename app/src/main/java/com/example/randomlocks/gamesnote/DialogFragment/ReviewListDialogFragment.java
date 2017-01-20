package com.example.randomlocks.gamesnote.DialogFragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.R;

/**
 * Created by randomlocks on 7/10/2016.
 */
public class ReviewListDialogFragment extends DialogFragment {

    CharSequence[] list;
    CommunicationInterface mCommunicationInterface = null;
    boolean isGameReview;

    public interface CommunicationInterface {
        void onItemSelected(int position, boolean isGameReview);
    }

    public static ReviewListDialogFragment newInstance(CharSequence[] list, boolean isGameReview) {

        Bundle args = new Bundle();
        args.putCharSequenceArray(GiantBomb.MODAL, list);
        args.putBoolean(GiantBomb.IS_GAME_REVIEW, isGameReview);
        ReviewListDialogFragment fragment = new ReviewListDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCommunicationInterface = (CommunicationInterface) getTargetFragment();
        } catch (Exception e) {
            Toaster.make(getContext(), "interface cast exception");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = getArguments().getCharSequenceArray(GiantBomb.MODAL);
        isGameReview = getArguments().getBoolean(GiantBomb.IS_GAME_REVIEW);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
        builder.setTitle(R.string.list_fragment_title)
                .setItems(list, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mCommunicationInterface.onItemSelected(which, isGameReview);
                    }
                });
        return builder.create();
    }


}
