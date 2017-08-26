package com.example.randomlocks.gamesnote.dialogFragment;

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
import android.widget.CompoundButton;
import android.widget.Spinner;

import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.helperClass.GiantBomb;
import com.example.randomlocks.gamesnote.helperClass.SharedPreference;
import com.example.randomlocks.gamesnote.helperClass.Toaster;
import com.example.randomlocks.gamesnote.modals.gamesVideoModal.GamesVideoModal;

public class VideoOptionFragment extends DialogFragment {

    private static final String VIDEO_OPTION = "video_option";
    private static final String INBUILT_VIDEO = "inbuilt_player";
    private static final String ELAPSED_TIME = "elapsed_time";
    GamesVideoModal modal;
    View view;
    Spinner video_spinner;
    CheckBox inbuilt_player_checkbox, elapsed_time_checkbox;
    int video_option;
    boolean use_inbuilt;
    OnPlayInterface onPlayInterface;

    int elapsed_time;


    public static VideoOptionFragment newInstance(GamesVideoModal modal, int elapsed_time) {

        Bundle args = new Bundle();
        args.putParcelable(GiantBomb.MODAL, modal);
        args.putInt(ELAPSED_TIME, elapsed_time);
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
        elapsed_time = getArguments().getInt(ELAPSED_TIME, 0);
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
                        onPlayInterface.onPlay(modal, video_spinner.getSelectedItemPosition(), inbuilt_player_checkbox.isChecked(), elapsed_time_checkbox.isChecked() ? elapsed_time : 0);
                        dismiss();
                    }
                }).create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.black_white));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.accent));
            }
        });
        return dialog;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        video_spinner = (Spinner) view.findViewById(R.id.spinner);
        inbuilt_player_checkbox = (CheckBox) view.findViewById(R.id.checkbox);
        elapsed_time_checkbox = (CheckBox) view.findViewById(R.id.resume_checkbox);
        elapsed_time_checkbox.setVisibility(elapsed_time > 0 ? View.VISIBLE : View.INVISIBLE);

        inbuilt_player_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && elapsed_time > 0) {
                    elapsed_time_checkbox.setVisibility(View.VISIBLE);
                } else {
                    elapsed_time_checkbox.setVisibility(View.INVISIBLE);
                }
            }
        });

        inbuilt_player_checkbox.setChecked(use_inbuilt);
        video_spinner.setSelection(video_option);


    }


    public interface OnPlayInterface {
        void onPlay(GamesVideoModal modal, int video_option, boolean use_inbuilt, int elapsed_time);
    }


}
