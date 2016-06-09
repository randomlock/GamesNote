package com.example.randomlocks.gamesnote.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.R;

/**
 * Created by randomlocks on 6/9/2016.
 */
public class LisviewDelet extends ArrayAdapter<String> {

    Context context;

    public LisviewDelet(Context context, int resource) {
        super(context, resource);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_fragment_font_option, parent, false);

        TextView textView = (TextView) view.findViewById(R.id.text);

        setAppearance(textView, position);


        return view;
    }

    private void setAppearance(TextView textView, int position) {

        switch (position) {

            case 0:


        }


    }


    @Override
    public int getCount() {
        return 12;
    }
}
