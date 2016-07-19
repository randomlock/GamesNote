package com.example.randomlocks.gamesnote.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.Modal.GamesVideoModal.GamesVideoModal;
import com.example.randomlocks.gamesnote.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by randomlocks on 7/19/2016.
 */
public class GameVideoAdapter extends RecyclerView.Adapter<GameVideoAdapter.MyViewHolder> {

    List<GamesVideoModal> modalList;
    boolean isSimple;
    Context context;
    private static final int SIMPLE_VIEW_TYPE = 0;
    private static final int CARD_VIEW_TYPE = 1;

    public GameVideoAdapter(List<GamesVideoModal> modalList, Context context, boolean isSimple) {
        this.modalList = modalList;
        this.isSimple = isSimple;
        this.context = context;

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == SIMPLE_VIEW_TYPE) {
            view = inflater.inflate(R.layout.custom_game_video_layout2, parent, false);
        } else {
            view = inflater.inflate(R.layout.custom_game_video_layout1, parent, false);
        }


        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        GamesVideoModal modal = modalList.get(position);
        if (modal.image.mediumUrl != null) {
            Picasso.with(context).load(modal.image.mediumUrl).fit().centerCrop().into(holder.videoThumb);
        }
        if (modal.name != null) {
            holder.title.setText(modal.name);
        }
        if (modal.deck != null) {
            holder.description.setText(modal.deck);
        }
        if (modal.lengthSeconds > 0) {
            int minute = modal.lengthSeconds / 60;
            int seconds = modal.lengthSeconds % 60;
            String date;
            if (seconds < 10) {
                date = minute + ":0" + seconds;
            } else {
                date = minute + ":" + seconds;
            }


            holder.length.setText(date);
        }
    }

    @Override
    public int getItemCount() {
        return modalList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return isSimple ? SIMPLE_VIEW_TYPE : CARD_VIEW_TYPE;
    }

    public void setSimple(boolean simple) {
        isSimple = simple;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView videoThumb;
        Button length, status;
        TextView title, description, date;
        ImageButton watchLater, like, share;


        public MyViewHolder(View itemView) {
            super(itemView);
            videoThumb = (ImageView) itemView.findViewById(R.id.imageView);
            length = (Button) itemView.findViewById(R.id.length);
            status = (Button) itemView.findViewById(R.id.status);
            watchLater = (ImageButton) itemView.findViewById(R.id.watch_later);
            like = (ImageButton) itemView.findViewById(R.id.like);
            share = (ImageButton) itemView.findViewById(R.id.share);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
        }
    }
}
