package com.example.emily.simplehealthtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String description = intent.getStringExtra(RemindFragment.ALARM_TEXT);
        RemindFragment.showNotification(context, RemindFragment.class, description);
    }
}
