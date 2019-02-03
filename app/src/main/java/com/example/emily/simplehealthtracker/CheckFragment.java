package com.example.emily.simplehealthtracker;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.emily.simplehealthtracker.data.Entry;
import com.example.emily.simplehealthtracker.data.EntryAdapter;
import com.example.emily.simplehealthtracker.data.EntryViewModel;
import com.example.emily.simplehealthtracker.data.SparseBooleanArrayParcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class CheckFragment extends Fragment implements SimpleActivity.XmlClickable, EntryAdapter.EntryClickListener {

    private EntryAdapter mEntryAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private static final String LOG_TAG = CheckFragment.class.getSimpleName();

    @BindView(R.id.rv_checklist) RecyclerView mRecyclerView;
    @BindView(R.id.btn_save) Button saveButton;

    private EntryViewModel entryViewModel;
    private List<Entry> entryList = new ArrayList<>();
    private List<Integer> toCheckOff = new ArrayList<>();


    public CheckFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        entryViewModel = ViewModelProviders.of(getActivity()).get(EntryViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_check, container, false);
        ButterKnife.bind(this, rootView);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addItemDecoration(new TaskDecoration(getActivity()));

        entryViewModel.getUndone().observe(this, new Observer<List<Entry>>() {
                    @Override
                    public void onChanged(@Nullable List<Entry> entries) {
                        entryList = entries;
                        mEntryAdapter.clear();
                        mEntryAdapter.addAll(entries);
                        mRecyclerView.setAdapter(mEntryAdapter);
                        toCheckOff.clear();
                        mEntryAdapter.clearSparseBooleanArray();


                    }
                });


            mEntryAdapter = new EntryAdapter(getActivity(), entryList, false, this);


        mRecyclerView.setAdapter(mEntryAdapter);
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);


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


    public void handleFragmentButtonPush(View view){ }

    @Override
    public void saveChanges(View view){

        SparseBooleanArrayParcelable sparseBooleanArray = mEntryAdapter.getSparseBooleanArray();
        for (int i = 0; i < sparseBooleanArray.size(); i++){
            int key = sparseBooleanArray.keyAt(i);
            if (sparseBooleanArray.get(key)){
                Entry checkedEntry = mEntryAdapter.getCurrentItem(key);
                toCheckOff.add(checkedEntry.getEntryId());
            }

        }


        if (toCheckOff.size() != 0){

             final List<Integer> finalCheckOff = toCheckOff;

            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    entryViewModel.checkOffScheduledItems(finalCheckOff);
                }
            });


        }

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //unbinder.unbind();
    }


    @Override
    public void checkOff(View v) {
        CheckBox doneCheckBox = v.findViewById(R.id.cb_done);
        TextView idHolderView = v.findViewById(R.id.tv_id_holder);
        Integer id = Integer.parseInt(idHolderView.getText().toString());

        if (doneCheckBox.isChecked()){
            toCheckOff.add(id);
        } else {
            toCheckOff.remove(id);
        }

    }


    public void showTimePickerDialog(View v){}
    public void showDatePickerDialog(View v){}

    @Override
    public void onEntryClick(int selectedEntryIndex) {}
}
