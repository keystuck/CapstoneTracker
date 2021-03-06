package com.example.emily.simplehealthtracker;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.example.emily.simplehealthtracker.data.Entry;
import com.example.emily.simplehealthtracker.data.EntryAdapter;
import com.example.emily.simplehealthtracker.data.EntryViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;




/**
 * A simple {@link Fragment} subclass.
 */
public class ViewHistoryFragment extends Fragment implements DetailedActivity.XmlClickable, EntryAdapter.EntryClickListener, SimpleActivity.XmlClickable {

    private EntryAdapter mEntryAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private static final String LOG_TAG = ViewHistoryFragment.class.getSimpleName();

    @BindView(R.id.rv_medhistory) RecyclerView mRecyclerView;
    @BindView(R.id.btn_remove) Button removeButton;

    private EntryViewModel entryViewModel;
    private List<Entry> entryList = new ArrayList<Entry>();
    private List<Entry> toDelete = new ArrayList<>();

    public ViewHistoryFragment() {
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
        mRecyclerView.addItemDecoration(new TaskDecoration(getActivity()));
        entryViewModel.getEntries().observe(this, new Observer<List<Entry>>() {
            @Override
            public void onChanged(@Nullable List<Entry> entries) {
                entryList = entries;
                mEntryAdapter.clear();
                mEntryAdapter.addAll(entries);
                mRecyclerView.setAdapter(mEntryAdapter);
            }
        });
        mEntryAdapter = new EntryAdapter(getActivity(), entryList, true, this);


        mRecyclerView.setAdapter(mEntryAdapter);

        return rootView;
    }

    @Override
    public void onEntryClick(int selectedEntryIndex) {

    }

    @Override
    public void onSearchClicked(View v) {
/*
        final long startMillis = 0;
        final long stopMillis = System.currentTimeMillis();
        //Use dates to retrieve info

*/
    }


    @Override
    public void handleFragmentButtonPush(View v) {
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
    public void showTimePickerDialog(View v) { }

    @Override
    public void showDatePickerDialog(View w) {
    }

    @Override
    public void showToDatePickerDialog(View v) {
    }


    @Override
    public void checkOff(View v) {
    }

    @Override
    public void saveChanges(View v) {
    }
}
