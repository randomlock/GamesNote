package com.example.randomlocks.gamesnote.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.activity.MainActivity;
import com.example.randomlocks.gamesnote.modals.NewsModal.NewsModal;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

/**
 * Created by randomlocks on 7/1/2016.
 */
public class GameNewsAdapter extends RecyclerView.Adapter<GameNewsAdapter.MyNewsHolder> {


    private static final int SIMPLE_VIEW_TYPE = 0;
    private static final int CARD_VIEW_TYPE = 1;
    Context context;
    Calendar calendar = Calendar.getInstance();
    private List<NewsModal> modals;
    private boolean isSimple;

    public GameNewsAdapter(List<NewsModal> modals, Context context, boolean isSimple) {
        this.modals = modals;
        this.context = context;
        this.isSimple = isSimple;
    }


    @Override
    public MyNewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;
        if (viewType == SIMPLE_VIEW_TYPE) {
            view = inflater.inflate(R.layout.custom_game_news_layout2, parent, false);
        } else {
            view = inflater.inflate(R.layout.custom_game_news_layout1, parent, false);
        }
        return new MyNewsHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return isSimple ? SIMPLE_VIEW_TYPE : CARD_VIEW_TYPE;
    }

    public void setSimple(boolean simple) {
        isSimple = simple;
        notifyItemRangeChanged(0,getItemCount());
    }

    @Override
    public void onBindViewHolder(MyNewsHolder holder, int position) {
        NewsModal newsModal = modals.get(position);
        if (newsModal!=null) {
            if (newsModal.title != null) {
                holder.heading.setText(newsModal.title);
            }

                if (isSimple) {
                    holder.cardView.setVisibility(View.VISIBLE);
                } else {
                    holder.image.setVisibility(View.VISIBLE);
                }
            Picasso.with(context).load(newsModal.content).fit().centerCrop().placeholder(R.drawable.news_image_drawable).error(R.drawable.news_image_drawable).into(holder.image);
               /* else {
                if (isSimple) {
                    holder.cardView.setVisibility(View.VISIBLE);
                } else {
                    holder.image.setVisibility(View.VISIBLE);
                }
                Picasso.with(context).cancelRequest(holder.image);
                holder.image.setBackgroundResource(R.drawable.news_image_drawable);
            }*/
            if (newsModal.pubDate!=null) {
                String dateArray[] = newsModal.pubDate.split(" ");

                if (dateArray.length >= 3) {
                    String date = dateArray[0] + " " + dateArray[1] + " " + dateArray[2];
                    holder.date.setText(date);
                } else if (newsModal.pubDate.length() >= 10) {

                    holder.date.setText(newsModal.pubDate.substring(0, 10));
                }
            }

            if (isSimple) {
                if (newsModal.smallDescription != null && newsModal.smallDescription.trim().length() > 0) {
                    if (newsModal.smallDescription.length() > 100) {
                        holder.description.setText(newsModal.smallDescription.substring(0,100));
                    } else {
                        holder.description.setText(newsModal.smallDescription);
                    }
                }

            }
        }

    }

    @Override
    public int getItemCount() {
        return modals.size();
    }

    class MyNewsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView heading, date, description;
        CardView cardView;
        ImageView image;


         MyNewsHolder(View itemView) {
            super(itemView);
            heading = (TextView) itemView.findViewById(R.id.news_heading);
            if (isSimple) {
                cardView = (CardView) itemView.findViewById(R.id.cardView);
                image = (ImageView) cardView.findViewById(R.id.imageView);

            } else {
                image = (ImageView) itemView.findViewById(R.id.imageView);

            }
            date = (TextView) itemView.findViewById(R.id.date);
            description = (TextView) itemView.findViewById(R.id.description);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            ((MainActivity) context).startNewsFragment(modals, getLayoutPosition());
        }
    }
}
