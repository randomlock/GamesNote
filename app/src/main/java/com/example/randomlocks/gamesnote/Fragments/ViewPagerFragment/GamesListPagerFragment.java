package com.example.randomlocks.gamesnote.Fragments.ViewPagerFragment;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.randomlocks.gamesnote.Adapter.GameListAdapter;
import com.example.randomlocks.gamesnote.DialogFragment.GameListDialog;
import com.example.randomlocks.gamesnote.DialogFragment.SearchFilterFragment;
import com.example.randomlocks.gamesnote.Fragments.GamesListFragment;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.SharedPreference;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.RealmDatabase.GameListDatabase;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.Date;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;


public class GamesListPagerFragment extends Fragment implements SearchView.OnQueryTextListener,SearchFilterFragment.SearchFilterInterface, SearchManager.OnDismissListener, SearchManager.OnCancelListener {

    private static final String IS_SIMPLE = "is_view_simple" ;
    private static final String STATUS = "total page";
    private GamesListFragment parentFragment;
    private LinearLayoutManager manager;
    private FastScrollRecyclerView recyclerView;
    private GameListAdapter adapter = null;
    private int status;
    private RealmResults<GameListDatabase> realmResult;
    private Realm realm;
    private TextView errorText;
    private GameListDatabase gameListDatabase;
    private GameListDialog dialog;
    private String sort_option;
    private boolean isAscending ;
    private boolean isSimple;
    private DividerItemDecoration itemDecoration;


    public GamesListPagerFragment() {
    }

    public static GamesListPagerFragment newInstance(int page,boolean isSimple) {
        Bundle args = new Bundle();
        args.putInt(STATUS, page);
        args.putBoolean(IS_SIMPLE,isSimple);
        GamesListPagerFragment fragment = new GamesListPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        status = getArguments().getInt(STATUS);
        setHasOptionsMenu(true);
        Log.d("tag1", "oncreate");
        itemDecoration = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);

        int index = SharedPreference.getFromSharedPreferences(GiantBomb.SORT_WHICH,1,getContext());
        sort_option = getField(index);
        isAscending = SharedPreference.getFromSharedPreferences(GiantBomb.SORT_ASCENDING,true,getContext());
        isSimple = SharedPreference.getFromSharedPreferences(GiantBomb.REDUCE_LIST_VIEW, false, getContext());


        if (isAscending) {
            if (status == GiantBomb.ALL_GAMES)
                realmResult = realm.where(GameListDatabase.class).findAllSorted(sort_option, Sort.ASCENDING);
            else
                realmResult = realm.where(GameListDatabase.class).equalTo("status", status).findAllSorted(sort_option, Sort.ASCENDING);
        } else {
            if (status == GiantBomb.ALL_GAMES)
                realmResult = realm.where(GameListDatabase.class).findAllSorted(sort_option, Sort.DESCENDING);
            else
                realmResult = realm.where(GameListDatabase.class).equalTo("status", status).findAllSorted(sort_option, Sort.DESCENDING);
        }

        realmResult.addChangeListener(new RealmChangeListener<RealmResults<GameListDatabase>>() {
            @Override
            public void onChange(RealmResults<GameListDatabase> element) {
                if (element.size() > 0)
                    errorText.setVisibility(View.GONE);
                else
                    errorText.setVisibility(View.VISIBLE);
            }
        });

    }





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("tag1", "oncreateview");
        View view = inflater.inflate(R.layout.fragment_pager_games_list, container, false);
        parentFragment = (GamesListFragment) getParentFragment();
        recyclerView = (FastScrollRecyclerView) view.findViewById(R.id.recycler_view);

        errorText = (TextView) view.findViewById(R.id.errortext);
        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);


        if(isSimple)
            recyclerView.addItemDecoration(itemDecoration);


        if (realmResult.isEmpty()) {
            if (AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES) {
                Drawable drawable = ContextCompat.getDrawable(getContext(),R.drawable.ic_error);
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable, Color.WHITE);
                DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_ATOP);
                errorText.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
            }
            errorText.setVisibility(View.VISIBLE);
        } else {
            adapter = new GameListAdapter(getContext(), realm, realmResult, true, status, isSimple, new GameListAdapter.OnClickInterface() {
                @Override
                public void onClick(GameListDatabase gameListDatabase) {
                    GamesListPagerFragment.this.gameListDatabase = gameListDatabase;
                    dialog = GameListDialog.newInstance(gameListDatabase.getGame_id());
                    dialog.setCancelable(false);
                    dialog.show(getActivity().getSupportFragmentManager(), "gamelist");
                }

                @Override
                public void onScoreClick(final int primaryKey, final int oldScore, final int position) {
                    //    final Parcelable scroll_state = recyclerView.getLayoutManager().onSaveInstanceState();
                    final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                            R.array.score, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    final Spinner sp = new Spinner(getActivity());
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    float dpi = getResources().getDisplayMetrics().density;
                    params.setMargins((int)(19*dpi), (int)(19*dpi), (int)(19*dpi), (int)(19*dpi));
                    FrameLayout layout = new FrameLayout(getContext());
                    sp.setLayoutParams(params);
                    sp.setAdapter(adapter);
                    sp.setSelection(oldScore / 10);
                    layout.addView(sp);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setCancelable(false).setTitle("SelectScore")
                            .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialogInterface, int i) {
                                    realm.executeTransactionAsync(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            if (sp.getSelectedItemPosition() * 10 == oldScore) {
                                                dialogInterface.dismiss();
                                            } else {
                                                GameListDatabase newListDatabase = realm.where(GameListDatabase.class).equalTo("game_id", primaryKey).findFirst();
                                                newListDatabase.setScore(sp.getSelectedItemPosition() * 10);
                                                newListDatabase.setLast_updated(new Date());
                                                dialogInterface.dismiss();
                                            }

                                        }
                                    }, new Realm.Transaction.OnSuccess() {
                                        @Override
                                        public void onSuccess() {
                                            //recyclerView.getLayoutManager().onRestoreInstanceState(scroll_state);
                                            Toasty.success(getContext(), "updated", Toast.LENGTH_SHORT, true).show();

                                        }
                                    });

                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            }).setView(layout);


                    final AlertDialog dialog = builder.create();

                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.black_white));
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.primary));

                        }
                    });
                    dialog.show();
                }
            }, sort_option, isAscending);
            recyclerView.setAdapter(adapter);
        }


        // realmResult.addChangeListener(callback);

        return view;
    }

    /*private RealmChangeListener<RealmResults<GameListDatabase>> callback = new RealmChangeListener<RealmResults<GameListDatabase>>() {
        @Override
        public void onChange(RealmResults<GameListDatabase> element) {

            if(element.isLoaded() && element.isValid() ){


            }
    }
    };*/



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem = menu.findItem(R.id.view);

        if (isSimple) {
            menuItem.setTitle(getString(R.string.card_view));
            menuItem.setIcon(R.drawable.ic_compat_white);

        } else {
            menuItem.setTitle(getString(R.string.list_view));
            menuItem.setIcon(R.drawable.ic_gamelist_white);

        }

    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.game_list_menu,menu);
        getSearchManager(getContext(),menu,false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id==R.id.search){
            return true;
        }else if(id==R.id.filter){
            SearchFilterFragment filterFragment = SearchFilterFragment.newInstance(R.array.sort_filter);
            filterFragment.setTargetFragment(this, 0);
            filterFragment.show(getActivity().getSupportFragmentManager(), "seach filter");
            return true;
        }else if(id==R.id.view){

            if(adapter==null || adapter.getItemCount()==0){
                Toasty.info(getContext(),"No games added").show();
                return true;
            }

            if (adapter!=null) {
                if (item.getTitle().equals(getString(R.string.card_view))) {
                    isSimple = false;
                    item.setTitle(getString(R.string.list_view));
                    item.setIcon(R.drawable.ic_gamelist_white);

                } else {
                    item.setTitle(getString(R.string.card_view));
                    isSimple = true;
                    item.setIcon(R.drawable.ic_compat_white);
                }


                if (recyclerView != null) {
                    if (isSimple) {
                        recyclerView.addItemDecoration(itemDecoration);
                    } else {
                        recyclerView.removeItemDecoration(itemDecoration);
                    }
                }
                adapter.setSimple(isSimple);

                /*Save the isSimple and update all viewpager item by calling notifyDatasetChanged()*/
                SharedPreference.saveToSharedPreference(GiantBomb.REDUCE_LIST_VIEW,isSimple,getContext());
                parentFragment.updateViewpagerFragment(isSimple);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateViewpager() {
        parentFragment.updateViewPager();
    }

    public void getSearchManager(final Context context, Menu menu, boolean isDefaultIconified) {

        SearchManager searchManager = (SearchManager) context.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(((AppCompatActivity) context).getComponentName()));
        searchView.setIconifiedByDefault(isDefaultIconified); // Do not iconify the widget; expand it by default
        searchManager.setOnCancelListener(this);
        searchView.setOnQueryTextListener(this);


    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("tag1", "onresume" + status);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //    realmResult.removeChangeListener(callback);
        if (!realm.isClosed()) {
            realm.close();
            realm = null;
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        query = query.toLowerCase();
        /*if (query.trim().length() >0) {
            if (adapter != null) {
                adapter.getFilter().filter(query);
                return true;
            }
        }
        return false;*/
        if (adapter != null && adapter.getFilter() != null) {
            adapter.getFilter().filter(query);
        }
        return true;
    }

    @Override
    public void onSelect(int which, boolean asc) {
        if(adapter==null || adapter.getItemCount()==0){
            Toaster.make(getContext(),"No game added");
        }else {
           /* if (asc) {
                realmResult = realmResult.sort(getField(which),Sort.ASCENDING);
            }else {
                realmResult = realmResult.sort(getField(which), Sort.DESCENDING);
            }
            if(adapter!=null){
                adapter.updateData(realmResult);
                adapter.updateSortOption(getField(which), asc);
            }*/
            parentFragment.updateViewPager();
        }


    }

    String getField(int index){

        String str = null;
        switch (index){
            case 0 :
                str = "name";
                break;
            case 1 :
                str = "date_added";
                break;
            case 2 :
                str = "last_updated";
                break;
            case 3 :
                str = "score";
                break;
            case 4 :
                str = "startDate";
                break;
            case 5 :
                str = "endDate";
                break;
            case 6 :
                str = "gameplay_hours";
                break;
            case 7 :
                str = "price";
                break;

        }

        return  str;

    }


    @Override
    public void onDismiss() {
        Toaster.make(getContext(),"hello?");
        adapter.getFilter().filter("");
    }

    @Override
    public void onCancel() {

    }

    public void updateAdapter(boolean isSimple) {
        if (adapter != null) {
            adapter.setSimple(isSimple);
        }
    }
}
