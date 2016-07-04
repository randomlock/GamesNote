package com.example.randomlocks.gamesnote.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.MainActivity;
import com.example.randomlocks.gamesnote.Modal.NewsModal.NewsModal;
import com.example.randomlocks.gamesnote.NewsDetailActivity;
import com.example.randomlocks.gamesnote.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by randomlocks on 7/1/2016.
 */
public class GameNewsAdapter extends RecyclerView.Adapter<GameNewsAdapter.MyNewsHolder> {

    List<NewsModal> modals;
    Context context;

    public GameNewsAdapter(List<NewsModal> modals,Context context) {
        this.modals = modals;
        this.context = context;
    }


    @Override
    public MyNewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.custom_game_news,parent,false);
        return new MyNewsHolder(v);
    }


    @Override
    public void onBindViewHolder(MyNewsHolder holder, int position) {
        NewsModal newsModal = modals.get(position);
        holder.heading.setText(newsModal.title);
        if (newsModal.content!=null) {
            Picasso.with(context).load(newsModal.content).fit().into(holder.image);
        }
        String dateArray[] = newsModal.pubDate.split(" ");
        String date = dateArray[0] +" " +dateArray[1] +" "+dateArray[2];
        holder.date.setText(date);
        String str;
        if(newsModal.isClicked){
            str = "Read";
        }else {
            str = "Not Read";
        }
        holder.status.setText(str);

    }

    @Override
    public int getItemCount() {
        return modals.size();
    }

    class MyNewsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView heading,date,status;
        ImageView image;



        public MyNewsHolder(View itemView) {
            super(itemView);
            heading = (TextView) itemView.findViewById(R.id.news_heading);
            image = (ImageView) itemView.findViewById(R.id.news_image);
            date = (TextView) itemView.findViewById(R.id.news_date);
            status = (TextView) itemView.findViewById(R.id.status);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            NewsModal newsModal = modals.get(getLayoutPosition());
            newsModal.isClicked=true;
            Intent intent = new Intent(context, NewsDetailActivity.class);
            intent.putExtra("DATA",newsModal);
            context.startActivity(intent);

        }
    }
}
