package com.johnston.lmhapp.MealMenus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Tom on 13/08/2014.
 */
public class DownloadNewMenuAsync extends AsyncTask<Object, String, Void> {
    Handler statusHandler =null;

    @Override
    protected Void doInBackground(Object[] objects) {
        Boolean widget;
        Context context = (Context) objects[0];
        widget = (Boolean) objects[1];
        Handler handler=null;
        if(objects[2]!=null){
            handler = (Handler) objects[2];
        }
        if(objects.length==4&&objects[3]!=null){
            statusHandler = (Handler)objects[3];
        }
        try {
            File file = new File(context.getCacheDir(), "Menu.txt");
            URL url = new URL("https://drive.google.com/uc?id=0Bzygl0tJta6ZZmdRdnZyb2Iyb0k&export=download");
            InputStream menus = url.openStream();
            FileOutputStream fos = new FileOutputStream(file);
            int length;
            byte[] buffer = new byte[1024];
            while ((length = menus.read(buffer)) > -1) {
                fos.write(buffer, 0, length);
            }
            fos.close();
            if (!widget&&handler!=null) {
                handler.obtainMessage(1).sendToTarget();
                Intent intent = new Intent(context, NotificationsService.class);
                context.sendBroadcast(intent);
                SharedPreferences widgetEnabled = context.getSharedPreferences("widgetEnabled", 0);
                if (widgetEnabled.getBoolean("widgetEnabled", false)) {
                    Intent updateWidget = new Intent(context, WidgetBroadcastReceiver.class);
                    context.sendBroadcast(updateWidget);
                }
            } else if(handler!=null) {
                handler.sendEmptyMessage(0);
            }
            BufferedReader br = new BufferedReader(new FileReader(file));
            String firstLine = br.readLine();
            SharedPreferences sharedPreferences = context.getSharedPreferences("mealVersionNumber",0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            try{
                Integer integer = Integer.parseInt(firstLine.substring(8).trim());
                editor.putInt("mealVersionNumber",integer);
                editor.commit();
            } catch (NumberFormatException e){
//                Put in a minus one so that it will automatically update.
                editor.putInt("mealVersionNumber",-1);
                editor.commit();
            }
            publishProgress("Finished Downloading");
        } catch (MalformedURLException e) {
            if (!widget&&handler!=null) {
                handler.obtainMessage(-1).sendToTarget();
            }
            e.printStackTrace();
        } catch (IOException e) {
            if (!widget&handler!=null) {
                handler.obtainMessage(-1).sendToTarget();
            }
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(String... update){
        if(statusHandler!=null){
            statusHandler.obtainMessage(0,update[0]);
        }
    }
}
