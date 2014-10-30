package com.johnston.lmhapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class VibrateSpinnerListener implements AdapterView.OnItemSelectedListener {
    public int last = 0;
    public MainActivity main;
    public Boolean firstCall = true;
    public Handler handler;
    public int lastBeforeDialog = 0;

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        System.out.println(R.id.vibrations);
        System.out.println(last);
        System.out.println(pos);
        if (firstCall == true) {
//           First call is set to true when creating the fragment,rotating the fragment and loading from back stack.
            firstCall = false;
            return;
        } else if (last == pos) {
//           This should fix the problem on rotation.
            return;
        }
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
        } else {
            lastBeforeDialog = last;
            main.notificationVibrate(handler);
        }
        last = pos;
        if (pos < 3) {
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
            lastBeforeDialog = last;
            handler.obtainMessage(0, false).sendToTarget();
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

}
