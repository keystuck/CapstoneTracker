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

import java.util.ArrayList;
import java.util.List;

public class SimpleActivity extends AppCompatActivity  {

    EntryViewModel entryViewModel;
    XmlClickable fragmentButton;

    private static final String TAG_TAG = "tag";
    private static final String CHECK_TAG = "check";
    private static final String NOTE_TAG = "note";
    private static final String REMIND_TAG = "remind";
    private static final String TRACK_TAG = "track";

    private Fragment mFragment;

    private List<Entry> entryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);



        if (savedInstanceState == null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);


            entryViewModel = ViewModelProviders.of(this).get(EntryViewModel.class);



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

        if (taskId == R.id.btn_check && fragmentManager.findFragmentByTag(CHECK_TAG) == null){
            CheckFragment checkFragment = new CheckFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.simple_fragment_container, checkFragment, CHECK_TAG)
                    .addToBackStack(null)
                    .commit();
            fragmentManager.executePendingTransactions();
            fragmentButton = checkFragment;
            mFragment = checkFragment;
        }

        else if (taskId == R.id.btn_reminder && fragmentManager.findFragmentByTag(REMIND_TAG)== null) {
            RemindFragment remindFragment = new RemindFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.simple_fragment_container, remindFragment, REMIND_TAG)
                    .addToBackStack(null)
                    .commit();
            fragmentManager.executePendingTransactions();
            fragmentButton = remindFragment;
            mFragment = remindFragment;
        }

        else if (taskId == R.id.btn_track && fragmentManager.findFragmentByTag(TRACK_TAG) == null){

            TrackFragment trackFragment = new TrackFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.simple_fragment_container, trackFragment, TRACK_TAG)
                    .addToBackStack(null)
                    .commit();
            fragmentManager.executePendingTransactions();
            fragmentButton = trackFragment;
            mFragment = trackFragment;
        }

        else if (taskId == R.id.btn_note && fragmentManager.findFragmentByTag(NOTE_TAG) == null){

            NoteFragment noteFragment = new NoteFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.simple_fragment_container, noteFragment, NOTE_TAG)
                    .addToBackStack(null)
                    .commit();
            fragmentManager.executePendingTransactions();
            fragmentButton = noteFragment;
            mFragment = noteFragment;

        }

    }

    public void handleButtonPush(View v){
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

}
