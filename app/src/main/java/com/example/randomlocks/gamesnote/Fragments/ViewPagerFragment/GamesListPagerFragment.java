package com.example.randomlocks.gamesnote.Fragments.ViewPagerFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.Adapter.GameListAdapter;
import com.example.randomlocks.gamesnote.DialogFragment.SearchFilterFragment;
import com.example.randomlocks.gamesnote.HelperClass.GiantBomb;
import com.example.randomlocks.gamesnote.HelperClass.SharedPreference;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.HelperClass.WebViewHelper.CustomTabActivityHelper;
import com.example.randomlocks.gamesnote.HelperClass.WebViewHelper.WebViewFallback;
import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.RealmDatabase.GameListDatabase;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by randomlocks on 3/17/2016.
 */
public class GamesListPagerFragment extends Fragment implements SearchView.OnQueryTextListener,SearchFilterFragment.SearchFilterInterface {

    private static final String STATUS = "total page";
    RecyclerView recyclerView;
    GameListAdapter adapter;
    int status;
    RealmResults<GameListDatabase> realmResult;
    Realm realm;
    TextView textView;
    GameListDatabase gameListDatabase;
    GameListDialog dialog;
    String sort_option;
    boolean isAscending ;

    public GamesListPagerFragment() {
    }

    public static GamesListPagerFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(STATUS, page);
        GamesListPagerFragment fragment = new GamesListPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        status = getArguments().getInt(STATUS);
        setHasOptionsMenu(true);
       int index = SharedPreference.getFromSharedPreferences(GiantBomb.SORT_WHICH,1,getContext());
        sort_option = getField(index);
        isAscending = SharedPreference.getFromSharedPreferences(GiantBomb.SORT_ASCENDING,true,getContext());
    }



    @Override
    public void onResume() {
        super.onResume();

    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pager_games_list, container, false);
        realm = Realm.getDefaultInstance();
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        textView = (TextView) view.findViewById(R.id.errortext);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (!realm.isInTransaction()) {
            if (isAscending) {
                realmResult = realm.where(GameListDatabase.class).equalTo("status", status).findAllSortedAsync(sort_option,Sort.ASCENDING);
            }else {
                realmResult = realm.where(GameListDatabase.class).equalTo("status",status).findAllSortedAsync(sort_option,Sort.DESCENDING);
            }
        }


        realmResult.addChangeListener(callback);

        return view;
    }

    private RealmChangeListener<RealmResults<GameListDatabase>> callback = new RealmChangeListener<RealmResults<GameListDatabase>>() {
        @Override
        public void onChange(RealmResults<GameListDatabase> element) {

            if(element.isLoaded() && element.isValid() ){
                if (realmResult.isEmpty()) {
                    textView.setVisibility(View.VISIBLE);
                } else {
                    adapter = new GameListAdapter(getContext(),realm, realmResult, true, status,new GameListAdapter.OnClickInterface() {
                        @Override
                        public void onClick(GameListDatabase gameListDatabase) {
                            GamesListPagerFragment.this.gameListDatabase = gameListDatabase;
                            dialog = new GameListDialog();
                            dialog.setCancelable(false);
                            dialog.show(getActivity().getSupportFragmentManager(),"gamelist");
                        }

                        @Override
                        public void onScoreClick(final String primaryKey, final int oldScore, int position) {
                            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                                    R.array.score, android.R.layout.simple_spinner_item);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            final Spinner sp = new Spinner(getActivity());
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            params.setMargins(50,50,0,0);
                            sp.setLayoutParams(params);
                            sp.setAdapter(adapter);
                            sp.setSelection(oldScore/10);
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
                                                        GameListDatabase newListDatabase = realm.where(GameListDatabase.class).equalTo("apiDetailUrl", primaryKey).findFirst();
                                                        newListDatabase.setScore(sp.getSelectedItemPosition() * 10);
                                                        newListDatabase.setLast_updated(new Date());
                                                        dialogInterface.dismiss();
                                                    }

                                                }
                                            }, new Realm.Transaction.OnSuccess() {
                                                @Override
                                                public void onSuccess() {
                                                    Toaster.make(getContext(),"updated");
                                                }
                                            });

                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                        }
                                    })
                                    .setView(sp,60,60,60,60); //make it not static
                            final AlertDialog dialog =  builder.create();
                            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface dialogInterface) {
                                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getContext(),R.color.black_white));
                                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(),R.color.primary));

                                }
                            });
                            dialog.show();
                        }
                    });
                    recyclerView.setAdapter(adapter);
                }

            }
    }
    };



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
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
        }

        return super.onOptionsItemSelected(item);
    }

    public void getSearchManager(final Context context, Menu menu, boolean isDefaultIconified) {

        SearchManager searchManager = (SearchManager) context.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint(Html.fromHtml("<font color = #ffffff>" + getResources().getString(R.string.search) + "</font>"));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(((AppCompatActivity) context).getComponentName()));
        searchView.setIconifiedByDefault(isDefaultIconified); // Do not iconify the widget; expand it by default
        searchView.setOnQueryTextListener(this);


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realmResult.removeChangeListener(callback);
        realmResult.removeChangeListeners();
        if (!realm.isClosed()) {
            realm.close();
            realm = null;
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Toaster.make(getContext(),query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        query = query.toLowerCase();
        if(adapter!=null){
            adapter.getFilter().filter(query);
            return true;
        }


        return false;
    }

    @Override
    public void onSelect(int which, boolean asc) {

        if (asc) {
            realmResult = realmResult.sort(getField(which),Sort.ASCENDING);
        }else {
            realmResult = realmResult.sort(getField(which), Sort.DESCENDING);
        }
        if(adapter!=null){
            adapter.updateData(realmResult);
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


    class GameListDialog extends DialogFragment implements View.OnClickListener {

        TextView startDate , endDate;
        Spinner status,platform , medium , hours , price ;
        int view_id;
        Button update,close;
        TextView youtube , google , wiki , htlb;
        ViewPager pager;





        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            View v = inflater.inflate(R.layout.dialog_game_list,container,false);
            pager = (ViewPager) getActivity().findViewById(R.id.my_pager);
            startDate = (TextView) v.findViewById(R.id.start_date);
            startDate.setText(gameListDatabase.getStartDate());
            endDate = (TextView) v.findViewById(R.id.end_date);
            endDate.setText(gameListDatabase.getEndDate());
            youtube = (TextView) v.findViewById(R.id.youtube);
            youtube.setOnClickListener(this);
            google = (TextView) v.findViewById(R.id.google);
            google.setOnClickListener(this);
            wiki = (TextView) v.findViewById(R.id.wiki);
            wiki.setOnClickListener(this);
            htlb = (TextView) v.findViewById(R.id.htlb);
            htlb.setOnClickListener(this);
            update = (Button) v.findViewById(R.id.update);
            update.setOnClickListener(this);
            close = (Button) v.findViewById(R.id.close);
            close.setOnClickListener(this);
            status = (Spinner) v.findViewById(R.id.status_spinner);
            platform = (Spinner) v.findViewById(R.id.platform_spinner);
            medium = (Spinner) v.findViewById(R.id.medium_spinner);
            hours = (Spinner) v.findViewById(R.id.gameplay_hours_spinner);
            price = (Spinner) v.findViewById(R.id.price_spinner);
            startDate.setOnClickListener(this);
            endDate.setOnClickListener(this);
            setSpinner(medium,gameListDatabase.getMedium(),R.array.medium);
            setSpinner(hours,gameListDatabase.getGameplay_hours(),R.array.price);
            setSpinner(price,gameListDatabase.getPrice(),R.array.price);
            setSpinner(status,getResources().getStringArray(R.array.status)[gameListDatabase.getStatus()-1],R.array.status);
            setCustomSpinner(platform,gameListDatabase.getPlatform());

            return v;
        }


        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.start_date :
                case R.id.end_date :
                    view_id = view.getId();
                    final DialogFragment newFragment = new DatePickerFragment();
                    newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
                    break;

                case R.id.youtube :
                    StringBuilder builder = new StringBuilder();
                    String str[] = gameListDatabase.getName().split(" ");
                    for(String s : str){
                        builder.append(s).append("+");
                    }
                    Toaster.make(getContext(),builder.toString());
                    runBrowser("https://www.youtube.com/results?search_query="+builder);
                    break;
                case R.id.google :
                     builder = new StringBuilder();
                     str = gameListDatabase.getName().split(" ");
                    for(String s : str){
                        builder.append(s).append("+");
                    }
                    Toaster.make(getContext(),builder.toString());
                    runBrowser("http://www.google.com/search?q="+builder);
                    break;
                case R.id.wiki :
                    builder = new StringBuilder();
                    str = gameListDatabase.getName().split(" ");
                    for(String s : str){
                        builder.append(s).append("_");
                    }
                    Toaster.make(getContext(),builder.toString());
                    runBrowser("https://en.wikipedia.org/wiki/"+builder);
                    break;
                case R.id.htlb :
                    break;

                case R.id.close :
                    getDialog().cancel();
                    break;

                case R.id.update :

                    final int old_status = gameListDatabase.getStatus();
                    //TODO run in async mode
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            gameListDatabase.setStatus(status.getSelectedItemPosition()+1);
                            gameListDatabase.setLast_updated(new Date());
                            gameListDatabase.setStartDate(startDate.getText().toString());
                            gameListDatabase.setEndDate(endDate.getText().toString());
                            gameListDatabase.setPlatform(platform.getSelectedItem().toString());
                            gameListDatabase.setGameplay_hours(hours.getSelectedItem().toString());
                            gameListDatabase.setMedium(medium.getSelectedItem().toString());
                            gameListDatabase.setPrice(price.getSelectedItem().toString());
                            if(old_status!=status.getSelectedItemPosition()+1){
                                pager.getAdapter().notifyDataSetChanged();
                            }

                        }
                    });
                    Toaster.make(getContext(),"updated");
                    getDialog().cancel();
                    break;

            }
        }



       private void runBrowser(String str){
            CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().setShowTitle(true).build();

            CustomTabActivityHelper.openCustomTab(
                    getActivity(), customTabsIntent, Uri.parse(str), new WebViewFallback());
        }


        void setSpinner(final Spinner spinner, String str , @ArrayRes final int array_id) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                    array_id, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
            spinner.setAdapter(adapter);
            spinner.setSelection(getIndex(spinner,str));



            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    ((TextView) adapterView.getChildAt(0)).setGravity(Gravity.CENTER);
                    ((TextView) adapterView.getChildAt(0)).setTextColor(ContextCompat.getColor(getContext(),R.color.primary));
                   // gameListDatabase.setPlatform(spinner.getItemAtPosition(i).toString());

                    if(array_id==R.array.status){
                        ((TextView) adapterView.getChildAt(0)).setTypeface(Typeface.DEFAULT_BOLD);

                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


        }

        public void setCustomSpinner(final Spinner spinner, final String str) {

            GameListDialog.CustomAdapter adapter = new GameListDialog.CustomAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item);
// Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
            spinner.setAdapter(adapter);
            spinner.setSelection(getIndex(spinner,str));

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    ((TextView) adapterView.getChildAt(0)).setGravity(Gravity.CENTER);
                    ((TextView) adapterView.getChildAt(0)).setTextColor(ContextCompat.getColor(getContext(),R.color.primary));

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


        }


        class CustomAdapter extends ArrayAdapter<CharSequence> {


            CustomAdapter(Context context, int resource) {
                super(context, resource);
            }

            @Override
            public CharSequence getItem(int position) {
                if(position==0)
                    return "-";
                return gameListDatabase.getPlatform_list().get(position-1).abbreviation;
            }

            @Override
            public int getCount() {
                return gameListDatabase.getPlatform_list().size()+1;
            }
        }


        class DatePickerFragment extends DialogFragment
                implements DatePickerDialog.OnDateSetListener {


            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {


                // Use the current date as the default date in the picker
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // Create a new instance of DatePickerDialog and return it
                return new DatePickerDialog(getActivity(), this, year, month, day);
            }

            public void onDateSet(DatePicker view, int year, int month, int day) {
                // Do something with the date chosen by the user
                if (view_id == R.id.start_date){
                    startDate.setText(month + 1 + "/" + day + "/" + year);
                }

                else{
                    endDate.setText(month + 1 + "/" + day + "/" + year);
                }
            }


        }

        private int getIndex(Spinner spinner, String myString)
        {
            int index = 0;

            for (int i=0;i<spinner.getCount();i++){
                if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                    index = i;
                    break;
                }
            }
            return index;
        }












    } //inner class



   /* void updateDatabase(final String startDate , final String endDate , final String hours , final String platform , final String medium , final String price ){

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                gameListDatabase.setStartDate(startDate);
                gameListDatabase.setEndDate(endDate);
                gameListDatabase.setGameplay_hours(hours);
                gameListDatabase.setPlatform(platform);
                gameListDatabase.setMedium(medium);
                gameListDatabase.setPrice(price);
                realm.copyToRealmOrUpdate(gameListDatabase);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Toaster.make(getContext(),"updated");
                dialog.getDialog().cancel();
            }
        });

    }*/





}
