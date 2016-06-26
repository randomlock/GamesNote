package com.example.randomlocks.gamesnote.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.randomlocks.gamesnote.HdImageViewerActivity;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.R;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by randomlocks on 4/28/2016.
 */
public class ImageViewerFragment extends DialogFragment   {

    ImageView imageView;
    ProgressBar progressBar;
    Button hdButton;
    String smallImageUrl;
    String mediumImageUrl;

    public static final String SMAll_IMAGE_URL = "smallURL";
    public static final String MEDIUM_IMAGE_URL="mediumURL";

    public static final ImageViewerFragment newInstance(String smallImageUrl, String MediumImageUrl) {

        Bundle args = new Bundle();
        args.putString(SMAll_IMAGE_URL,smallImageUrl);
        args.putString(MEDIUM_IMAGE_URL,MediumImageUrl);
        ImageViewerFragment fragment = new ImageViewerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        smallImageUrl = getArguments().getString(SMAll_IMAGE_URL);
        mediumImageUrl = getArguments().getString(MEDIUM_IMAGE_URL);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_image_viewer,container,false);
        getDialog().requestWindowFeature(STYLE_NO_TITLE);
        setCancelable(true);
        getDialog().setCanceledOnTouchOutside(true);


        imageView = (ImageView) v.findViewById(R.id.imageview);
        hdButton = (Button)v.findViewById(R.id.button);
        progressBar = (ProgressBar) v.findViewById(R.id.progress);
        Picasso.with(getContext()).load(smallImageUrl).fit().centerCrop().into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                Toaster.make(getContext(),"cannot load image");
            }
        });

        hdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

                Intent it = new Intent(getContext(), HdImageViewerActivity.class);
                int[] coordinate = new int[2];
                imageView.getLocationOnScreen(coordinate);
                it.putExtra(MEDIUM_IMAGE_URL, mediumImageUrl);
                startActivity(it);

            }
        });







        return v;
    }
}
