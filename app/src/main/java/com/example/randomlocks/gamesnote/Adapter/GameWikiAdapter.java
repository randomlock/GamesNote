package com.example.randomlocks.gamesnote.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.DialogFragment.ImageViewerFragment;
import com.example.randomlocks.gamesnote.GameDetailActivity;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.MyAnimation;
import com.example.randomlocks.gamesnote.Modal.GameWikiModal;
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
    private static GameWikiAdapter gameWikiAdapter = null;

    public GameWikiAdapter(List<GameWikiModal> list,Context context,int lastPosition){
        this.list = list;
        this.context = context;
        this.lastPosition=lastPosition;
    }









    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View v = inflater.inflate(R.layout.custom_game_wiki_layout,parent,false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder( final MyViewHolder holder, int position) {
        final GameWikiModal modal = list.get(position);
        holder.title.setText(modal.name);

        if(modal.deck!=null)
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


if(modal.image!=null && modal.image.smallUrl!=null){
    holder.imageView.setTag(R.string.smallImageUrl,modal.image.smallUrl);
    holder.imageView.setTag(R.string.mediumImageUrl,modal.image.mediumUrl);
     Picasso.with(context).load(modal.image.smallUrl).fit().into(holder.imageView);
}

        String date_time = modal.originalReleaseDate;
        String date[];
        if(date_time!=null){
            date = date_time.split(" ");
            holder.date.setText(date[0]);
        }


/****************** SET ON CLICK LISTENER *************************/

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, GameDetailActivity.class);
                it.putExtra("apiUrl",modal.apiDetailUrl);
                context.startActivity(it);
            }
        });


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

      class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        public TextView title,description,date,platform1,platform2,platform3;

        public ImageView imageView;

          public View view;

          public CardView cardView;


        public MyViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView)itemView.findViewById(R.id.description);
            date = (TextView) itemView.findViewById(R.id.date);
            platform1 = (TextView) itemView.findViewById(R.id.platform1);
            platform2 = (TextView) itemView.findViewById(R.id.platform2);
            platform3 = (TextView)itemView.findViewById(R.id.platform3);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            view = itemView.findViewById(R.id.dropdown);
            view.setOnClickListener(this);
            imageView.setOnClickListener(this);







        }


         @Override
         public void onClick(View view) {

         switch (view.getId()){
             case R.id.dropdown :
                 if(description.getVisibility()==View.GONE){
                     // expand(description);

                     MyAnimation.expand(description, view, context);
                 }

                 else {
                     //   collapse(description);

                     MyAnimation.collapse(description,view,context);
                 }

                 break;

             case R.id.imageView :


                ImageViewerFragment dialog = ImageViewerFragment.newInstance((String) view.getTag(R.string.smallImageUrl),(String)view.getTag(R.string.mediumImageUrl));
                 dialog.show(((FragmentActivity)context).getSupportFragmentManager(),"ImageViewer");



                 break;

         }




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

