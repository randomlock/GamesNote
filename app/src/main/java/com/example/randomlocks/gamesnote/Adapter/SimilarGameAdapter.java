package com.example.randomlocks.gamesnote.Adapter;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.Modal.GameDetailModal.GameDetailSimilarGames;
import com.example.randomlocks.gamesnote.R;

import java.util.List;

/**
 * Created by randomlocks on 6/10/2016.
 */
public class SimilarGameAdapter extends RecyclerView.Adapter<SimilarGameAdapter.MyViewHolder> {


    List<GameDetailSimilarGames> stringList;
    List<String> apiUrl;
    Context context;
    int style;

    public SimilarGameAdapter(List<GameDetailSimilarGames> stringList, int style, Context context) {
        this.stringList = stringList;
        this.style = style;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_game_detail_similar_game_character, parent, false);

        MyViewHolder holder = new MyViewHolder(v);
        return holder;


    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.name.setText(stringList.get(position).name);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;

        public MyViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView;


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                name.setTextAppearance(style);

            } else {
                name.setTextAppearance(context, style);

            }


        }


    }


}
