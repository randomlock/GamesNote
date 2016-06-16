package com.example.randomlocks.gamesnote.Adapter;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.Modal.GameDetailModal.CharacterGamesImage;
import com.example.randomlocks.gamesnote.Modal.GameDetailModal.GameDetailCharacters;
import com.example.randomlocks.gamesnote.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by randomlocks on 6/10/2016.
 */
public class GameDetailCharacterAdapter extends RecyclerView.Adapter<GameDetailCharacterAdapter.MyViewHolder> {


    List<GameDetailCharacters> stringList;
    List<String> apiUrl;
    List<CharacterGamesImage> images = null;
    Context context;
    int style;

    public GameDetailCharacterAdapter(List<GameDetailCharacters> stringList, List<CharacterGamesImage> images, int style, Context context) {
        this.stringList = stringList;
        this.style = style;
        this.context = context;
        if (images != null) {
            this.images = images;
        }


        this.apiUrl = new ArrayList<>();

        for (int i = 0; i < stringList.size(); i++) {
            apiUrl.add(stringList.get(i).apiDetailUrl);
        }


    }

    public void setImages(List<CharacterGamesImage> images) {
        this.images = images;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_game_detail_similar_game_character, parent, false);
        return new MyViewHolder(v);


    }

    @Override
    public int getItemCount() {
        return stringList.size() <= 10 ? stringList.size() : 10;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.name.setText(stringList.get(position).name);

        if (images != null) {
            Picasso.with(context).load(images.get(position).imageUrl).into(holder.image);
        }


    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.text);
            image = (ImageView) itemView.findViewById(R.id.image);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                name.setTextAppearance(style);

            } else {
                name.setTextAppearance(context, style);

            }

            itemView.setOnClickListener(this);


        }


        @Override
        public void onClick(View v) {
            Toaster.make(context, String.valueOf(apiUrl.get(2)));
        }
    }


}
