package com.johnston.lmhapp;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by Tom on 12/08/2014.
 */
public class WidgetProvider extends AppWidgetProvider {

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        SharedPreferences widgetEnabled = context.getSharedPreferences("widgetEnabled", 0);
        SharedPreferences.Editor editor = widgetEnabled.edit();
        editor.putBoolean("widgetEnabled", true);
        editor.commit();
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        SharedPreferences widgetEnabled = context.getSharedPreferences("widgetEnabled", 0);
        SharedPreferences.Editor editor = widgetEnabled.edit();
        editor.putBoolean("widgetEnabled", false);
        editor.commit();
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Intent intent = new Intent(context, MealMenuWidgetReceiver.class);
        context.sendBroadcast(intent);
//        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
//        am.set(AlarmManager.RTC,0,pi);

    }

}
