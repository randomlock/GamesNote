package com.example.randomlocks.gamesnote.DialogFragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.R;

/**
 * Created by randomlock on 4/1/2017.
 */

public class ImageUrlFragment extends DialogFragment implements View.OnClickListener {



    public interface ImageUrlInterface{
        void onSelect(String url);
    }

    EditText imageUrl;
    Button cancel,remove,update;


    ImageUrlInterface mImageUrlInterface;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mImageUrlInterface = (ImageUrlInterface) getActivity();
        } catch (Exception e) {
            Toaster.make(getContext(), "interface cast exception");
        }


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public static ImageUrlFragment newInstance() {

        Bundle args = new Bundle();

        ImageUrlFragment fragment = new ImageUrlFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_image_url,container,false);
        imageUrl = (EditText) view.findViewById(R.id.edit_text);
        view.findViewById(R.id.cancel).setOnClickListener(this);
        view.findViewById(R.id.remove).setOnClickListener(this);
        view.findViewById(R.id.update).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.cancel :
                dismiss();
                break;
            case R.id.remove :
                mImageUrlInterface.onSelect(null);
                break;
            case R.id.update :
                mImageUrlInterface.onSelect(imageUrl.getText().toString());
                break;



        }

    }



}
