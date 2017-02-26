package com.example.randomlocks.gamesnote.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
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
import android.widget.TextView;

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
import com.example.randomlocks.gamesnote.RealmDatabase.GameListDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by randomlocks on 4/25/2016.
 */

//TODO fix popup speed database
public class GameWikiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<GameWikiModal> list;
    private Context context;
    private int lastPosition;
    private Realm realm;
    private GameListDatabase database;
    int viewType=0;


    //variable for endless scroll
    private final int VIEW_TYPE_LOADING = -1;

    private OnLoadMoreListener mOnLoadMoreListener;

    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;


    public GameWikiAdapter(List<GameWikiModal> list,int viewType, Context context, int lastPosition,RecyclerView recyclerView) {
        this.list = list;
        this.viewType = viewType;
        this.context = context;
        this.lastPosition = lastPosition;
        realm = Realm.getDefaultInstance();


        //setting scroll event
        final ConsistentGridLayoutManager linearLayoutManager = (ConsistentGridLayoutManager) recyclerView.getLayoutManager();
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
                Picasso.with(context).load(modal.image.smallUrl).fit().into(viewHolder.imageView);
            }else {
                Picasso.with(context).cancelRequest(viewHolder.imageView);
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
                Picasso.with(context).load(modal.image.smallUrl).fit().into(viewHolder.imageView);
            }else {
                Picasso.with(context).cancelRequest(viewHolder.imageView);
            }


            /*********************ANIMATION*********************/

            Animation animation = AnimationUtils.loadAnimation(context,
                    (position > lastPosition) ? R.anim.up_from_bottom
                            : R.anim.down_from_top);
            holder.itemView.startAnimation(animation);
            lastPosition = position;

        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setVisibility(View.VISIBLE);
        }else if(holder instanceof MyViewHolder3){

            final GameWikiModal modal = list.get(holder.getAdapterPosition());
            MyViewHolder3 viewHolder = (MyViewHolder3) holder;
            viewHolder.title.setText(modal.name);

            if (modal.image != null) {
                viewHolder.imageView.setTag(R.string.smallImageUrl, modal.image.smallUrl);
                viewHolder.imageView.setTag(R.string.mediumImageUrl, modal.image.mediumUrl);
                Picasso.with(context).load(modal.image.smallUrl).fit().into(viewHolder.imageView);
            }else {
                Picasso.with(context).cancelRequest(viewHolder.imageView);
            }


            /*********************ANIMATION*********************/

            Animation animation = AnimationUtils.loadAnimation(context,
                    (position > lastPosition) ? R.anim.up_from_bottom
                            : R.anim.down_from_top);
            holder.itemView.startAnimation(animation);
            lastPosition = position;

        }


    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
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



     private class MyViewHolder1 extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {


        TextView title, date;
        Button platform1, platform2, platform3;
        ImageView imageView;
        public View view;
        CardView cardView;
        ImageButton popup;

        String str[];


        MyViewHolder1(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            title = (TextView) itemView.findViewById(R.id.title);
            date = (TextView) itemView.findViewById(R.id.date);
            platform1 = (Button) itemView.findViewById(R.id.platform1);
            platform2 = (Button) itemView.findViewById(R.id.platform2);
            platform3 = (Button) itemView.findViewById(R.id.platform3);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            popup = (ImageButton) itemView.findViewById(R.id.popup);
            int mode = AppCompatDelegate.getDefaultNightMode();
            if(mode==AppCompatDelegate.MODE_NIGHT_YES)
            popup.setColorFilter(Color.argb(255, 255, 255, 255)); // White Tint


            popup.setOnClickListener(this);
            itemView.setOnClickListener(this);
            imageView.setOnClickListener(this);
            platform1.setOnClickListener(this);
            platform2.setOnClickListener(this);
            platform3.setOnClickListener(this);

        }


        @Override
        public void onClick(View view) {

            switch (view.getId()) {

                case R.id.cardView:
                    GameWikiModal modal = list.get(getLayoutPosition());
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
                        Toaster.make(context,"no image found");
                    else {
                        CoverImageViewerFragment dialog = CoverImageViewerFragment.newInstance(small_url,medium_url,list.get(getAdapterPosition()).name);
                        dialog.show(((FragmentActivity) context).getSupportFragmentManager(), "ImageViewer");
                    }



                    break;

                case R.id.platform1:
                    if (list.get(getLayoutPosition()).platforms != null) {
                        Toaster.make(context, list.get(getLayoutPosition()).platforms.get(0).name);
                    }
                    break;

                case R.id.platform2:
                    if (list.get(getLayoutPosition()).platforms != null) {
                        if (list.get(getLayoutPosition()).platforms.size() == 1)
                            Toaster.make(context, list.get(getLayoutPosition()).platforms.get(0).name);
                        else
                            Toaster.make(context, list.get(getLayoutPosition()).platforms.get(1).name);
                    }
                    break;

                case R.id.platform3:
                    if(list.get(getLayoutPosition()).platforms!=null){
                        if(list.get(getLayoutPosition()).platforms.size()==2){
                            Toaster.make(context,list.get(getLayoutPosition()).platforms.get(1).name);
                        } else if(list.get(getLayoutPosition()).platforms.size()==3){
                            Toaster.make(context,list.get(getLayoutPosition()).platforms.get(2).name);
                        }else if(list.get(getLayoutPosition()).platforms.size()>3) {
                            Toaster.make(context,"third");

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
                    final PopupMenu popup = new PopupMenu(context, view);
                    popup.setOnMenuItemClickListener(this);
                    final MenuInflater inflater = popup.getMenuInflater();
                     str = list.get(getAdapterPosition()).apiDetailUrl.split("/");
                    database = realm.where(GameListDatabase.class).equalTo("apiDetailUrl",str[str.length - 1]).findFirst();
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

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.where(GameListDatabase.class).equalTo("apiDetailUrl", str[str.length - 1]).findFirst().deleteFromRealm();
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        Toaster.make(context,"deleted");
                    }
                });
                return true;
            }else {
                GameWikiModal newModal = list.get(getAdapterPosition());
                RealmList<GameWikiPlatform> platforms = new RealmList<>();
                if (newModal.platforms!=null) {
                    platforms.addAll(newModal.platforms);
                }

                final GameListDatabase newListDatabase = new GameListDatabase(str[str.length-1],newModal.name,newModal.image!=null ? newModal.image.mediumUrl:null,platforms);

                if(id==R.id.replaying)
                    newListDatabase.setStatus(GiantBomb.REPLAYING);
                else if(id==R.id.planning)
                    newListDatabase.setStatus(GiantBomb.PLANNING);
                else if(id==R.id.dropped)
                    newListDatabase.setStatus(GiantBomb.DROPPED);
                else if(id==R.id.playing)
                    newListDatabase.setStatus(GiantBomb.PLAYING);
                else
                    newListDatabase.setStatus(GiantBomb.COMPLETED);




                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.insert(newListDatabase);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        Toaster.make(context,"added");
                    }
                });

                return true;

            }


        }







    }

    //viewholder for layout 2

    private class MyViewHolder2 extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {


        TextView title, description;
        ImageView imageView;
        CardView cardView;
        ImageButton popup;

        String str[];


        MyViewHolder2(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            popup = (ImageButton) itemView.findViewById(R.id.popup);
            int mode = AppCompatDelegate.getDefaultNightMode();
            if(mode==AppCompatDelegate.MODE_NIGHT_YES)
                popup.setColorFilter(Color.argb(255, 255, 255, 255)); // White Tint


            popup.setOnClickListener(this);
            itemView.setOnClickListener(this);
            imageView.setOnClickListener(this);

        }


        @Override
        public void onClick(View view) {

            switch (view.getId()) {

                case R.id.cardView:
                    GameWikiModal modal = list.get(getLayoutPosition());
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
                        Toaster.make(context,"no image found");
                    else {
                        CoverImageViewerFragment dialog = CoverImageViewerFragment.newInstance(small_url,medium_url,list.get(getAdapterPosition()).name);
                        dialog.show(((FragmentActivity) context).getSupportFragmentManager(), "ImageViewer");
                    }



                    break;



                case R.id.popup :
                    final PopupMenu popup = new PopupMenu(context, view);
                    popup.setOnMenuItemClickListener(this);
                    final MenuInflater inflater = popup.getMenuInflater();
                    str = list.get(getAdapterPosition()).apiDetailUrl.split("/");
                    database = realm.where(GameListDatabase.class).equalTo("apiDetailUrl",str[str.length - 1]).findFirst();
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

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.where(GameListDatabase.class).equalTo("apiDetailUrl", str[str.length - 1]).findFirst().deleteFromRealm();
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        Toaster.make(context,"deleted");
                    }
                });
                return true;
            }else {
                GameWikiModal newModal = list.get(getAdapterPosition());
                RealmList<GameWikiPlatform> platforms = new RealmList<>();
                if (newModal.platforms!=null) {
                    platforms.addAll(newModal.platforms);
                }

                final GameListDatabase newListDatabase = new GameListDatabase(str[str.length-1],newModal.name,newModal.image!=null ? newModal.image.mediumUrl:null,platforms);

                if(id==R.id.replaying)
                    newListDatabase.setStatus(GiantBomb.REPLAYING);
                else if(id==R.id.planning)
                    newListDatabase.setStatus(GiantBomb.PLANNING);
                else if(id==R.id.dropped)
                    newListDatabase.setStatus(GiantBomb.DROPPED);
                else if(id==R.id.playing)
                    newListDatabase.setStatus(GiantBomb.PLAYING);
                else
                    newListDatabase.setStatus(GiantBomb.COMPLETED);




                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.insert(newListDatabase);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        Toaster.make(context,"added");
                    }
                });

                return true;

            }


        }


    }

    private class MyViewHolder3 extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {


        TextView title;
        ImageView imageView;
        CardView cardView;
        ImageButton popup;

        String str[];


        MyViewHolder3(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            title = (TextView) itemView.findViewById(R.id.title);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            popup = (ImageButton) itemView.findViewById(R.id.popup);
            int mode = AppCompatDelegate.getDefaultNightMode();
            if(mode==AppCompatDelegate.MODE_NIGHT_YES)
                popup.setColorFilter(Color.argb(255, 255, 255, 255)); // White Tint


            popup.setOnClickListener(this);
            itemView.setOnClickListener(this);
            imageView.setOnClickListener(this);

        }


        @Override
        public void onClick(View view) {

            switch (view.getId()) {

                case R.id.cardView:
                    GameWikiModal modal = list.get(getLayoutPosition());
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
                    final PopupMenu popup = new PopupMenu(context, view);
                    popup.setOnMenuItemClickListener(this);
                    final MenuInflater inflater = popup.getMenuInflater();
                    str = list.get(getAdapterPosition()).apiDetailUrl.split("/");
                    database = realm.where(GameListDatabase.class).equalTo("apiDetailUrl",str[str.length - 1]).findFirst();
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

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.where(GameListDatabase.class).equalTo("apiDetailUrl", str[str.length - 1]).findFirst().deleteFromRealm();
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        Toaster.make(context,"deleted");
                    }
                });
                return true;
            }else {
                GameWikiModal newModal = list.get(getAdapterPosition());
                RealmList<GameWikiPlatform> platforms = new RealmList<>();
                if (newModal.platforms!=null) {
                    platforms.addAll(newModal.platforms);
                }

                final GameListDatabase newListDatabase = new GameListDatabase(str[str.length-1],newModal.name,newModal.image!=null ? newModal.image.mediumUrl:null,platforms);

                if(id==R.id.replaying)
                    newListDatabase.setStatus(GiantBomb.REPLAYING);
                else if(id==R.id.planning)
                    newListDatabase.setStatus(GiantBomb.PLANNING);
                else if(id==R.id.dropped)
                    newListDatabase.setStatus(GiantBomb.DROPPED);
                else if(id==R.id.playing)
                    newListDatabase.setStatus(GiantBomb.PLAYING);
                else
                    newListDatabase.setStatus(GiantBomb.COMPLETED);




                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.insert(newListDatabase);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        Toaster.make(context,"added");
                    }
                });

                return true;

            }


        }


    }








    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);


        if (holder instanceof MyViewHolder1 || holder instanceof MyViewHolder2) {
            holder.itemView.clearAnimation();
        }


    }

}

