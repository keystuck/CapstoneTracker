package com.example.emily.simplehealthtracker;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.emily.simplehealthtracker.data.Entry;
import com.example.emily.simplehealthtracker.data.EntryAdapter;
import com.example.emily.simplehealthtracker.data.EntryViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewMedHistoryFragment extends Fragment implements DetailedActivity.XmlClickable, EntryAdapter.EntryClickListener {
    private final static String LOG_TAG = ViewMedHistoryFragment.class.getSimpleName();

    private EntryAdapter mEntryAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    @BindView(R.id.rv_medhistory) RecyclerView mRecyclerView;
    @BindView(R.id.btn_remove) Button removeButton;
    @BindView(R.id.et_date) EditText fromDate;
    @BindView(R.id.et_to) EditText toDate;
    @BindView(R.id.btn_search) Button searchButton;

    private EntryViewModel entryViewModel;
    private List<Entry> entryList = new ArrayList<Entry>();
    private List<Entry> toDelete = new ArrayList<>();

    public ViewMedHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            getActivity().onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_share){
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String message = "";
            for (Entry entry : entryList){
                message += entry.toString();
            }
            shareIntent.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(shareIntent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_menu, menu);
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
        View rootView = inflater.inflate(R.layout.fragment_view_med_history, container, false);
        ButterKnife.bind(this, rootView);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        //TODO: change this
 /*       entryViewModel.getEntriesOfType(getResources().getString(R.string.type_meds)).observe(this, new Observer<List<Entry>>() {
            @Override
            public void onChanged(@Nullable List<Entry> entries) {
                entryList = entries;
                mEntryAdapter.clear();
                mEntryAdapter.addAll(entries);
                mRecyclerView.setAdapter(mEntryAdapter);
            }
        });
*/
        mEntryAdapter = new EntryAdapter(getActivity(), entryList, true, this);


        mRecyclerView.setAdapter(mEntryAdapter);

        return rootView;
    }

    @Override
    public void onEntryClick(int selectedEntryIndex) {

    }

    @Override
    public void insertNewRecordOrCancel(View v) {
        String beginDate = fromDate.getText().toString();
        String endDate = toDate.getText().toString();
        long beginMillis = 0;
        long endMillis = System.currentTimeMillis();
        if (beginDate.isEmpty()){
            beginDate = "Jan 1, 1900";
        }
        String fullDate = beginDate.concat(" 12:00 am");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy K:mm aa");

        try {
            Date mDate = simpleDateFormat.parse(fullDate);
            beginMillis = mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        //TODO: enforce from before to
        if (!endDate.isEmpty()){
            fullDate = endDate.concat(" 11:59 pm");

            try {
                Date mDate = simpleDateFormat.parse(fullDate);
                endMillis = mDate.getTime();
            } catch (ParseException e){
                e.printStackTrace();
            }
        }

        final long startMillis = beginMillis;
        final long stopMillis = endMillis;
        //Use dates to retrieve info
        entryViewModel.getEntriesTypeTime(getResources().getString(R.string.type_meds),
                startMillis, stopMillis).observe(this, new Observer<List<Entry>>() {
            @Override
            public void onChanged(@Nullable List<Entry> entries) {
                entryList = entries;
                mEntryAdapter.clear();
                mEntryAdapter.addAll(entries);
                mRecyclerView.setAdapter(mEntryAdapter);
            }
        });

    }

    @Override
    public void saveChanges(View v) {
        SparseBooleanArray sparseBooleanArray = mEntryAdapter.getSparseBooleanArray();
        for (int i = 0; i < sparseBooleanArray.size(); i++){
            int key = sparseBooleanArray.keyAt(i);
            if (sparseBooleanArray.get(key)){
                Entry checkedEntry = mEntryAdapter.getCurrentItem(key);
                toDelete.add(checkedEntry);
            }
        }
        if (toDelete.size() > 0){
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    entryViewModel.deleteList(toDelete);
                }
            });
        }
    }



    @Override
    public void checkOff(View v) { }

    @Override
    public void showTimePickerDialog(View v) { }

    @Override
    public void showDatePickerDialog(View w) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getActivity().getSupportFragmentManager(),
                getResources().getString(R.string.date_picker));
    }

    @Override
    public void showToDatePickerDialog(View v) {
        DialogFragment newFragment = new ToDatePickerFragment();
        newFragment.show(getActivity().getSupportFragmentManager(),
                getResources().getString(R.string.to_date_picker));
    }
}
