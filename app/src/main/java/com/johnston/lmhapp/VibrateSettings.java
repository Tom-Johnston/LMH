package com.johnston.lmhapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by Johnston on 18/09/2014.
 */
public class VibrateSettings extends DialogFragment {
    View view;

    static VibrateSettings newInstance() {
        VibrateSettings f = new VibrateSettings();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.vibrate_settings, null);
        builder.setView(view);
        builder.setTitle("Set Vibrate Pattern (off,on,off,on,...)");
        builder.setPositiveButton("Save", null);
        builder.setNeutralButton("Test", null);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        SharedPreferences vibratePattern = getActivity().getSharedPreferences("vibratePattern", 0);
        if (vibratePattern.contains("vibratePattern")) {
            EditText editText = (EditText) view.findViewById(R.id.vibratePattern);
            editText.setText(vibratePattern.getString("vibratePattern", ""));
        }
        final AlertDialog d = builder.create();
        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View useless) {
                        EditText editText = (EditText) view.findViewById(R.id.vibratePattern);
                        String pattern = editText.getText().toString();
                        if (!pattern.endsWith(",")) {
                            pattern = pattern + ",";
                        }
                        StringTokenizer stringTokenizer = new StringTokenizer(pattern, ",");
                        Boolean parse = true;
                        ArrayList<Long> vibratePatternList = new ArrayList<Long>();
                        while (stringTokenizer.hasMoreElements()) {
                            try {
                                vibratePatternList.add(Long.parseLong(stringTokenizer.nextToken()));
                            } catch (NumberFormatException nfe) {
                                parse = false;
                                break;
                            }
                        }
                        if (!parse || vibratePatternList.size() < 1) {
                            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Problem parsing integers.", Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            SharedPreferences vibratePattern = getActivity().getSharedPreferences("vibratePattern", 0);
                            SharedPreferences.Editor editor = vibratePattern.edit();
                            editor.putString("vibratePattern", pattern);
                            editor.commit();
                            Vibrate();
                            d.dismiss();
                            Handler handler = ((MainActivity) getActivity()).handler;
                            handler.obtainMessage(0, true).sendToTarget();
                        }

                    }
                });

                Button b2 = d.getButton(AlertDialog.BUTTON_NEUTRAL);
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View useless) {
                        Vibrate();

                    }

                });
                Button b3 = d.getButton(AlertDialog.BUTTON_NEGATIVE);
                b3.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Handler handler = ((MainActivity) getActivity()).handler;
                        handler.obtainMessage(0, false).sendToTarget();
                        d.dismiss();
                    }
                });


            }
        });
        return d;
    }

    public void Vibrate() {
        EditText editText = (EditText) view.findViewById(R.id.vibratePattern);
        String pattern = editText.getText().toString();
        if (!pattern.endsWith(",")) {
            pattern = pattern + ",";
        }
        StringTokenizer stringTokenizer = new StringTokenizer(pattern, ",");
        Boolean parse = true;
        ArrayList<Long> vibratePatternList = new ArrayList<Long>();
        while (stringTokenizer.hasMoreElements()) {
            try {
                vibratePatternList.add(Long.parseLong(stringTokenizer.nextToken()));
            } catch (NumberFormatException nfe) {
                parse = false;
                break;
            }
        }
        if (!parse || vibratePatternList.size() < 1) {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Problem parsing integers.", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Vibrator v = (Vibrator) getActivity().getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
            long[] vibrateArray = new long[vibratePatternList.size()];
            for (int i = 0; i < vibratePatternList.size(); i++) {
                vibrateArray[i] = vibratePatternList.get(i);
            }
            v.vibrate(vibrateArray, -1);
        }

    }
}
