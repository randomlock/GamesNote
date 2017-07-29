package com.example.randomlocks.gamesnote.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.modals.GameCharacterModal.CharacterImage;
import com.example.randomlocks.gamesnote.modals.GameDetailModal.GameDetailVideo;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by randomlocks on 8/18/2016.
 */
public class GameDetailVideoCharacter extends RecyclerView.Adapter<GameDetailVideoCharacter.MyViewHolder> {

    private Context context;
    private List<CharacterImage> image;
    private List<GameDetailVideo> videos;
    private int totalImageSize = 0;

    private GameDetailVideoInterface gameDetailVideoInterface;

    public GameDetailVideoCharacter(List<CharacterImage> image, List<GameDetailVideo> videos, Context context,GameDetailVideoInterface gameDetailVideoInterface) {
        this.context = context;
        this.image = image;
        this.videos = videos;
        totalImageSize = image.size();
        this.gameDetailVideoInterface = gameDetailVideoInterface;
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
            Picasso.with(context).load(image.get(totalImageSize - 1 - position).mediumUrl).fit().centerCrop().placeholder(R.drawable.news_image_drawable).error(R.drawable.news_image_drawable).into(holder.imageView);
        }

        holder.textView.setText(videos.get(position).name);


    }

    @Override
    public int getItemCount() {
        return videos.size()-1;
    }

    public static interface GameDetailVideoInterface {
        void onClickVideo(GameDetailVideo video, int next_video_pos);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView textView;

        MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            textView = (TextView) itemView.findViewById(R.id.video_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            gameDetailVideoInterface.onClickVideo(videos.get(getAdapterPosition()),getAdapterPosition());
        }
    }
}
