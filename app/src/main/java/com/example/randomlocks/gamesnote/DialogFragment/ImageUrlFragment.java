package com.example.randomlocks.gamesnote.DialogFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.example.randomlocks.gamesnote.HelperClass.Toaster;

/**
 * Created by randomlock on 4/1/2017.
 */

public class ImageUrlFragment extends DialogFragment {

    public interface ImageUrlInterface{
        void onSelect(String url);
    }

    ImageUrlInterface mImageUrlInterface;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mImageUrlInterface = (ImageUrlInterface) getTargetFragment();
        } catch (Exception e) {
            Toaster.make(getContext(), "interface cast exception");
        }


    }
    public static ImageUrlFragment newInstance() {

        Bundle args = new Bundle();

        ImageUrlFragment fragment = new ImageUrlFragment();
        fragment.setArguments(args);
        return fragment;
    }


}
