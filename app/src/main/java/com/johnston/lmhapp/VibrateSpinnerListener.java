package com.johnston.lmhapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class VibrateSpinnerListener implements AdapterView.OnItemSelectedListener {
    public boolean firstCall = true;
    public int last = 0;
    public MainActivity main;

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        System.out.println("First call: " + firstCall + pos);


        // Need to fix re-opening of custom.
        if (firstCall) {
            firstCall = false;
        } else {

            Toast.makeText(parent.getContext(),
                    "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString() + Integer.toString(pos),
                    Toast.LENGTH_SHORT).show();
            if (pos == 0) {
                SharedPreferences vibratePattern = main.getSharedPreferences("vibratePattern", 0);
                SharedPreferences.Editor editor = vibratePattern.edit();
                editor.putString("vibratePattern", main.getResources().getString(R.string.buzz1));
                editor.commit();
            } else if (pos == 1) {
                SharedPreferences vibratePattern = main.getSharedPreferences("vibratePattern", 0);
                SharedPreferences.Editor editor = vibratePattern.edit();
                editor.putString("vibratePattern", main.getResources().getString(R.string.buzz2));
                editor.commit();
            } else if (pos == 2) {
                SharedPreferences vibratePattern = main.getSharedPreferences("vibratePattern", 0);
                SharedPreferences.Editor editor = vibratePattern.edit();
                editor.putString("vibratePattern", main.getResources().getString(R.string.buzz3));
                editor.commit();
            } else if (pos == 3) {
                //User selected "Custom"
                main.notificationVibrate();
            }

            SharedPreferences vibratePattern = main.getSharedPreferences("vibratePattern", 0);
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
                Vibrator v = (Vibrator) main.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(vibrateArray, -1);

            }
            last = pos;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

}
