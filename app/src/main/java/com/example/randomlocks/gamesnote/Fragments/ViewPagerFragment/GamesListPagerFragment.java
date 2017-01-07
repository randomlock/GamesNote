package com.example.randomlocks.gamesnote.Fragments.ViewPagerFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.Adapter.GameListAdapter;
import com.example.randomlocks.gamesnote.DialogFragment.AddToBottomFragment;
import com.example.randomlocks.gamesnote.HelperClass.Toaster;
import com.example.randomlocks.gamesnote.HelperClass.WebViewHelper.CustomTabActivityHelper;
import com.example.randomlocks.gamesnote.HelperClass.WebViewHelper.WebViewFallback;
import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.RealmDatabase.GameListDatabase;

import java.util.Calendar;

import io.realm.Realm;
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
            startDate.setText(gameListDatabase.startDate);
            endDate = (TextView) v.findViewById(R.id.end_date);
            endDate.setText(gameListDatabase.endDate);
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
            hours = (Spinner) v.findViewById(R.id.gameplay_hours);
            price = (Spinner) v.findViewById(R.id.price);
            startDate.setOnClickListener(this);
            endDate.setOnClickListener(this);
            setSpinner(medium,gameListDatabase.medium,R.array.medium);
            setSpinner(hours,gameListDatabase.gameplay_hours,R.array.price);
            setSpinner(price,gameListDatabase.price,R.array.price);
            setCustomSpinner(platform,gameListDatabase.platform);

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
                    String str[] = gameListDatabase.name.split(" ");
                    for(String s : str){
                        builder.append(s).append("+");
                    }
                    Toaster.make(getContext(),builder.toString());
                    runBrowser("https://www.youtube.com/results?search_query="+builder);
                    break;
                case R.id.google :
                     builder = new StringBuilder();
                     str = gameListDatabase.name.split(" ");
                    for(String s : str){
                        builder.append(s).append("+");
                    }
                    Toaster.make(getContext(),builder.toString());
                    runBrowser("http://www.google.com/search?q="+builder);
                    break;
                case R.id.wiki :
                    builder = new StringBuilder();
                    str = gameListDatabase.name.split(" ");
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


                    updateDatabase(startDate.getText().toString(),endDate.getText().toString(),hours.getSelectedItem().toString(),platform.getSelectedItem().toString(), medium.getSelectedItem().toString(),price.getSelectedItem().toString());
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

        public void setCustomSpinner(final Spinner spinner, String str) {

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

                   /* if(spinner.getId()==R.id.gameplay_hours){
                        gameListDatabase.setGameplay_hours(spinner.getItemAtPosition(i).toString());
                    }else if(spinner.getId()==R.id.medium_spinner){
                        gameListDatabase.setMedium(spinner.getItemAtPosition(i).toString());
                    }else {
                     gameListDatabase.setPrice(spinner.getItemAtPosition(i).toString());
                    }*/


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

                return gameListDatabase.platform_list.get(position).abbreviation;
            }

            @Override
            public int getCount() {
                return gameListDatabase.platform_list.size();
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
                  //  gameListDatabase.setStartDate(startDate.getText().toString());
                }

                else{
                    endDate.setText(month + 1 + "/" + day + "/" + year);
                  //  gameListDatabase.setEndDate(endDate.getText().toString());
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



    void updateDatabase(final String startDate , final String endDate , final String hours , final String platform , final String medium , final String price ){

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

    }



}
