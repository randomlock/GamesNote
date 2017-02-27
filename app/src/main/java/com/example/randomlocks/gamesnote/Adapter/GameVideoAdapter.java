package com.example.randomlocks.gamesnote.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.Modal.GamesVideoModal.GamesVideoModal;
import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.RealmDatabase.VideoListDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmResults;

//TODO FIX optimization on query ASAP effectively
public class GameVideoAdapter extends RecyclerView.Adapter<GameVideoAdapter.MyViewHolder> {

    private List<GamesVideoModal> modalList;
    private boolean isSimple, isAllVideo;
    Context context;
    private static final int SIMPLE_VIEW_TYPE = 0;
    private static final int CARD_VIEW_TYPE = 1;
    private OnClickInterface mOnClickInteraface;
    private Realm realm;
    private RealmAsyncTask transaction;
    private HashMap<Integer, GamesVideoModal> hashResults;

    public void swapModal(List<GamesVideoModal> listModals, boolean isAllVideo) {

        if (listModals != null) {
            modalList = listModals;
            this.isAllVideo = isAllVideo;
            notifyDataSetChanged();
        }

    }


    public interface OnClickInterface {
        void onWatchLater(GamesVideoModal modal, int viewId);

        void onLike(GamesVideoModal modal, int viewId);

        void onShare();
    }


    public GameVideoAdapter(List<GamesVideoModal> modalList, Context context, boolean isSimple, OnClickInterface mOnClickInterface, Realm realm, boolean isAllVideo, HashMap<Integer, GamesVideoModal> realmResults) {
        this.modalList = modalList;
        this.isSimple = isSimple;
        this.context = context;
        this.mOnClickInteraface = mOnClickInterface;
        this.realm = realm;
        this.isAllVideo = isAllVideo;
        this.hashResults = realmResults;

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
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final GamesVideoModal modal = modalList.get(position);
        if (modal.image.mediumUrl != null) {
            Picasso.with(context).load(modal.image.mediumUrl).fit().into(holder.videoThumb);
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
        if (!isSimple && isAllVideo && modal.videoType != null) {
            holder.video_type.setVisibility(View.VISIBLE);
            holder.video_type.setText(modal.videoType);
        } else if (holder.video_type != null) {
            holder.video_type.setVisibility(View.GONE);
        }
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                final GamesVideoModal realmModal = hashResults.get(modal.id);
                if (realmModal != null) {
                    if (realmModal.isWatchLater) {
                        modal.isWatchLater = true;
                        holder.watchLater.setColorFilter(ContextCompat.getColor(context, R.color.linecolor));
                    }
                    if (realmModal.isFavorite) {
                        modal.isFavorite = true;
                        holder.like.setColorFilter(ContextCompat.getColor(context, R.color.linecolor));
                    }
                } else {
                    holder.watchLater.setColorFilter(ContextCompat.getColor(context, R.color.white));
                    holder.like.setColorFilter(ContextCompat.getColor(context, R.color.white));

                }
            }
        });


        RealmResults<VideoListDatabase> realmModal = realm.where(VideoListDatabase.class).equalTo("id", modal.id).findAll();
        if (realmModal.size() == 0) {
            holder.status.setVisibility(View.GONE);
        } else {
            holder.status.setVisibility(View.VISIBLE);
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
        notifyItemRangeChanged(0,getItemCount());
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView videoThumb;
        Button length, status, video_type;
        TextView title, description, date;
        ImageButton watchLater, like, share;


        public MyViewHolder(View itemView) {
            super(itemView);
            videoThumb = (ImageView) itemView.findViewById(R.id.imageView);
            length = (Button) itemView.findViewById(R.id.length);
            status = (Button) itemView.findViewById(R.id.status);
            video_type = (Button) itemView.findViewById(R.id.video_type);

            watchLater = (ImageButton) itemView.findViewById(R.id.watch_later);
            watchLater.setVisibility(View.VISIBLE);
            like = (ImageButton) itemView.findViewById(R.id.like);
            like.setVisibility(View.VISIBLE);
            share = (ImageButton) itemView.findViewById(R.id.share);
            share.setVisibility(View.VISIBLE);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);

            itemView.setOnClickListener(this);
            like.setOnClickListener(this);
            share.setOnClickListener(this);
            watchLater.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            GamesVideoModal modal = modalList.get(getLayoutPosition());
            ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
            scale.setDuration(500);
            scale.setInterpolator(new OvershootInterpolator());
            switch (v.getId()) {

                case R.id.watch_later:
                    v.startAnimation(scale);
                    modal.isWatchLater = !modal.isWatchLater;
                    if (modal.isWatchLater) {
                        ((ImageButton) v).setColorFilter(ContextCompat.getColor(context, R.color.linecolor));
                    } else {
                        ((ImageButton) v).setColorFilter(ContextCompat.getColor(context, R.color.white));

                    }
                    mOnClickInteraface.onWatchLater(modalList.get(getLayoutPosition()), R.id.watch_later);
                    break;

                case R.id.like:
                    v.startAnimation(scale);
                    modal.isFavorite = !modal.isFavorite;

                    if (modal.isFavorite) {
                        ((ImageButton) v).setColorFilter(ContextCompat.getColor(context, R.color.linecolor));
                    } else {
                        ((ImageButton) v).setColorFilter(ContextCompat.getColor(context, R.color.white));

                    }

                    mOnClickInteraface.onLike(modalList.get(getLayoutPosition()), R.id.like);
                    break;

                case R.id.root_view:
                  /*  GamesVideoModal videoModal = modalList.get(getLayoutPosition());
                    String highUrl = videoModal.highUrl + "?api_key=" + GiantBomb.API_KEY;
                    Intent intent = new Intent(context, PlayerActivity.class)
                            .setData(Uri.parse(highUrl))
                            .putExtra(PlayerActivity.CONTENT_ID_EXTRA, highUrl.toLowerCase(Locale.US).replaceAll("\\s", ""))
                            .putExtra(PlayerActivity.CONTENT_TYPE_EXTRA, Util.TYPE_OTHER)
                            .putExtra(GiantBomb.KEY, videoModal.id)
                            .putExtra(PlayerActivity.PROVIDER_EXTRA, "");

                    context.startActivity(intent);*/


                    break;


            }
        }
    }

    @Override
    public void onViewDetachedFromWindow(MyViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (transaction != null && !transaction.isCancelled()) {
            Log.d("tag", "cancel transaction");
            transaction.cancel();
        }
    }
}
