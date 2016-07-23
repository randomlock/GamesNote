package com.example.randomlocks.gamesnote.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.Activity.GameDetailActivity;
import com.example.randomlocks.gamesnote.DialogFragment.ImageViewerFragment;
import com.example.randomlocks.gamesnote.HelperClass.MyAnimation;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.Modal.GameWikiModal;
import com.example.randomlocks.gamesnote.Modal.GameWikiPlatform;
import com.example.randomlocks.gamesnote.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by randomlocks on 4/25/2016.
 */
public class GameWikiAdapter extends RecyclerView.Adapter<GameWikiAdapter.MyViewHolder> {

    private List<GameWikiModal> list;
    Context context;
    int lastPosition;

    public GameWikiAdapter(List<GameWikiModal> list, Context context, int lastPosition) {
        this.list = list;
        this.context = context;
        this.lastPosition = lastPosition;
    }

    public void swap(List<GameWikiModal> newList) {
        list.clear();
        list.addAll(newList);
        notifyDataSetChanged();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View v = inflater.inflate(R.layout.custom_game_wiki_layout, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final GameWikiModal modal = list.get(position);
        holder.title.setText(modal.name);

        if (modal.deck != null)
            holder.description.setText(modal.deck);
        else
            holder.description.setText(R.string.no_description);

        //   holder.view.setOnClickListener(new MyClickListener(holder.description,context,GameWikiModal modal));


      /*  if(!modal.isClicked) {
            MyAnimation.collapse(holder.description,holder.view,context);

        } else {
            MyAnimation.expand(holder.description, holder.view, context);
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                modal.isClicked=true;

                if(holder.description.getVisibility()==View.GONE){
                    // expand(description);
                    modal.isClicked=true;

                    MyAnimation.expand(holder.description, view, context);
                }

                else {
                    //   collapse(description);
                    modal.isClicked=false;

                    MyAnimation.collapse(holder.description,view,context);
                }

            }
        }); */


        // Picasso.with(context).load(modal.image.iconUrl).fit().into(holder.imageView);


        if (modal.image != null && modal.image.smallUrl != null && modal.image.mediumUrl != null) {
            holder.imageView.setTag(R.string.smallImageUrl, modal.image.smallUrl);
            holder.imageView.setTag(R.string.mediumImageUrl, modal.image.mediumUrl);
            Picasso.with(context).load(modal.image.smallUrl).fit().centerCrop().into(holder.imageView);
        }

        String date_time = modal.originalReleaseDate;
        String date[];
        if (date_time != null) {
            date = date_time.split(" ");
            holder.date.setText(date[0]);
        }


        /**************** SETTING PLATFORMS*************************/
        List<GameWikiPlatform> platform = modal.platforms;


        if (platform != null) {
            holder.platform1.setVisibility(View.VISIBLE);
            holder.platform2.setVisibility(View.VISIBLE);
            holder.platform3.setVisibility(View.VISIBLE);


            switch (platform.size()) {
                case 0:
                    holder.platform1.setVisibility(View.INVISIBLE);
                    holder.platform2.setVisibility(View.INVISIBLE);
                    holder.platform3.setVisibility(View.INVISIBLE);

                    break;
                case 1:
                    holder.platform1.setVisibility(View.INVISIBLE);
                    holder.platform3.setVisibility(View.INVISIBLE);
                    holder.platform2.setText(platform.get(0).abbreviation);
                    break;
                case 2:
                    holder.platform2.setVisibility(View.INVISIBLE);
                    holder.platform1.setText(platform.get(0).abbreviation);
                    holder.platform3.setText(platform.get(1).abbreviation);
                    break;
                case 3:


                    holder.platform1.setText(platform.get(0).abbreviation);
                    holder.platform2.setText(platform.get(1).abbreviation);
                    holder.platform3.setText(platform.get(2).abbreviation);
                    break;

                default:
                    holder.platform1.setText(platform.get(0).abbreviation);
                    holder.platform2.setText(platform.get(1).abbreviation);
                    holder.platform3.setText("+" + (platform.size() - 2) + " more");

            }
        }


/****************** SET ON CLICK LISTENER *************************/


        // holder.description.setText(modal.getDescription());


        /*********************ANIMATION*********************/

        Animation animation = AnimationUtils.loadAnimation(context,
                (position > lastPosition) ? R.anim.up_from_bottom
                        : R.anim.down_from_top);
        holder.itemView.startAnimation(animation);
        lastPosition = position;


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {


        public TextView title, description, date;
        public Button platform1, platform2, platform3;
        public ImageView imageView;
        public View view;
        public CardView cardView;


        public MyViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            date = (TextView) itemView.findViewById(R.id.date);
            platform1 = (Button) itemView.findViewById(R.id.platform1);
            platform2 = (Button) itemView.findViewById(R.id.platform2);
            platform3 = (Button) itemView.findViewById(R.id.platform3);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            imageView.setOnClickListener(this);
            platform1.setOnClickListener(this);
            platform2.setOnClickListener(this);


        }


        @Override
        public void onClick(View view) {

            switch (view.getId()) {

                case R.id.cardView:
                    GameWikiModal modal = list.get(getLayoutPosition());
                    Intent it = new Intent(context, GameDetailActivity.class);
                    it.putExtra("apiUrl", modal.apiDetailUrl);
                    it.putExtra("name", modal.name);

                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation((Activity) context, imageView, "profile");


                    if (modal.image != null && modal.image.mediumUrl != null) {
                        it.putExtra("imageUrl", modal.image.mediumUrl);
                    }
                    context.startActivity(it, options.toBundle());


                    break;


                case R.id.imageView:


                    ImageViewerFragment dialog = ImageViewerFragment.newInstance((String) view.getTag(R.string.smallImageUrl), (String) view.getTag(R.string.mediumImageUrl));


                    dialog.show(((FragmentActivity) context).getSupportFragmentManager(), "ImageViewer");


                    break;

                case R.id.platform1:
                    if (list.get(getLayoutPosition()).platforms != null) {
                        Toaster.make(context, list.get(getLayoutPosition()).platforms.get(0).name);
                    }
                    break;

                case R.id.platform2:
                    if (list.get(getLayoutPosition()).platforms != null) {
                        if (list.get(getLayoutPosition()).platforms.size() == 1)
                            Toaster.make(context, list.get(getLayoutPosition()).platforms.get(0).name);
                        else
                            Toaster.make(context, list.get(getLayoutPosition()).platforms.get(1).name);
                    }
                    break;

            }


        }


        @Override
        public boolean onLongClick(View v) {

            if (description.getVisibility() == View.GONE) {
                // expand(description);

                MyAnimation.expand(description, context);
            } else {
                //   collapse(description);

                MyAnimation.collapse(description, context);
            }


            return true;
        }
    }

    @Override
    public void onViewDetachedFromWindow(MyViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();

    }


/*    private class MyClickListener implements View.OnClickListener {

        Context context;
        TextView description;

        public MyClickListener(TextView description, Context context,GameWikiModal modal) {
            this.description = description;
            this.context = context;
        }

        @Override
        public void onClick(View view) {




            if(description.getVisibility()==View.GONE){
                // expand(description);

                MyAnimation.expand(description, view, context);
            }

            else {
                //   collapse(description);

                MyAnimation.collapse(description,view,context);
            } */


}

