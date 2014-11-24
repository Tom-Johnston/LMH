package com.johnston.lmhapp.LaundryView;

import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.johnston.lmhapp.R;

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
public class LaundryViewAsync extends AsyncTask<Object, Void, Boolean> {
    View view;
    Handler handler;
    String Error;
    @Override
    protected Boolean doInBackground(Object[] objects) {
        view = (View) objects[0];
        handler = (Handler) objects[1];

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
                ArrayList<String> stringArrayList = new ArrayList<String>();
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
                handler.obtainMessage(i-1,stringArrayList).sendToTarget();
            }
            return true;
        } catch (MalformedURLException e) {
            Error = "Error.Malformed URL.I have no idea why.";
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            Error = "Error.IO Exception. Check internet connection.";
            e.printStackTrace();
            return false;
        }

    }


    @Override
    protected void onPostExecute(Boolean v) {
        final TextView timerView = (TextView) view.findViewById(R.id.LastUpdate);
        if(!v){
            timerView.setText(Error);
            return;

        }
        final long startTime = System.currentTimeMillis();
        Runnable updateTime = new Runnable() {

            @Override
            public void run() {
                long runningTime = System.currentTimeMillis() - startTime;
                int minutes = (int) runningTime / (1000 * 60);
                int seconds = (int) (runningTime / (1000)) % 60;
                timerView.setText("Last updated: " + minutes + "m " + seconds + "s ago.");
                handler.postDelayed(this, 1000);
            }
        };
        handler.obtainMessage(0, startTime).sendToTarget();
        handler.post(updateTime);
    }
}
