package com.example.emily.simplehealthtracker;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emily.simplehealthtracker.data.Entry;
import com.example.emily.simplehealthtracker.data.EntryViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrackFragment extends Fragment implements SimpleActivity.XmlClickable, AdapterView.OnItemSelectedListener {
    @BindView(R.id.et_description) EditText descriptionView;
    @BindView(R.id.et_amplitude) EditText amplitudeView;
    @BindView(R.id.tv_date) TextView dateView;
    @BindView(R.id.tv_time) TextView timeView;
    @BindView(R.id.sp_type)
    Spinner typeView;
    @BindView(R.id.button) Button saveButton;

    private EntryViewModel entryViewModel;
    private List<Entry> mEntryList = new ArrayList<>();

    private static final String FIELDS_INFO = "fields";
    private String entryType;


    public TrackFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        entryViewModel = ViewModelProviders.of(getActivity()).get(EntryViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_track, container, false);
        ButterKnife.bind(this, rootView);

        entryViewModel.getEntries().observe(this, new Observer<List<Entry>>() {
            @Override
            public void onChanged(@Nullable List<Entry> entries) {
                mEntryList = entries;
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.types_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeView.setAdapter(adapter);

        typeView.setOnItemSelectedListener(this);


        if (savedInstanceState != null && savedInstanceState.getStringArrayList(FIELDS_INFO) != null){
            ArrayList<String> fields = savedInstanceState.getStringArrayList(FIELDS_INFO);
            descriptionView.setText(fields.get(0));
            amplitudeView.setText(fields.get(1));
            dateView.setText(fields.get(2));
            timeView.setText(fields.get(3));
            String type = fields.get(4);
            if (type.equals(getResources().getString(R.string.no_type))){
                typeView.setSelection(0);
            } else {
                boolean found = false;
                ArrayList<String> resourcesArray = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.types_array)));
                for (int i = 0; i < resourcesArray.size(); i++){
                    if (resourcesArray.get(i).equals(type)){
                        found = true;
                        typeView.setSelection(i);
                        break;
                    }
                }
                if (found == false){
                    typeView.setSelection(0);
                }
            }
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);


        ArrayList<String> fieldContents = new ArrayList<>();
        fieldContents.add(descriptionView.getText().toString());
        fieldContents.add(amplitudeView.getText().toString());
        fieldContents.add(dateView.getText().toString());
        fieldContents.add(timeView.getText().toString());
        if (entryType != null){
            fieldContents.add(entryType);
        } else {
            fieldContents.add(getResources().getString(R.string.no_type));
        }
        outState.putStringArrayList(FIELDS_INFO, fieldContents);



    }

    @Override
    public void handleFragmentButtonPush(View view) {
        final String thingToTrack = descriptionView.getText().toString();
        String amplitude = amplitudeView.getText().toString();
        int ampInt = 0;
        if (!(amplitude.isEmpty())){
            ampInt = Integer.parseInt(amplitudeView.getText().toString());
        }
        final int amp = ampInt;
        final int taken = 1;


        long eventTime = System.currentTimeMillis();
        String date = dateView.getText().toString();
        String enteredTime = timeView.getText().toString();
        String fullTime;
        if (date != null && enteredTime != null){
            fullTime = date + " " + enteredTime;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy K:mm aa");

            try {
                Date mDate = simpleDateFormat.parse(fullTime);
                eventTime = mDate.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        if (entryType.isEmpty()){
            entryType = getResources().getString(R.string.default_entry);
        }
        final String type = entryType;
        final long time = eventTime;

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                entryViewModel.addEntry(thingToTrack, amp, taken, time, type, 0);
            }
        });

        Toast.makeText(getContext(), getResources().getString(R.string.add_OK), Toast.LENGTH_SHORT).show();

        descriptionView.setText("");
        amplitudeView.setText("");
        dateView.setText("");
        timeView.setText("");
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            getActivity().onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        entryType = adapterView.getItemAtPosition(i).toString();

    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        entryType = getResources().getString(R.string.default_entry);
    }

    @Override
    public void saveChanges(View view){

    }

    @Override
    public void checkOff(View v) {

    }

    public void showTimePickerDialog(View v){
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getActivity().getSupportFragmentManager(), getResources().getString(R.string.time_picker));

    }
    public void showDatePickerDialog(View v){
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getActivity().getSupportFragmentManager(), getResources().getString(R.string.date_picker));
    }

}
