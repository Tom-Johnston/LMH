package com.johnston.lmhapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * Created by Tom on 14/08/2014.
 */
public class MealMenuWidgetReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println(System.currentTimeMillis());
        System.out.println("Update");
        File file = new File(context.getFilesDir(), "Menu.txt");

        try {
            if (!file.exists()) {
                DownloadNewMenuTask task = (DownloadNewMenuTask) new DownloadNewMenuTask().execute(context, true);
                task.get();
            }
            BufferedReader br = new BufferedReader(new FileReader(file));
//            Check the date.
            String dateString = br.readLine();
            Date date = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH).parse(dateString);
            long time = date.getTime();
            String[] output = constructMenu(br, time);
            String Day = output[0];
            String Meal = output[1];
            String nextMeal = output[2];
            long TimeOfMeal = Long.parseLong(output[3]);

            if (nextMeal.equals("")) {
//                We have an old menu.
                DownloadNewMenuTask task = (DownloadNewMenuTask) new DownloadNewMenuTask().execute(context, true);
                task.get();
                br = new BufferedReader(new FileReader(file));
                dateString = br.readLine();
                date = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH).parse(dateString);
                time = date.getTime();
                output = constructMenu(br, time);
                Day = output[0];
                Meal = output[1];
                nextMeal = output[2];
                TimeOfMeal = Long.parseLong(output[3]);
            }
            if (nextMeal.equals("")) {
//                Looks like even the new menu is old;
                Date download_date = new Date(System.currentTimeMillis());
                DateFormat Date_format = DateFormat.getDateTimeInstance();
                nextMeal = "Old Menu" + "¬" + "Downloaded:" + Date_format.format(download_date);
                TimeOfMeal = System.currentTimeMillis() + 7200000;
            }
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.lmh_widget);
            Intent svcIntent = new Intent(context, ListViewService.class);
            svcIntent.putExtra("Options", nextMeal);
            svcIntent.setData(Uri.parse(
                    svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

            remoteViews.setRemoteAdapter(R.id.Menu, svcIntent);

            remoteViews.setTextViewText(R.id.WidgetTitle, Day);
            remoteViews.setTextViewText(R.id.Day, Meal);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName widget = new ComponentName(context, WidgetProvider.class);
//            System.out.println(nextMeal);
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent newIntent = new Intent(context, MealMenuWidgetReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, newIntent, 0);
            Intent intentmenu = new Intent(context, MainActivity.class);
            intentmenu.putExtra("Launch", true);
            PendingIntent pi2 = PendingIntent.getActivity(context, 0, intentmenu, 0);
            remoteViews.setOnClickPendingIntent(R.id.WidgetTitle, pi2);
            remoteViews.setOnClickPendingIntent(R.id.Day, pi2);
            appWidgetManager.updateAppWidget(widget, remoteViews);
            am.set(AlarmManager.RTC, TimeOfMeal + 1000, pi);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    public String[] constructMenu(BufferedReader br, long time) throws IOException {
        String inputLine;
        int day = 0;
        String nextMeal = "";
        String Day;
        String Meal = "Meal";
        int keepDay = 0;
        int Hours;
        int Minutes;
        Boolean record = false;
        long currentTime = System.currentTimeMillis();
        System.out.println(currentTime);
        long TimeOfMeal = 0;
        long startOfNextMeal = 0;
        long startOfMeal = 0;
        String Times = "";
        while (true) {
            inputLine = br.readLine();
            if (inputLine == null) {
                break;
            }
            if (inputLine.equals("Monday") || inputLine.equals("Monday,")) {
                day = 2;
            } else if ((inputLine.equals("Tuesday") || inputLine.equals("Tuesday,"))) {
                day = 3;
            } else if ((inputLine.equals("Wednesday") || inputLine.equals("Wednesday,"))) {
                day = 4;
            } else if ((inputLine.equals("Thursday") || inputLine.equals("Thursday,"))) {
                day = 5;
            } else if ((inputLine.equals("Friday") || inputLine.equals("Friday,"))) {
                day = 6;
            } else if ((inputLine.equals("Saturday") || inputLine.equals("Saturday,"))) {
                day = 7;
            } else if ((inputLine.equals("Sunday") || inputLine.equals("Sunday,"))) {
                day = 8;
            } else if (inputLine.contains(":")) {
                if (record) {
                    Hours = Integer.parseInt(inputLine.substring(0, 2));
                    Minutes = Integer.parseInt(inputLine.substring(3, 5));
                    startOfNextMeal = time + (day - 2) * 86400000 + Hours * 3600000 + Minutes * 60000;
                    break;
                } else {
                    Times = inputLine;
                    Hours = Integer.parseInt(inputLine.substring(6, 8));
                    Minutes = Integer.parseInt(inputLine.substring(9, 11));
//                    System.out.println(day + "//" + Hours + "//" + Minutes);
                    TimeOfMeal = time + (day - 2) * 86400000 + Hours * 3600000 + Minutes * 60000;
                    Hours = Integer.parseInt(inputLine.substring(0, 2));
                    Minutes = Integer.parseInt(inputLine.substring(3, 5));
                    startOfMeal = time + (day - 2) * 86400000 + Hours * 3600000 + Minutes * 60000;

                    if (currentTime < TimeOfMeal) {
                        System.out.println("This One");
                        record = true;
                        Meal = br.readLine();
                        keepDay = day;
                    }
                }
            } else if (record) {
                nextMeal = nextMeal + inputLine + "¬";
            }
        }
        day = keepDay;
        if (day == 2) {
            Day = "Monday";
        } else if (day == 3) {
            Day = "Tuesday";
        } else if (day == 4) {
            Day = "Wednesday";
        } else if (day == 5) {
            Day = "Thursday";
        } else if (day == 6) {
            Day = "Friday";
        } else if (day == 7) {
            Day = "Saturday";
        } else if (day == 8) {
            Day = "Sunday";
        } else {
            Day = "Problem";
        }
        String[] output = new String[7];
        output[0] = Day;
        output[1] = Meal;
        output[2] = nextMeal;
        output[3] = String.valueOf(TimeOfMeal);
        output[4] = String.valueOf(startOfNextMeal);
        output[5] = Times;
        output[6] = String.valueOf(startOfMeal);
//        This is undoubtedly bad practice but I am lazy. To be fixed in the future.
        return output;
    }


}
