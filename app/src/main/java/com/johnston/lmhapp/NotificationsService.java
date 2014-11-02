package com.johnston.lmhapp;

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
import android.support.v4.app.NotificationCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

/**
 * Created by Johnston on 15/09/2014.
 */
public class NotificationsService extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        long refreshTime = 2 * 60 * 60 * 1000;
        long notifyTime = 600000;
//        I think alarm manager automatically holds a wakelock for me.
//        Wakelock so the notification can be sent even when the device is asleep;
//        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "LMH NOTIFICATION");
//        wl.setReferenceCounted(false);
//        wl.acquire();
        SharedPreferences Notifications = context.getSharedPreferences("Notifications", 0);
        System.out.println("Starting");
        Boolean toggle = Notifications.getBoolean("toggle", false);
        if (!toggle) {
            System.out.println("Turned OFf");
            return;
        }
        MealMenuWidgetReceiver mealMenu = new MealMenuWidgetReceiver();
//        It is no longer as much of a copy and paste anymore.
//        This following section is a copy and paste from MealMenuWidgetReceiver.java. I might split up the method in MealMenuWidgetReceiver.java so I am not repeating lots of stuff but this is quicker and easier right now.
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
            String[] output = mealMenu.constructMenu(br, time, context);

            String nextMeal = output[2];


            if (nextMeal.equals("")) {
//                We have an old menu.
                DownloadNewMenuTask task = (DownloadNewMenuTask) new DownloadNewMenuTask().execute(context, true);
                task.get();
                br = new BufferedReader(new FileReader(file));
                dateString = br.readLine();
                date = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH).parse(dateString);
                time = date.getTime();
                output = mealMenu.constructMenu(br, time, context);
                nextMeal = output[2];
            }
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
                return;
            }

            if (startOfNextMeal == 0) {
                startOfNextMeal = System.currentTimeMillis() + refreshTime;
            }
            if (System.currentTimeMillis() + notifyTime > startOfMeal) {
//                We are supposed to be showing a notification. This is because this class is called to start the chain of notifications.
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                builder.setContentTitle(Meal + " at " + Times);
                builder.setSmallIcon(R.drawable.ic_notification);
                builder.setContentText("Expand to see menu");
                SharedPreferences NotificationSound = context.getSharedPreferences("NotificationSound", 0);
                if (NotificationSound.contains("SoundURI")) {
                    System.out.println("contains SOundURi");
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
            } else {
                startOfNextMeal = startOfMeal;
            }
            System.out.println("Finished");
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent newIntent = new Intent(context, NotificationsService.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, newIntent, 0);
            am.set(AlarmManager.RTC_WAKEUP, startOfNextMeal - notifyTime, pi);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
