package com.example.randomlocks.gamesnote.adapter;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.fragments.GameDetailFragment;
import com.example.randomlocks.gamesnote.modals.gameDetailModal.CharacterGamesImage;
import com.example.randomlocks.gamesnote.modals.gameDetailModal.GameDetailIInnerJson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * Created by randomlocks on 6/10/2016.
 */
public class SimilarGameAdapter extends RecyclerView.Adapter<SimilarGameAdapter.MyViewHolder> {


    List<GameDetailIInnerJson> stringList;
    List<CharacterGamesImage> images = null;
    GameDetailFragment fragment;
    List<String> apiUrl;
    Context context;
    OnClickInterface onClickInterface;
    public SimilarGameAdapter(List<GameDetailIInnerJson> stringList, List<CharacterGamesImage> images, GameDetailFragment fragment, Context context, OnClickInterface onClickInterface) {
        this.stringList = stringList;
        this.context = context;
        this.fragment = fragment;

        if (images != null) {
            this.images = images;
        }


        this.apiUrl = new ArrayList<>();

        for (int i = 0; i < stringList.size(); i++) {
            apiUrl.add(stringList.get(i).apiDetailUrl);
        }

        this.onClickInterface = onClickInterface;


    }

    public void setImages(List<CharacterGamesImage> images) {
        this.images = images;
        notifyItemRangeChanged(0,getItemCount());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_game_detail_similar, parent, false);

        return new MyViewHolder(v);


    }

    @Override
    public int getItemCount() {
        return stringList.size() <= 30 ? stringList.size() : 30;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if (images != null) {
            Picasso.with(context).load(images.get(position).imageUrl).fit().centerCrop().into(holder.imageView);
            holder.textView.setText(stringList.get(position).name);
        }


    }

    public interface OnClickInterface {
        void onItemClick(String apiUrl, String image);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView imageView;
        TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.image);
            textView = (TextView) itemView.findViewById(R.id.video_title);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);


        }


        @Override
        public void onClick(View v) {

            GameDetailFragment fragment = null;
            fragment = (GameDetailFragment) ((AppCompatActivity) context).getSupportFragmentManager().findFragmentByTag("GameDetail");
            FragmentTransaction ft = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();

           /* fragment.getArguments().putString(GameDetailFragment.API_URL,apiUrl.get(getLayoutPosition()));
            if (images != null) {
                fragment.getArguments().putString(GameDetailFragment.IMAGE_URL, images.get(getLayoutPosition()).imageUrl);
            }
            fragment.getArguments().putString(GameDetailFragment.NAME,stringList.get(getLayoutPosition()).name);*/
            ft.addToBackStack(null);
            ft.remove(fragment);
            fragment = GameDetailFragment.newInstance(apiUrl.get(getLayoutPosition()), stringList.get(getLayoutPosition()).name, images != null ? images.get(getLayoutPosition()).imageUrl : null);
            ft.add(R.id.fragment_parent_layout, fragment, "GameDetail");
            ft.commit();

            //  onClickInterface.onItemClick(apiUrl.get(getLayoutPosition()),images.get(getLayoutPosition()).imageUrl);


        }

        @Override
        public boolean onLongClick(View v) {

            Toasty.info(context,stringList.get(getLayoutPosition()).name, Toast.LENGTH_SHORT,true).show();

            return true;
        }
    }


}
