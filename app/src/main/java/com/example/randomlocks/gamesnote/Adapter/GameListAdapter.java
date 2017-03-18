package com.example.randomlocks.gamesnote.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.randomlocks.gamesnote.Activity.GameDetailActivity;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.RealmDatabase.GameListDatabase;
import com.flyco.labelview.LabelView;
import com.squareup.picasso.Picasso;

import at.grabner.circleprogress.CircleProgressView;
import es.dmoral.toasty.Toasty;
import io.realm.Case;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by randomlocks on 3/19/2016.
 */
public class GameListAdapter extends RealmRecyclerViewAdapter<GameListDatabase, GameListAdapter.MyViewHolder> implements Filterable {


    private static final int SIMPLE_VIEW_TYPE = 0;
    private static final int CARD_VIEW_TYPE = 1;
    Context context;
    private int[] rainbow;
    private OnClickInterface mOnClickInterface;
    private Realm realm;
    private OrderedRealmCollection<GameListDatabase> model;
    private int status;
    private boolean isSimple;
    private int last_position = -1;

    public GameListAdapter(@NonNull Context context, Realm realm, @Nullable OrderedRealmCollection<GameListDatabase> data, boolean autoUpdate, int status, boolean isSimple, OnClickInterface mOnClickInterface) {
        super(context, data, autoUpdate);
        this.context = context;
        this.mOnClickInterface = mOnClickInterface;
        rainbow = context.getResources().getIntArray(R.array.score_color);
        this.realm = realm;
        model = data;
        this.status = status;
        this.isSimple = isSimple;
    }

    @Override
    public Filter getFilter() {
        return new GameListFilter(this);
    }

    public void setSimple(boolean isSimple) {
        this.isSimple = isSimple;
        notifyItemRangeChanged(0,getItemCount());

    }

    private void filterResults(String text) {
        text = text == null ? null : text.toLowerCase().trim();

        if(text == null || "".equals(text)) {
            model = realm.where(GameListDatabase.class).equalTo("status",status).findAll();
        } else {
            model = realm.where(GameListDatabase.class)
                    .equalTo("status",status)
                    .contains("name", text, Case.INSENSITIVE)
                    .findAll();
        }
        updateData(model);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == SIMPLE_VIEW_TYPE) {
            view = inflater.inflate(R.layout.custom_game_list1, parent, false);
        } else {
            view = inflater.inflate(R.layout.custom_game_list2, parent, false);
        }
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        GameListDatabase listDatabase = getData().get(position);
        if (listDatabase.getImageUrl() != null) {
            Picasso.with(context).load(listDatabase.getImageUrl()).centerCrop().fit().into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.news_image_drawable);
        }
        holder.title.setText(listDatabase.getName());

        holder.scoreView.setValueAnimated(listDatabase.getScore());
        holder.scoreView.setBarColor(rainbow[listDatabase.getScore()/10]);


        if(status==GiantBomb.ALL_GAMES){
            holder.statusLabel.setText(context.getResources().getStringArray(R.array.status)[listDatabase.getStatus()-1]);
        }

        setFadeAnimation(holder.itemView, position);


    }

    @Override
    public int getItemViewType(int position) {
        return isSimple ? SIMPLE_VIEW_TYPE : CARD_VIEW_TYPE;
    }

    private void setFadeAnimation(View view, int position) {
        if (position > last_position) {
            AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(700);
            view.startAnimation(anim);
            last_position = position;
        }
    }

    @Override
    public void onViewDetachedFromWindow(MyViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        ((MyViewHolder) holder).itemView.clearAnimation();
    }

    public interface OnClickInterface {
        void onClick(GameListDatabase gameListDatabase);

        void onScoreClick(String primaryKey, int oldScore, int position);
    }

    private class GameListFilter
            extends Filter {
        private final GameListAdapter adapter;

        private GameListFilter(GameListAdapter adapter) {
            super();
            this.adapter = adapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            return new FilterResults();
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.filterResults(constraint.toString());
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        TextView title;
        ImageView image;
        CircleProgressView scoreView;
        ImageButton popupMenu;
        LabelView statusLabel;

        PopupMenu popup;
        GameListDatabase gameListDatabase;


         MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            image = (ImageView) itemView.findViewById(R.id.image);
             statusLabel = (LabelView) itemView.findViewById(R.id.status_label);
             if(status!=GiantBomb.ALL_GAMES)
                 statusLabel.setVisibility(View.GONE);
             popupMenu = (ImageButton) itemView.findViewById(R.id.popup);
            int mode = AppCompatDelegate.getDefaultNightMode();
            if(mode==AppCompatDelegate.MODE_NIGHT_YES)
                popupMenu.setColorFilter(Color.argb(255, 255, 255, 255)); // White Tint
            scoreView = (CircleProgressView) itemView.findViewById(R.id.score_view);
            itemView.setOnClickListener(this);
            scoreView.setOnClickListener(this);
            popupMenu.setOnClickListener(this);
              popup = new PopupMenu(context, popupMenu);
             popup.setOnMenuItemClickListener(this);
             MenuInflater inflater = popup.getMenuInflater();
             inflater.inflate(R.menu.popup_list,popup.getMenu());

        }

        @Override
        public void onClick(View view) {
            gameListDatabase = getItem(getAdapterPosition());
            if(view.getId()==R.id.score_view){

                mOnClickInterface.onScoreClick(gameListDatabase.getApiDetailUrl(), gameListDatabase.getScore(), getAdapterPosition());
            }else  if(view.getId()==R.id.popup){

                popup.show();


            } else {
                mOnClickInterface.onClick(gameListDatabase);
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();
            if(id==R.id.remove){

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        getData().deleteFromRealm(getAdapterPosition());
                        Toasty.error(context,"game deleted",Toast.LENGTH_SHORT).show();
                       // realm.where(GameListDatabase.class).equalTo("apiDetailUrl",gameListDatabase.getApiDetailUrl()).findFirst().deleteFromRealm();
                    }
                });
                return true;
            }else {
                //open wiki fragment ;
                Intent it = new Intent(context, GameDetailActivity.class);
                it.putExtra("apiUrl", "http://www.giantbomb.com/api/game/" + gameListDatabase.getApiDetailUrl() + "/");
                it.putExtra("name",gameListDatabase.getName());
                if (gameListDatabase.getImageUrl()!= null) {
                    it.putExtra("imageUrl", gameListDatabase.getImageUrl());
                }
                context.startActivity(it);


                return true;

            }
        }
    }
}

