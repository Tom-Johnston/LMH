package com.johnston.lmhapp.MealMenus;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Johnston on 03/10/2014.
 */
class MenuAsync extends AsyncTask<Object, String, Void> {
    Handler statusHandler;

    @Override
    protected Void doInBackground(Object[] objects) {
        Context context = (Context) objects[0];
        Handler handler = (Handler) objects[1];
        statusHandler = (Handler) objects[2];
        ArrayList<String> meals = new ArrayList<>();
        File file = new File(context.getCacheDir(), "Menu.txt");
        try {
            publishProgress("Reading Menu");
            BufferedReader br = new BufferedReader(new FileReader(file));
            int Hours;
            int Minutes;
            long endOfMeal;
            long startOfDay=0;
            long currentTime = System.currentTimeMillis();
            String inputLine;
            String outputLine = "";
            Boolean old = true;

            while (true) {
                inputLine = br.readLine();
                if (inputLine == null) {
                    break;
                }
                if(checkForValidDate(inputLine)!=-1){
                    startOfDay = checkForValidDate(inputLine);
                } else if (inputLine.contains(":")) {
                    if (!outputLine.equals("")) {
                        meals.add(outputLine);
                        outputLine = "";
                    }
                    Hours = Integer.parseInt(inputLine.substring(6, 8));
                    Minutes = Integer.parseInt(inputLine.substring(9, 11));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(startOfDay);
                    calendar.set(Calendar.HOUR_OF_DAY, Hours);
                    calendar.set(Calendar.MINUTE, Minutes);
                    endOfMeal = calendar.getTimeInMillis();
                    if (endOfMeal > currentTime) {
                        old = false;
                        meals.add(inputLine); // Add the times.
                        outputLine = outputLine + br.readLine() + " on " + new SimpleDateFormat("EEEE d MMMM").format(new Date(endOfMeal));
                        meals.add(outputLine);
                        outputLine = "";
                    }
                } else {
                    if (!old && !inputLine.equals("")) {
                        if (!outputLine.equals("")) {
                            outputLine = outputLine + "\n";
                        }
                        outputLine = outputLine + inputLine;
                    }
                }

            }
            if (!outputLine.equals("")) {
                meals.add(outputLine);
            }
            publishProgress("Finished");
        } catch (FileNotFoundException e) {
            publishProgress("Error reading menu: FileNotFoundException");
            handler.obtainMessage(-1).sendToTarget();
            e.printStackTrace();
        } catch (IOException e) {
            publishProgress("Error reading menu: IOException");
            handler.obtainMessage(-1).sendToTarget();
            e.printStackTrace();
        }
        handler.obtainMessage(0, meals).sendToTarget();
        return null;
    }
    long checkForValidDate(String inputLine){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH);
        try{
            return simpleDateFormat.parse(inputLine).getTime();
        } catch (ParseException e) {
            return -1;
        }
    }
    @Override
    protected void onProgressUpdate(String... update) {
        statusHandler.obtainMessage(0,update[0]).sendToTarget();
    }
}
