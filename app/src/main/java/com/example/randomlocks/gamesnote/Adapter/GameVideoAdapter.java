package com.example.randomlocks.gamesnote.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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


//TODO FIX optimization on query ASAP effectively
public class GameVideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<GamesVideoModal> modalList;
    Context context;
    private OnClickInterface mOnClickInteraface;
    private HashMap<Integer, GamesVideoModal> hashResults;


    //variable for endless scroll
    private static final int VIEW_TYPE_LOADING = -1;
    private static final int SIMPLE_VIEW_TYPE = 0;
    private static final int CARD_VIEW_TYPE = 1;

    int viewType = 0;



    private OnLoadMoreListener mOnLoadMoreListener;

    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;


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
        modalList.clear();
        notifyItemRangeRemoved(0,getItemCount());
    }

    public void updateModal(List<GamesVideoModal> modals){
        this.modalList.remove(this.modalList.size() - 1);
        notifyItemRemoved(this.modalList.size());
        int size = this.modalList.size();
        this.modalList.addAll(modals);
        notifyItemRangeChanged(size,this.modalList.size());
        setLoaded();

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




    public interface OnClickInterface {
        void onWatchLater(GamesVideoModal modal);

        void onLike(GamesVideoModal modal);

        void onShare();
    }


    public GameVideoAdapter(final List<GamesVideoModal> modalList, Context context, boolean isSimple, OnClickInterface mOnClickInterface, HashMap<Integer, GamesVideoModal> realmResults, RecyclerView recyclerView) {
        this.modalList = modalList;
        viewType = isSimple ? 0 : 1;
        this.context = context;
        this.mOnClickInteraface = mOnClickInterface;
        this.hashResults = realmResults;

        final ConsistentLinearLayoutManager linearLayoutManager = (ConsistentLinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)&& modalList.size()>=50 && modalList.size() % 50 ==0) {
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });




    }






    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == SIMPLE_VIEW_TYPE) {
            View view;
            view = inflater.inflate(R.layout.custom_game_video_layout2, parent, false);
            return new MyViewHolder2(view);

        } else if(viewType==CARD_VIEW_TYPE){
            View view;
            view = inflater.inflate(R.layout.custom_game_video_layout1, parent, false);
            return new MyViewHolder1(view);

        } else if(viewType==VIEW_TYPE_LOADING) {
            View view;
            view = inflater.inflate(R.layout.recycler_bottom_progress, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof GameVideoAdapter.MyViewHolder1) {
            final GameVideoAdapter.MyViewHolder1 myHolder = (MyViewHolder1)holder;
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


                myHolder.length.setText(date.toString());
            }
            if (modal.videoType != null) {
                myHolder.video_type.setVisibility(View.VISIBLE);
                myHolder.video_type.setText(modal.videoType);
            } else {
                myHolder.video_type.setVisibility(View.GONE);
            }

            final GamesVideoModal realmModal = hashResults.get(modal.id);
            if (realmModal != null) {
                if (realmModal.isWatchLater) {
                    modal.isWatchLater = true;
                    myHolder.watchLater.setColorFilter(ContextCompat.getColor(context, R.color.primary));
                }
                if (realmModal.isFavorite) {
                    modal.isFavorite = true;
                    myHolder.like.setColorFilter(ContextCompat.getColor(context, R.color.primary));
                }
            } else {
                myHolder.watchLater.setColorFilter(ContextCompat.getColor(context, R.color.white));
                myHolder.like.setColorFilter(ContextCompat.getColor(context, R.color.white));

            }




            /*  RealmResults<VideoListDatabase> realmModal = realm.where(VideoListDatabase.class).equalTo("id", modal.id).findAll();
            if (realmModal.size() == 0) {
                myHolder.status.setVisibility(View.GONE);
            } else {
                myHolder.status.setVisibility(View.VISIBLE);
            }*/
        }else if(holder instanceof MyViewHolder2){
            final GameVideoAdapter.MyViewHolder2 myHolder = (MyViewHolder2)holder;
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

            final GamesVideoModal realmModal = hashResults.get(modal.id);
            if (realmModal != null) {
                if (realmModal.isWatchLater) {
                    modal.isWatchLater = true;
                }
                if (realmModal.isFavorite) {
                    modal.isFavorite = true;
                }
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







    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        AVLoadingIndicatorView progressBar;

        LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (AVLoadingIndicatorView) itemView.findViewById(R.id.progressBar);
        }
    }



    private class MyViewHolder1 extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView videoThumb;
        Button length, status, video_type;
        TextView title, description, date;
        ImageButton watchLater, like, share;


        MyViewHolder1(View itemView) {
            super(itemView);
            videoThumb = (ImageView) itemView.findViewById(R.id.imageView);
            length = (Button) itemView.findViewById(R.id.length);
            status = (Button) itemView.findViewById(R.id.status);
            video_type = (Button) itemView.findViewById(R.id.video_type);

            watchLater = (ImageButton) itemView.findViewById(R.id.watch_later);
            watchLater.setVisibility(View.VISIBLE);
            like = (ImageButton) itemView.findViewById(R.id.like);
            like.setVisibility(View.VISIBLE);
            share = (ImageButton) itemView.findViewById(R.id.share);
            share.setVisibility(View.VISIBLE);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);

            itemView.setOnClickListener(this);
            like.setOnClickListener(this);
            share.setOnClickListener(this);
            watchLater.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            GamesVideoModal modal = modalList.get(getLayoutPosition());
            ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
            scale.setDuration(500);
            scale.setInterpolator(new OvershootInterpolator());
            switch (v.getId()) {

                case R.id.watch_later:
                    v.startAnimation(scale);
                    modal.isWatchLater = !modal.isWatchLater;
                    if (modal.isWatchLater) {
                        ((ImageButton) v).setColorFilter(ContextCompat.getColor(context, R.color.primary));
                    } else {
                        ((ImageButton) v).setColorFilter(ContextCompat.getColor(context, R.color.white));

                    }
                    mOnClickInteraface.onWatchLater(modalList.get(getLayoutPosition()));
                    break;

                case R.id.like:
                    v.startAnimation(scale);
                    modal.isFavorite = !modal.isFavorite;

                    if (modal.isFavorite) {
                        ((ImageButton) v).setColorFilter(ContextCompat.getColor(context, R.color.primary));
                    } else {
                        ((ImageButton) v).setColorFilter(ContextCompat.getColor(context, R.color.white));

                    }

                    mOnClickInteraface.onLike(modalList.get(getLayoutPosition()));
                    break;

                case R.id.root_view:
                  /*  GamesVideoModal videoModal = modalList.get(getLayoutPosition());
                    String highUrl = videoModal.highUrl + "?api_key=" + GiantBomb.API_KEY;
                    Intent intent = new Intent(context, PlayerActivity.class)
                            .setData(Uri.parse(highUrl))
                            .putExtra(PlayerActivity.CONTENT_ID_EXTRA, highUrl.toLowerCase(Locale.US).replaceAll("\\s", ""))
                            .putExtra(PlayerActivity.CONTENT_TYPE_EXTRA, Util.TYPE_OTHER)
                            .putExtra(GiantBomb.KEY, videoModal.id)
                            .putExtra(PlayerActivity.PROVIDER_EXTRA, "");

                    context.startActivity(intent);*/


                    break;


            }
        }
    }



    private class MyViewHolder2 extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        ImageView videoThumb;
        TextView title, description, date,category;
        LabelView timeLabel,isWatchLabel;
        ImageButton popup;
        GamesVideoModal modal;



        MyViewHolder2(View itemView) {
            super(itemView);
            videoThumb = (ImageView) itemView.findViewById(R.id.imageView);
            title = (TextView) itemView.findViewById(R.id.title);
            popup = (ImageButton) itemView.findViewById(R.id.popup);
            int mode = AppCompatDelegate.getDefaultNightMode();
            if(mode==AppCompatDelegate.MODE_NIGHT_YES)
                popup.setColorFilter(Color.argb(255, 255, 255, 255)); // White Tint
            popup.setOnClickListener(this);
            description = (TextView) itemView.findViewById(R.id.description);
            date = (TextView) itemView.findViewById(R.id.date);
             category = (TextView) itemView.findViewById(R.id.category);
             timeLabel  = (LabelView) itemView.findViewById(R.id.time);
             isWatchLabel = (LabelView) itemView.findViewById(R.id.is_watch);
             itemView.setOnClickListener(this);



        }

        @Override
        public void onClick(View v) {
            modal = modalList.get(getLayoutPosition());
            switch (v.getId()){
                case R.id.popup :
                    final PopupMenu popup = new PopupMenu(context, v);
                    popup.setOnMenuItemClickListener(this);
                    final MenuInflater inflater = popup.getMenuInflater();

                    inflater.inflate(R.menu.popup_video,popup.getMenu());
                    GamesVideoModal realmModel = hashResults.get(modalList.get(getAdapterPosition()).id);
                    if(realmModel!=null){
                        if(realmModel.isWatchLater)
                            popup.getMenu().getItem(0).setTitle(R.string.remove_watch_list);
                        if(realmModel.isFavorite)
                            popup.getMenu().getItem(1).setTitle(R.string.remove_like);
                    }

                    popup.show();
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


                default:
                    return false;

            }
        }
    }










}
