package com.example.randomlocks.gamesnote.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.randomlocks.gamesnote.Activity.GameDetailActivity;
import com.example.randomlocks.gamesnote.DialogFragment.CoverImageViewerFragment;
import com.example.randomlocks.gamesnote.HelperClass.CustomView.AVLoadingIndicatorView;
import com.example.randomlocks.gamesnote.HelperClass.CustomView.ConsistentGridLayoutManager;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.Interface.OnLoadMoreListener;
import com.example.randomlocks.gamesnote.Modal.GameWikiModal;
import com.example.randomlocks.gamesnote.Modal.GameWikiPlatform;
import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.RealmDatabase.GameDetailDatabase;
import com.example.randomlocks.gamesnote.RealmDatabase.GameListDatabase;
import com.example.randomlocks.gamesnote.RealmDatabase.RealmInteger;
import com.squareup.picasso.Picasso;

import java.util.List;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by randomlocks on 4/25/2016.
 */

//TODO fix popup speed database
public class GameWikiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //variable for endless scroll
    private final int VIEW_TYPE_LOADING = -1;
    int viewType = 0;
    private List<GameWikiModal> list;
    private Context context;
    private int lastPosition;
    private int imageQuality;
    private Realm realm;
    private GameListDatabase database;
    private OnLoadMoreListener mOnLoadMoreListener;

    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private DisplayMetrics displayMetrics;

    private static final String IMAGE_QUALITY_KEY = "image_preference";


    public interface OnPopupClickInterface{
        void onRemove(GameWikiModal gameWikiModal);
        void onAdd(GameWikiModal gameWikiModal,int addTypeId);
    }

    private OnPopupClickInterface onPopupClickInterface;



    public GameWikiAdapter(List<GameWikiModal> list, int viewType, Context context, int lastPosition, RecyclerView recyclerView,Realm realm,OnPopupClickInterface onPopupClickInterface) {
        imageQuality = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString(IMAGE_QUALITY_KEY,"1"));
        this.list = list;
        this.viewType = viewType;
        this.context = context;
        this.lastPosition = lastPosition;
        this.realm = realm;
        this.onPopupClickInterface = onPopupClickInterface;

        //setting scroll event
        final ConsistentGridLayoutManager linearLayoutManager = (ConsistentGridLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && GameWikiAdapter.this.list.size() >= 50 && GameWikiAdapter.this.list.size() % 50 == 0) {
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });

        displayMetrics = context.getResources().getDisplayMetrics();



    }

    public void changeView(int viewType){
        this.viewType = viewType;
        notifyItemRangeChanged(0,getItemCount());
    }

    public void swap(List<GameWikiModal> newList) {
        list.clear();
        list.addAll(newList);
        notifyItemRangeInserted(0,getItemCount());

    }

    public void removeAll() {
        int size = this.list.size();
        this.list.clear();
        notifyItemRangeRemoved(0, size);

    }



    public void updateModal(List<GameWikiModal> list){
        this.list.remove(this.list.size() - 1);
        notifyItemRemoved(this.list.size());
        int size = this.list.size();
        this.list.addAll(list);
        notifyItemRangeChanged(size,this.list.size());
        setLoaded();

    }

    public void addNull() {
        list.add(null);
        notifyItemInserted(list.size()-1);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        //return list.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;

        //load more view
        if(list.get(position)==null)
            return  VIEW_TYPE_LOADING;
            //normal view
        else
            return viewType;


    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == 0) {
            View view = inflater.inflate(R.layout.custom_game_wiki_layout1, parent, false);
            return new MyViewHolder1(view);

        }else if(viewType==1){
            View view = inflater.inflate(R.layout.custom_game_wiki_layout2, parent, false);
            return new MyViewHolder2(view);
        } else if(viewType==2){
            View view = inflater.inflate(R.layout.custom_game_wiki_layout3, parent, false);
            return new MyViewHolder3(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = inflater.inflate(R.layout.recycler_bottom_progress, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GameWikiAdapter.MyViewHolder1) {
            final GameWikiModal modal = list.get(holder.getAdapterPosition());
            MyViewHolder1 viewHolder = (MyViewHolder1) holder;
            viewHolder.title.setText(modal.name);


            if (modal.image != null) {
                viewHolder.imageView.setTag(R.string.smallImageUrl, modal.image.smallUrl);
                viewHolder.imageView.setTag(R.string.mediumImageUrl, modal.image.mediumUrl);
                String url = imageQuality==0 ? modal.image.thumbUrl : modal.image.smallUrl;
                Picasso.with(context).load(url).fit().centerCrop().placeholder(R.drawable.news_image_drawable).into(viewHolder.imageView);
            }else {
                viewHolder.imageView.setImageResource(R.drawable.news_image_drawable);
            }

            String date_time = modal.originalReleaseDate;
            String date[];
            if (date_time != null) {
                date = date_time.split(" ");
                viewHolder.date.setText(date[0]);
            }


            /**************** SETTING PLATFORMS*************************/
            List<GameWikiPlatform> platform = modal.platforms;


            if (platform != null) {
                viewHolder.platform1.setVisibility(View.VISIBLE);
                viewHolder.platform2.setVisibility(View.VISIBLE);
                viewHolder.platform3.setVisibility(View.VISIBLE);


                switch (platform.size()) {
                    case 0:
                        viewHolder.platform1.setVisibility(View.INVISIBLE);
                        viewHolder.platform2.setVisibility(View.INVISIBLE);
                        viewHolder.platform3.setVisibility(View.INVISIBLE);

                        break;
                    case 1:
                        viewHolder.platform1.setVisibility(View.INVISIBLE);
                        viewHolder.platform3.setVisibility(View.INVISIBLE);
                        viewHolder.platform2.setText(platform.get(0).abbreviation);
                        break;
                    case 2:
                        viewHolder.platform2.setVisibility(View.INVISIBLE);
                        viewHolder.platform1.setText(platform.get(0).abbreviation);
                        viewHolder.platform3.setText(platform.get(1).abbreviation);
                        break;
                    case 3:


                        viewHolder.platform1.setText(platform.get(0).abbreviation);
                        viewHolder.platform2.setText(platform.get(1).abbreviation);
                        viewHolder.platform3.setText(platform.get(2).abbreviation);
                        break;

                    default:
                        viewHolder.platform1.setText(platform.get(0).abbreviation);
                        viewHolder.platform2.setText(platform.get(1).abbreviation);
                        viewHolder.platform3.setText("+" + (platform.size() - 2) + " more");

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
        }else if(holder instanceof MyViewHolder2){

            final GameWikiModal modal = list.get(holder.getAdapterPosition());
            MyViewHolder2 viewHolder = (MyViewHolder2) holder;
            viewHolder.title.setText(modal.name);

            if (modal.deck != null)
                viewHolder.description.setText(modal.deck);
            else
                viewHolder.description.setText(R.string.no_description);


            if (modal.image != null) {
                viewHolder.imageView.setTag(R.string.smallImageUrl, modal.image.smallUrl);
                viewHolder.imageView.setTag(R.string.mediumImageUrl, modal.image.mediumUrl);
                Picasso.with(context).load(modal.image.smallUrl).fit().centerCrop().placeholder(R.drawable.news_image_drawable).into(viewHolder.imageView);
            }else {
                viewHolder.imageView.setImageResource(R.drawable.news_image_drawable);
            }


            /*********************ANIMATION*********************/

            Animation animation = AnimationUtils.loadAnimation(context,
                    (position > lastPosition) ? R.anim.up_from_bottom
                            : R.anim.down_from_top);
            holder.itemView.startAnimation(animation);
            lastPosition = position;

        } else if(holder instanceof MyViewHolder3){

            final GameWikiModal modal = list.get(holder.getAdapterPosition());
            MyViewHolder3 viewHolder = (MyViewHolder3) holder;

            if (modal.image != null) {
                viewHolder.imageView.setTag(R.string.smallImageUrl, modal.image.smallUrl);
                viewHolder.imageView.setTag(R.string.mediumImageUrl, modal.image.mediumUrl);
                Picasso.with(context).load(modal.image.smallUrl).fit().centerCrop().placeholder(R.drawable.news_image_drawable).into(viewHolder.imageView);
            }else {
                viewHolder.imageView.setImageResource(R.drawable.news_image_drawable);
            }


            /*********************ANIMATION*********************/

            Animation animation = AnimationUtils.loadAnimation(context,
                    (position > lastPosition) ? R.anim.up_from_bottom
                            : R.anim.down_from_top);
            holder.itemView.startAnimation(animation);
            lastPosition = position;

        }else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    private void setLoaded() {
        isLoading = false;
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);


        if (holder instanceof MyViewHolder1 || holder instanceof MyViewHolder2) {
            holder.itemView.clearAnimation();
        }


    }

     private class LoadingViewHolder extends RecyclerView.ViewHolder {
         AVLoadingIndicatorView progressBar;

        LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (AVLoadingIndicatorView) itemView.findViewById(R.id.progressBar);
        }
    }

    //viewholder for layout 1

     private class MyViewHolder1 extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {


        TextView title, date;
        Button platform1, platform2, platform3;
        ImageView imageView;
        CardView cardView;
        ImageButton popupMenu;
         PopupMenu popup;
         GameWikiModal modal;



        MyViewHolder1(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            title = (TextView) itemView.findViewById(R.id.title);
            date = (TextView) itemView.findViewById(R.id.date);
            platform1 = (Button) itemView.findViewById(R.id.platform1);
            platform2 = (Button) itemView.findViewById(R.id.platform2);
            platform3 = (Button) itemView.findViewById(R.id.platform3);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            popupMenu = (ImageButton) itemView.findViewById(R.id.popup);
            int mode = AppCompatDelegate.getDefaultNightMode();
            if(mode==AppCompatDelegate.MODE_NIGHT_YES)
            popupMenu.setColorFilter(Color.argb(255, 255, 255, 255)); // White Tint

            popup = new PopupMenu(context, popupMenu);
            popup.setOnMenuItemClickListener(this);
            popupMenu.setOnClickListener(this);
            itemView.setOnClickListener(this);
            imageView.setOnClickListener(this);
            platform1.setOnClickListener(this);
            platform2.setOnClickListener(this);
            platform3.setOnClickListener(this);

        }


        @Override
        public void onClick(View view) {
             modal = list.get(getLayoutPosition());
            Toasty.info(context,modal.id+"").show();
            switch (view.getId()) {

                case R.id.cardView:
                    Toaster.make(context,modal.name);
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
                    String medium_url = (String) view.getTag(R.string.mediumImageUrl);
                    String small_url = (String) view.getTag(R.string.smallImageUrl);
                    if(medium_url==null)
                    Toasty.error(context,"no image found", Toast.LENGTH_SHORT,true).show();

                    else {
                        CoverImageViewerFragment dialog = CoverImageViewerFragment.newInstance(small_url,medium_url,list.get(getAdapterPosition()).name);
                        dialog.show(((FragmentActivity) context).getSupportFragmentManager(), "ImageViewer");
                    }



                    break;

                case R.id.platform1:
                    if (list.get(getLayoutPosition()).platforms != null) {
                        Toasty.info(context,list.get(getLayoutPosition()).platforms.get(0).name, Toast.LENGTH_SHORT,true).show();

                    }
                    break;

                case R.id.platform2:
                    if (list.get(getLayoutPosition()).platforms != null) {
                        if (list.get(getLayoutPosition()).platforms.size() == 1)
                        Toasty.info(context,list.get(getLayoutPosition()).platforms.get(0).name, Toast.LENGTH_SHORT,true).show();

                        else
                        Toasty.info(context,list.get(getLayoutPosition()).platforms.get(1).name, Toast.LENGTH_SHORT,true).show();

                    }
                    break;

                case R.id.platform3:
                    if(list.get(getLayoutPosition()).platforms!=null){
                        if(list.get(getLayoutPosition()).platforms.size()==2){
                            Toasty.info(context,list.get(getLayoutPosition()).platforms.get(1).name, Toast.LENGTH_SHORT,true).show();
                        } else if(list.get(getLayoutPosition()).platforms.size()==3){
                            Toasty.info(context,list.get(getLayoutPosition()).platforms.get(2).name, Toast.LENGTH_SHORT,true).show();
                        }else if(list.get(getLayoutPosition()).platforms.size()>3) {
                            PopupMenu popup = new PopupMenu(context, view);
                            GameWikiModal platformModal = list.get(getLayoutPosition());
                            for(int i=2, len = platformModal.platforms.size();i<len;i++){
                                popup.getMenu().add(platformModal.platforms.get(i).name);
                            }
                            popup.show();
                        }

                    }
                    break;

                case R.id.popup :
                    popup.getMenu().clear();
                    final MenuInflater inflater = popup.getMenuInflater();
                    database = realm.where(GameListDatabase.class).equalTo("game_id",modal.id).findFirst();
                    if(database==null){
                        inflater.inflate(R.menu.popup_add,popup.getMenu());
                    }else {

                        inflater.inflate(R.menu.popup_remove,popup.getMenu());
                    }

                    popup.show();
                    break;



            }


        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {



            int id = item.getItemId();
            if(id==R.id.remove){
                onPopupClickInterface.onRemove(modal);
                return true;
            }else {
                onPopupClickInterface.onAdd(modal,id);
                return true;

            }


        }







    }

    private class MyViewHolder2 extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {


        TextView title, description;
        ImageView imageView;
        CardView cardView;
        ImageButton popupMenu;
        PopupMenu popup;
        GameWikiModal modal;




        MyViewHolder2(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            popupMenu = (ImageButton) itemView.findViewById(R.id.popup);
            int mode = AppCompatDelegate.getDefaultNightMode();
            if(mode==AppCompatDelegate.MODE_NIGHT_YES)
                popupMenu.setColorFilter(Color.argb(255, 255, 255, 255)); // White Tint


            popupMenu.setOnClickListener(this);
            popup = new PopupMenu(context, popupMenu);
            popup.setOnMenuItemClickListener(this);
            itemView.setOnClickListener(this);
            imageView.setOnClickListener(this);

        }


        @Override
        public void onClick(View view) {
            modal = list.get(getLayoutPosition());
            switch (view.getId()) {

                case R.id.cardView:
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
                    String medium_url = (String) view.getTag(R.string.mediumImageUrl);
                    String small_url = (String) view.getTag(R.string.smallImageUrl);
                    if(medium_url==null)
                    Toasty.error(context,"no image found", Toast.LENGTH_SHORT,true).show();

                    else {
                        CoverImageViewerFragment dialog = CoverImageViewerFragment.newInstance(small_url,medium_url,list.get(getAdapterPosition()).name);
                        dialog.show(((FragmentActivity) context).getSupportFragmentManager(), "ImageViewer");
                    }



                    break;



                case R.id.popup :
                    popup.getMenu().clear();
                    final MenuInflater inflater = popup.getMenuInflater();
                    database = realm.where(GameListDatabase.class).equalTo("game_id",modal.id).findFirst();
                    if(database==null){
                        inflater.inflate(R.menu.popup_add,popup.getMenu());
                    }else {

                        inflater.inflate(R.menu.popup_remove,popup.getMenu());
                    }

                    popup.show();
                    break;



            }


        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {



            int id = item.getItemId();

            if(id==R.id.remove){
                onPopupClickInterface.onRemove(modal);
                return true;
            }else {
                onPopupClickInterface.onAdd(modal,id);
                return true;

            }


        }


    }

    private class MyViewHolder3 extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener, View.OnLongClickListener {


        ImageView imageView;
        ImageButton popupMenu;
        PopupMenu popup;
        GameWikiModal modal;





        MyViewHolder3(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imageView.getLayoutParams().height = (int) ((displayMetrics.widthPixels / 2)*1.4);
            popupMenu = (ImageButton) itemView.findViewById(R.id.popup);

            popupMenu.setColorFilter(Color.argb(255, 255, 255, 255)); // White Tint


            popupMenu.setOnClickListener(this);
            popup = new PopupMenu(context, popupMenu);
            popup.setOnMenuItemClickListener(this);
            imageView.setOnClickListener(this);
            imageView.setOnLongClickListener(this);

        }


        @Override
        public void onClick(View view) {
             modal = list.get(getLayoutPosition());
            switch (view.getId()) {

                case R.id.imageView:
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


                case R.id.popup :
                    popup.getMenu().clear();
                    final MenuInflater inflater = popup.getMenuInflater();
                    database = realm.where(GameListDatabase.class).equalTo("game_id",modal.id).findFirst();
                    if(database==null){
                        inflater.inflate(R.menu.popup_add,popup.getMenu());
                    }else {

                        inflater.inflate(R.menu.popup_remove,popup.getMenu());
                    }

                    popup.show();
                    break;



            }


        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {



            int id = item.getItemId();


            if(id==R.id.remove){
                onPopupClickInterface.onRemove(modal);

                return true;
            }else {
                onPopupClickInterface.onAdd(modal,id);
                return true;
            }


        }


        @Override
        public boolean onLongClick(View v) {
            Toaster.make(context,list.get(getLayoutPosition()).name);
            return true;
        }
    }

}

