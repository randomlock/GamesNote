package com.example.randomlocks.gamesnote.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.HelperClass.CustomView.AVLoadingIndicatorView;
import com.example.randomlocks.gamesnote.HelperClass.CustomView.ConsistentLinearLayoutManager;
import com.example.randomlocks.gamesnote.Interface.OnLoadMoreListener;
import com.example.randomlocks.gamesnote.Modal.GamesVideoModal.GamesVideoModal;
import com.example.randomlocks.gamesnote.R;
import com.flyco.labelview.LabelView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import io.realm.Realm;


//TODO FIX optimization on query ASAP effectively
public class GameVideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //variable for endless scroll
    private static final int VIEW_TYPE_LOADING = -1;
    private static final int SIMPLE_VIEW_TYPE = 0;
    private static final int CARD_VIEW_TYPE = 1;
    Context context;
    int viewType = 0;
    private List<GamesVideoModal> modalList;
    private OnClickInterface mOnClickInteraface;
    private HashMap<Integer, Integer> hashResults;
    private Realm realm;
    private OnLoadMoreListener mOnLoadMoreListener;

    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;


    public GameVideoAdapter(List<GamesVideoModal> modalList, Context context, boolean isSimple, Realm realm, OnClickInterface mOnClickInterface, HashMap<Integer, Integer> realmResults, RecyclerView recyclerView) {
        this.modalList = modalList;
        viewType = isSimple ? 0 : 1;
        this.context = context;
        this.mOnClickInteraface = mOnClickInterface;
        this.hashResults = realmResults;
        this.realm = realm;

        final ConsistentLinearLayoutManager linearLayoutManager = (ConsistentLinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && GameVideoAdapter.this.modalList.size() >= 50 && GameVideoAdapter.this.modalList.size() % 50 == 0) {
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });


    }

    public void setSimple(boolean simple) {
        this.viewType = simple ? SIMPLE_VIEW_TYPE : CARD_VIEW_TYPE;
        notifyItemRangeChanged(0,getItemCount());
    }

    public void swap(List<GamesVideoModal> newModals) {
        modalList.clear();
        modalList.addAll(newModals);
        notifyItemRangeInserted(0,getItemCount());
    }

    public void removeAll() {
        int size = this.modalList.size();
        this.modalList.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void updateModal(List<GamesVideoModal> modals){
        this.modalList.remove(this.modalList.size() - 1);
        notifyItemRemoved(this.modalList.size());
        int size = this.modalList.size();
        this.modalList.addAll(modals);
        notifyItemRangeChanged(size,this.modalList.size());
        setLoaded();

    }

    public void updateModal(int position, HashMap<Integer, Integer> hashResults) {
        this.hashResults = hashResults;
        notifyItemChanged(position);
    }

    public void addNull() {
        modalList.add(null);
        notifyItemInserted(modalList.size()-1);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        //return list.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;

        //load more view
        if(modalList.get(position)==null)
            return  VIEW_TYPE_LOADING;
            //normal view
        else
            return viewType;


    }

    private void setLoaded() {
        isLoading = false;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == SIMPLE_VIEW_TYPE) {
            View view;
            view = inflater.inflate(R.layout.custom_game_video_layout2, parent, false);
            return new MyViewHolder(view);

        } else if(viewType==CARD_VIEW_TYPE){
            View view;
            view = inflater.inflate(R.layout.custom_game_video_layout1, parent, false);
            return new MyViewHolder(view);

        } else if(viewType==VIEW_TYPE_LOADING) {
            View view;
            view = inflater.inflate(R.layout.recycler_bottom_progress, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof MyViewHolder) {
            MyViewHolder myHolder = (MyViewHolder) holder;
            final GamesVideoModal modal = modalList.get(position);
            if (modal.image.mediumUrl != null) {
                Picasso.with(context).load(modal.image.mediumUrl).fit().centerCrop().into(myHolder.videoThumb);
            }
            if (modal.name != null) {
                myHolder.title.setText(modal.name);
            }
            if (modal.deck != null) {
                myHolder.description.setText(modal.deck);
            }

            if(modal.videoType!=null){
                myHolder.category.setText(modal.videoType);
            }

            if(modal.publishDate!=null)
                myHolder.date.setText(modal.publishDate.split(" ")[0]);
            if (modal.lengthSeconds > 0) {
                int minute = modal.lengthSeconds / 60;
                int seconds = modal.lengthSeconds % 60;
                int hour = minute / 60;
                StringBuilder date = new StringBuilder();
                if (hour > 0) {
                    minute = minute - hour * 60;

                    if (minute < 10) {
                        date.append(hour).append(":0");
                    } else {
                        date.append(hour).append(":");

                    }

                }


                if (seconds < 10) {
                    date.append(minute).append(":0").append(seconds);
                } else {
                    date.append(minute).append(":").append(seconds);
                }
            myHolder.timeLabel.setText(date.toString());


                if (hashResults.containsKey(modal.id)) {

                    myHolder.isWatchLabel.setVisibility(View.VISIBLE);

                }


            }

        }else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setVisibility(View.VISIBLE);
        };


    }


    @Override
    public int getItemCount() {
        return modalList==null ? 0:modalList.size();
    }


    public interface OnClickInterface {
        void onWatchLater(GamesVideoModal modal);

        void onLike(GamesVideoModal modal);

        void onShare(GamesVideoModal modal);

        void onVideoClick(GamesVideoModal modal, int adapterPosition, int elapsed_time);
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        AVLoadingIndicatorView progressBar;

        LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (AVLoadingIndicatorView) itemView.findViewById(R.id.progressBar);
        }
    }




    private class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        ImageView videoThumb;
        TextView title, description, date,category;
        LabelView timeLabel,isWatchLabel;
        ImageButton popupMenu;
        GamesVideoModal modal;

        PopupMenu popup;






        MyViewHolder(View itemView) {
            super(itemView);
            videoThumb = (ImageView) itemView.findViewById(R.id.imageView);
            title = (TextView) itemView.findViewById(R.id.title);
            popupMenu = (ImageButton) itemView.findViewById(R.id.popup);
            int mode = AppCompatDelegate.getDefaultNightMode();
            if(mode==AppCompatDelegate.MODE_NIGHT_YES)
                popupMenu.setColorFilter(Color.argb(255, 255, 255, 255)); // White Tint
            popupMenu.setOnClickListener(this);
            popup = new PopupMenu(context,popupMenu);
            popup.setOnMenuItemClickListener(this);
            final MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.popup_video,popup.getMenu());


            description = (TextView) itemView.findViewById(R.id.description);
            date = (TextView) itemView.findViewById(R.id.date);
            category = (TextView) itemView.findViewById(R.id.category);
            timeLabel  = (LabelView) itemView.findViewById(R.id.time);
            isWatchLabel = (LabelView) itemView.findViewById(R.id.is_watch);
            itemView.setOnClickListener(this);

            if (viewType==CARD_VIEW_TYPE) {
                DisplayMetrics metrics;
                metrics = context.getResources().getDisplayMetrics();
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,metrics.heightPixels/3);
                videoThumb.setLayoutParams(params);
            }


        }

        @Override
        public void onClick(View v) {
            modal = modalList.get(getLayoutPosition());
            switch (v.getId()){
                case R.id.popup :

                    GamesVideoModal realmModel = realm.where(GamesVideoModal.class).equalTo("id",modal.id).findFirst();
                    if(realmModel!=null){
                        if(realmModel.isWatchLater){
                            modal.isWatchLater = true;
                            popup.getMenu().getItem(0).setTitle(R.string.remove_watch_list);
                        }else {
                            modal.isWatchLater = false;
                            popup.getMenu().getItem(0).setTitle(R.string.watch_later);
                        }

                        if(realmModel.isFavorite){
                            modal.isFavorite = true;
                            popup.getMenu().getItem(1).setTitle(R.string.remove_like);
                        }else {
                            modal.isFavorite = false;
                            popup.getMenu().getItem(1).setTitle(R.string.like);
                        }

                    }else {
                        popup.getMenu().getItem(0).setTitle(R.string.watch_later);
                        popup.getMenu().getItem(1).setTitle(R.string.like);
                    }

                    popup.show();
                    break;


                case R.id.root_view:
                    mOnClickInteraface.onVideoClick(modal, getAdapterPosition(), hashResults.containsKey(modal.id) ? hashResults.get(modal.id) : 0);
                    break;




            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){
                case  R.id.watch_later :
                    modal.isWatchLater = !modal.isWatchLater;
                    mOnClickInteraface.onWatchLater(modalList.get(getLayoutPosition()));
                    return true;
                case  R.id.like :
                    modal.isFavorite = !modal.isFavorite;
                    mOnClickInteraface.onLike(modalList.get(getLayoutPosition()));
                    return true;

                case R.id.share:
                    mOnClickInteraface.onShare(modalList.get(getLayoutPosition()));
                    return true;


                default:
                    return false;

            }
        }
    }










}
