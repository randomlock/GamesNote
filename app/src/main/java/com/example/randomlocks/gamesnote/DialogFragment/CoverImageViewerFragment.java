package com.example.randomlocks.gamesnote.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.randomlocks.gamesnote.Activity.ImageViewPagerActivity;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

/**
 * Created by randomlocks on 4/28/2016.
 */
public class CoverImageViewerFragment extends DialogFragment {

    ImageView imageView;
    ProgressBar progressBar;
    Button hdButton;
    String smallImageUrl;
    String mediumImageUrl;
    String title;

    public static final String SMAll_IMAGE_URL = "smallURL";
    public static final String MEDIUM_IMAGE_URL = "mediumURL";

    public static final CoverImageViewerFragment newInstance(String smallImageUrl, String MediumImageUrl, String title) {

        Bundle args = new Bundle();
        args.putString(SMAll_IMAGE_URL, smallImageUrl);
        args.putString(MEDIUM_IMAGE_URL, MediumImageUrl);
        args.putString(GiantBomb.TITLE,title);
        CoverImageViewerFragment fragment = new CoverImageViewerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyDialogTheme);
        smallImageUrl = getArguments().getString(SMAll_IMAGE_URL);
        mediumImageUrl = getArguments().getString(MEDIUM_IMAGE_URL);
        title = getArguments().getString(GiantBomb.TITLE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_image_viewer, container, false);
        getDialog().requestWindowFeature(STYLE_NO_TITLE);
        setCancelable(true);
        getDialog().setCanceledOnTouchOutside(true);


        imageView = (ImageView) v.findViewById(R.id.imageview);
        hdButton = (Button) v.findViewById(R.id.button);
        progressBar = (ProgressBar) v.findViewById(R.id.progress);
        Picasso.with(getContext()).load(mediumImageUrl).fit().centerCrop().into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                Toasty.error(getContext(),"cannot load image", Toast.LENGTH_SHORT,true).show();
            }
        });

        hdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

               /* Intent it = new Intent(getContext(), HdImageViewerActivity.class);
                int[] coordinate = new int[2];
                imageView.getLocationOnScreen(coordinate);
                it.putExtra(MEDIUM_IMAGE_URL, mediumImageUrl);
                startActivity(it);*/

                Intent intent = new Intent(getContext(), ImageViewPagerActivity.class);
                intent.putExtra(GiantBomb.POSITION,0);
                ArrayList<String> images = new ArrayList<>();
                images.add(mediumImageUrl);
                intent.putStringArrayListExtra(GiantBomb.IMAGE_URL, images);
                intent.putExtra(GiantBomb.TITLE,title);
            /*ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation((Activity) context,imageView,context.getResources().getString(R.string.transition_viewpager));*/
                getContext().startActivity(intent);


            }
        });


        return v;
    }
}
