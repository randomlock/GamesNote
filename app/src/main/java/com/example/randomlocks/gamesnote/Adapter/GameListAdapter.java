package com.example.randomlocks.gamesnote.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.RealmDatabase.GameListDatabase;
import com.squareup.picasso.Picasso;

import at.grabner.circleprogress.CircleProgressView;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by randomlocks on 3/19/2016.
 */
public class GameListAdapter extends RealmRecyclerViewAdapter<GameListDatabase, GameListAdapter.MyViewHolder> {


    Context context;
    private int[] rainbow;
    private OnClickInterface mOnClickInterface;


   public interface OnClickInterface{
       void onClick(GameListDatabase gameListDatabase);
       void onScoreClick(String primaryKey,int oldScore , int position);
    }


    public GameListAdapter(@NonNull Context context, @Nullable OrderedRealmCollection<GameListDatabase> data, boolean autoUpdate,OnClickInterface mOnClickInterface) {
        super(context, data, autoUpdate);
        this.context = context;
        this.mOnClickInterface = mOnClickInterface;
        rainbow = context.getResources().getIntArray(R.array.score_color);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.custom_game_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        GameListDatabase listDatabase = getData().get(position);
        if (listDatabase.getImageUrl() != null) {
            Picasso.with(context).load(listDatabase.getImageUrl()).centerCrop().fit().into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.testimage);
        }
        holder.title.setText(listDatabase.getName());

        holder.scoreView.setValue(listDatabase.getScore());
        holder.scoreView.setBarColor(rainbow[listDatabase.getScore()/10]);


    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        ImageView image;
        CircleProgressView scoreView;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            image = (ImageView) itemView.findViewById(R.id.image);
            scoreView = (CircleProgressView) itemView.findViewById(R.id.score_view);
            itemView.setOnClickListener(this);
            scoreView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            if(view.getId()==R.id.score_view){
                mOnClickInterface.onScoreClick(getData().get(getAdapterPosition()).getApiDetailUrl(),getData().get(getAdapterPosition()).getScore(),getAdapterPosition());
            }else {
                mOnClickInterface.onClick(getData().get(getAdapterPosition()));
            }
        }
    }


}

