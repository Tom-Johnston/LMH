package com.johnston.lmhapp.MealMenus;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.widget.RemoteViews;

import com.johnston.lmhapp.MainActivity;
import com.johnston.lmhapp.PermissionAsync;
import com.johnston.lmhapp.PermissionFailedDialog;
import com.johnston.lmhapp.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Tom on 14/08/2014.
 */
public class WidgetBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(final Context context, Intent intent) {
        Handler permissionHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                if (message.what == 0) {
//                Success!
                    part1(context);
                } else{
//                Failure
                    SharedPreferences refreshTimePreference = context.getSharedPreferences("RefreshTime", 0);
                    long refreshTime = refreshTimePreference.getLong("refreshTime", 2 * 60 * 60 * 1000);
                    RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.lmh_widget);
                    Intent svcIntent = new Intent(context, WidgetListViewService.class);
                    svcIntent.putExtra("Options", "Unable to get permission for new Menu");
                    svcIntent.setData(Uri.parse(
                            svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

                    remoteViews.setRemoteAdapter(R.id.Menu, svcIntent);
                    remoteViews.setTextViewText(R.id.Day,"Day");
                    remoteViews.setTextViewText(R.id.Meal, "Meal");
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    ComponentName widget = new ComponentName(context, WidgetProvider.class);
                    AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent newIntent = new Intent(context, WidgetBroadcastReceiver.class);
                    PendingIntent pi = PendingIntent.getBroadcast(context, 0, newIntent, 0);
                    Intent intentmenu = new Intent(context, MainActivity.class);
                    intentmenu.putExtra("Launch", true);
                    PendingIntent pi2 = PendingIntent.getActivity(context, 0, intentmenu, 0);
                    remoteViews.setOnClickPendingIntent(R.id.Day, pi2);
                    remoteViews.setOnClickPendingIntent(R.id.Meal, pi2);
                    appWidgetManager.updateAppWidget(widget, remoteViews);

                    if (refreshTime != -1) {
                        am.set(AlarmManager.RTC, System.currentTimeMillis() + refreshTime + 1000, pi);
                    }
                }
            }
        };
        new PermissionAsync().execute(context, permissionHandler,null);
    }

    public void part1(final Context context){
        final File file = new File(context.getFilesDir(), "Menu.txt");

        if (!file.exists()) {
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message message) {
                    part3(file, context);
                }
            };
            new DownloadNewMenuAsync().execute(context, true, handler);

        } else {
            part2(file, context);
        }
    }


    public void part2(final File file, final Context context) {
        try {

            BufferedReader br = new BufferedReader(new FileReader(file));
//            Check the date.
            long startOfMeal  = (long) constructMenu(br)[2];
            if (startOfMeal==-1) {
//                We have an old menu.
                final Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message message) {
                        part3(file, context);
                    }
                };
                new DownloadNewMenuAsync().execute(context, true, handler);
            } else {
                part3(file, context);
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void part3(File file, Context context) {
        try {
            SharedPreferences refreshTimePreference = context.getSharedPreferences("RefreshTime", 0);
            long refreshTime = refreshTimePreference.getLong("refreshTime", 2 * 60 * 60 * 1000);

            BufferedReader br = new BufferedReader(new FileReader(file));

            Object[] output = constructMenu(br);
            String Meal = (String) output[0];
            String menu = (String) output[1];
            long startOfMeal  = (long) output[2];
            long endOfMeal = (long) output[3];
            if (startOfMeal==-1) {
//                Looks like even the new menu is old;
                Date download_date = new Date(System.currentTimeMillis());
                DateFormat Date_format = DateFormat.getDateTimeInstance();
                menu = "Old Menu" + "¬" + "Downloaded:" + Date_format.format(download_date);

                if(refreshTime==-1){
                    endOfMeal = -1;
                }else{
                    endOfMeal = System.currentTimeMillis() + refreshTime;
                }
            }

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.lmh_widget);
            Intent svcIntent = new Intent(context, WidgetListViewService.class);
            svcIntent.putExtra("Options", menu);
            svcIntent.setData(Uri.parse(
                    svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

            remoteViews.setRemoteAdapter(R.id.Menu, svcIntent);

            int width = context.getSharedPreferences("widgetWidth", 0).getInt("width", 0);
            String[] namesOfDays;
            if (width > 145) {
                namesOfDays = DateFormatSymbols.getInstance().getWeekdays();
            } else {
                namesOfDays = DateFormatSymbols.getInstance().getShortWeekdays();
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(startOfMeal);
            remoteViews.setTextViewText(R.id.Day,namesOfDays[calendar.DAY_OF_WEEK]);
            remoteViews.setTextViewText(R.id.Meal, Meal);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName widget = new ComponentName(context, WidgetProvider.class);
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent newIntent = new Intent(context, WidgetBroadcastReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, newIntent, 0);
            Intent intentmenu = new Intent(context, MainActivity.class);
            intentmenu.putExtra("Launch", true);
            PendingIntent pi2 = PendingIntent.getActivity(context, 0, intentmenu, 0);
            remoteViews.setOnClickPendingIntent(R.id.Day, pi2);
            remoteViews.setOnClickPendingIntent(R.id.Meal, pi2);
            appWidgetManager.updateAppWidget(widget, remoteViews);

            if (endOfMeal != -1) {
                am.set(AlarmManager.RTC, endOfMeal + 1000, pi);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Object[] constructMenu(BufferedReader br) throws IOException {

        String inputLine;
        String menu = "";
        String Meal = "Meal";
        int Hours;
        int Minutes;
        Boolean record = false;
        long currentTime = System.currentTimeMillis();
        long endOfMeal = -1;
        long startOfNextMeal = -1;
        long startOfMeal = -1;
        String Times = "";
        long timeOfBeginningOfDay=0;
        while (true) {
            inputLine = br.readLine();
            if (inputLine == null) {
                break;
            }
            if(checkForValidDate(inputLine)!=-1){
                timeOfBeginningOfDay=checkForValidDate(inputLine);
            } else if (inputLine.contains(":")) {
                if (record) {
                    Hours = Integer.parseInt(inputLine.substring(0, 2));
                    Minutes = Integer.parseInt(inputLine.substring(3, 5));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(timeOfBeginningOfDay);
                    calendar.set(Calendar.HOUR_OF_DAY, Hours);
                    calendar.set(Calendar.MINUTE, Minutes);
                    startOfNextMeal = calendar.getTimeInMillis();
                    break;
                } else {
                    Times = inputLine;
                    Hours = Integer.parseInt(inputLine.substring(6, 8));
                    Minutes = Integer.parseInt(inputLine.substring(9, 11));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(timeOfBeginningOfDay);
                    calendar.set(Calendar.HOUR_OF_DAY, Hours);
                    calendar.set(Calendar.MINUTE, Minutes);
                    endOfMeal = calendar.getTimeInMillis();
                    Hours = Integer.parseInt(inputLine.substring(0, 2));
                    Minutes = Integer.parseInt(inputLine.substring(3, 5));
                    calendar.setTimeInMillis(timeOfBeginningOfDay);
                    calendar.set(Calendar.HOUR_OF_DAY, Hours);
                    calendar.set(Calendar.MINUTE, Minutes);
                    startOfMeal = calendar.getTimeInMillis();

                    if (currentTime < endOfMeal) {
                        record = true;
                        Meal = br.readLine();
                    }
                }
            } else if (record) {
                menu = menu + inputLine + "¬";
            }
        }


        Object[] output = new Object[8];
        output[0] = Meal;
        output[1] = menu;
        output[2] = startOfMeal;
        output[3] = endOfMeal;
        output[4] = Times;
        output[5] = startOfNextMeal;
        return output;
    }

    public long checkForValidDate(String inputLine){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH);
        try{
            return simpleDateFormat.parse(inputLine).getTime();
        } catch (ParseException e) {
            return -1;
        }
    }

}
