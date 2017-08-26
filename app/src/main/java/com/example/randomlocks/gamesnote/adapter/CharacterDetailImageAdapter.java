package com.example.randomlocks.gamesnote.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.activity.ImageViewPagerActivity;
import com.example.randomlocks.gamesnote.helperClass.GiantBomb;
import com.example.randomlocks.gamesnote.modals.gameCharacterModal.CharacterImage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by randomlocks on 7/23/2016.
 */
public class CharacterDetailImageAdapter extends RecyclerView.Adapter<CharacterDetailImageAdapter.MyViewHolder> {

    private List<CharacterImage> imageUrls;
    private String title;
    private Context context;
    private ArrayList<String> images=null;


    public CharacterDetailImageAdapter(List<CharacterImage> imageUrls, Context context,String title) {
        this.imageUrls = imageUrls;
        this.context = context;
        this.title = title;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_character_detail_image_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        String imageUrl = imageUrls.get(position).mediumUrl;
        if (imageUrl != null && imageUrl.trim().length() > 0) {
            Picasso.with(context).load(imageUrl).fit().centerCrop().placeholder(R.drawable.news_image_drawable).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return imageUrls==null ? 0:imageUrls.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;

        MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, ImageViewPagerActivity.class);
            intent.putExtra(GiantBomb.POSITION, getLayoutPosition());
            if(images==null){
                images = new ArrayList<>();
                for (CharacterImage image : imageUrls) {
                    images.add(image.mediumUrl);
                }
            }


            intent.putStringArrayListExtra(GiantBomb.IMAGE_URL, images);
            intent.putExtra(GiantBomb.TITLE,title);
            /*ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation((Activity) context,imageView,context.getResources().getString(R.string.transition_viewpager));*/
            context.startActivity(intent);


        }
    }

}
