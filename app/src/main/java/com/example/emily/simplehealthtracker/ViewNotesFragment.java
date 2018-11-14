package com.example.emily.simplehealthtracker;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
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
public class ViewNotesFragment extends Fragment implements DetailedActivity.XmlClickable, EntryAdapter.EntryClickListener {

    private EntryAdapter mEntryAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    @BindView(R.id.rv_noteshistory) RecyclerView mRecyclerView;
    @BindView(R.id.btn_delete) Button deleteButton;


    private EntryViewModel entryViewModel;
    private List<Entry> entryList = new ArrayList<Entry>();
    private List<Entry> toDelete = new ArrayList<>();

    public ViewNotesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        entryViewModel = ViewModelProviders.of(getActivity()).get(EntryViewModel.class);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_notes, container, false);
        // Inflate the layout for this fragment
        ButterKnife.bind(this, rootView);

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addItemDecoration(new TaskDecoration(getActivity()));

        entryViewModel.getEntriesOfType(getResources().getString(R.string.type_note)).observe(this, new Observer<List<Entry>>() {
            @Override
            public void onChanged(@Nullable List<Entry> entries) {
                entryList = entries;
                mEntryAdapter.clear();
                mEntryAdapter.addAll(entries);
                mRecyclerView.setAdapter(mEntryAdapter);
                toDelete.clear();
                mEntryAdapter.clearSparseBooleanArray();
            }
        });

        mEntryAdapter = new EntryAdapter(getActivity(), entryList, false, this);
        mRecyclerView.setAdapter(mEntryAdapter);

        return rootView;
    }

    @Override
    public void handleButtonClick(View v) {
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
    public void onSearchClicked(View v) {

    }

    @Override
    public void onEntryClick(int selectedEntryIndex) { }

    @Override
    public void showTimePickerDialog(View v) { }

    @Override
    public void showDatePickerDialog(View w) { }

    @Override
    public void showToDatePickerDialog(View v) { }
}
