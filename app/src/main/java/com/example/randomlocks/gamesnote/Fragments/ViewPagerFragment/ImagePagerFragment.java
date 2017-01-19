package com.example.randomlocks.gamesnote.Fragments.ViewPagerFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by randomlock on 1/19/2017.
 */
//TODO check with image viewpager
public class ImagePagerFragment extends Fragment {

    String imageUrl;

    public static ImagePagerFragment newInstance(String url) {

        Bundle args = new Bundle();
        args.putString(GiantBomb.IMAGE_URL,url);
        ImagePagerFragment fragment = new ImagePagerFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageUrl = getArguments().getString(GiantBomb.IMAGE_URL);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_image_viewpager_layout,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        PhotoView photoView = (PhotoView) getActivity().findViewById(R.id.image);

        final PhotoViewAttacher attacher = new PhotoViewAttacher(photoView);

        Picasso.with(getContext())
                .load(imageUrl)
                .into(photoView, new Callback() {
                    @Override
                    public void onSuccess() {
                        attacher.update();
                    }

                    @Override
                    public void onError() {
                    }
                });
    }





}
