package com.example.randomlocks.gamesnote.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.Modal.GamesVideoModal.GamesVideoModal;
import com.example.randomlocks.gamesnote.R;
import com.flyco.labelview.LabelView;
import com.squareup.picasso.Picasso;

import es.dmoral.toasty.Toasty;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by randomlocks on 7/20/2016.
 */
public class GameVideoOtherAdapter extends RealmRecyclerViewAdapter<GamesVideoModal, GameVideoOtherAdapter.MyViewHolder> {

    private static final int SIMPLE_VIEW_TYPE = 0;
    private static final int CARD_VIEW_TYPE = 1;
    private final int position;
    Context context;
    private boolean isSimple;
    Realm realm;

    public GameVideoOtherAdapter(@NonNull Context context, @Nullable OrderedRealmCollection<GamesVideoModal> data, boolean autoUpdate, boolean isSimple,Realm realm,int position) {
        super(context, data, autoUpdate);
        this.context = context;
        this.isSimple = isSimple;
        this.realm = realm;
        this.position = position;
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

        if(modal.videoType!=null){
            holder.category.setText(modal.videoType);
        }


        if(modal.publishDate!=null)
            holder.date.setText(modal.publishDate.split(" ")[0]);


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


             holder.timeLabel.setText(date.toString());
        }


    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        ImageView videoThumb;
        LabelView timeLabel, isWatchLabel;
        TextView title, description, date, category;
        ImageButton popupMenu;

        PopupMenu popup;
        GamesVideoModal modal;

        MyViewHolder(View itemView) {
            super(itemView);
            videoThumb = (ImageView) itemView.findViewById(R.id.imageView);
            timeLabel = (LabelView) itemView.findViewById(R.id.time);
            isWatchLabel = (LabelView) itemView.findViewById(R.id.is_watch);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            date = (TextView) itemView.findViewById(R.id.date);
            category = (TextView) itemView.findViewById(R.id.category);
            popupMenu = (ImageButton) itemView.findViewById(R.id.popup);
            int mode = AppCompatDelegate.getDefaultNightMode();
            if (mode == AppCompatDelegate.MODE_NIGHT_YES)
                popupMenu.setColorFilter(Color.argb(255, 255, 255, 255)); // White Tint
            popupMenu.setOnClickListener(this);
            popup = new PopupMenu(context, popupMenu);
            popup.setOnMenuItemClickListener(this);
            final MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.popup_video_other, popup.getMenu());


            itemView.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            modal = getItem(getAdapterPosition());
            switch (v.getId()) {
                case R.id.popup:

                    popup.show();
                    break;


            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(item.getItemId()==R.id.remove){
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        //if video is in both section dont delete the video just update
                        if(modal.isWatchLater && modal.isFavorite){
                            if(position==1){
                                getData().get(getAdapterPosition()).isFavorite = false;
                            }else{
                                getData().get(getAdapterPosition()).isWatchLater = false;
                            }
                        }else {
                            getData().deleteFromRealm(getAdapterPosition());
                        }
                        Toasty.error(context,"video deleted", Toast.LENGTH_SHORT).show();
                        // realm.where(GameListDatabase.class).equalTo("apiDetailUrl",gameListDatabase.getApiDetailUrl()).findFirst().deleteFromRealm();
                    }
                });
                return true;
            }
            return false;
        }
    }

}
