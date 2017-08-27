package com.example.randomlocks.gamesnote.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.R;

import java.util.ArrayList;

/**
 * Created by randomlocks on 6/5/2016.
 */
public class GameDetailOverviewAdapter extends RecyclerView.Adapter<GameDetailOverviewAdapter.MyViewHolder> {

    private ArrayList<String> key;
    private ArrayList<String> values;
    private boolean is_hltb;

    public GameDetailOverviewAdapter(ArrayList<String> key, ArrayList<String> values, boolean is_hltb) {
        this.key = key;
        this.values = values;
        this.is_hltb = is_hltb;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_game_detail_layout, parent, false);


        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if (is_hltb) {
            boolean is_first_time = true;
            String str = key.get(position);
            String[] strArray = str.split(" ");
            StringBuilder builder = new StringBuilder();
            String cap;
            for (String s : strArray) {
                if (is_first_time) {
                    is_first_time = false;
                    cap = s.substring(0, 1).toUpperCase() + s.substring(1);

                } else {
                    cap = s.substring(0, 1).toLowerCase() + s.substring(1);

                }
                builder.append(cap).append(" ");
            }
            holder.key.setText(builder.toString());
        } else {
            holder.key.setText(key.get(position));
        }

        String valueStr = values.get(position);
        //  valueStr = valueStr.substring(0, valueStr.length() - 2);

        if (!valueStr.equals("null"))
            holder.value.setText(valueStr);
        else {
            holder.value.setText("-");
        }


    }

    @Override
    public int getItemCount() {
        return key != null ? key.size() : 0;
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
