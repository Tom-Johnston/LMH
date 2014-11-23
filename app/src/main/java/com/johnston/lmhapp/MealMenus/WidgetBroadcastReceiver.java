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
import com.johnston.lmhapp.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
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


    public void part2(final File file,final Context context) {
        try {

            BufferedReader br = new BufferedReader(new FileReader(file));
//            Check the date.
            String dateString = br.readLine();
            Date date = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH).parse(dateString);
            long time = date.getTime();
            String[] output = constructMenu(br, time, context);
            String nextMeal = output[2];
            if (nextMeal.equals("")) {
//                We have an old menu.
                final Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message message) {
                        part3(file, context);
                    }
                };
                new DownloadNewMenuAsync().execute(context, true, handler);
            }else{
                part3(file, context);
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (ParseException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void part3(File file,Context context) {
        try {
            SharedPreferences refreshTimePreference = context.getSharedPreferences("RefreshTime",0);
            long  refreshTime = refreshTimePreference.getLong("refreshTime",2*60*60*1000);

            BufferedReader br = new BufferedReader(new FileReader(file));
            String dateString = br.readLine();
            Date date = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH).parse(dateString);
            long time = date.getTime();
            String[] output = constructMenu(br, time, context);
            String Day = output[0];
            String ShortDay = output[7];
            String Meal = output[1];
            String nextMeal = output[2];
            long TimeOfMeal = Long.parseLong(output[3]);
            if (nextMeal.equals("")) {
//                Looks like even the new menu is old;
                Date download_date = new Date(System.currentTimeMillis());
                DateFormat Date_format = DateFormat.getDateTimeInstance();
                nextMeal = "Old Menu" + "¬" + "Downloaded:" + Date_format.format(download_date);
                TimeOfMeal = System.currentTimeMillis() + refreshTime;
            }
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.lmh_widget);
            Intent svcIntent = new Intent(context, WidgetListViewService.class);
            svcIntent.putExtra("Options", nextMeal);
            svcIntent.setData(Uri.parse(
                    svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

            remoteViews.setRemoteAdapter(R.id.Menu, svcIntent);

            int width = context.getSharedPreferences("widgetWidth", 0).getInt("width", 0);
            if (width > 145) {
                remoteViews.setTextViewText(R.id.WidgetTitle, Day);
            } else {
                remoteViews.setTextViewText(R.id.WidgetTitle, ShortDay);
            }
            remoteViews.setTextViewText(R.id.Day, Meal);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName widget = new ComponentName(context, WidgetProvider.class);
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent newIntent = new Intent(context, WidgetBroadcastReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, newIntent, 0);
            Intent intentmenu = new Intent(context, MainActivity.class);
            intentmenu.putExtra("Launch", true);
            PendingIntent pi2 = PendingIntent.getActivity(context, 0, intentmenu, 0);
            remoteViews.setOnClickPendingIntent(R.id.WidgetTitle, pi2);
            remoteViews.setOnClickPendingIntent(R.id.Day, pi2);
            appWidgetManager.updateAppWidget(widget, remoteViews);
            if(TimeOfMeal!=-1){
                am.set(AlarmManager.RTC, TimeOfMeal + 1000, pi);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public String[] constructMenu(BufferedReader br, long time, Context context) throws IOException {

        String inputLine;
        int day = 0;
        String nextMeal = "";
        String Day;
        String ShortDay;
        String Meal = "Meal";
        int keepDay = 0;
        int Hours;
        int Minutes;
        Boolean record = false;
        long currentTime = System.currentTimeMillis();
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
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(time);
                    calendar.roll(Calendar.DAY_OF_WEEK,(day-2));
                    calendar.set(Calendar.HOUR_OF_DAY,Hours);
                    calendar.set(Calendar.MINUTE,Minutes);
                    startOfNextMeal = calendar.getTimeInMillis();
                    break;
                } else {
                    Times = inputLine;
                    Hours = Integer.parseInt(inputLine.substring(6, 8));
                    Minutes = Integer.parseInt(inputLine.substring(9, 11));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(time);
                    calendar.roll(Calendar.DAY_OF_WEEK,(day-2));
                    calendar.set(Calendar.HOUR_OF_DAY,Hours);
                    calendar.set(Calendar.MINUTE,Minutes);
                    TimeOfMeal = calendar.getTimeInMillis();
                    Hours = Integer.parseInt(inputLine.substring(0, 2));
                    Minutes = Integer.parseInt(inputLine.substring(3, 5));
                    calendar.setTimeInMillis(time);
                    calendar.roll(Calendar.DAY_OF_WEEK,(day-2));
                    calendar.set(Calendar.HOUR_OF_DAY,Hours);
                    calendar.set(Calendar.MINUTE,Minutes);
                    startOfMeal = calendar.getTimeInMillis();

                    if (currentTime < TimeOfMeal) {
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
            ShortDay = "Mon";
        } else if (day == 3) {
            Day = "Tuesday";
            ShortDay = "Tues";
        } else if (day == 4) {
            Day = "Wednesday";
            ShortDay = "Weds";
        } else if (day == 5) {
            Day = "Thursday";
            ShortDay = "Thurs";
        } else if (day == 6) {
            Day = "Friday";
            ShortDay = "Fri";
        } else if (day == 7) {
            Day = "Saturday";
            ShortDay = "Sat";
        } else if (day == 8) {
            Day = "Sunday";
            ShortDay = "Sun";
        } else {
            Day = "Problem";
            ShortDay = "Problem";
        }
        String[] output = new String[8];
        output[0] = Day;
        output[1] = Meal;
        output[2] = nextMeal;
        output[3] = String.valueOf(TimeOfMeal);
        output[4] = String.valueOf(startOfNextMeal);
        output[5] = Times;
        output[6] = String.valueOf(startOfMeal);
        output[7] = ShortDay;
//        This is undoubtedly bad practice but I am lazy. To be fixed in the future.
        return output;
    }


}
