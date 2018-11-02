package com.example.emily.simplehealthtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = BroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String description = intent.getStringExtra(RemindFragment.ALARM_TEXT);
        Log.d(LOG_TAG, "alarm going now");
        RemindFragment.showNotification(context, RemindFragment.class, description);
    }
}
