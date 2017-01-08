package com.example.randomlocks.gamesnote.Fragments.ViewPagerFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.HelperClass.WebViewHelper.CustomTabActivityHelper;
import com.example.randomlocks.gamesnote.HelperClass.WebViewHelper.WebViewFallback;
import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.RealmDatabase.GameListDatabase;
import com.example.randomlocks.gamesnote.RealmDatabase.RealmString;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by randomlocks on 3/17/2016.
 */
public class GamesListPagerFragment extends Fragment {

    private static final String STATUS = "total page";
    RecyclerView recyclerView;
    int status;
    RealmResults<GameListDatabase> realmResult;
    Realm realm;
    TextView textView;
    GameListDatabase gameListDatabase;
    GameListDialog dialog;

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
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        status = getArguments().getInt(STATUS);
        realm = Realm.getDefaultInstance();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pager_games_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        textView = (TextView) view.findViewById(R.id.errortext);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        realmResult = realm.where(GameListDatabase.class).equalTo("status", status).findAll();
        if (realmResult.isEmpty()) {
            textView.setVisibility(View.VISIBLE);
        } else {
            GameListAdapter adapter = new GameListAdapter(getContext(), realmResult, true, new GameListAdapter.OnClickInterface() {
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
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    if(sp.getSelectedItemPosition()*10==oldScore){
                                        dialogInterface.dismiss();
                                    } else {
                                        GameListDatabase newListDatabase = realm.where(GameListDatabase.class).equalTo("apiDetailUrl",primaryKey).findFirst();
                                        newListDatabase.setScore(sp.getSelectedItemPosition()*10);
                                        Toaster.make(getContext(),"updated");
                                        dialogInterface.dismiss();
                                    }

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
                    builder.create().show();
                }
            });
            recyclerView.setAdapter(adapter);
        }


        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }



    class GameListDialog extends DialogFragment implements View.OnClickListener {

        TextView startDate , endDate;
        Spinner platform , medium , hours , price;
        int view_id;
        Button update,close;
        TextView youtube , google , wiki , htlb;




        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            View v = inflater.inflate(R.layout.dialog_game_list,container,false);
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
            platform = (Spinner) v.findViewById(R.id.platform_spinner);
            medium = (Spinner) v.findViewById(R.id.medium_spinner);
            hours = (Spinner) v.findViewById(R.id.gameplay_hours_spinner);
            price = (Spinner) v.findViewById(R.id.price_spinner);
            startDate.setOnClickListener(this);
            endDate.setOnClickListener(this);
            setSpinner(medium,gameListDatabase.getMedium(),R.array.medium);
            setSpinner(hours,gameListDatabase.getGameplay_hours(),R.array.price);
            setSpinner(price,gameListDatabase.getPrice(),R.array.price);
            setCustomSpinner(platform,gameListDatabase.getPlatform());

            return v;
        }


        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.start_date :
                case R.id.end_date :
                    view_id = view.getId();
                    DialogFragment newFragment = new DatePickerFragment();
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
            //TODO run in async mode
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            GameListDatabase newGameListDatabase = realm.where(GameListDatabase.class).equalTo("apiDetailUrl", gameListDatabase.getApiDetailUrl()).findFirst();
                            Toaster.make(getContext(), newGameListDatabase.getApiDetailUrl());
                            newGameListDatabase.setStartDate(startDate.getText().toString());
                            newGameListDatabase.setEndDate(endDate.getText().toString());
                            newGameListDatabase.setPlatform(platform.getSelectedItem().toString());
                            newGameListDatabase.setGameplay_hours(hours.getSelectedItem().toString());
                            newGameListDatabase.setMedium(medium.getSelectedItem().toString());
                            newGameListDatabase.setPrice(price.getSelectedItem().toString());
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


        void setSpinner(final Spinner spinner, String str , @ArrayRes int array_id) {
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
                return gameListDatabase.getPlatform_list().get(position-1).getAbbreviation();
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
