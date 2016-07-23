package com.example.randomlocks.gamesnote.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.Modal.CharacterSearchModal.CharacterSearchModal;
import com.example.randomlocks.gamesnote.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by randomlocks on 7/11/2016.
 */
public class CharacterSearchAdapter extends RecyclerView.Adapter<CharacterSearchAdapter.MyViewHolder> {

    List<CharacterSearchModal> modals;
    Context context;
    OnClickInterface mOnClickInterface;


    public interface OnClickInterface {
        void onItemClick(String apiUrl, String image);
    }

    public CharacterSearchAdapter(List<CharacterSearchModal> modals, Context context, OnClickInterface mOnClickInterface) {
        this.modals = modals;
        this.context = context;
        this.mOnClickInterface = mOnClickInterface;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.custom_character_wiki_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CharacterSearchModal modal = modals.get(position);
        if (modal.name != null) {
            holder.name.setText(modal.name);
        }
        if (modal.image != null && modal.image.thumbUrl != null && !modal.image.thumbUrl.isEmpty()) {
            Picasso.with(context).load(modal.image.thumbUrl).fit().centerCrop().into(holder.profileImage);
        } else {
            holder.profileImage.setImageResource(R.drawable.headerbackground);
        }
    }

    @Override
    public int getItemCount() {
        return modals.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CircleImageView profileImage;
        TextView name;

        public MyViewHolder(View itemView) {
            super(itemView);
            profileImage = (CircleImageView) itemView.findViewById(R.id.profile_image);
            name = (TextView) itemView.findViewById(R.id.name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            CharacterSearchModal modal = modals.get(getLayoutPosition());
            mOnClickInterface.onItemClick(modal.apiDetailUrl, modal.image != null ? modal.image.mediumUrl : null);
        }
    }
}
