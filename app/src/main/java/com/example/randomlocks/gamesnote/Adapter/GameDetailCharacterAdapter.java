package com.example.randomlocks.gamesnote.Adapter;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.Modal.GameDetailModal.CharacterGamesImage;
import com.example.randomlocks.gamesnote.Modal.GameDetailModal.GameDetailCharacters;
import com.example.randomlocks.gamesnote.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by randomlocks on 6/10/2016.
 */
public class GameDetailCharacterAdapter extends RecyclerView.Adapter<GameDetailCharacterAdapter.MyViewHolder> {


    List<GameDetailCharacters> stringList;
    List<CharacterGamesImage> images = null;
    OnClickInterface onClickInterface;
    Context context;
    int style;

    public GameDetailCharacterAdapter(List<GameDetailCharacters> stringList, List<CharacterGamesImage> images, int style, Context context, OnClickInterface onClickInterface) {
        this.stringList = stringList;
        this.style = style;
        this.context = context;
        if (images != null) {
            this.images = images;
        }


        this.onClickInterface = onClickInterface;

    }

    public interface OnClickInterface {
        void onItemClick(String apiUrl, String image);
    }


    public void setImages(List<CharacterGamesImage> images) {
        this.images = images;
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
            Picasso.with(context).load(images.get(position).imageUrl).fit().error(R.drawable.headerbackground).into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.headerbackground);
        }


    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        CircleImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.text);
            image = (CircleImageView) itemView.findViewById(R.id.image);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                name.setTextAppearance(style);

            } else {
                name.setTextAppearance(context, style);

            }

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
