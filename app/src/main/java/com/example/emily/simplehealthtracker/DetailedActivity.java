package com.example.emily.simplehealthtracker;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.emily.simplehealthtracker.data.EntryViewModel;

public class DetailedActivity extends AppCompatActivity {
    EntryViewModel entryViewModel;

    private static final String LOG_TAG = DetailedActivity.class.getSimpleName();

    private static final String NOTES = "notes";
    private static final String MED_HISTORY = "med_history";
    private static final String REMINDERS = "reminders";
    private static final String REPORT = "report";

    DetailedActivity.XmlClickable fragmentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        FragmentManager fragmentManager = getSupportFragmentManager();
        entryViewModel = ViewModelProviders.of(this).get(EntryViewModel.class);

        DetailedMenuFragment detailedMenuFragment = new DetailedMenuFragment();
        fragmentManager.beginTransaction()
                .add(R.id.detailed_fragment_container, detailedMenuFragment)
                .addToBackStack(null)
                .commit();
        fragmentManager.executePendingTransactions();

    }

    public void openDetailedTask(View view){
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch(view.getId()){
            case R.id.btn_notification_settings:
                Log.d(LOG_TAG, "in notification settings");
                if (fragmentManager.findFragmentByTag(REMINDERS) == null){
                    ViewRemindersFragment viewRemindersFragment = new ViewRemindersFragment();
                    fragmentManager.beginTransaction()
                            .replace(R.id.detailed_fragment_container, viewRemindersFragment, REMINDERS)
                            .addToBackStack(null)
                            .commit();
                    fragmentManager.executePendingTransactions();
                    fragmentButton = viewRemindersFragment;
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
                }
                break;

            case R.id.btn_medhistory:
                if (fragmentManager.findFragmentByTag(MED_HISTORY) == null){
                    ViewMedHistoryFragment viewMedHistoryFragment = new ViewMedHistoryFragment();
                    fragmentManager.beginTransaction()
                            .replace(R.id.detailed_fragment_container, viewMedHistoryFragment, MED_HISTORY)
                            .addToBackStack(null)
                            .commit();
                    fragmentManager.executePendingTransactions();
                    fragmentButton = viewMedHistoryFragment;
                }
                break;

            case R.id.btn_report:
                break;

            default:
                break;
        }
    }

    public void saveChanges(View v){
        fragmentButton.saveChanges(v);
    }
    public void insertNewRecord(View v){fragmentButton.insertNewRecordOrCancel(v);}
    public void showTimePickerDialog(View v){ fragmentButton.showTimePickerDialog(v); }
    public void showDatePickerDialog(View v){ fragmentButton.showDatePickerDialog(v); }
    public void showToDatePickerDialog(View v){ fragmentButton.showToDatePickerDialog(v);}

    public interface XmlClickable{
        //TODO: not really ideal
        void insertNewRecordOrCancel(View v);
        void saveChanges(View v);
        void checkOff(View v);
        void showTimePickerDialog(View v);
        void showDatePickerDialog(View w);
        void showToDatePickerDialog(View v);
    }


}
