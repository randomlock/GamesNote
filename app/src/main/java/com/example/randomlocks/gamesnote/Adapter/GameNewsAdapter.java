package com.example.randomlocks.gamesnote.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.Activity.MainActivity;
import com.example.randomlocks.gamesnote.Modal.NewsModal.NewsModal;
import com.example.randomlocks.gamesnote.R;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

/**
 * Created by randomlocks on 7/1/2016.
 */
public class GameNewsAdapter extends RecyclerView.Adapter<GameNewsAdapter.MyNewsHolder> {


    private static final int SIMPLE_VIEW_TYPE = 0;
    private static final int CARD_VIEW_TYPE = 1;

    List<NewsModal> modals;
    Context context;
    boolean isSimple;
    Calendar calendar = Calendar.getInstance();

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
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(MyNewsHolder holder, int position) {
        NewsModal newsModal = modals.get(position);
        if (newsModal.title != null) {
            holder.heading.setText(newsModal.title);
        }
        if (newsModal.content != null) {
            holder.cardView.setVisibility(View.VISIBLE);
            Picasso.with(context).load(newsModal.content).fit().into(holder.image);
        } else {
            if (isSimple) {
                holder.cardView.setVisibility(View.GONE);
            }
        }
        String dateArray[] = newsModal.pubDate.split(" ");

        if (dateArray.length >= 3) {
            String date = dateArray[0] + " " + dateArray[1] + " " + dateArray[2];
            holder.date.setText(date);
        } else if (newsModal.pubDate.length() >= 10) {

            holder.date.setText(newsModal.pubDate.substring(0, 10));
        }

        if (isSimple) {
            if (newsModal.smallDescription != null && newsModal.smallDescription.trim().length() > 0) {
                if (newsModal.smallDescription.length() > 50) {
                    holder.description.setText(newsModal.smallDescription.substring(0, 50));
                } else {
                    holder.description.setText(newsModal.smallDescription);
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


        public MyNewsHolder(View itemView) {
            super(itemView);
            heading = (TextView) itemView.findViewById(R.id.news_heading);
            if (isSimple) {
                cardView = (CardView) itemView.findViewById(R.id.cardView);
                image = (ImageView) cardView.findViewById(R.id.news_image);

            } else {
                image = (ImageView) itemView.findViewById(R.id.news_image);

            }
            date = (TextView) itemView.findViewById(R.id.news_date);
            description = (TextView) itemView.findViewById(R.id.news_description);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            ((MainActivity) context).startNewsFragment(modals, getLayoutPosition());
        }
    }
}
