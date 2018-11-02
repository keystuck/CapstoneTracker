package com.example.emily.simplehealthtracker;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.emily.simplehealthtracker.data.EntryAdapter;
import com.example.emily.simplehealthtracker.data.EntryViewModel;

public class SimpleActivity extends AppCompatActivity  {
    EntryViewModel entryViewModel;
    XmlClickable fragmentButton;

    private static final String CHECK_TAG = "check";
    private static final String NOTE_TAG = "note";
    private static final String REMIND_TAG = "remind";
    private static final String TRACK_TAG = "track";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        entryViewModel = ViewModelProviders.of(this).get(EntryViewModel.class);

        FragmentManager fragmentManager = getSupportFragmentManager();

        Intent intent = getIntent();
        if (intent.hasExtra(RemindFragment.OPEN_FRAGMENT)
                && intent.getStringExtra(RemindFragment.OPEN_FRAGMENT).equals(RemindFragment.CHECK_FRAGMENT)) {
            SimpleMenuFragment simpleMenuFragment = new SimpleMenuFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.simple_fragment_container, simpleMenuFragment)
                    //.addToBackStack(null)
                    .commit();
            fragmentManager.executePendingTransactions();

            CheckFragment checkFragment = new CheckFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.simple_fragment_container, checkFragment)
                    //.addToBackStack(null)
                    .commit();
            fragmentManager.executePendingTransactions();
        } else {
            SimpleMenuFragment simpleMenuFragment = new SimpleMenuFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.simple_fragment_container, simpleMenuFragment)
                    //.addToBackStack(null)
                    .commit();
            fragmentManager.executePendingTransactions();
        }
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
        }

        else if (taskId == R.id.btn_reminder && fragmentManager.findFragmentByTag(REMIND_TAG)== null) {
            RemindFragment remindFragment = new RemindFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.simple_fragment_container, remindFragment, REMIND_TAG)
                    .addToBackStack(null)
                    .commit();
            fragmentManager.executePendingTransactions();
            fragmentButton = remindFragment;
        }

        else if (taskId == R.id.btn_track && fragmentManager.findFragmentByTag(TRACK_TAG) == null){

            TrackFragment trackFragment = new TrackFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.simple_fragment_container, trackFragment, TRACK_TAG)
                    .addToBackStack(null)
                    .commit();
            fragmentManager.executePendingTransactions();
            fragmentButton = trackFragment;
        }

        else if (taskId == R.id.btn_note && fragmentManager.findFragmentByTag(NOTE_TAG) == null){

            NoteFragment noteFragment = new NoteFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.simple_fragment_container, noteFragment, NOTE_TAG)
                    .addToBackStack(null)
                    .commit();
            fragmentManager.executePendingTransactions();
            fragmentButton = noteFragment;

        }

    }

    public void insertNewRecord(View v){
        fragmentButton.insertNewRecordOrCancel(v);
    }

    public void checkOff(View v){
        fragmentButton.checkOff(v);
    }

    public void saveChanges(View v){
        fragmentButton.saveChanges(v);
    }

    public void showTimePickerDialog(View v){ fragmentButton.showTimePickerDialog(v); }

    public void showDatePickerDialog(View v){ fragmentButton.showDatePickerDialog(v); }

    public interface XmlClickable{
        //TODO: not really ideal
        void insertNewRecordOrCancel(View v);
        void saveChanges(View v);
        void checkOff(View v);
        void showTimePickerDialog(View v);
        void showDatePickerDialog(View w);
    }

}
