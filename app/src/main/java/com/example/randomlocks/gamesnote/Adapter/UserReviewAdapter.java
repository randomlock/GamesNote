package com.example.randomlocks.gamesnote.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.Modal.UserReviewModal.UserReviewModal;
import com.example.randomlocks.gamesnote.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;

/**
 * Created by randomlocks on 7/10/2016.
 */
public class UserReviewAdapter extends RecyclerView.Adapter<UserReviewAdapter.MyViewHolder> {

    Context context;
    List<UserReviewModal> userReviewModals;

    public UserReviewAdapter(List<UserReviewModal> userReviewModals, Context context) {
        this.userReviewModals = userReviewModals;
        this.context = context;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.custom_user_review_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        UserReviewModal userReviewModal = userReviewModals.get(position);
        if (userReviewModal.reviewer != null) {
            holder.userName.setText(userReviewModal.reviewer);
        }
        if (userReviewModal.score >= 0) {
            holder.ratingBar.setRating(userReviewModal.score);
        }
        if (userReviewModal.dateAdded != null) {
            String[] date = userReviewModal.dateAdded.split(" ");
            holder.date.setText(date[0]);
        }
        if (userReviewModal.deck != null) {
            holder.smallDescription.setText(userReviewModal.deck);
        }
        if (userReviewModal.description != null) {
            Document jsoup = Jsoup.parse(userReviewModal.description);
            Element element = jsoup.getElementsByTag("p").first();
            if (element != null) {
                holder.largeDescription.setText(element.text());
            }
        }

    }

    @Override
    public int getItemCount() {
        return userReviewModals.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView userName, date, smallDescription, largeDescription;
        RatingBar ratingBar;

        public MyViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.user_name);
            date = (TextView) itemView.findViewById(R.id.date);
            smallDescription = (TextView) itemView.findViewById(R.id.deck);
            largeDescription = (TextView) itemView.findViewById(R.id.description);
            ratingBar = (RatingBar) itemView.findViewById(R.id.myRatingBar);

        }
    }

}
