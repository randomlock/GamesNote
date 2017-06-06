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
public class GameDetailOverviewAdapter extends RecyclerView.Adapter<GameDetailOverviewAdapter.MyViewHolder> {

    private ArrayList<String> key;
    private ArrayList<String> values;

    public GameDetailOverviewAdapter(ArrayList<String> key, ArrayList<String> values) {
        this.key = key;
        this.values = values;
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




        }

    }
}
