package com.example.randomlocks.gamesnote.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.R;

import java.util.List;

/**
 * Created by randomlocks on 3/19/2016.
 */
public class MyRecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> {


    List<String> stringList;
    Context context;

    public MyRecyclerAdapter(List<String> stringList, Context context) {
        this.stringList = stringList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_game_list, parent, false);

        MyViewHolder holder = new MyViewHolder(v);
        return holder;


    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.name.setText(stringList.get(position));
    }


}

class MyViewHolder extends RecyclerView.ViewHolder {

    TextView name;

    public MyViewHolder(View itemView) {
        super(itemView);

        name = (TextView) itemView.findViewById(R.id.text);

    }
}