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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.randomlocks.gamesnote.Activity.GameDetailActivity;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.RealmDatabase.GameListDatabase;
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


    Context context;
    private int[] rainbow;
    private OnClickInterface mOnClickInterface;
    private Realm realm;
    private OrderedRealmCollection<GameListDatabase> model;
    private int status;
    private boolean isSimple;

    private static final int SIMPLE_VIEW_TYPE = 0;
    private static final int CARD_VIEW_TYPE = 1;

    @Override
    public Filter getFilter() {
        return new GameListFilter(this);
    }

    public void setSimple(boolean isSimple) {
        this.isSimple = isSimple;
        notifyItemRangeChanged(0,getItemCount());

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


    public interface OnClickInterface{
        void onClick(GameListDatabase gameListDatabase);
        void onScoreClick(String primaryKey,int oldScore , int position);
    }


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
            holder.image.setImageResource(R.drawable.testimage);
        }
        holder.title.setText(listDatabase.getName());

        holder.scoreView.setValue(listDatabase.getScore());
        holder.scoreView.setBarColor(rainbow[listDatabase.getScore()/10]);



    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }


    @Override
    public int getItemViewType(int position) {
        return isSimple ? SIMPLE_VIEW_TYPE : CARD_VIEW_TYPE;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        TextView title;
        ImageView image;
        CircleProgressView scoreView;
        ImageButton popup;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            image = (ImageView) itemView.findViewById(R.id.image);
            popup = (ImageButton) itemView.findViewById(R.id.popup);
            int mode = AppCompatDelegate.getDefaultNightMode();
            if(mode==AppCompatDelegate.MODE_NIGHT_YES)
                popup.setColorFilter(Color.argb(255, 255, 255, 255)); // White Tint
            scoreView = (CircleProgressView) itemView.findViewById(R.id.score_view);
            itemView.setOnClickListener(this);
            scoreView.setOnClickListener(this);
            popup.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {

            if(view.getId()==R.id.score_view){
                mOnClickInterface.onScoreClick(getData().get(getAdapterPosition()).getApiDetailUrl(),getData().get(getAdapterPosition()).getScore(),getAdapterPosition());
            }else  if(view.getId()==R.id.popup){
                PopupMenu popup = new PopupMenu(context, view);
                popup.setOnMenuItemClickListener(this);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.popup_list,popup.getMenu());
                popup.show();


            } else {
                mOnClickInterface.onClick(getData().get(getAdapterPosition()));
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();
            final GameListDatabase gameListDatabase = getData().get(getAdapterPosition());

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

