package com.example.emily.simplehealthtracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emily.simplehealthtracker.data.Entry;
import com.example.emily.simplehealthtracker.data.EntryViewModel;
import com.example.emily.simplehealthtracker.data.RepeatingEntryViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RemindFragment extends Fragment implements SimpleActivity.XmlClickable, AdapterView.OnItemSelectedListener {
    private List<Entry> entryList;
    private EntryViewModel entryViewModel;
    private RepeatingEntryViewModel repeatingEntryViewModel;
    @BindView(R.id.et_description) EditText etDescription;
    @BindView(R.id.et_dose) EditText etDose;
    @BindView(R.id.tv_date) TextView tvDate;
    @BindView(R.id.tv_time) TextView tvTime;
    @BindView(R.id.sp_type) Spinner spType;
    @BindView(R.id.cb_repeating) CheckBox cbRepeating;
    @BindView(R.id.np_days) NumberPicker npDays;

    private String entryType;
    private long entryId;
    public static final String ALARM_TEXT = "description";
    public static final String OPEN_FRAGMENT = "fragment";
    public static final String CHECK_FRAGMENT = "check";
    public static final String FIELDS_INFO = "fields";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        entryViewModel = ViewModelProviders.of(getActivity()).get(EntryViewModel.class);
        repeatingEntryViewModel = ViewModelProviders.of(getActivity()).get(RepeatingEntryViewModel.class);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<String> fieldContents = new ArrayList<>();
        fieldContents.add(etDescription.getText().toString());
        fieldContents.add(etDose.getText().toString());
        fieldContents.add(tvDate.getText().toString());
        fieldContents.add(tvTime.getText().toString());
        if (entryType != null){
            fieldContents.add(entryType);
        } else {
            fieldContents.add(getResources().getString(R.string.no_type));
        }
        outState.putStringArrayList(FIELDS_INFO, fieldContents);
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

        npDays.setMinValue(0);
        npDays.setMaxValue(7);
        npDays.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //TODO: save repeating interval
            }
        });

        cbRepeating.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //TODO: set as repeating reminder
                }
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.types_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(adapter);

        spType.setOnItemSelectedListener(this);

        if (savedInstanceState != null && savedInstanceState.getStringArrayList(FIELDS_INFO) != null){
            ArrayList<String> fields = savedInstanceState.getStringArrayList(FIELDS_INFO);
            etDescription.setText(fields.get(0));
            etDose.setText(fields.get(1));
            tvDate.setText(fields.get(2));
            tvTime.setText(fields.get(3));
            String type = fields.get(4);
            if (type.equals(getResources().getString(R.string.no_type))){
                spType.setSelection(0);
            } else {
                boolean found = false;
                ArrayList<String> resourcesArray = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.types_array)));
                for (int i = 0; i < resourcesArray.size(); i++){
                    if (resourcesArray.get(i).equals(type)){
                        found = true;
                        spType.setSelection(i);
                        break;
                    }
                }
                if (found == false){
                    spType.setSelection(0);
                }
            }
        }
        return rootView;
    }

    public void handleFragmentButtonPush(View view){
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
        String date = tvDate.getText().toString();
        String enteredTime = tvTime.getText().toString();
        if (date != null && enteredTime != null){
            String fullTime = date + " " + enteredTime;
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

        final Entry currentEntry = new Entry(augDescription, amp, taken, time, type, 1);

        Log.d(RemindFragment.class.getSimpleName(), "checkbox? " + cbRepeating.isChecked());

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
               entryId = entryViewModel.addEntry(currentEntry);
                if(cbRepeating.isChecked()){

                    repeatingEntryViewModel.addRepeatingEntry((int)entryId,
                            time,
                            npDays.getValue());
                }
            }
        });





        Entry earliestEntry = currentEntry;
        for (Entry entry : entryList){
            if (entry.getTimeStamp() < earliestEntry.getTimeStamp() &&
                    entry.getTimeStamp() > System.currentTimeMillis()){
                earliestEntry = entry;
            }
        }
        if (earliestEntry.getTimeStamp() > System.currentTimeMillis()){
            NextAlarmWidgetProvider.updateWithNewReminder(getActivity(), earliestEntry.getDescription(), earliestEntry.getTimeStamp());
        } else {
            NextAlarmWidgetProvider.updateWithNewReminder(getActivity(), "", 0);
        }

        int id = generateId(augDescription, time);
        int repeat = (cbRepeating.isChecked()) ? npDays.getValue() : 0;
        setAlarm(getActivity(), AlarmReceiver.class, time, id, augDescription, repeat);

        Toast.makeText(getActivity(), getResources().getString(R.string.add_OK), Toast.LENGTH_SHORT).show();
        etDescription.setText("");
        etDose.setText("");
        tvDate.setText("");
        tvTime.setText("");
        if (cbRepeating.isChecked()) {
            cbRepeating.toggle();
        }
        npDays.setValue(0);
    }

    public static int generateId(String description, long time){
        String combined = description + time;
        int alarmId = combined.hashCode();
        return alarmId;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
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



    public static void setAlarm(Context context, Class<?> cls, long alarmTime, int id, String description, int repeatDays){



            Intent i = new Intent(context, cls);
            i.putExtra(ALARM_TEXT, description);

            PendingIntent pendingI = PendingIntent.getBroadcast(context,
                    id, i, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (repeatDays == 0) {
                am.set(AlarmManager.RTC_WAKEUP, alarmTime,
                        pendingI);
            } else {
                am.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                        alarmTime,
                        (AlarmManager.INTERVAL_DAY * repeatDays),
                        pendingI);
            }
    }



}
