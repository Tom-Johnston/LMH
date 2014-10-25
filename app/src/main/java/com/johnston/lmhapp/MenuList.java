package com.johnston.lmhapp;

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
import java.util.Date;
import java.util.Locale;

/**
 * Created by Johnston on 03/10/2014.
 */
public class MenuList extends AsyncTask<Object, Void, Void> {

    @Override
    protected Void doInBackground(Object[] objects) {
        Context context = (Context) objects[0];
        Handler handler = (Handler) objects[1];
        ArrayList<String> meals = new ArrayList<String>();
        File file = new File(context.getFilesDir(), "Menu.txt");
        int startat = -1;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String dateString = br.readLine();
            Date readDate = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH).parse(dateString);
            long time = readDate.getTime();
            Byte day = 1;
            int Hours;
            int Minutes;
            long mealTime;
            long currentTime = System.currentTimeMillis();
            String inputLine;
            String outputLine = "00";
            Boolean old = false;

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
                    Hours = Integer.parseInt(inputLine.substring(6, 8));
                    Minutes = Integer.parseInt(inputLine.substring(9, 11));
                    System.out.println(day + "//" + Hours + "//" + Minutes);
                    mealTime = time + (day - 2) * 86400000 + Hours * 3600000 + Minutes * 60000;
                    if (mealTime < currentTime) {
                        old = true;
                        outputLine = "10";
                    } else {
                        old = false;
                        if (startat == -1) {
                            startat = meals.size();
                        }
                        outputLine = "11";
                    }
                    outputLine = outputLine + br.readLine() + " on " + new SimpleDateFormat("EEEE d MMMM").format(new Date(mealTime));
                    meals.add(outputLine);
                } else {
                    if (old) {
                        outputLine = "00";
                    } else {
                        outputLine = "01";
                    }
                    outputLine = outputLine + inputLine;
                    meals.add(outputLine);
                }
            }
            System.out.println(meals);
            meals.add(outputLine.substring(0, 2));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Object[] newObjects = new Object[2];
        newObjects[0] = meals;
        newObjects[1] = startat;
        handler.obtainMessage(0, newObjects).sendToTarget();
        return null;
    }
}
