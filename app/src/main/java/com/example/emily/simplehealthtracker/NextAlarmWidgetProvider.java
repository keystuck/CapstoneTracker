package com.example.emily.simplehealthtracker;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Implementation of App Widget functionality.
 */
public class NextAlarmWidgetProvider extends AppWidgetProvider {

    public static void updateWithNewReminder(Context context, String nextReminder, long timeOfReminder){
        String timeString = "";
        if (nextReminder.isEmpty() || timeOfReminder == 0){
            timeString = context.getResources().getString(R.string.widget_failure_text);
        }
        else {
            if (timeOfReminder > System.currentTimeMillis()) {
                Date date = new Date(timeOfReminder);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd hh:mm");
                String time = simpleDateFormat.format(date);

                timeString = nextReminder + " " + time;
            } else {
                timeString = context.getResources().getString(R.string.widget_failure_text);
            }
        }
        ComponentName componentName = new ComponentName(context, NextAlarmWidgetProvider.class);
        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(componentName);
        for (int i = 0; i < ids.length; i++) {
            updateAppWidget(context, AppWidgetManager.getInstance(context), ids[i], timeString);
        }

    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
        int appWidgetId, String nextReminder){

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.next_alarm_widget);
        views.setTextViewText(R.id.appwidget_text, nextReminder);

    Intent intent = new Intent(context, MainActivity.class);
    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget, pendingIntent);
        views.setTextViewText(R.id.appwidget_text, nextReminder);


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, "");
        }
    }

}

