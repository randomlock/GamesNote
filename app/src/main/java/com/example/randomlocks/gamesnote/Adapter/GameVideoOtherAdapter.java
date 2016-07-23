package com.example.randomlocks.gamesnote.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.Modal.GamesVideoModal.GamesVideoModal;
import com.example.randomlocks.gamesnote.R;
import com.squareup.picasso.Picasso;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by randomlocks on 7/20/2016.
 */
public class GameVideoOtherAdapter extends RealmRecyclerViewAdapter<GamesVideoModal, GameVideoOtherAdapter.MyViewHolder> {

    private static final int SIMPLE_VIEW_TYPE = 0;
    private static final int CARD_VIEW_TYPE = 1;
    Context context;
    OnClickInterface mOnClickInteraface;
    boolean isSimple;


    public interface OnClickInterface {
        void onWatchLater(GamesVideoModal modal);

        void onLike(GamesVideoModal modal);

        void onShare();
    }

    public GameVideoOtherAdapter(@NonNull Context context, @Nullable OrderedRealmCollection<GamesVideoModal> data, boolean autoUpdate, boolean isSimple, OnClickInterface mOnClickInterface) {
        super(context, data, autoUpdate);
        this.context = context;
        this.isSimple = isSimple;
        this.mOnClickInteraface = mOnClickInterface;
    }

    @Override
    public int getItemViewType(int position) {
        return isSimple ? SIMPLE_VIEW_TYPE : CARD_VIEW_TYPE;
    }

    public void setSimple(boolean simple) {
        isSimple = simple;
        notifyDataSetChanged();
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

        GamesVideoModal modal = getData().get(position);

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
            int hour = minute / 60;
            StringBuilder date = new StringBuilder();
            if (hour > 0) {
                minute = minute - hour * 60;
                if (minute < 10) {
                    date.append(hour).append(":0");
                } else {
                    date.append(hour).append(":");

                }
            }
            if (seconds < 10) {
                date.append(minute).append(":0").append(seconds);
            } else {
                date.append(minute).append(":").append(seconds);
            }


            holder.length.setText(date.toString());
        }


    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView videoThumb;
        Button length, status;
        TextView title, description, date;


        public MyViewHolder(View itemView) {
            super(itemView);
            videoThumb = (ImageView) itemView.findViewById(R.id.imageView);
            length = (Button) itemView.findViewById(R.id.length);
            status = (Button) itemView.findViewById(R.id.status);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);

        }


    }

}
