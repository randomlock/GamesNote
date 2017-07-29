package com.example.randomlocks.gamesnote.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.modals.GameDetailModal.CharacterGamesImage;
import com.example.randomlocks.gamesnote.modals.GameDetailModal.GameDetailIInnerJson;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by randomlocks on 6/10/2016.
 */
public class GameDetailCharacterAdapter extends RecyclerView.Adapter<GameDetailCharacterAdapter.MyViewHolder> {


    List<GameDetailIInnerJson> stringList;
    List<CharacterGamesImage> images = null;
    OnClickInterface onClickInterface;
    Context context;

    public GameDetailCharacterAdapter(List<GameDetailIInnerJson> stringList, List<CharacterGamesImage> images, Context context, OnClickInterface onClickInterface) {
        this.stringList = stringList;
        this.context = context;
        if (images != null) {
            this.images = images;
        }


        this.onClickInterface = onClickInterface;

    }

    public void setImages(List<CharacterGamesImage> images) {
        this.images = images;
        notifyItemRangeChanged(0,getItemCount());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_game_detail_characters, parent, false);
        return new MyViewHolder(v);


    }

    @Override
    public int getItemCount() {
        return stringList.size() <= 30 ? stringList.size() : 30;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.name.setText(stringList.get(position).name);

        if (images != null) {
            holder.image.setTag(images.get(position));
            Picasso.with(context).load(images.get(position).imageUrl).fit().placeholder(R.drawable.news_image_drawable).error(R.drawable.news_image_drawable).into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.news_image_drawable);
        }


    }

    public interface OnClickInterface {
        void onItemClick(String apiUrl, String image);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        CircleImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.text);
            image = (CircleImageView) itemView.findViewById(R.id.image);




            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getLayoutPosition();
                    if (images != null) {
                        onClickInterface.onItemClick(stringList.get(pos).apiDetailUrl, images.get(pos).imageUrl);
                    } else {
                        onClickInterface.onItemClick(stringList.get(pos).apiDetailUrl, null);
                    }
                }
            });


        }


    }

}
