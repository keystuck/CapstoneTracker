package com.example.emily.simplehealthtracker;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
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
import com.example.emily.simplehealthtracker.data.EntryAdapter$EntryViewHolder_ViewBinding;
import com.example.emily.simplehealthtracker.data.EntryViewModel;

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
    //implements ScheduledAdapter.OnBoxChecked
    private final static String LOG_TAG = CheckFragment.class.getSimpleName();

    private EntryAdapter mEntryAdapter;
//    private ScheduledAdapter mScheduledAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    @BindView(R.id.rv_checklist) RecyclerView mRecyclerView;
    @BindView(R.id.btn_save) Button saveButton;
    private Unbinder unbinder;

    private EntryViewModel entryViewModel;
    private List<Entry> entryList = new ArrayList<>();
    private List<Integer> toCheckOff = new ArrayList<>();

    //TODO: onclicklistener to listen for checkboxes; add ids to a list
    //savebutton causes viewmodel to make all those ids taken=true

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
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_check, container, false);
        ButterKnife.bind(this, rootView);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        entryViewModel.getEntries().observe(this, new Observer<List<Entry>>() {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            getActivity().onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /*
        @Override
        public void onCheckBoxClick(int position) {
            if (mRecyclerView.get)
            int id = scheduledList.get(position).getScheduledId();
        }
    */
    public void insertNewRecordOrCancel(View view){
/*        CheckBox checkBox = view.findViewById(R.id.cb_done);
        TextView idView = view.findViewById(R.id.tv_id_holder);
        Integer id = Integer.parseInt(idView.getText().toString());

        if (checkBox.isChecked()){
            Log.d(LOG_TAG, "MUMU?");
            toCheckOff.add(id);
        } else {
            toCheckOff.remove(id);
        }
*/    }

    @Override
    public void saveChanges(View view){

        SparseBooleanArray sparseBooleanArray = mEntryAdapter.getSparseBooleanArray();
        Log.d(LOG_TAG, "boolean array? " + sparseBooleanArray.size());
        for (int i = 0; i < sparseBooleanArray.size(); i++){
            Log.d(LOG_TAG, "looking for position " + i);
            int key = sparseBooleanArray.keyAt(i);
            Log.d(LOG_TAG, "key at position " + i + " is " + key);
            if (sparseBooleanArray.get(key)){
                Entry checkedEntry = mEntryAdapter.getCurrentItem(key);
                Log.d(LOG_TAG, "will get rid of item " + key + ": " + checkedEntry.getDescription());
                toCheckOff.add(checkedEntry.getEntryId());
            }

        }


        Log.d(LOG_TAG, "in save: " + toCheckOff.size());
        if (toCheckOff.size() != 0){

             final List<Integer> finalCheckOff = toCheckOff;

            Log.d(LOG_TAG, "about to pass list to viewModel; size: " + finalCheckOff.size());
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    entryViewModel.checkOffScheduledItems(finalCheckOff);
                    Log.d(LOG_TAG, "called with " + finalCheckOff.size());
                }
            });
            Log.d(LOG_TAG, "what the f? " + finalCheckOff.size());


        }

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //unbinder.unbind();
    }


    //TODO: Not working quiiiite right
    @Override
    public void checkOff(View v) {
        CheckBox doneCheckBox = v.findViewById(R.id.cb_done);
        TextView idHolderView = v.findViewById(R.id.tv_id_holder);
        Integer id = Integer.parseInt(idHolderView.getText().toString());
        Log.d(LOG_TAG, "id? " + id);

        if (doneCheckBox.isChecked()){
            toCheckOff.add(id);
            Log.d(LOG_TAG, "checked; list now " + toCheckOff.size());
        } else {
            toCheckOff.remove(id);
            Log.d(LOG_TAG, "list now " + toCheckOff.size());
        }

    }


    public void showTimePickerDialog(View v){}
    public void showDatePickerDialog(View v){}

    @Override
    public void onEntryClick(int selectedEntryIndex) {
/*        RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForAdapterPosition(selectedEntryIndex);
        TextView idHolderView = viewHolder.itemView.findViewById(R.id.tv_id_holder);
        Integer entryId =  Integer.parseInt(idHolderView.getText().toString());
        CheckBox doneBox = viewHolder.itemView.findViewById(R.id.cb_done);
        if (doneBox.isChecked()){
            toCheckOff.add(entryId);
        } else {
            toCheckOff.remove(entryId);
        }
        */
    }
}
