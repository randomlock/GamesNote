package com.example.randomlocks.gamesnote.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.RealmDatabase.GameListDatabase;

import java.util.List;

import at.grabner.circleprogress.CircleProgressView;

/**
 * Created by randomlock on 6/7/2017.
 */

public class GameStatsAdapter extends RecyclerView.Adapter<GameStatsAdapter.MyViewHolder> {

    List<GameListDatabase> list;
    Context context;
    int rainbow[];

    public  GameStatsAdapter(Context context,List<GameListDatabase> list,int rainbow[]){
        this.list = list;
        this.context = context;
        this.rainbow  = rainbow;
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view =  inflater.inflate(R.layout.custom_game_stat,parent,false) ;
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        GameListDatabase database = list.get(position);
        holder.textView.setText(database.getName());
        holder.scoreView.setValue(database.getScore());
        holder.scoreView.setBarColor(rainbow[database.getScore()/10]);


    }

    @Override
    public int getItemCount() {
        return list.size()>=5 ? 5 : list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        CircleProgressView scoreView;

    public MyViewHolder(View itemView) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.text_list_view);
        scoreView = (CircleProgressView) itemView.findViewById(R.id.score_list_view);
    }
}




}
