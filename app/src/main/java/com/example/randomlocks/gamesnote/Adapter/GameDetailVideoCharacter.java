package com.example.randomlocks.gamesnote.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.Modal.GameCharacterModal.CharacterImage;
import com.example.randomlocks.gamesnote.Modal.GameDetailModal.GameDetailVideo;
import com.example.randomlocks.gamesnote.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by randomlocks on 8/18/2016.
 */
public class GameDetailVideoCharacter extends RecyclerView.Adapter<GameDetailVideoCharacter.MyViewHolder> {

    Context context;
    List<CharacterImage> image;
    List<GameDetailVideo> videos;
    int totalImageSize = 0;

    public GameDetailVideoCharacter(List<CharacterImage> image, List<GameDetailVideo> videos, Context context) {
        this.context = context;
        this.image = image;
        this.videos = videos;
        totalImageSize = image.size();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.custom_game_detail_video, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if (totalImageSize - position - 1 >= 0) {
            Picasso.with(context).load(image.get(totalImageSize - 1 - position).mediumUrl).fit().centerCrop().into(holder.imageView);
        }

        holder.textView.setText(videos.get(position).name);


    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            textView = (TextView) itemView.findViewById(R.id.video_title);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
