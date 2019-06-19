package com.example.emily.simplehealthtracker;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.emily.simplehealthtracker.data.Entry;
import com.example.emily.simplehealthtracker.data.EntryViewModel;
import com.example.emily.simplehealthtracker.data.RepeatingEntry;
import com.example.emily.simplehealthtracker.data.RepeatingEntryViewModel;

import java.util.ArrayList;
import java.util.List;

public class SimpleActivity extends AppCompatActivity  {

    EntryViewModel entryViewModel;
    RepeatingEntryViewModel repeatingEntryViewModel;
    XmlClickable fragmentButton;

    private static final String LOG_TAG = SimpleActivity.class.getSimpleName();
    private static final long TWO_HOURS = 2 * 3600 * 1000;

    private static final String TAG_TAG = "tag";
    private static final String CHECK_TAG = "check";
    private static final String NOTE_TAG = "note";
    private static final String REMIND_TAG = "remind";
    private static final String TRACK_TAG = "track";

    private static final String DELETE_TAG = "delete";

    private static final String ADD_TAG = "add";
    private static final String REMOVE_TAG = "remove";

    private Fragment mFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);



        if (savedInstanceState == null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);


            entryViewModel = ViewModelProviders.of(this).get(EntryViewModel.class);
            repeatingEntryViewModel = ViewModelProviders.of(this).get(RepeatingEntryViewModel.class);



            updateRepeatingList();



            FragmentManager fragmentManager = getSupportFragmentManager();


                SimpleMenuFragment simpleMenuFragment = new SimpleMenuFragment();
                fragmentManager.beginTransaction()
                        .add(R.id.simple_fragment_container, simpleMenuFragment)
                        //.addToBackStack(null)
                        .commit();
                fragmentManager.executePendingTransactions();
                mFragment = simpleMenuFragment;


        }

        else {
            fragmentButton = (XmlClickable) getSupportFragmentManager().getFragment(savedInstanceState, TAG_TAG);
            mFragment = getSupportFragmentManager().getFragment(savedInstanceState, TAG_TAG);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, TAG_TAG, mFragment);
    }

    @Override
    public View onCreateView(View parent, String name, final Context context, AttributeSet attrs) {
        entryViewModel = ViewModelProviders.of(this).get(EntryViewModel.class);

        entryViewModel.getNextReminder().observe(this, new Observer<Entry>() {
            @Override
            public void onChanged(@Nullable Entry entry) {
                if (entry == null){
                    NextAlarmWidgetProvider.updateWithNewReminder(context, "", 0);
                } else {
                    NextAlarmWidgetProvider.updateWithNewReminder(context,
                            entry.getDescription(),
                            entry.getTimeStamp());
                }
            }
        });

        return super.onCreateView(parent, name, context, attrs);
    }

    public void openTask(View view){
        int taskId = view.getId();
        FragmentManager fragmentManager = getSupportFragmentManager();


        if (taskId == R.id.btn_add && fragmentManager.findFragmentByTag(ADD_TAG) == null){
            AddMenuFragment addMenuFragment = new AddMenuFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.simple_fragment_container, addMenuFragment, ADD_TAG)
                    .addToBackStack(null)
                    .commit();
            Log.d(LOG_TAG, "clicked add");
            fragmentManager.executePendingTransactions();
            //TODO: this is a mismatch btwn Simple & Detailed -- fix
//            fragmentButton = addMenuFragment;
            mFragment = addMenuFragment;
        }

        else if (taskId == R.id.btn_remove && fragmentManager.findFragmentByTag(REMOVE_TAG) == null){
            RemoveMenuFragment removeMenuFragment = new RemoveMenuFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.simple_fragment_container, removeMenuFragment, REMOVE_TAG)
                    .addToBackStack(null)
                    .commit();
            fragmentManager.executePendingTransactions();
            Log.d(LOG_TAG, "clicked remove");
            //TODO: this is a mismatch btwn Simple & Detailed -- fix
//            fragmentButton = removeMenuFragment;
            mFragment = removeMenuFragment;
        }
        else if (taskId == R.id.btn_add_future && fragmentManager.findFragmentByTag(REMIND_TAG) == null){
            RemindFragment remindFragment = new RemindFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.simple_fragment_container, remindFragment, REMIND_TAG)
                    .addToBackStack(null)
                    .commit();
            fragmentManager.executePendingTransactions();
            Log.d(LOG_TAG, "clicked add future");
            fragmentButton = remindFragment;
            mFragment = remindFragment;
        }
        else if (taskId == R.id.btn_add_past && fragmentManager.findFragmentByTag(TRACK_TAG) == null){
            TrackFragment trackFragment = new TrackFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.simple_fragment_container, trackFragment, TRACK_TAG)
                    .addToBackStack(null)
                    .commit();
            fragmentManager.executePendingTransactions();
            Log.d(LOG_TAG, "clicked add past");
            fragmentButton = trackFragment;
            mFragment = trackFragment;
        }
        else if (taskId == R.id.btn_add_note && fragmentManager.findFragmentByTag(NOTE_TAG) == null){
            NoteFragment noteFragment = new NoteFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.simple_fragment_container, noteFragment, NOTE_TAG)
                    .addToBackStack(null)
                    .commit();
            fragmentManager.executePendingTransactions();
            Log.d(LOG_TAG, "clicked add note");
            fragmentButton = noteFragment;
            mFragment = noteFragment;
        }
        else if (taskId == R.id.btn_check_off && fragmentManager.findFragmentByTag(CHECK_TAG) == null){
            CheckFragment checkFragment = new CheckFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.simple_fragment_container, checkFragment, CHECK_TAG)
                    .addToBackStack(null)
                    .commit();
            fragmentManager.executePendingTransactions();
            fragmentButton = checkFragment;
            mFragment = checkFragment;
        }
        else if (taskId == R.id.btn_delete_mistake && fragmentManager.findFragmentByTag(DELETE_TAG) == null){
            Log.d(LOG_TAG, "clicked delete");
            ViewHistoryFragment viewHistoryFragment = new ViewHistoryFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.simple_fragment_container, viewHistoryFragment, DELETE_TAG)
                    .addToBackStack(null)
                    .commit();
            fragmentManager.executePendingTransactions();
            fragmentButton = viewHistoryFragment;
            mFragment = viewHistoryFragment;

        }

    }

    public void handleFragmentButtonPush(View v){
        fragmentButton.handleFragmentButtonPush(v);
    }

    public void checkOff(View v){
        fragmentButton.checkOff(v);
    }

    public void saveChanges(View v){
        fragmentButton.saveChanges(v);
    }

    public void showTimePickerDialog(View v){ fragmentButton.showTimePickerDialog(v); }

    public void showDatePickerDialog(View v){ fragmentButton.showDatePickerDialog(v); }

    //implementation from Blundell at
    //https://stackoverflow.com/questions/6091194/how-to-handle-button-clicks-using-the-xml-onclick-within-fragments
    public interface XmlClickable{
        void handleFragmentButtonPush(View v);
        void saveChanges(View v);
        void checkOff(View v);
        void showTimePickerDialog(View v);
        void showDatePickerDialog(View w);
    }


    //instead of generating all possible entries for repeating reminders, update lazily
    //update with all entries that should exist between the last one and now
    public void updateRepeatingList() {



        Log.d(LOG_TAG, "inside udpateRepeatingList");
        repeatingEntryViewModel.getEntries().observe(this, new Observer<List<RepeatingEntry>>() {
            @Override
            public void onChanged(@Nullable List<RepeatingEntry> repeatingEntries) {
                for (final RepeatingEntry repeatingEntry : repeatingEntries) {
                    Log.d(LOG_TAG, repeatingEntry.toString());

                    while ((repeatingEntry.lastEntryMade + repeatingEntry.repeatInterval)
                            < (System.currentTimeMillis() + TWO_HOURS)) {
                        Log.d(LOG_TAG, "inside the loop");
                        final long nextUpdate = repeatingEntry.lastEntryMade + repeatingEntry.repeatInterval;
                        Log.d(LOG_TAG, "last update " + repeatingEntry.lastEntryMade + " next update " +
                                (repeatingEntry.lastEntryMade + repeatingEntry.repeatInterval));
                        //get details from entryId
                        final int entryId = repeatingEntry.firstEntryId;
                        Log.d(LOG_TAG, "looking for entry " + entryId);


                        AppExecutors.getInstance().diskIO().execute(new Runnable() {

                            @Override
                            public void run() {
                                Entry tempEntry = entryViewModel.getEntryWithId(entryId);
                                if (tempEntry != null) {
                                    Log.d(LOG_TAG, "found Entry with id " + tempEntry.getEntryId());
                                    //put an undone task in entry table
                                    final Entry newEntry = new Entry(tempEntry.getDescription(),
                                            tempEntry.getAmplitude(),
                                            0,
                                            nextUpdate,
                                            tempEntry.getRecordType(),
                                            1);
                                    entryViewModel.addEntry(newEntry);
                                    //then update the repeatingentries table
                                    repeatingEntry.setLastEntryMade(nextUpdate);
                                }
                            }
                        });
                    }
                }
            }
        });
    }




}
