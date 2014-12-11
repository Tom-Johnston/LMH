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
import java.util.Date;
import java.util.Locale;

/**
 * Created by Johnston on 03/10/2014.
 */
public class MenuAsync extends AsyncTask<Object, Void, Void> {

    @Override
    protected Void doInBackground(Object[] objects) {
        Context context = (Context) objects[0];
        Handler handler = (Handler) objects[1];
        ArrayList<String> meals = new ArrayList<>();
        File file = new File(context.getFilesDir(), "Menu.txt");
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
            String outputLine = "";
            Boolean old = true;

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
                    if (!outputLine.equals("")) {
                        meals.add(outputLine);
                        outputLine = "";
                    }
                    Hours = Integer.parseInt(inputLine.substring(6, 8));
                    Minutes = Integer.parseInt(inputLine.substring(9, 11));
                    mealTime = time + (day - 2) * 86400000 + Hours * 3600000 + Minutes * 60000;
                    if (mealTime > currentTime) {
                        old = false;
                        outputLine = outputLine + br.readLine() + " on " + new SimpleDateFormat("EEEE d MMMM").format(new Date(mealTime));
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        handler.obtainMessage(0, meals).sendToTarget();
        return null;
    }
}
