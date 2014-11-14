package com.johnston.lmhapp.LaundryView;

import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.johnston.lmhapp.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

/**
 * Created by Tom on 03/06/2014.
 */
public class LaundryViewAsync extends AsyncTask<Object, Void, String> {
    View view;
    Handler handler;

    @Override
    protected String doInBackground(Object[] objects) {
        view = (View) objects[0];
        handler = (Handler) objects[1];
        StringBuilder PassBack = new StringBuilder();

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
                    PassBack.append(Type + Result + "\n");

                }
                PassBack.append("%%%");
            }
            return PassBack.toString();
        } catch (MalformedURLException e) {
            String Error = "Error.Malformed URL.I have no idea why.";
            e.printStackTrace();
            return Error;
        } catch (IOException e) {
            String Error = "Error.IO Exception. Check internet connection.";
            e.printStackTrace();
            return Error;

        }

    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        final TextView timerView = (TextView) view.findViewById(R.id.LastUpdate);
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
        StringTokenizer st = new StringTokenizer(s, "%%%");
        ProgressBar P1 = (ProgressBar) view.findViewById(R.id.P1);
        ProgressBar P2 = (ProgressBar) view.findViewById(R.id.P2);
        ProgressBar P3 = (ProgressBar) view.findViewById(R.id.P3);
        P1.setVisibility(View.GONE);
        P2.setVisibility(View.GONE);
        P3.setVisibility(View.GONE);
        try {
            TextView KatieLee = (TextView) view.findViewById(R.id.KatieLee);
            TextView NewOldHall = (TextView) view.findViewById(R.id.NewOldHall);
            TextView Talbot = (TextView) view.findViewById(R.id.Talbot);
            KatieLee.setVisibility(View.VISIBLE);
            NewOldHall.setVisibility(View.VISIBLE);
            Talbot.setVisibility(View.VISIBLE);
            KatieLee.setText(st.nextToken());
            NewOldHall.setText(st.nextToken());
            Talbot.setText(st.nextToken());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
