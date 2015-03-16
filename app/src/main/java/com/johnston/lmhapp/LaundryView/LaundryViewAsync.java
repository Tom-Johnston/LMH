package com.johnston.lmhapp.LaundryView;

import android.os.AsyncTask;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Tom on 03/06/2014.
 */
class LaundryViewAsync extends AsyncTask<Object, Void, Void> {
    private Handler statusHandler;
    private Handler handler;

    @Override
    protected Void doInBackground(Object[] objects) {
        Handler statusHandler = (Handler) objects[0];
        handler = (Handler) objects[1];
        statusHandler.obtainMessage(0,"Pulling data").sendToTarget();
        try {
            for (int i = 2; i < 5; i++) {
                URL url = new URL(objects[i].toString());
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        urlc.getInputStream(), "UTF-8"));
                String inputLine;
                StringBuilder a = new StringBuilder();
                while ((inputLine = in.readLine()) != null)
                    a.append(inputLine);
                in.close();
                int Start = 0;
                ArrayList<String> stringArrayList = new ArrayList<>();
                while (a.indexOf("\"stat\">", Start) > 0) {
                    Start = a.indexOf("\"stat\">", Start) + 7;
                    int end = a.indexOf("<", Start);
                    String Result = a.substring(Start, end).trim();
                    Result = Result.substring(0, 1).toUpperCase() + Result.substring(1);
                    String Type;
                    if (a.lastIndexOf("washer", Start) > a.lastIndexOf("dryer", Start)) {
                        Type = "Washer:  ";
                    } else if (a.lastIndexOf("washer", Start) < a.lastIndexOf("dryer", Start)) {
                        Type = "Dryer:      ";
                    } else {
                        Type = "Unknown";
                    }
                    stringArrayList.add(Type + Result);

                }
                handler.obtainMessage(i - 1, stringArrayList).sendToTarget();
            }
            final long startTime = System.currentTimeMillis();
            handler.obtainMessage(0, startTime).sendToTarget();
            statusHandler.obtainMessage(0,"Finished").sendToTarget();
            handler.obtainMessage(4).sendToTarget();
            return null;
        } catch (MalformedURLException e) {
            statusHandler.obtainMessage(0,"Error getting LaundryView: MalformedURLException").sendToTarget();
            handler.obtainMessage(-1).sendToTarget();
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            statusHandler.obtainMessage(0, "Error getting LaundryView: IO Exception. Check your network connection.").sendToTarget();
            handler.obtainMessage(-1).sendToTarget();
            e.printStackTrace();
            return null;
        }

    }


}
