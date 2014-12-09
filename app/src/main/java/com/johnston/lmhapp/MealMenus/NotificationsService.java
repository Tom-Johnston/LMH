package com.johnston.lmhapp.MealMenus;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import com.johnston.lmhapp.MainActivity;
import com.johnston.lmhapp.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by Johnston on 15/09/2014.
 */
public class NotificationsService extends BroadcastReceiver {

    long notifyTime = 10 * 60 * 1000;
    PowerManager.WakeLock wl;

    @Override
    public void onReceive(final Context context, Intent intent) {

        int nt = context.getSharedPreferences("NotifyTime", 0).getInt("NotifyTime", 10);
        notifyTime = nt * 60 * 1000;

//        I think alarm manager automatically holds a wakelock for me.
//        Wakelock so the notification can be sent even when the device is asleep;

//        Now I am splitting this method up the wakelock for onReceive will not cover it. I think.
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LMH NOTIFICATION");
        wl.setReferenceCounted(false);
        wl.acquire();
        SharedPreferences Notifications = context.getSharedPreferences("Notifications", 0);
        Boolean toggle = Notifications.getBoolean("toggle", false);
        if (!toggle) {
            wl.release();
            return;
        }
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

    private void part2(final File file, final Context context) {
        try {
            WidgetBroadcastReceiver mealMenu = new WidgetBroadcastReceiver();
            BufferedReader br = new BufferedReader(new FileReader(file));
//            Check the date.
            String dateString = null;
            dateString = br.readLine();
            Date date = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH).parse(dateString);
            long time = date.getTime();
            String[] output = mealMenu.constructMenu(br, time, context);
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
            } else {
                part3(file, context);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public void part3(File file, Context context) {
        try {
            SharedPreferences refreshTimePreference = context.getSharedPreferences("RefreshTime", 0);
            long refreshTime = refreshTimePreference.getLong("refreshTime", 2 * 60 * 60 * 1000);
            WidgetBroadcastReceiver mealMenu = new WidgetBroadcastReceiver();
            BufferedReader br = new BufferedReader(new FileReader(file));
//            Check the date.
            String dateString = null;
            dateString = br.readLine();
            Date date = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH).parse(dateString);
            long time = date.getTime();
            String[] output = mealMenu.constructMenu(br, time, context);
            String nextMeal = output[2];

            long startOfNextMeal = Long.parseLong(output[4]);
            String Meal = output[1];
            String Times = output[5];
            long startOfMeal = Long.parseLong(output[6]);
            if (nextMeal.equals("")) {
//                Looks like even the new menu is old;
//                Handle this.
                startOfNextMeal = System.currentTimeMillis() + refreshTime;
                AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent newIntent = new Intent(context, NotificationsService.class);
                PendingIntent pi = PendingIntent.getBroadcast(context, 0, newIntent, 0);
                am.set(AlarmManager.RTC_WAKEUP, startOfNextMeal - notifyTime, pi);
                wl.release();
                return;
            }

            if (startOfNextMeal == 0) {
                startOfNextMeal = System.currentTimeMillis() + refreshTime;
            }
            SharedPreferences sharedPreferences = context.getSharedPreferences("mealsToNotifyFor", 0);
            Boolean notifyForLunch = sharedPreferences.getBoolean("Lunch", true);
            Boolean notifyForDinner = sharedPreferences.getBoolean("Dinner", true);
            Boolean skipNotification = false;
            if (!notifyForLunch && Meal.equals("Lunch")) {
                skipNotification = true;
            } else if (!notifyForDinner && Meal.equals("Dinner")) {
                skipNotification = true;
            }

            if (System.currentTimeMillis() + notifyTime > startOfMeal && !skipNotification) {
//                We are supposed to be showing a notification. This is because this class is called to start the chain of notifications.
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                builder.setContentTitle(Meal + " at " + Times);
                builder.setSmallIcon(R.drawable.ic_notification);
                builder.setContentText("Expand to see menu");
                SharedPreferences NotificationSound = context.getSharedPreferences("NotificationSound", 0);
                if (NotificationSound.contains("SoundURI")) {
                    String SoundURI = (NotificationSound.getString("SoundURI", "null"));
                    if (!SoundURI.equals("None")) {

                        builder.setSound(Uri.parse(SoundURI));
                    }
                } else {
                    builder.setDefaults(Notification.DEFAULT_SOUND);
                }
                SharedPreferences vibratePattern = context.getSharedPreferences("vibratePattern", 0);
                if (vibratePattern.contains("vibratePattern")) {
                    String pattern = vibratePattern.getString("vibratePattern", "null");
                    StringTokenizer stringTokenizer = new StringTokenizer(pattern, ",");
                    ArrayList<Long> vibratePatternList = new ArrayList<Long>();
                    while (stringTokenizer.hasMoreElements()) {
                        vibratePatternList.add(Long.parseLong(stringTokenizer.nextToken()));
                    }
                    long[] vibrateArray = new long[vibratePatternList.size()];
                    for (int i = 0; i < vibratePatternList.size(); i++) {
                        vibrateArray[i] = vibratePatternList.get(i);
                    }
                    builder.setVibrate(vibrateArray);
                } else {
                    builder.setDefaults(Notification.DEFAULT_VIBRATE);
                }
                SharedPreferences LEDSettings = context.getSharedPreferences("LEDSettings", 0);
                int r = LEDSettings.getInt("redValue", 0);
                int g = LEDSettings.getInt("greenValue", 33);
                int b = LEDSettings.getInt("blueValue", 71);
                int onFor = LEDSettings.getInt("onFor", 1000);
                int offFor = LEDSettings.getInt("offFor", 1000);
                Intent intentmenu = new Intent(context, MainActivity.class);
                intentmenu.putExtra("Launch", true);
                PendingIntent pi2 = PendingIntent.getActivity(context, 0, intentmenu, 0);
                builder.setContentIntent(pi2);
                builder.setLights(Color.argb(255, r, g, b), onFor, offFor);
                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                inboxStyle.setBigContentTitle(Meal + " at " + Times);
                StringTokenizer stringTokenizer = new StringTokenizer(nextMeal, "¬");
                while (stringTokenizer.hasMoreTokens()) {
                    inboxStyle.addLine(stringTokenizer.nextToken());
                }
                builder.setStyle(inboxStyle);
                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(1, builder.build());
            } else if (!skipNotification) {
                startOfNextMeal = startOfMeal;
            }
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent newIntent = new Intent(context, NotificationsService.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, newIntent, 0);
            am.set(AlarmManager.RTC_WAKEUP, startOfNextMeal - notifyTime, pi);
            wl.release();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
