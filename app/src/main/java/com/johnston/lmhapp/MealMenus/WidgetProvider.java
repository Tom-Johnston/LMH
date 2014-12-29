package com.johnston.lmhapp.MealMenus;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * Created by Tom on 12/08/2014.
 */
public class WidgetProvider extends AppWidgetProvider {

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {

        int width = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);

        SharedPreferences widgetWidth = context.getSharedPreferences("widgetWidth", 0);
        SharedPreferences.Editor editor = widgetWidth.edit();
        editor.putInt("width", width);
        editor.apply();
        Intent intent = new Intent(context, WidgetBroadcastReceiver.class);
        context.sendBroadcast(intent);

    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        SharedPreferences widgetEnabled = context.getSharedPreferences("widgetEnabled", 0);
        SharedPreferences.Editor editor = widgetEnabled.edit();
        editor.putBoolean("widgetEnabled", true);
        editor.apply();
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        SharedPreferences widgetEnabled = context.getSharedPreferences("widgetEnabled", 0);
        SharedPreferences.Editor editor = widgetEnabled.edit();
        editor.putBoolean("widgetEnabled", false);
        editor.apply();
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Intent intent = new Intent(context, WidgetBroadcastReceiver.class);
        context.sendBroadcast(intent);
//        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
//        am.set(AlarmManager.RTC,0,pi);

    }

}
