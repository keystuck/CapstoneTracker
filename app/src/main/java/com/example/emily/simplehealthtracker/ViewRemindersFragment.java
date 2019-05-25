package com.example.emily.simplehealthtracker;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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
public class ViewRemindersFragment extends Fragment implements DetailedActivity.XmlClickable, EntryAdapter.EntryClickListener {
    private EntryAdapter mEntryAdapter;
    private LinearLayoutManager mLinearLayoutManager;


    @BindView(R.id.rv_viewreminders)
    RecyclerView mRecyclerView;
    @BindView(R.id.btn_remove)
    Button removeButton;

    private EntryViewModel entryViewModel;
    private List<Entry> entryList = new ArrayList<Entry>();
    private List<Entry> toDelete = new ArrayList<>();

    public ViewRemindersFragment() {
        // Required empty public constructor
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        entryViewModel = ViewModelProviders.of(getActivity()).get(EntryViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_reminders, container, false);
        ButterKnife.bind(this, rootView);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addItemDecoration(new TaskDecoration(getActivity()));

       entryViewModel.getEntriesWithReminders(System.currentTimeMillis()).observe(this, new Observer<List<Entry>>() {
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
    public void handleFragmentButtonPush(View v) {
        SparseBooleanArray sparseBooleanArray = mEntryAdapter.getSparseBooleanArray();
        for (int i = 0; i < sparseBooleanArray.size(); i++){
            int key = sparseBooleanArray.keyAt(i);
            if (sparseBooleanArray.get(key)){
                Entry checkedEntry = mEntryAdapter.getCurrentItem(key);
                toDelete.add(checkedEntry);
            }
        }

        //cancel alarms before deleting
        for (Entry entry : toDelete){
            int idForCancel = RemindFragment.generateId(entry.getDescription(), entry.getEntryId());
            cancelReminder(getActivity(), AlarmReceiver.class, idForCancel);
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

    public static void cancelReminder(Context context, Class<?> cls, int id){
        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        Intent intent1 = new Intent(context, cls);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context,
                id, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pendingIntent1);
        pendingIntent1.cancel();
    }

    @Override
    public void onSearchClicked(View v) {
    }


    @Override
    public void showTimePickerDialog(View v) { }

    @Override
    public void showDatePickerDialog(View w) { }

    @Override
    public void showToDatePickerDialog(View v) { }
}
