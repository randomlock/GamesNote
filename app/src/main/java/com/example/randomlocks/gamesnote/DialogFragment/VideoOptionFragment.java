package com.example.randomlocks.gamesnote.DialogFragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.SharedPreference;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.Modal.GamesVideoModal.GamesVideoModal;
import com.example.randomlocks.gamesnote.R;

/**
 * Created by randomlock on 3/13/2017.
 */

public class VideoOptionFragment extends DialogFragment {

    private static String VIDEO_OPTION = "video_option";
    private static String INBUILT_VIDEO = "inbuilt_player";
    GamesVideoModal modal;
    View view;
    Spinner video_spinner;
    CheckBox inbuilt_player_checkbox;
    int video_option;
    boolean use_inbuilt;
    OnPlayInterface onPlayInterface;

    public static VideoOptionFragment newInstance(GamesVideoModal modal) {

        Bundle args = new Bundle();
        args.putParcelable(GiantBomb.MODAL, modal);
        VideoOptionFragment fragment = new VideoOptionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onPlayInterface = (OnPlayInterface) getTargetFragment();
        } catch (Exception e) {
            Toaster.make(getContext(), "interface cast exception");
        }


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        modal = getArguments().getParcelable(GiantBomb.MODAL);
        video_option = SharedPreference.getFromSharedPreferences(VIDEO_OPTION, 1, getContext());
        use_inbuilt = SharedPreference.getFromSharedPreferences(INBUILT_VIDEO, true, getContext());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.dialog_video_option, null);
        final AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme)
                .setCancelable(false)
                .setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                })
                .setPositiveButton("Play", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreference.saveToSharedPreference(VIDEO_OPTION, video_spinner.getSelectedItemPosition(), getContext());
                        SharedPreference.saveToSharedPreference(INBUILT_VIDEO, inbuilt_player_checkbox.isChecked(), getContext());
                        onPlayInterface.onPlay(modal, video_spinner.getSelectedItemPosition(), inbuilt_player_checkbox.isChecked());
                        dismiss();
                    }
                }).create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
            }
        });
        return dialog;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        video_spinner = (Spinner) view.findViewById(R.id.spinner);
        inbuilt_player_checkbox = (CheckBox) view.findViewById(R.id.checkbox);
        inbuilt_player_checkbox.setChecked(use_inbuilt);
        video_spinner.setSelection(video_option);


    }


    public interface OnPlayInterface {
        void onPlay(GamesVideoModal modal, int video_option, boolean use_inbuilt);
    }


}
