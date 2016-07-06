package com.example.randomlocks.gamesnote.Adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.Modal.BottomSheetImage;
import com.example.randomlocks.gamesnote.R;

import java.util.ArrayList;

/**
 * Created by randomlocks on 4/30/2016.
 */














public class HdImageBottomSheetAdapter extends ArrayAdapter {

    Context context;
    ArrayList<BottomSheetImage> arrayList;





    public HdImageBottomSheetAdapter(Context context, ArrayList<BottomSheetImage> arrayList) {
        super(context,0);
        this.context = context;
        this.arrayList = arrayList;




    }






    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       View view = layoutInflater.inflate(R.layout.custombottomimage, parent, false);
        TextView textView = (TextView) view.findViewById(R.id.textview);
        AppCompatImageView imageView = (AppCompatImageView) view.findViewById(R.id.imageview);

        BottomSheetImage modal = arrayList.get(position);
        textView.setText(modal.title);
        imageView.setImageResource(modal.imageResource);



        return view;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }
}
