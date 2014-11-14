package com.johnston.lmhapp.MealMenus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Tom on 13/08/2014.
 */
public class DownloadNewMenuAsync extends AsyncTask<Object, Void, Void> {

    @Override
    protected Void doInBackground(Object[] contexts) {
        Boolean widget;
        try {
            Context context = (Context) contexts[0];
            widget = (Boolean) contexts[1];
            File file = new File(context.getFilesDir(), "Menu.txt");
            URL url = new URL("https://drive.google.com/uc?id=0Bzygl0tJta6ZZmdRdnZyb2Iyb0k&export=download");
            InputStream menus = url.openStream();
            FileOutputStream fos = new FileOutputStream(file);
            int length;
            byte[] buffer = new byte[1024];
            while ((length = menus.read(buffer)) > -1) {
                fos.write(buffer, 0, length);
            }
            fos.close();
            if (!widget) {
                Handler handler = (Handler) contexts[2];
                handler.obtainMessage(1).sendToTarget();
                Intent intent = new Intent(context, NotificationsService.class);
                context.sendBroadcast(intent);
                SharedPreferences widgetEnabled = context.getSharedPreferences("widgetEnabled", 0);
                if (widgetEnabled.getBoolean("widgetEnabled", false)) {
                    Intent updateWidget = new Intent(context, WidgetBroadcastReceiver.class);
                    context.sendBroadcast(updateWidget);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
