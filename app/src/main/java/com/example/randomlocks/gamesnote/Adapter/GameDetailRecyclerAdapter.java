package com.example.randomlocks.gamesnote.Adapter;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.Fragments.GameDetailFragment;
import com.example.randomlocks.gamesnote.R;

import java.util.ArrayList;

/**
 * Created by randomlocks on 6/5/2016.
 */
public class GameDetailRecyclerAdapter extends RecyclerView.Adapter<GameDetailRecyclerAdapter.MyViewHolder> {

    ArrayList<String> key;
    ArrayList<String> values;
    GameDetailFragment fragment;
    int style;

    public GameDetailRecyclerAdapter(ArrayList<String> key, ArrayList<String> values, GameDetailFragment fragment, int style) {
        this.key = key;
        this.values = values;
        this.fragment = fragment;
        this.style = style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_game_detail_layout, parent, false);


        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.key.setText(key.get(position));
        String valueStr = values.get(position);
        valueStr = valueStr.substring(0, valueStr.length() - 2);

        if (!valueStr.equals("nu"))
            holder.value.setText(valueStr);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            holder.key.setTextAppearance(style);
            holder.value.setTextAppearance(style);
        } else {
            holder.key.setTextAppearance(fragment.getContext(), style);
            holder.value.setTextAppearance(fragment.getContext(), style);
        }

    }

    @Override
    public int getItemCount() {
        return key.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView key, value;


        public MyViewHolder(View itemView) {
            super(itemView);
            key = (TextView) itemView.findViewById(R.id.key);
            value = (TextView) itemView.findViewById(R.id.value);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                key.setTextAppearance(style);
                value.setTextAppearance(style);
            } else {
                key.setTextAppearance(fragment.getContext(), style);
                value.setTextAppearance(fragment.getContext(), style);
            }


        }

    }
}
