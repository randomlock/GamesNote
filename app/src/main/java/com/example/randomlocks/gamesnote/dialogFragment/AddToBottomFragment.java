package com.example.randomlocks.gamesnote.dialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.content.ContextCompat;
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

import com.example.randomlocks.gamesnote.R;
import com.example.randomlocks.gamesnote.helperClass.GiantBomb;
import com.example.randomlocks.gamesnote.helperClass.Toaster;
import com.example.randomlocks.gamesnote.modals.GameWikiPlatform;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by randomlocks on 6/27/2016.
 */
public class AddToBottomFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    TextView startDate, endDate;
    Spinner scoreSpinner, platformSpinner;
    Button quickAdd, addToList;
    int view_id;
    List<GameWikiPlatform> platforms;
    AddToBottomInterface mAddToBottomInterface;
    private BottomSheetBehavior mBehavior;

    public static AddToBottomFragment newInstance(List<GameWikiPlatform> platform) {

        Bundle args = new Bundle();
        args.putParcelableArrayList(GiantBomb.PLATFORM, new ArrayList<>(platform));
        AddToBottomFragment fragment = new AddToBottomFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mAddToBottomInterface = (AddToBottomInterface) getTargetFragment();
        } catch (Exception e) {
            Toaster.make(getContext(), "interface cast exception");
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        platforms = getArguments().getParcelableArrayList(GiantBomb.PLATFORM);
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_to_list, null);
        scoreSpinner = (Spinner) view.findViewById(R.id.score_spinner);
        platformSpinner = (Spinner) view.findViewById(R.id.platform_spinner);
        startDate = (TextView) view.findViewById(R.id.start_date);
        endDate = (TextView) view.findViewById(R.id.end_date);
        quickAdd = (Button) view.findViewById(R.id.quick_add);
        addToList = (Button) view.findViewById(R.id.add_to_list);
        quickAdd.setOnClickListener(this);
        addToList.setOnClickListener(this);
        setSpinner(scoreSpinner, "score");
        setCustomSpinner(platformSpinner, "platform");
        startDate.setOnClickListener(this);
        endDate.setOnClickListener(this);
        dialog.setContentView(view);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.start_date:
            case R.id.end_date:
                view_id = v.getId();
               // DialogFragment newFragment = new DatePickerFragment();
               // newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);



                new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        if (view_id == R.id.start_date){
                            startDate.setText(month + 1 + "/" + day + "/" + year);
                            startDate.setTextColor(ContextCompat.getColor(getContext(), R.color.accent));
                        } else {
                            endDate.setText(month + 1 + "/" + day + "/" + year);
                            endDate.setTextColor(ContextCompat.getColor(getContext(), R.color.accent));
                        }
                    }
                },year,month,day).show();


                break;
            case R.id.quick_add:
                mAddToBottomInterface.onAdd(0, "-", "-", "-");
                mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                break;

            case R.id.add_to_list:
                if(scoreSpinner.getSelectedItemPosition()==0){
                    Toaster.make(getContext(),"score is not set");
                }else if (startDate.getText().equals(getResources().getString(R.string.start_date))) {
                    Toaster.make(getContext(), "start date not set");
                } else if (endDate.getText().equals(getResources().getString(R.string.end_date))) {
                    Toaster.make(getContext(), "end date not set");
                } else if(platformSpinner.getSelectedItemPosition()==0){
                    Toaster.make(getContext(),"platform is not set");
                }
                else
                {
                    mAddToBottomInterface.onAdd(Integer.parseInt(scoreSpinner.getSelectedItem().toString()), startDate.getText().toString(), endDate.getText().toString(), platformSpinner.getSelectedItem().toString());
                    mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                }

                break;


        }
    }

    void setSpinner(Spinner spinner, String str) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.score,R.layout.spinner);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setPrompt(str);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setGravity(Gravity.END);
                ((TextView) adapterView.getChildAt(0)).setTextColor(ContextCompat.getColor(getContext(), R.color.accent));


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public void setCustomSpinner(Spinner spinner, String str) {

        CustomAdapter adapter = new CustomAdapter(getContext(),R.layout.spinner);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setPrompt(str);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setGravity(Gravity.END);
                ((TextView) adapterView.getChildAt(0)).setTextColor(ContextCompat.getColor(getContext(), R.color.accent));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public interface AddToBottomInterface {

        void onAdd(int score, String startDate, String endDate, String platform);
    }

    private class CustomAdapter extends ArrayAdapter<CharSequence> {


        CustomAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public CharSequence getItem(int position) {

            if(position==0)
                return "-";

            return platforms.get(position-1).abbreviation;
        }

        @Override
        public int getCount() {
            return platforms.size()+1;
        }

    }





}