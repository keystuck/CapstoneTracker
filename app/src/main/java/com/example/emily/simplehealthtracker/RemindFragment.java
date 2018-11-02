package com.example.emily.simplehealthtracker;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
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
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.emily.simplehealthtracker.data.Entry;
import com.example.emily.simplehealthtracker.data.EntryViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RemindFragment extends Fragment implements SimpleActivity.XmlClickable, AdapterView.OnItemSelectedListener {
    private static final String LOG_TAG = RemindFragment.class.getSimpleName();
    private List<Entry> entryList;
    private EntryViewModel entryViewModel;
    @BindView(R.id.et_description) EditText etDescription;
    @BindView(R.id.et_dose) EditText etDose;
    @BindView(R.id.et_date) EditText etDate;
    @BindView(R.id.et_time) EditText etTime;
    @BindView(R.id.sp_type) Spinner spType;

    private String entryType;
    public static final String ALARM_TEXT = "description";
    public static final String OPEN_FRAGMENT = "fragment";
    public static final String CHECK_FRAGMENT = "check";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        entryViewModel = ViewModelProviders.of(getActivity()).get(EntryViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_remind, container, false);
        entryViewModel.getEntries().observe(this, new Observer<List<Entry>>() {
            @Override
            public void onChanged(@Nullable List<Entry> entries) {
                entryList = entries;
            }
        });
        ButterKnife.bind(this, rootView);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.types_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(adapter);

        spType.setOnItemSelectedListener(this);

        return rootView;
    }

    public void insertNewRecordOrCancel(View view){
        String descriptionString = etDescription.getText().toString();
        String dose = etDose.getText().toString();
        if (!dose.isEmpty()){
            descriptionString += " (" + dose + ")";
        }
        final String augDescription = descriptionString;
        //final Entry newEntry = new Entry(descriptionString, 0, 0, 0, "MEDS");
        final int amp = 0;
        final int taken = 0;
        long reminderTime = System.currentTimeMillis();
        String date = etDate.getText().toString();
        String enteredTime = etTime.getText().toString();
        //TODO: ADD DATA VALIDATION
        if (date != null && enteredTime != null){
            String fullTime = date + " " + enteredTime;
            Log.d(LOG_TAG, "entered: " + fullTime);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy K:mm aa");

            try {
                Date mDate = simpleDateFormat.parse(fullTime);
                reminderTime = mDate.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        final long time = reminderTime;

        if (entryType.isEmpty()){
            entryType = getResources().getString(R.string.default_entry);
        }
        final String type = entryType;

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                entryViewModel.addEntry(augDescription, amp, taken, time, type, 1);
            }
        });

        int id = generateId(augDescription, time);
        setAlarm(getActivity(), AlarmReceiver.class, time, id, augDescription);
    }

    public static int generateId(String description, long time){
        String combined = description + time;
        int alarmId = combined.hashCode();
        return alarmId;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d(LOG_TAG, "setting entryType");
        entryType = adapterView.getItemAtPosition(i).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        entryType = getResources().getString(R.string.default_entry);
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
    public void saveChanges(View v) {
        //TODO: Yuck
    }

    @Override
    public void checkOff(View v) {

    }

    public void showTimePickerDialog(View v){
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getActivity().getSupportFragmentManager(),
                getResources().getString(R.string.time_picker));

    }

    public void showDatePickerDialog(View v){
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getActivity().getSupportFragmentManager(),
                getResources().getString(R.string.date_picker));

    }

    public static void showNotification(Context context, Class<?> cls, String description){
        Log.d(LOG_TAG, "showNotification now");



        Intent intent = new Intent(context, SimpleActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(OPEN_FRAGMENT, CHECK_FRAGMENT);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        int notificationId = 300;

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_playlist_add_check_red_24dp)
                .setContentTitle(context.getResources().getString(R.string.notification_title))
                .setContentText(context.getResources().getString(R.string.notification_text) + " " + description)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, mBuilder.build());
    }



    public static void setAlarm(Context context, Class<?> cls, long alarmTime, int id, String description){
/*        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
*/
        Intent i = new Intent(context, cls);
        i.putExtra(ALARM_TEXT, description);

        PendingIntent pendingI = PendingIntent.getBroadcast(context,
                id, i, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, alarmTime,
                pendingI);
    }



}
