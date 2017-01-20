package com.example.randomlocks.gamesnote.Adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.HelperClass.CustomView.AVLoadingIndicatorView;
import com.example.randomlocks.gamesnote.HelperClass.CustomView.ConsistentLinearLayoutManager;
import com.example.randomlocks.gamesnote.HelperClass.DynamicHeightImageView;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.Interface.OnLoadMoreListener;
import com.example.randomlocks.gamesnote.Modal.CharacterSearchModal.CharacterSearchModal;
import com.example.randomlocks.gamesnote.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by randomlocks on 7/11/2016.
 */
public class CharacterSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<CharacterSearchModal> modals;
    Context context;
    private OnClickInterface mOnClickInterface;


    //variable for endless scroll
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private OnLoadMoreListener mOnLoadMoreListener;

    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;



    public interface OnClickInterface {
        void onItemClick(String apiUrl, String image, String name);
    }

    public CharacterSearchAdapter(List<CharacterSearchModal> modals, Context context,RecyclerView recyclerView,OnClickInterface mOnClickInterface) {
        this.modals = modals;
        this.context = context;
        this.mOnClickInterface = mOnClickInterface;


        final LinearLayoutManager linearLayoutManager;
        final GridLayoutManager gridLayoutManager;
        if(recyclerView.getLayoutManager() instanceof LinearLayoutManager){
            linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener.onLoadMore();
                        }
                        isLoading = true;
                    }
                }
            });
        }else {

            gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = gridLayoutManager.getItemCount();
                    lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();

                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener.onLoadMore();
                        }
                        isLoading = true;
                    }
                }
            });
        } //else




    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        return modals.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == VIEW_TYPE_ITEM) {
            View view = inflater.inflate(R.layout.custom_character_wiki_layout, parent, false);
            return new MyViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = inflater.inflate(R.layout.recycler_bottom_progress, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof  CharacterSearchAdapter.MyViewHolder) {
            CharacterSearchModal modal = modals.get(position);
            MyViewHolder viewHolder = (MyViewHolder) holder;

            if (modal.name != null) {
                viewHolder.name.setText(modal.name);
            }
            if (modal.image != null && modal.image.thumbUrl != null && !modal.image.thumbUrl.isEmpty()) {
                /*Picasso.with(context).load(modal.image.mediumUrl).into(holder.profileImage, new Callback() {
                    @Override
                    public void onSuccess() {
                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.profileImage.getLayoutParams();
                            params.height = holder.profileImage.getHeight();
                            holder.profileImage.setLayoutParams(params);

                    }

                    @Override
                    public void onError() {

                    }
                });*/

                Picasso.with(context).load(modal.image.mediumUrl).fit().centerCrop().into(viewHolder.profileImage);


            } else {
                viewHolder.profileImage.setImageResource(R.drawable.headerbackground);
            }

            if(modal.deck!=null){
                viewHolder.deck.setText(modal.deck);
            }
        }else if (holder instanceof CharacterSearchAdapter.LoadingViewHolder) {
            CharacterSearchAdapter.LoadingViewHolder loadingViewHolder = (CharacterSearchAdapter.LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setVisibility(View.VISIBLE);
        };

    }

    @Override
    public int getItemCount() {
        return modals==null?0:modals.size();
    }

    public void setLoaded() {
        isLoading = false;
    }


    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        AVLoadingIndicatorView progressBar;

        LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (AVLoadingIndicatorView) itemView.findViewById(R.id.progressBar);
        }
    }


    private class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView profileImage;
        TextView name,deck;

        MyViewHolder(View itemView) {
            super(itemView);
            profileImage = (ImageView) itemView.findViewById(R.id.image);
            name = (TextView) itemView.findViewById(R.id.name);
            deck = (TextView) itemView.findViewById(R.id.card_deck);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            CharacterSearchModal modal = modals.get(getLayoutPosition());
            mOnClickInterface.onItemClick(modal.apiDetailUrl, modal.image != null ? modal.image.mediumUrl : null,modal.name);
        }
    }




}
