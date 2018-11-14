package com.example.emily.simplehealthtracker;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import com.example.emily.simplehealthtracker.data.Entry;
import com.example.emily.simplehealthtracker.data.EntryViewModel;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class DetailedActivity extends AppCompatActivity {
    EntryViewModel entryViewModel;


    private static final String FRAGMENT_TAG = "fragment";
    private static final String NOTES = "notes";
    private static final String MED_HISTORY = "med_history";
    private static final String REMINDERS = "reminders";

    private Tracker mTracker;

    DetailedActivity.XmlClickable fragmentButton;
    private Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);


        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();



        if (savedInstanceState == null) {

            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);

            entryViewModel = ViewModelProviders.of(this).get(EntryViewModel.class);

            FragmentManager fragmentManager = getSupportFragmentManager();

            DetailedMenuFragment detailedMenuFragment = new DetailedMenuFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.detailed_fragment_container, detailedMenuFragment)
                    .commit();
            fragmentManager.executePendingTransactions();
            mFragment = detailedMenuFragment;
        } else {
            mFragment = getSupportFragmentManager().getFragment(savedInstanceState, FRAGMENT_TAG);
            fragmentButton = (XmlClickable) mFragment;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, FRAGMENT_TAG, mFragment);

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

    @Override
    protected void onResume() {
        super.onResume();
        String name = DetailedActivity.class.getSimpleName();


        mTracker.setScreenName("Image~" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }

    public void openDetailedTask(View view){
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch(view.getId()){


            case R.id.btn_notification_settings:
                if (fragmentManager.findFragmentByTag(REMINDERS) == null){
                    ViewRemindersFragment viewRemindersFragment = new ViewRemindersFragment();
                    fragmentManager.beginTransaction()
                            .replace(R.id.detailed_fragment_container, viewRemindersFragment, REMINDERS)
                            .addToBackStack(null)
                            .commit();
                    fragmentManager.executePendingTransactions();
                    fragmentButton = viewRemindersFragment;
                    mFragment = viewRemindersFragment;
                }
                break;




            case R.id.btn_notes:
                if (fragmentManager.findFragmentByTag(NOTES) == null){
                    ViewNotesFragment viewNotesFragment = new ViewNotesFragment();
                    fragmentManager.beginTransaction()
                            .replace(R.id.detailed_fragment_container, viewNotesFragment, NOTES)
                            .addToBackStack(null)
                            .commit();
                    fragmentManager.executePendingTransactions();
                    fragmentButton = viewNotesFragment;
                    mFragment = viewNotesFragment;
                }
                break;

            case R.id.btn_medhistory:
                if (fragmentManager.findFragmentByTag(MED_HISTORY) == null){

                    mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Fragment")
                    .setAction("Medical History")
                    .build());

                    ViewMedHistoryFragment viewMedHistoryFragment = new ViewMedHistoryFragment();
                    fragmentManager.beginTransaction()
                            .replace(R.id.detailed_fragment_container, viewMedHistoryFragment, MED_HISTORY)
                            .addToBackStack(null)
                            .commit();
                    fragmentManager.executePendingTransactions();
                    fragmentButton = viewMedHistoryFragment;
                    mFragment = viewMedHistoryFragment;
                }
                break;


            default:
                break;
        }
    }


    public void handleButtonClick(View v){
        fragmentButton.handleButtonClick(v);
    }
    public void insertNewRecordOrCancel(View v){ fragmentButton.onSearchClicked(v);}
    public void showTimePickerDialog(View v){ fragmentButton.showTimePickerDialog(v); }
    public void showDatePickerDialog(View v){ fragmentButton.showDatePickerDialog(v); }
    public void showToDatePickerDialog(View v){ fragmentButton.showToDatePickerDialog(v);}

    public interface XmlClickable{
        void onSearchClicked(View v);
        void handleButtonClick(View v);
        void showTimePickerDialog(View v);
        void showDatePickerDialog(View w);
        void showToDatePickerDialog(View v);
    }


}
