package com.example.randomlocks.gamesnote.adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.dialogFragment.CoverImageViewerFragment;
import com.example.randomlocks.gamesnote.helperClass.CustomView.AVLoadingIndicatorView;
import com.example.randomlocks.gamesnote.helperClass.CustomView.ConsistentLinearLayoutManager;
import com.example.randomlocks.gamesnote.interfaces.OnLoadMoreListener;
import com.example.randomlocks.gamesnote.modals.characterSearchModal.CharacterSearchModal;
import com.squareup.picasso.Picasso;

import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * Created by randomlocks on 7/11/2016.
 */
public class GameCharacterSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String IMAGE_QUALITY_KEY = "image_preference";
    //variable for endless scroll
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private List<CharacterSearchModal> modals;
    private Context context;
    private OnClickInterface mOnClickInterface;
    private OnLoadMoreListener mOnLoadMoreListener;
    private int imageQuality;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;


    public GameCharacterSearchAdapter(List<CharacterSearchModal> modals, final Context context, RecyclerView recyclerView, OnClickInterface mOnClickInterface) {
        imageQuality = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString(IMAGE_QUALITY_KEY,"1"));
        this.modals = modals;
        this.context = context;
        this.mOnClickInterface = mOnClickInterface;


        final ConsistentLinearLayoutManager linearLayoutManager;
        final GridLayoutManager gridLayoutManager;
        if(recyclerView.getLayoutManager() instanceof ConsistentLinearLayoutManager){
            linearLayoutManager = (ConsistentLinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && GameCharacterSearchAdapter.this.modals.size() >= 50 && GameCharacterSearchAdapter.this.modals.size() % 50 == 0) {
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

                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && GameCharacterSearchAdapter.this.modals.size() >= 50 && GameCharacterSearchAdapter.this.modals.size() % 50 == 0) {
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener.onLoadMore();
                        }
                        isLoading = true;
                    }
                }
            });
        } //else




    }

    public void swap(List<CharacterSearchModal> newModals) {
        modals.clear();
        modals.addAll(newModals);
        notifyItemRangeInserted(0, getItemCount());
    }

    public void removeAll() {
        int size = this.modals.size();
        this.modals.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void updateModal(List<CharacterSearchModal> modals) {
        this.modals.remove(this.modals.size() - 1);
        notifyItemRemoved(this.modals.size());
        int size = this.modals.size();
        this.modals.addAll(modals);
        notifyItemRangeChanged(size, this.modals.size());
        setLoaded();

    }

    public void addNull() {
        modals.add(null);
        notifyItemInserted(modals.size() - 1);
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
        if (holder instanceof  GameCharacterSearchAdapter.MyViewHolder) {
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
                viewHolder.profileImage.setTag(R.string.smallImageUrl, modal.image.thumbUrl);
                viewHolder.profileImage.setTag(R.string.mediumImageUrl, modal.image.mediumUrl);
                String url = imageQuality==0 ? modal.image.thumbUrl : modal.image.smallUrl;
                Picasso.with(context).load(url).fit().centerCrop().placeholder(R.drawable.news_image_drawable).into(viewHolder.profileImage);

            } else {
                viewHolder.profileImage.setImageResource(R.drawable.news_image_drawable);
            }

            if(modal.deck!=null){
                viewHolder.deck.setText(modal.deck);
            }
        }else if (holder instanceof GameCharacterSearchAdapter.LoadingViewHolder) {
            GameCharacterSearchAdapter.LoadingViewHolder loadingViewHolder = (GameCharacterSearchAdapter.LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setVisibility(View.VISIBLE);
        };

    }

    @Override
    public int getItemCount() {
        return modals==null?0:modals.size();
    }

    private void setLoaded() {
        isLoading = false;
    }

    public interface OnClickInterface {
        void onItemClick(String apiUrl, String image, String name);
    }

//    public boolean getLoaded(){ return isLoading;}

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
            profileImage = (ImageView) itemView.findViewById(R.id.imageView);
            name = (TextView) itemView.findViewById(R.id.title);
            deck = (TextView) itemView.findViewById(R.id.description);
            profileImage.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if(v.getId()==R.id.image){
                String medium_url = (String) profileImage.getTag(R.string.mediumImageUrl);
                String small_url = (String) profileImage.getTag(R.string.smallImageUrl);
                if(medium_url==null)
                Toasty.error(context,"no image found", Toast.LENGTH_SHORT,true).show();

                else {
                    CoverImageViewerFragment dialog = CoverImageViewerFragment.newInstance(small_url,medium_url,modals.get(getAdapterPosition()).name);
                    dialog.show(((FragmentActivity) context).getSupportFragmentManager(), "ImageViewer");
                }
            }

            else {
                CharacterSearchModal modal = modals.get(getLayoutPosition());
                mOnClickInterface.onItemClick(modal.apiDetailUrl, modal.image != null ? modal.image.mediumUrl : null,modal.name);
            }

        }
    }




}
