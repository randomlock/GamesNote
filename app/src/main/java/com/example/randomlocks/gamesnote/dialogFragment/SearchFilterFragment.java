package com.example.randomlocks.gamesnote.dialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.helperClass.GiantBomb;
import com.example.randomlocks.gamesnote.helperClass.SharedPreference;
import com.example.randomlocks.gamesnote.helperClass.Toaster;

import java.util.Calendar;

public class SearchFilterFragment extends DialogFragment implements View.OnClickListener {

    boolean isAscending;
    CheckBox checkbox;
    Spinner spinner;
    TextView start_date, end_date;
    View view;
    SearchFilterInterface searchFilterInterface = null;
    int which_one;
    int array_id;

    public static SearchFilterFragment newInstance(@ArrayRes int array_id) {

        Bundle args = new Bundle();
        args.putInt(GiantBomb.ARRAY,array_id);
        SearchFilterFragment fragment = new SearchFilterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            searchFilterInterface = (SearchFilterInterface) getTargetFragment();
        } catch (Exception e) {
            Toaster.make(getContext(), "interface cast exception");
        }


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        array_id = getArguments().getInt(GiantBomb.ARRAY);
        if (array_id==R.array.search_filter) {
            which_one = SharedPreference.getFromSharedPreferences(GiantBomb.WHICH, 3, getContext());
            isAscending = SharedPreference.getFromSharedPreferences(GiantBomb.ASCENDING, true, getContext());
        }else {
            which_one = SharedPreference.getFromSharedPreferences(GiantBomb.SORT_WHICH, 0, getContext());
            isAscending = SharedPreference.getFromSharedPreferences(GiantBomb.SORT_ASCENDING,true,getContext());
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        view = getActivity().getLayoutInflater().inflate(R.layout.search_option_layout,null);
        spinner = (Spinner) view.findViewById(R.id.spinner);
        setSpinner(spinner, array_id);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                which_one = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        checkbox = (CheckBox) view.findViewById(R.id.checkbox);
        checkbox.setChecked(!isAscending);

        start_date = (TextView) view.findViewById(R.id.start_date);
        start_date.setOnClickListener(this);
        end_date = (TextView) view.findViewById(R.id.end_date);
        end_date.setOnClickListener(this);


        final AlertDialog dialog =  new AlertDialog.Builder(getContext(),R.style.MyDialogTheme)
                .setCancelable(true)
                .setTitle("Sort result")
               /* .setSingleChoiceItems(array_id,which_one, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        which_one = which;

                    }
                })*/
                .setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                })
                .setPositiveButton("Sort", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (array_id == R.array.search_filter) {
                            SharedPreference.saveToSharedPreference(GiantBomb.WHICH, which_one, getContext());
                            SharedPreference.saveToSharedPreference(GiantBomb.ASCENDING, !checkbox.isChecked(), getContext());
                        }else {
                            SharedPreference.saveToSharedPreference(GiantBomb.SORT_WHICH, which_one, getContext());
                            SharedPreference.saveToSharedPreference(GiantBomb.SORT_ASCENDING, !checkbox.isChecked(), getContext());
                        }
                        searchFilterInterface.onSelect(which_one, !checkbox.isChecked(), start_date.getText().toString(), end_date.getText().toString());
                        dismiss();
                    }
                }).create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.black_white));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.accent));

            }
        });
        /*  dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(),R.color.primary));
                    }
                });*/
        return dialog;
    }

    void setSpinner(final Spinner spinner, @ArrayRes final int array_id) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                array_id, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setSelection(which_one, true);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setGravity(Gravity.CENTER);
                ((TextView) adapterView.getChildAt(0)).setTextColor(ContextCompat.getColor(getContext(), R.color.accent));

                // gameListDatabase.setPlatform(spinner.getItemAtPosition(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @Override
    public void onClick(final View v) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);


        new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month++;
                if (v.getId() == R.id.start_date) {
                    start_date.setText(year + "/" + month + "/" + day);
                } else {
                    end_date.setText(year + "/" + month + "/" + day);
                }
            }
        }, year, month, day).show();


    }

    public interface SearchFilterInterface {

        void onSelect(int which, boolean asc, String start_date, String end_date);
    }


}




