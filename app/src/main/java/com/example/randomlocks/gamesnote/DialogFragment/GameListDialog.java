package com.example.randomlocks.gamesnote.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
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
import android.widget.Toast;

import com.example.randomlocks.gamesnote.HelperClass.WebViewHelper.CustomTabActivityHelper;
import com.example.randomlocks.gamesnote.HelperClass.WebViewHelper.WebViewFallback;
import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.RealmDatabase.GameListDatabase;

import java.util.Calendar;
import java.util.Date;

import es.dmoral.toasty.Toasty;
import io.realm.Realm;

/**
 * Created by randomlock on 3/3/2017.
 */

public  class GameListDialog extends DialogFragment implements View.OnClickListener {

    private static final String PRIMARY_KEY = "PRIMARY_KEY";
    TextView startDate , endDate;
    Spinner status,platform , medium , hours , price ;
    int view_id;
    Button update,close;
    TextView youtube , google , wiki , htlb;
    ViewPager pager;
    GameListDatabase gameListDatabase;
    Realm realm;

    public static GameListDialog newInstance(int primaryId) {

        Bundle args = new Bundle();
        args.putInt(PRIMARY_KEY,primaryId);
        GameListDialog fragment = new GameListDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.MyDialogTheme);
        realm = Realm.getDefaultInstance();
        gameListDatabase = realm.where(GameListDatabase.class).equalTo("game_id",getArguments().getInt(PRIMARY_KEY)).findFirst();
    }



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
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);



                new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        if (view_id == R.id.start_date){
                            startDate.setText(month + 1 + "/" + day + "/" + year);
                        }

                        else{
                            endDate.setText(month + 1 + "/" + day + "/" + year);
                        }
                    }
                },year,month,day).show();

                break;

            case R.id.youtube :
                StringBuilder builder = new StringBuilder();
                String str[] = gameListDatabase.getName().split(" ");
                for(String s : str){
                    builder.append(s).append("+");
                }
                runBrowser("https://www.youtube.com/results?search_query="+builder);
                break;
            case R.id.google :
                builder = new StringBuilder();
                str = gameListDatabase.getName().split(" ");
                for(String s : str){
                    builder.append(s).append("+");
                }
                runBrowser("http://www.google.com/search?q="+builder);
                break;
            case R.id.wiki :
                builder = new StringBuilder();
                str = gameListDatabase.getName().split(" ");
                for(String s : str){
                    builder.append(s).append("_");
                }
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


                    }
                });

                if (old_status != status.getSelectedItemPosition() + 1) {
                    pager.getAdapter().notifyDataSetChanged();
                }

                Toasty.success(getContext(),"updated", Toast.LENGTH_SHORT,true).show();
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
                ((TextView) adapterView.getChildAt(0)).setGravity(Gravity.END);
                ((TextView) adapterView.getChildAt(0)).setTextColor(ContextCompat.getColor(getContext(),R.color.primary));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private int getIndex(Spinner spinner, String myString) {
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                index = i;
                break;
            }
        }
        return index;
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

